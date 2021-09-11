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

import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import it.zerono.mods.zerocore.lib.network.AbstractModTileMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.LogicalSide;

import java.util.Objects;

public class TileCommandMessage
        extends AbstractModTileMessage {

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
                                                final CompoundNBT parameters) {
        //return new TileCommandMessage(tile.getPos(), tile.getWorld().dimensionType(), commandName, parameters);
        return new TileCommandMessage(tile, commandName, parameters);
    }

    public TileCommandMessage(final PacketBuffer buffer) {

        super(buffer);
        this._name = buffer.readUtf(4096);

        if (buffer.readBoolean()) {
            this._parameters = buffer.readNbt();
        } else {
            this._parameters = NBTHelper.EMPTY_COMPOUND;
        }
    }

    //region AbstractModTileMessage

    @Override
    public void encodeTo(final PacketBuffer buffer) {

        super.encodeTo(buffer);
        buffer.writeUtf(this._name);

        if (this._parameters.isEmpty()) {

            buffer.writeBoolean(false);

        } else {

            buffer.writeBoolean(true);
            buffer.writeNbt(this._parameters);
        }
    }

    /**
     * Process the data received from the network.
     *
     * @param tileEntity the TileEntity object on the other side of this message exchange
     */
    @Override
    protected void processTileEntityMessage(LogicalSide sourceSide, TileEntity tileEntity) {

        if (tileEntity instanceof AbstractModBlockEntity) {
            ((AbstractModBlockEntity)tileEntity).handleCommand(sourceSide, this._name, this._parameters);
        } else {
            Log.LOGGER.error(Log.NETWORK, "No command-aware Tile Entity found while processing a command message: skipping");
        }
    }

    //endregion
    //region internals

    /**
     * Construct the message on the sender side
     *
//     * @param tileEntityPosition the coordinates of the TileEntity
     * @param commandName the command name
     * @param parameters the parameters for the command, if any
     */
    protected TileCommandMessage(/*final BlockPos tileEntityPosition, final DimensionType dimension,*/
                                 final AbstractModBlockEntity tile,
                                 final String commandName, final CompoundNBT parameters) {

        //super(tileEntityPosition);
        super(tile.getBlockPos(), Objects.requireNonNull(tile.getLevel()).dimension());
        this._name = commandName;
        this._parameters = parameters;
    }

    private final String _name;
    private final CompoundNBT _parameters;

    //endregion
}
