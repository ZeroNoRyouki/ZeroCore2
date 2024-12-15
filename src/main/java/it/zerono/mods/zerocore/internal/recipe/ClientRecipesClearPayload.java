package it.zerono.mods.zerocore.internal.recipe;

import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Lib;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record ClientRecipesClearPayload()
        implements CustomPacketPayload {

    public static final ClientRecipesClearPayload INSTANCE = new ClientRecipesClearPayload();

    public static final Type<ClientRecipesClearPayload> TYPE = new Type<>(ZeroCore.ROOT_LOCATION.buildWithSuffix("recipes_clear"));

    public static final StreamCodec<ByteBuf, ClientRecipesClearPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void send(ServerPlayer player) {
        Lib.NETWORK_HANDLER.sendToPlayer(player, INSTANCE);
    }

    //region CustomPacketPayload

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    //endregion
}
