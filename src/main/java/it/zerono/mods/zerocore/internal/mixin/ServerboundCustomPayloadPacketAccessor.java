package it.zerono.mods.zerocore.internal.mixin;

import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundCustomPayloadPacket.class)
public interface ServerboundCustomPayloadPacketAccessor {

    @Accessor("MAX_PAYLOAD_SIZE")
    static int getMaxPayloadSize() {
        throw new UnsupportedOperationException("Mixin accessor");
    }
}
