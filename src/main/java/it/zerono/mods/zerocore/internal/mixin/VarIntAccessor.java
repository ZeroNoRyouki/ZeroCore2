package it.zerono.mods.zerocore.internal.mixin;

import net.minecraft.network.VarInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VarInt.class)
public interface VarIntAccessor {

    @Accessor("MAX_VARINT_SIZE")
    static int getMaxByteSize() {
        throw new UnsupportedOperationException("Mixin accessor");
    }
}
