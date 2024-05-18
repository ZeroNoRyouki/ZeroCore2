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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.network.AbstractBlockEntityPlayPacket;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class TileCommandMessage
        extends AbstractBlockEntityPlayPacket<TileCommandMessage> {

    public static final Type<TileCommandMessage> TYPE = createType(ZeroCore.ROOT_LOCATION, "tile_command");

    public static final StreamCodec<ByteBuf, TileCommandMessage> STREAM_CODEC = createStreamCodec(
            ByteBufCodecs.STRING_UTF8, packet -> packet._name,
            ByteBufCodecs.COMPOUND_TAG, packet -> packet._parameters,
            TileCommandMessage::new);

    /**
     * Initializes a newly created {@code TileCommandMessage} object
     *
     * @param position the position of the BlockEntity
     * @param commandName the name of this command
     * @param commandParameters the parameters of this command
     */
    public TileCommandMessage(GlobalPos position, String commandName, CompoundTag commandParameters) {

        super(TYPE, position);

        Preconditions.checkArgument(!Strings.isNullOrEmpty(commandName), "Command name must not be null or empty");
        Preconditions.checkNotNull(commandParameters, "Command parameters must not be null");

        this._name = commandName;
        this._parameters = commandParameters;
    }

    /**
     * Initializes a newly created {@code TileCommandMessage} object
     *
     * @param blockEntity the BlockEntity
     * @param commandName the name of this command
     * @param commandParameters the parameters of this command
     */
    public TileCommandMessage(AbstractModBlockEntity blockEntity, String commandName, CompoundTag commandParameters) {

        super(TYPE, blockEntity);

        Preconditions.checkArgument(!Strings.isNullOrEmpty(commandName), "Command name must not be null or empty");
        Preconditions.checkNotNull(commandParameters, "Command parameters must not be null");

        this._name = commandName;
        this._parameters = commandParameters;
    }

    //region AbstractBlockEntityPlayPacket

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

    private final String _name;
    private final CompoundTag _parameters;

    //endregion
}
