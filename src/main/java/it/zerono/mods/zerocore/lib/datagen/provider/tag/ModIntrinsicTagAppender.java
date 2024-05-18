package it.zerono.mods.zerocore.lib.datagen.provider.tag;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModIntrinsicTagAppender<T>
        extends ModTagAppender<T> {

    public ModIntrinsicTagAppender(TagBuilder builder, Function<T, ResourceKey<T>> keyProvider) {

        super(builder);

        Preconditions.checkNotNull(keyProvider, "Key provider must not be null");

        this._keyProvider = keyProvider;
    }

    public ModIntrinsicTagAppender<T> add(T element) {
        return this.add(this._keyProvider.apply(element));
    }

    @SafeVarargs
    public final ModIntrinsicTagAppender<T> add(T... elements) {

        for (final var element : elements) {
            this.add(element);
        }

        return this;
    }

    public ModIntrinsicTagAppender<T> add(Supplier<? extends T> element) {
        return this.add(element.get());
    }

    @SafeVarargs
    public final ModIntrinsicTagAppender<T> add(Supplier<? extends T>... elements) {

        for (final var element : elements) {
            this.add(element);
        }

        return this;
    }

    //region ModTagAppender<T>

    @Override
    public ModIntrinsicTagAppender<T> add(ResourceKey<T> element) {

        super.add(element);
        return this;
    }

    @Override
    public ModIntrinsicTagAppender<T> addOptional(ResourceLocation optionalElement) {

        super.addOptional(optionalElement);
        return this;
    }

    @Override
    public ModTagAppender<T> addTag(TagKey<T> tag) {

        super.addTag(tag);
        return this;
    }

    @Override
    public ModTagAppender<T> addOptionalTag(ResourceLocation optionalTag) {

        super.addOptionalTag(optionalTag);
        return this;
    }

    //endregion
    //region internals

    private final Function<T, ResourceKey<T>> _keyProvider;

    //endregion
}
