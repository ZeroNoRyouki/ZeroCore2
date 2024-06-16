package it.zerono.mods.zerocore.lib.functional;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface ComponentSupplier
        extends Supplier<@NotNull Component> {
}
