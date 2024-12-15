package it.zerono.mods.zerocore.lib.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public record ModCodecs<Type, Buffer extends ByteBuf>(Codec<Type> codec, StreamCodec<Buffer, Type> streamCodec) {

    public static StreamCodec<ByteBuf, List<byte[]>> BYTE_ARRAY_LIST = ByteBufCodecs.BYTE_ARRAY.apply(ByteBufCodecs.list());

    public <Encoded> DataResult<Encoded> encode(Type value, DynamicOps<Encoded> ops) {
        return this.codec.encodeStart(ops, value);
    }

    public <Encoded> DataResult<Encoded> encode(Type value, DynamicOps<Encoded> ops, Encoded output) {
        return this.codec.encode(value, ops, output);
    }

    public <Encoded> DataResult<Type> decode(Encoded input, DynamicOps<Encoded> ops) {
        return this.codec.parse(ops, input);
    }

    public void encode(Type value, Buffer buffer) {
        this.streamCodec.encode(buffer, value);
    }

    public Type decode(Buffer buffer) {
        return this.streamCodec.decode(buffer);
    }

    public Codec<List<Type>> listCodec() {
        return this.codec.listOf();
    }

    public Codec<List<Type>> listCodec(int minSize, int maxSize) {
        return this.codec.listOf(minSize, maxSize);
    }

    public <O> RecordCodecBuilder<O, Type> field(String name, Function<O, Type> getter) {
        return this.codec.fieldOf(name).forGetter(getter);
    }

    public <O> RecordCodecBuilder<O, List<Type>> listField(String name, Function<O, List<Type>> getter) {
        return this.listCodec().fieldOf(name).forGetter(getter);
        }

    public <O> RecordCodecBuilder<O, List<Type>> listField(String name, Function<O, List<Type>> getter,
                                                           int minSize, int maxSize) {
        return this.listCodec(minSize, maxSize).fieldOf(name).forGetter(getter);
    }

    public StreamCodec<Buffer, List<Type>> listStreamCodec() {
        return this.streamCodec.apply(ByteBufCodecs.list());
    }

    public StreamCodec<Buffer, List<Type>> listStreamCodec(int maxSize) {
        return this.streamCodec.apply(ByteBufCodecs.list(maxSize));
    }

    public Codec<Type> validate(Function<Type, DataResult<Type>> validator) {
        return this.codec.validate(validator);
    }

    public StreamCodec<Buffer, Type> validate(Consumer<Type> validator) {
        return validateStreamCodec(this.streamCodec, validator);
    }

    //region Helpers

    public static <T, Buffer extends ByteBuf> StreamCodec<Buffer, T> validateStreamCodec(StreamCodec<Buffer, T> codec,
                                                                                         Consumer<T> validator) {
        return new StreamCodec<>() {

            @Override
            public void encode(Buffer buffer, T value) {

                try {
                    validator.accept(value);
                } catch (Exception original) {
                    throw new EncoderException("Validation failed", original);
                }

                codec.encode(buffer, value);
            }

            @Override
            public T decode(Buffer buffer) {

                final var value = codec.decode(buffer);

                try {
                    validator.accept(value);
                } catch (Exception original) {
                    throw new DecoderException("Validation failed", original);
                }

                return value;
            }
        };
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, HolderSet<T>> holderSetStreamCodec(ResourceKey<? extends Registry<T>> registryKey,
                                                                                              StreamCodec<RegistryFriendlyByteBuf, Holder<T>> codec) {
        return new StreamCodec<>() {

            public void encode(RegistryFriendlyByteBuf buffer, HolderSet<T> holderSet) {

                final Optional<TagKey<T>> tagKey = holderSet.unwrapKey();

                if (tagKey.isPresent()) {

                    VarInt.write(buffer, 0);
                    ResourceLocation.STREAM_CODEC.encode(buffer, tagKey.get().location());

                } else {

                    VarInt.write(buffer, holderSet.size() + 1);

                    for (final Holder<T> holder : holderSet) {
                        codec.encode(buffer, holder);
                    }
                }
            }

            public HolderSet<T> decode(RegistryFriendlyByteBuf buffer) {

                final int marker = VarInt.read(buffer);

                if (0 == marker) {

                    // a TagKey<T>

                    final Registry<T> registry = buffer.registryAccess().lookupOrThrow(registryKey);
                    final var location = ResourceLocation.STREAM_CODEC.decode(buffer);

                    return registry.get(TagKey.create(registryKey, location)).orElseThrow();

                } else {

                    // a list of Holder<T>

                    final int length = marker - 1;
                    final List<Holder<T>> list = new ObjectArrayList<>(Math.min(length, 65536));

                    for (int i = 0; i < length; ++i) {
                        list.add(codec.decode(buffer));
                    }

                    return HolderSet.direct(list);
                }
            }
        };
    }

    public static <T> Codec<List<T>> nonEmptyListCodec(Codec<List<T>> codec) {
        return codec.validate(list -> list.isEmpty() ?
                DataResult.error(() -> "An empty list is not allowed") :
                DataResult.success(list));
    }

    public static <T, Buffer extends ByteBuf> StreamCodec<Buffer, List<T>>
    nonEmptyListStreamCodec(StreamCodec<Buffer, List<T>> codec) {
        return validateStreamCodec(codec, list -> {

            if (list.isEmpty()) {
                throw new UnsupportedOperationException("An empty list is not allowed");
            }
        });
    }

    public static <M extends Map<?, ?>> Codec<M> nonEmptyMapCodec(Codec<M> codec) {
        return codec.validate(map -> map.isEmpty() ?
                DataResult.error(() -> "An empty map is not allowed") :
                DataResult.success(map));
    }

    public static <M extends Map<?, ?>, Buffer extends ByteBuf> StreamCodec<Buffer, M>
    nonEmptyMapStreamCodec(StreamCodec<Buffer, M> codec) {
        return validateStreamCodec(codec, map -> {

            if (map.isEmpty()) {
                throw new UnsupportedOperationException("An empty map is not allowed");
            }
        });
    }

    public static <T> Codec<NonNullList<T>> nonNullListCodec(Codec<T> elementCodec) {
        return elementCodec.listOf().xmap(NonNullList::copyOf, Function.identity());
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, NonNullList<T>>
    nonNullListStreamCodec(StreamCodec<RegistryFriendlyByteBuf, T> elementCodec) {
        return elementCodec
                .apply(ByteBufCodecs.collection(NonNullList::createWithCapacity))
                .map(CodeHelper::asNonNullList, Function.identity());
    }

    public static <T> Codec<TagKey<T>> tagKeyCodec(ResourceKey<? extends Registry<T>> registry) {
        return TagKey.codec(registry);
    }

    public static <T> StreamCodec<ByteBuf, TagKey<T>> tagKeyStreamCodec(ResourceKey<? extends Registry<T>> registry) {
        return ResourceLocation.STREAM_CODEC.map(tagId -> TagKey.create(registry, tagId), TagKey::location);
    }

    public static <F, S> Codec<Pair<F, S>> pairCodec(Codec<F> firstCodec, Codec<S> secondCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                firstCodec.fieldOf("fist").forGetter(Pair::getFirst),
                secondCodec.fieldOf("second").forGetter(Pair::getSecond)
        ).apply(instance, Pair::of));
    }

    public static <Buffer, F, S> StreamCodec<Buffer, Pair<F, S>> pairStreamCodec(StreamCodec<? super Buffer, F> firstCodec,
                                                                                 StreamCodec<? super Buffer, S> secondCodec) {
        return StreamCodec.composite(firstCodec, Pair::getFirst, secondCodec, Pair::getSecond, Pair::of);
    }

    //endregion
}
