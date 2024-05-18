package it.zerono.mods.zerocore.lib.item.inventory.container.data.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record AmountChangedEntry(int amount)
        implements ISyncedSetEntry {

    public static ISyncedSetEntry from(RegistryFriendlyByteBuf buffer,
                                       Function<@NotNull RegistryFriendlyByteBuf, @NotNull ISyncedSetEntry> fullEntryFactory) {

        if (Byte.MIN_VALUE != buffer.readByte()) {
            return fullEntryFactory.apply(buffer);
        }

        return new AmountChangedEntry(buffer.readInt());
    }

    @Override
    public void accept(@NotNull RegistryFriendlyByteBuf buffer) {

        buffer.writeByte(Byte.MIN_VALUE);
        buffer.writeInt(this.amount);
    }
}
