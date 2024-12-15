package it.zerono.mods.zerocore.internal.recipe;

import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.network.payload.AbstractBytesPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public class ClientRecipesSyncPayload
        extends AbstractBytesPayload {

    public static final Type<ClientRecipesSyncPayload> TYPE = new CustomPacketPayload.Type<>(ZeroCore.ROOT_LOCATION
            .buildWithSuffix("recipes_sync"));

    public static final StreamCodec<ByteBuf, ClientRecipesSyncPayload> STREAM_CODEC = codec(ClientRecipesSyncPayload::new);

    public ClientRecipesSyncPayload(List<byte[]> bytes) {
        super(bytes);
    }

    //region CustomPacketPayload

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    //endregion
}
