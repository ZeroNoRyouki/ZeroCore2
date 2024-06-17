///*
// * ContainerDataMessage
// *
// * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *
// * Do not remove or edit this header
// *
// */
//
//package it.zerono.mods.zerocore.internal.network;
//
//import it.zerono.mods.zerocore.ZeroCore;
//import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
//import it.zerono.mods.zerocore.lib.network.AbstractPlayPacket;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.chat.ComponentSerialization;
//import net.minecraft.network.codec.ByteBufCodecs;
//import net.minecraft.network.codec.StreamCodec;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Objects;
//import java.util.Optional;
//import java.util.function.Consumer;
//
//public class ContainerDataMessage
//        extends AbstractPlayPacket<ContainerDataMessage> {
//
//    public static final Type<ContainerDataMessage> TYPE = createType(ZeroCore.ROOT_LOCATION, "container");
//
//    public static final StreamCodec<RegistryFriendlyByteBuf, ErrorReportMessage> STREAM_CODEC = StreamCodec.composite(
//            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), packet -> Optional.ofNullable(packet._position),
//            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), packet -> packet._errors,
//            ErrorReportMessage::new);
//
//    /**
//     * Construct the local message to be sent over the network.
//     */
//    public ContainerDataMessage(final ModContainer container) {
//
//        super(TYPE);
//        this._container = container;
//        this._buffer = null;
//    }
//
//    /**
//     * Construct the message from the data received from the network.
//     * Read your payload from the {@link FriendlyByteBuf} and store it locally for later processing.
//     *
//     * @param buffer the {@link FriendlyByteBuf} containing the data received from the network.
//     */
//    public ContainerDataMessage(final FriendlyByteBuf buffer) {
//
//        super(ID, buffer);
//        this._container = null;
//        this._buffer = buffer;
//    }
//
//    //region AbstractPlayPacket
//
//    @Override
//    public void write(FriendlyByteBuf buffer) {
//
//        final Consumer<@NotNull FriendlyByteBuf> writer = Objects.requireNonNull(this._container).getContainerDataWriter();
//
//        if (null != writer) {
//            writer.accept(buffer);
//        }
//    }
//
//    @Override
//    public void handlePacket(PlayPayloadContext context) {
//
//        context.workHandler().execute(() -> {
//
//            final LocalPlayer player = Minecraft.getInstance().player;
//
//            if (player != null && player.containerMenu instanceof ModContainer) {
//                ((ModContainer)player.containerMenu).readContainerData(Objects.requireNonNull(this._buffer));
//            }
//        });
//    }
//
//    //endregion
//    //region internals
//
//    final ModContainer _container;
//    final FriendlyByteBuf _buffer;
//
//    //endregion
//}
