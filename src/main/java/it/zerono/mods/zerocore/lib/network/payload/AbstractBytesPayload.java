package it.zerono.mods.zerocore.lib.network.payload;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractBytesPayload
        implements CustomPacketPayload {

    protected AbstractBytesPayload(List<byte[]> bytes) {

        Preconditions.checkNotNull(bytes, "Bytes must not be null");

        this._bytes = Collections.unmodifiableList(bytes);
    }

    protected static <Payload extends AbstractBytesPayload>
    StreamCodec<ByteBuf, Payload> codec(Function<List<byte[]>, Payload> factory) {
        return StreamCodec.composite(ModCodecs.BYTE_ARRAY_LIST, AbstractBytesPayload::getBytes, factory);
    }

    public final List<byte[]> getBytes() {
        return this._bytes;
    }

    //region internals

    private final List<byte[]> _bytes;

    //endregion
}
