/*
 *
 * AbstractBlockEntityPlayPacket.java
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.zerocore.lib.network;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A generic custom packet for the play phase to send data from a BlockEntity on one side to it's corresponding
 * BlockEntity on the other side.
 */
public abstract class AbstractBlockEntityPlayPacket<Packet extends AbstractBlockEntityPlayPacket<Packet>>
        extends AbstractPlayPacket<Packet> {

    public static <B extends ByteBuf, Packet extends AbstractBlockEntityPlayPacket<Packet>, T2> StreamCodec<B, Packet>
    createStreamCodec(final StreamCodec<? super B, T2> codec2, final Function<Packet, T2> getter2,
                      final BiFunction<GlobalPos, T2, Packet> packetFactory) {
        return StreamCodec.composite(GlobalPos.STREAM_CODEC, Packet::getPosition, codec2, getter2, packetFactory);
    }

    public static <B extends ByteBuf, Packet extends AbstractBlockEntityPlayPacket<Packet>, T2, T3> StreamCodec<B, Packet>
    createStreamCodec(final StreamCodec<? super B, T2> codec2, final Function<Packet, T2> getter2,
                      final StreamCodec<? super B, T3> codec3, final Function<Packet, T3> getter3,
                      final Function3<GlobalPos, T2, T3, Packet> packetFactory) {
        return StreamCodec.composite(GlobalPos.STREAM_CODEC, Packet::getPosition, codec2, getter2, codec3, getter3,
                packetFactory);
    }

    public static <B extends ByteBuf, Packet extends AbstractBlockEntityPlayPacket<Packet>, T2, T3, T4> StreamCodec<B, Packet>
    createStreamCodec(final StreamCodec<? super B, T2> codec2, final Function<Packet, T2> getter2,
                      final StreamCodec<? super B, T3> codec3, final Function<Packet, T3> getter3,
                      final StreamCodec<? super B, T4> codec4, final Function<Packet, T4> getter4,
                      final Function4<GlobalPos, T2, T3, T4, Packet> packetFactory) {
        return StreamCodec.composite(GlobalPos.STREAM_CODEC, Packet::getPosition, codec2, getter2, codec3, getter3,
                codec4, getter4, packetFactory);
    }

    public static <B extends ByteBuf, Packet extends AbstractBlockEntityPlayPacket<Packet>, T2, T3, T4, T5> StreamCodec<B, Packet>
    createStreamCodec(final StreamCodec<? super B, T2> codec2, final Function<Packet, T2> getter2,
                      final StreamCodec<? super B, T3> codec3, final Function<Packet, T3> getter3,
                      final StreamCodec<? super B, T4> codec4, final Function<Packet, T4> getter4,
                      final StreamCodec<? super B, T5> codec5, final Function<Packet, T5> getter5,
                      final Function5<GlobalPos, T2, T3, T4, T5, Packet> packetFactory) {
        return StreamCodec.composite(GlobalPos.STREAM_CODEC, Packet::getPosition, codec2, getter2, codec3, getter3,
                codec4, getter4, codec5, getter5, packetFactory);
    }

    public static <B extends ByteBuf, Packet extends AbstractBlockEntityPlayPacket<Packet>, T2, T3, T4, T5, T6> StreamCodec<B, Packet>
    createStreamCodec(final StreamCodec<? super B, T2> codec2, final Function<Packet, T2> getter2,
                      final StreamCodec<? super B, T3> codec3, final Function<Packet, T3> getter3,
                      final StreamCodec<? super B, T4> codec4, final Function<Packet, T4> getter4,
                      final StreamCodec<? super B, T5> codec5, final Function<Packet, T5> getter5,
                      final StreamCodec<? super B, T6> codec6, final Function<Packet, T6> getter6,
                      final Function6<GlobalPos, T2, T3, T4, T5, T6, Packet> packetFactory) {
        return StreamCodec.composite(GlobalPos.STREAM_CODEC, Packet::getPosition, codec2, getter2, codec3, getter3,
                codec4, getter4, codec5, getter5, codec6, getter6, packetFactory);
    }

    /**
     * Initializes a newly created {@code AbstractBlockEntityPlayPacket} object
     *
     * @param type The {@link Type} of this packet
     * @param position the position of the BlockEntity
     */
    protected AbstractBlockEntityPlayPacket(Type<Packet> type, GlobalPos position) {

        super(type);

        Preconditions.checkNotNull(position, "Position must not be null");
        this._position = position;
    }

    /**
     * Initializes a newly created {@code AbstractBlockEntityPlayPacket} object
     *
     * @param type The {@link Type} of this packet
     * @param blockEntity the BlockEntity
     */
    protected AbstractBlockEntityPlayPacket(Type<Packet> type, AbstractModBlockEntity blockEntity) {
        this(type, positionFrom(blockEntity));
    }

    public GlobalPos getPosition() {
        return this._position;
    }

    /**
     * Returns the coordinates of the BlockEntity
     * @return the coordinates
     */
    public BlockPos getBlockPosition() {
        return this.getPosition().pos();
    }

    /**
     * Returns the dimension of the target BlockEntity
     * @return the dimension of the target
     */
    public ResourceKey<Level> getDimension() {
        return this.getPosition().dimension();
    }

    /**
     * Process the data received from the network.
     *
     * @param flow the packet destination
     * @param blockEntity the BlockEntity object on the other side of this packet exchange
     */
    protected abstract void processBlockEntity(PacketFlow flow, BlockEntity blockEntity);

    //region AbstractPlayPacket

    @Override
    public void handlePacket(IPayloadContext context) {

        final BlockPos position = this.getBlockPosition();

        CodeHelper.optionalIfPresentOrElse(this.getWorld(context),
                w -> CodeHelper.optionalIfPresentOrElse(WorldHelper.getTile(w, position),
                        tile -> this.processBlockEntity(context.flow(), tile),
                        () -> Log.LOGGER.error(Log.NETWORK, "No tile entity found at {}, {}, {} while processing a ModTileEntityMessage: skipping message",
                                position.getX(), position.getY(), position.getZ())),
                () -> Log.LOGGER.error(Log.NETWORK, "Invalid world instance found while processing a ModTileEntityMessage: skipping message")
        );
    }

    //endregion
    //region internals

    private Optional<Level> getWorld(IPayloadContext context) {
        return switch (context.flow()) {
            case CLIENTBOUND -> WorldHelper.getClientWorld();
            case SERVERBOUND -> WorldHelper.getServerWorld(this.getDimension()).map(serverLevel -> (Level) serverLevel);
        };
    }

    private static GlobalPos positionFrom(AbstractModBlockEntity blockEntity) {

        Preconditions.checkNotNull(blockEntity, "Block entity must not be null");
        Preconditions.checkState(null != blockEntity.getLevel());

        return new GlobalPos(blockEntity.getLevel().dimension(), blockEntity.getBlockPos());
    }

    private final GlobalPos _position;

    //endregion
}
