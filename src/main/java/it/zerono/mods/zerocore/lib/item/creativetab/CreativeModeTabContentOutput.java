package it.zerono.mods.zerocore.lib.item.creativetab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public interface CreativeModeTabContentOutput
        extends CreativeModeTab.Output {

    default void accept(Supplier<? extends ItemLike> item) {
        this.accept(item.get());
    }

    default void accept(CreativeModeTab.TabVisibility visibility, Supplier<? extends ItemLike> item) {
        this.accept(item.get(), visibility);
    }

    @SafeVarargs
    static <T> void acceptAll(CreativeModeTabContentOutput output, Supplier<? extends ItemLike>... items) {
        acceptAll(output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, items);
    }

    @SafeVarargs
    static <T> void acceptAll(CreativeModeTabContentOutput output, CreativeModeTab.TabVisibility visibility,
                              Supplier<? extends ItemLike>... items) {

        for (var item : items) {
            output.accept(visibility, item);
        }
    }
}
