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

import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

/**
 * A generic custom packet for the play phase to send data from a BlockEntity on one side to it's corresponding
 * BlockEntity on the other side.
 */
public abstract class AbstractBlockEntityPlayPacket
        extends AbstractPlayPacket {

    /**
     * Construct the local message to be sent over the network
     *
     * @param messageId The ID of this message
     * @param entityPosition the coordinates of the BlockEntity
     */
    protected AbstractBlockEntityPlayPacket(ResourceLocation messageId, BlockPos entityPosition) {

        super(messageId);
        this._tilePosition = entityPosition;
        this._dimension = null;
    }

    /**
     * Construct the local message to be sent over the network.
     *
     * @param messageId The ID of this message
     * @param entityPosition the coordinates of the BlockEntity
     * @param dimension The dimension of the target
     */
    protected AbstractBlockEntityPlayPacket(ResourceLocation messageId, BlockPos entityPosition, ResourceKey<Level> dimension) {

        super(messageId);
        this._tilePosition = entityPosition;
        this._dimension = dimension;
    }

    /**
     * Construct the message from the data received from the network.
     * Read your payload from the {@link FriendlyByteBuf} and store it locally for later processing.
     *
     * @param messageId The ID of this message
     * @param buffer The {@link FriendlyByteBuf} containing the data received from the network
     */
    protected AbstractBlockEntityPlayPacket(ResourceLocation messageId, FriendlyByteBuf buffer) {

        super(messageId, buffer);

        if (buffer.readBoolean()) {
            this._dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buffer.readUtf(4096)));
        } else {
            this._dimension = null;
        }

        this._tilePosition = buffer.readBlockPos();
    }

    /**
     * Returns the coordinates of the BlockEntity
     * @return the coordinates
     */
    public BlockPos getTileEntityPosition() {
        return this._tilePosition;
    }

    /**
     * Returns the dimension of the target BlockEntity
     * @return the dimension of the target
     */
    public Optional<ResourceKey<Level>> getDimension() {
        return Optional.ofNullable(this._dimension);
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
    public void write(FriendlyByteBuf buffer) {

        if (null != this._dimension) {

            buffer.writeBoolean(true);
            buffer.writeResourceLocation(this._dimension.location());

        } else {

            buffer.writeBoolean(false);
        }

        buffer.writeBlockPos(this._tilePosition);
    }

    @Override
    public void handlePacket(PlayPayloadContext context) {

        final BlockPos position = this.getTileEntityPosition();

        context.workHandler().execute(() -> CodeHelper.optionalIfPresentOrElse(this.getWorld(context),
                w -> CodeHelper.optionalIfPresentOrElse(WorldHelper.getTile(w, position),
                        tile -> this.processBlockEntity(context.flow(), tile),
                        () -> Log.LOGGER.error(Log.NETWORK, "No tile entity found at {}, {}, {} while processing a ModTileEntityMessage: skipping message",
                                position.getX(), position.getY(), position.getZ())),
                () -> Log.LOGGER.error(Log.NETWORK, "Invalid world instance found while processing a ModTileEntityMessage: skipping message")
        ));
    }

    //endregion
    //region internals

    private Optional<Level> getWorld(PlayPayloadContext context) {
        return switch (context.flow()) {

            case CLIENTBOUND -> WorldHelper.getClientWorld();

            case SERVERBOUND -> context.player()
                    .flatMap(player -> {

                        if (null != this._dimension) {
                            return WorldHelper.getServerWorld(this._dimension);
                        } else {
                            return Optional.of(player.getCommandSenderWorld());
                        }
                    });
        };
    }

    private final BlockPos _tilePosition;
    private final ResourceKey<Level> _dimension;

    //endregion
}
