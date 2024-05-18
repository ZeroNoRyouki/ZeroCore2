package it.zerono.mods.zerocore.lib.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.function.Function;

public record ModCodecs<Type, Buffer extends ByteBuf>(Codec<Type> codec, StreamCodec<Buffer, Type> streamCodec) {

    public <Encoded> DataResult<Encoded> encode(Type value, DynamicOps<Encoded> ops) {
        return this.codec.encodeStart(ops, value);
    }

    public <Encoded> DataResult<Encoded> encode(Type value, DynamicOps<Encoded> ops, Encoded output) {
        return this.codec.encode(value, ops, output);
    }

    public <Encoded> DataResult<Type> decode(Encoded input, DynamicOps<Encoded> ops) {
        return this.codec.parse(ops, input);
    }

    public <Encoded> void encode(Type value, Buffer buffer) {
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

    //region Helpers

    public static <T> Codec<NonNullList<T>> nonNullListCodec(Codec<T> elementCodec) {
        return elementCodec.listOf().xmap(NonNullList::copyOf, Function.identity());
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, NonNullList<T>>
    nonNullListStreamCodec(StreamCodec<RegistryFriendlyByteBuf, T> elementCodec) {
        return elementCodec
                .apply(ByteBufCodecs.collection(NonNullList::createWithCapacity))
                .map(CodeHelper::asNonNullList, Function.identity());
    }

    public static <T> StreamCodec<ByteBuf, TagKey<T>> tagKeyStreamCodec(ResourceKey<? extends Registry<T>> registry) {
        return ResourceLocation.STREAM_CODEC.map(tagId -> TagKey.create(registry, tagId), TagKey::location);
    }

    //endregion
}
