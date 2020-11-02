/*
 *
 * AbstractModTileMessage.java
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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;

/**
 * A generic network message to send data from a TileEntity on one side to it's corrective on the other side
 */
@SuppressWarnings({"WeakerAccess"})
public abstract class AbstractModTileMessage
        extends AbstractModMessage {

    /**
     * Construct the message from the data received from the network.
     * Read your payload from the {@link PacketBuffer} and store it locally for later processing.
     *
     * @param buffer the {@link PacketBuffer} containing the data received from the network.
     */
    protected AbstractModTileMessage(final PacketBuffer buffer) {

        if (buffer.readBoolean()) {
            this._dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(buffer.readString(4096)));
        } else {
            this._dimension = null;
        }

        this._tilePosition = buffer.readBlockPos();
    }

    /**
     * Returns the coordinates of the TileEntity
     * @return the coordinates
     */
    public BlockPos getTileEntityPosition() {
        return this._tilePosition;
    }

    /**
     * Returns the dimension of the target TileEntity
     * @return the dimension of the target
     */
    public Optional<RegistryKey<World>> getDimension() {
        return Optional.ofNullable(this._dimension);
    }

    /**
     * Process the data received from the network.
     *
     * @param sourceSide the LogicalSide this message is coming from
     * @param tileEntity the TileEntity object on the other side of this message exchange
     */
    protected abstract void processTileEntityMessage(LogicalSide sourceSide, TileEntity tileEntity);

    //region AbstractModMessage

    /**
     * Encode your data into the {@link PacketBuffer} so it could be sent on the network to the other side.
     *
     * @param buffer the {@link PacketBuffer} to encode your data into
     */
    @Override
    public void encodeTo(PacketBuffer buffer) {

        if (null != this._dimension) {

            buffer.writeBoolean(true);
            buffer.writeResourceLocation(this._dimension.getLocation());

        } else {

            buffer.writeBoolean(false);
        }

        buffer.writeBlockPos(this._tilePosition);
    }

    @Override
    public void processMessage(NetworkEvent.Context messageContext) {

        final BlockPos position = this.getTileEntityPosition();

        CodeHelper.optionalIfPresentOrElse(this.getWorld(messageContext),
                w -> CodeHelper.optionalIfPresentOrElse(WorldHelper.getTile(w, position),
                        tile -> this.processTileEntityMessage(messageContext.getDirection().getOriginationSide(), tile),
                        () -> Log.LOGGER.error(Log.NETWORK, "No tile entity found at {}, {}, {} while processing a ModTileEntityMessage: skipping message",
                                position.getX(), position.getY(), position.getZ())),
                () -> Log.LOGGER.error(Log.NETWORK, "Invalid world instance found while processing a ModTileEntityMessage: skipping message")
        );
    }

    /**
     * Construct the local message to be sent over the network.
     *
     * @param tileEntityPosition the coordinates of the TileEntity
     */
    protected AbstractModTileMessage(final BlockPos tileEntityPosition) {

        this._tilePosition = tileEntityPosition;
        this._dimension = null;
    }

    /**
     * Construct the local message to be sent over the network.
     *
     * @param tileEntityPosition the coordinates of the TileEntity
     * @param dimension the dimension of the target
     */
    protected AbstractModTileMessage(final BlockPos tileEntityPosition, final RegistryKey<World> dimension) {

        this._tilePosition = tileEntityPosition;
        this._dimension = dimension;
    }

    //endregion
    //region internals

    private Optional<World> getWorld(final NetworkEvent.Context messageContext) {

        switch (messageContext.getDirection()) {

            // Server -> Client
            case PLAY_TO_CLIENT:
                return WorldHelper.getClientWorld();

            // Client -> Server
            case PLAY_TO_SERVER: {

                final ServerPlayerEntity player = messageContext.getSender();

                if (null != player) {

                    if (null != this._dimension) {
                        return WorldHelper.getServerWorld(this._dimension).map(sw -> sw);
                    } else {
                        return Optional.of(player.getEntityWorld());
                    }
                }

                break;
            }
        }

        return Optional.empty();
    }

    private final BlockPos _tilePosition;
    private final RegistryKey<World> _dimension;

    //endregion
}
