/*
 *
 * InternalCommandMessage.java
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
import it.zerono.mods.zerocore.internal.InternalCommand;
import it.zerono.mods.zerocore.lib.network.AbstractPlayPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class InternalCommandMessage
        extends AbstractPlayPacket {

    public static final ResourceLocation ID = ZeroCore.ROOT_LOCATION.buildWithSuffix("internal_command");

    /**
     * Construct the local message to be sent over the network.
     */
    public InternalCommandMessage(final InternalCommand command, final CompoundTag data) {

        super(ID);
        this._command = command;
        this._data = data;
    }

    /**
     * Construct the local message to be sent over the network.
     */
    public InternalCommandMessage(final InternalCommand command) {

        super(ID);
        this._command = command;
        this._data = null;
    }

    /**
     * Construct the message from the data received from the network.
     * Read your payload from the {@link FriendlyByteBuf} and store it locally for later processing.
     *
     * @param buffer the {@link FriendlyByteBuf} containing the data received from the network.
     */
    public InternalCommandMessage(final FriendlyByteBuf buffer) {

        super(ID, buffer);
        this._command = buffer.readEnum(InternalCommand.class);
        this._data = buffer.readBoolean() ? buffer.readNbt() : new CompoundTag();
    }

    //region AbstractPlayPacket

    @Override
    public void write(FriendlyByteBuf buffer) {

        buffer.writeEnum(this._command);

        if (null != this._data) {

            buffer.writeBoolean(true);
            buffer.writeNbt(this._data);

        } else {

            buffer.writeBoolean(false);
        }
    }

    @Override
    public void handlePacket(PlayPayloadContext context) {
        context.workHandler().execute(() -> ZeroCore.getProxy().handleInternalCommand(this._command, this._data, context.flow()));
    }

    //endregion
    //region internals

    private final InternalCommand _command;
    private final CompoundTag _data;

    //endregion
}
