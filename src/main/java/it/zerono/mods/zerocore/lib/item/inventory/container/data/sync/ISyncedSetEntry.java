package it.zerono.mods.zerocore.lib.item.inventory.container.data.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface ISyncedSetEntry
        extends Consumer<@NotNull RegistryFriendlyByteBuf> {
}
