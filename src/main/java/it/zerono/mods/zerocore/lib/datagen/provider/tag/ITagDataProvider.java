package it.zerono.mods.zerocore.lib.datagen.provider.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ITagDataProvider<T> {

    String getName();

    void build(HolderLookup.Provider registryLookup, Function<@NotNull TagKey<T>, @NotNull ModTagAppender<T>> builder);
}
