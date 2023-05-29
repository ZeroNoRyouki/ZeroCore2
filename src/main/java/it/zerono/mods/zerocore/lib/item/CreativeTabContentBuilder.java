package it.zerono.mods.zerocore.lib.item;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

public interface CreativeTabContentBuilder {

    /**
     * Add content to a {@link CreativeModeTab}
     *
     * @param tab             The {@link CreativeModeTab} whose content is being modified. If null, a
     *                        new {@link CreativeModeTab} is being created.
     * @param enabledFeatures The currently enabled features of Minecraft.
     * @param showOpOnlyItems If true, operator-only content can be added to this {@link CreativeModeTab}.
     * @param output          Add items to the {@link CreativeModeTab}.
     */
    void build(@Nullable CreativeModeTab tab, FeatureFlagSet enabledFeatures, boolean showOpOnlyItems, CreativeModeTabContentOutput output);
}
