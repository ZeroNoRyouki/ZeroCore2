package it.zerono.mods.zerocore.lib.datagen.provider.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.util.NonNullFunction;

public interface IIntrinsicTagDataProvider<T> {

    String getName();

    void build(HolderLookup.Provider registryLookup, NonNullFunction<TagKey<T>, ModIntrinsicTagAppender<T>> builder);
}
