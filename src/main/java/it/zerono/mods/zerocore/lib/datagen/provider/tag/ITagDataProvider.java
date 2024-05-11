package it.zerono.mods.zerocore.lib.datagen.provider.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.util.NonNullFunction;

public interface ITagDataProvider<T> {

    String getName();

    void build(HolderLookup.Provider registryLookup, NonNullFunction<TagKey<T>, ModTagAppender<T>> builder);
}
