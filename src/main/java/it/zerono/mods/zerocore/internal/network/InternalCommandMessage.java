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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.InternalCommand;
import it.zerono.mods.zerocore.lib.network.AbstractPlayPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InternalCommandMessage
        extends AbstractPlayPacket<InternalCommandMessage> {

    public static final Type<InternalCommandMessage> TYPE = createType(ZeroCore.ROOT_LOCATION, "internal_command");

    public static final StreamCodec<FriendlyByteBuf, InternalCommandMessage> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(InternalCommand.class), packet -> packet._command,
            ByteBufCodecs.OPTIONAL_COMPOUND_TAG, packet -> Optional.ofNullable(packet._data),
            InternalCommandMessage::new);

    /**
     * Initializes a newly created {@code InternalCommandMessage} object
     *
     * @param command The command
     * @param data The payload of the command
     */
    public InternalCommandMessage(InternalCommand command, CompoundTag data) {

        super(TYPE);

        Preconditions.checkNotNull(command, "Command must not be null");
        Preconditions.checkNotNull(data, "Data must not be null");

        this._command = command;
        this._data = data;
    }

    /**
     * Initializes a newly created {@code InternalCommandMessage} object
     *
     * @param command The command
     */
    public InternalCommandMessage(InternalCommand command) {

        super(TYPE);

        Preconditions.checkNotNull(command, "Command must not be null");

        this._command = command;
        this._data = null;
    }

    //region AbstractPlayPacket

    @Override
    public void handlePacket(IPayloadContext context) {
        ZeroCore.getProxy()
                .handleInternalCommand(this._command, null == this._data ? new CompoundTag() : this._data, context.flow());
    }

    //endregion
    //region internals

    /**
     * Initializes a newly created {@code InternalCommandMessage} object
     *
     * @param command The command
     * @param data The payload of the command
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private InternalCommandMessage(InternalCommand command, Optional<CompoundTag> data) {

        super(TYPE);

        Preconditions.checkNotNull(command, "Command must not be null");

        this._command = command;
        this._data = data.orElse(null);
    }

    private final InternalCommand _command;
    @Nullable
    private final CompoundTag _data;

    //endregion
}
