/*
 *
 * TileCommandMessage.java
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

package it.zerono.mods.zerocore.internal.network;

import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import it.zerono.mods.zerocore.lib.network.AbstractBlockEntityPlayPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public final class TileCommandMessage
        extends AbstractBlockEntityPlayPacket {

    public static final ResourceLocation ID = ZeroCore.ROOT_LOCATION.buildWithSuffix("tile_command");

    /**
     * Create a parameterless command message for the provided {@link AbstractModBlockEntity}
     *
     * @param tile the Tile Entity
     * @param commandName the command name
     * @return the new command message
     */
    public static TileCommandMessage create(final AbstractModBlockEntity tile, final String commandName) {
        return create(tile, commandName, NBTHelper.EMPTY_COMPOUND);
    }

    /**
     * Create a command message for the provided {@link AbstractModBlockEntity}
     *
     * @param tile the Tile Entity
     * @param commandName the command name
     * @param parameters the parameters for the command
     * @return the new command message
     */
    public static TileCommandMessage create(final AbstractModBlockEntity tile, final String commandName,
                                                final CompoundTag parameters) {
        return new TileCommandMessage(tile, commandName, parameters);
    }

    public TileCommandMessage(final FriendlyByteBuf buffer) {

        super(ID, buffer);
        this._name = buffer.readUtf(4096);

        if (buffer.readBoolean()) {
            this._parameters = buffer.readNbt();
        } else {
            this._parameters = NBTHelper.EMPTY_COMPOUND;
        }
    }

    //region AbstractBlockEntityPlayPacket

    @Override
    public void write(FriendlyByteBuf buffer) {

        super.write(buffer);

        buffer.writeUtf(this._name);

        if (this._parameters.isEmpty()) {

            buffer.writeBoolean(false);

        } else {

            buffer.writeBoolean(true);
            buffer.writeNbt(this._parameters);
        }
    }

    @Override
    protected void processBlockEntity(PacketFlow flow, BlockEntity blockEntity) {

        if (blockEntity instanceof AbstractModBlockEntity be) {
            be.handleCommand(flow, this._name, this._parameters);
        } else {
            Log.LOGGER.error(Log.NETWORK, "No command-aware Tile Entity found while processing a command message: skipping");
        }
    }

    //endregion
    //region internals

    /**
     * Construct the message on the sender side
     *
     * @param commandName the command name
     * @param parameters the parameters for the command, if any
     */
    private TileCommandMessage(final AbstractModBlockEntity tile,
                               final String commandName, final CompoundTag parameters) {

        super(ID, tile.getBlockPos(), Objects.requireNonNull(tile.getLevel()).dimension());
        this._name = commandName;
        this._parameters = parameters;
    }

    private final String _name;
    private final CompoundTag _parameters;

    //endregion
}
