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

import com.google.common.collect.Lists;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.network.AbstractModMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.List;

public class ErrorReportMessage
    extends AbstractModMessage {

    /**
     * Create the message on the sender side
     *
     * @param position the position, in world, of the error message. Can be null.
     * @param errors the error messages
     * @return the new message
     */
    public static ErrorReportMessage create(@Nullable final BlockPos position, final Component... errors) {
        return new ErrorReportMessage(position, errors);
    }

    /**
     * Create the message on the sender side
     *
     * @param position the position, in world, of the error message. Can be null.
     * @param errors the error messages
     * @return the new message
     */
    public static ErrorReportMessage create(@Nullable final BlockPos position, final List<Component> errors) {
        return new ErrorReportMessage(position, errors);
    }

    public ErrorReportMessage(final FriendlyByteBuf buffer) {

        super(buffer);

        final int count = buffer.readInt();

        this._errors = Lists.newArrayListWithCapacity(count);

        for (int i = 0; i < count; ++i) {
            this._errors.add(buffer.readComponent());
        }

        this._position = buffer.readBoolean() ? buffer.readBlockPos() : null;
    }

    //region AbstractModMessage

    /**
     * Encode your data into the {@link PacketBuffer} so it could be sent on the network to the other side.
     *
     * @param buffer the {@link PacketBuffer} to encode your data into
     */
    @Override
    public void encodeTo(final FriendlyByteBuf buffer) {

        buffer.writeInt(this._errors.size());
        this._errors.forEach(buffer::writeComponent);

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
            ZeroCore.getProxy().reportErrorToPlayer(null, this._position, this._errors);
        }
    }

    //endregion
    //region internals

    /**
     * Construct the message on the sender side
     *
     * @param position the position, in world, of the error message. Can be null.
     * @param errors the error messages
     */
    protected ErrorReportMessage(@Nullable final BlockPos position, final Component... errors) {
        this(position, Lists.newArrayList(errors));
    }

    /**
     * Construct the message on the sender side
     *
     * @param position the position, in world, of the error message. Can be null.
     * @param errors the error messages
     */
    protected ErrorReportMessage(@Nullable final BlockPos position, final List<Component> errors) {

        this._errors = Lists.newArrayList(errors);
        this._position = position;
    }

    private final List<Component> _errors;
    private final BlockPos _position;

    //endregion
}
