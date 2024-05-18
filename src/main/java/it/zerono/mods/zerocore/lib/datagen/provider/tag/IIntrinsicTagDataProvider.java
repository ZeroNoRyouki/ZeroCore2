package it.zerono.mods.zerocore.lib.datagen.provider.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface IIntrinsicTagDataProvider<T> {

    String getName();

    void build(HolderLookup.Provider registryLookup, Function<@NotNull TagKey<T>, ModIntrinsicTagAppender<T>> builder);
}
