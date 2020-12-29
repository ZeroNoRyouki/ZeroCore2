/*
 *
 * MultiblockErrorMessage.java
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
import it.zerono.mods.zerocore.lib.network.AbstractModMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;

public class MultiblockErrorMessage
    extends AbstractModMessage {

    /**
     * Create the message on the sender side
     *
     * @param error the error message
     * @param position the position, in world, of the error message. Can be null.
     * @return the new message
     */
    public static MultiblockErrorMessage create(final ITextComponent error, @Nullable final BlockPos position) {
        return new MultiblockErrorMessage(error, position);
    }

    public MultiblockErrorMessage(final PacketBuffer buffer) {

        super(buffer);
        this._error = buffer.readTextComponent();

        if (buffer.readBoolean()) {
            this._position = buffer.readBlockPos();
        } else {
            this._position = null;
        }
    }

    //region AbstractModMessage

    /**
     * Encode your data into the {@link PacketBuffer} so it could be sent on the network to the other side.
     *
     * @param buffer the {@link PacketBuffer} to encode your data into
     */
    @Override
    public void encodeTo(final PacketBuffer buffer) {

        buffer.writeTextComponent(this._error);

        if (null == this._position) {

            buffer.writeBoolean(false);

        } else {

            buffer.writeBoolean(true);
            buffer.writeBlockPos(this._position);
        }
    }

    /**
     * Process the data received from the network.
     *
     * @param messageContext context for {@link NetworkEvent}
     */
    @Override
    public void processMessage(final NetworkEvent.Context messageContext) {

        if (NetworkDirection.PLAY_TO_CLIENT == messageContext.getDirection()) {
            ZeroCore.getProxy().notifyMultiblockError(null, this._error, this._position);
        }
    }

    //endregion
    //region internals

    /**
     * Construct the message on the sender side
     *
     * @param error the error message
     * @param position the position, in world, of the error message. Can be null.
     */
    protected MultiblockErrorMessage(final ITextComponent error, @Nullable final BlockPos position) {

        this._error = error;
        this._position = position;
    }

    private final ITextComponent _error;
    private final BlockPos _position;

    //endregion
}
