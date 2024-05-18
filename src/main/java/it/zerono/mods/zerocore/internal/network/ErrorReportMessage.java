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

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.network.AbstractPlayPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class ErrorReportMessage
    extends AbstractPlayPacket<ErrorReportMessage> {

    public static final Type<ErrorReportMessage> TYPE = createType(ZeroCore.ROOT_LOCATION, "error");

    public static final StreamCodec<RegistryFriendlyByteBuf, ErrorReportMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), packet -> Optional.ofNullable(packet._position),
            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), packet -> packet._errors,
            ErrorReportMessage::new);

    /**
     * Initializes a newly created {@code ErrorReportMessage} object
     *
     * @param position the position of the error source
     * @param errors the error messages
     */
    public ErrorReportMessage(@Nullable BlockPos position, List<Component> errors) {

        super(TYPE);

        Preconditions.checkNotNull(errors, "Errors must not but null");
        Preconditions.checkArgument(!errors.isEmpty(), "Errors must not be empty");

        this._position = position;
        this._errors = ObjectLists.unmodifiable(new ObjectArrayList<>(errors));
    }

    //region AbstractPlayPacket

    @Override
    public void handlePacket(IPayloadContext context) {

        if (PacketFlow.CLIENTBOUND == context.flow()) {
            ZeroCore.getProxy().displayErrorToPlayer(this._position, this._errors);
        }
    }

    //endregion
    //region internals

    /**
     * Initializes a newly created {@code ErrorReportMessage} object
     *
     * @param position the position of the error source
     * @param errors the error messages
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ErrorReportMessage(Optional<BlockPos> position, List<Component> errors) {

        super(TYPE);

        Preconditions.checkNotNull(errors, "Errors must not but null");
        Preconditions.checkArgument(!errors.isEmpty(), "Errors must not be empty");

        this._position = position.orElse(null);
        this._errors = ObjectLists.unmodifiable(new ObjectArrayList<>(errors));
    }

    private final List<Component> _errors;
    @Nullable
    private final BlockPos _position;

    //endregion
}
