package it.zerono.mods.zerocore.internal.network;
/*
 * ContainerDataMessage
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
 * Do not remove or edit this header
 *
 */

import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.network.AbstractModMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Objects;

public class ContainerDataMessage
        extends AbstractModMessage {

    /**
     * Construct the local message to be sent over the network.
     */
    public ContainerDataMessage(final ModContainer container) {

        this._container = container;
        this._buffer = null;
    }

    /**
     * Construct the message from the data received from the network.
     * Read your payload from the {@link FriendlyByteBuf} and store it locally for later processing.
     *
     * @param buffer the {@link FriendlyByteBuf} containing the data received from the network.
     */
    public ContainerDataMessage(final FriendlyByteBuf buffer) {

        super(buffer);
        this._container = null;
        this._buffer = buffer;
    }

    //region AbstractModMessage

    /**
     * Encode your data into the {@link FriendlyByteBuf} so it could be sent on the network to the other side.
     *
     * @param buffer the {@link FriendlyByteBuf} to encode your data into
     */
    @Override
    public void encodeTo(final FriendlyByteBuf buffer) {

        final NonNullConsumer<FriendlyByteBuf> writer = Objects.requireNonNull(this._container).getContainerDataWriter();

        if (null != writer) {
            writer.accept(buffer);
        }
    }

    /**
     * Process the data received from the network.
     *
     * @param messageContext context for {@link NetworkEvent}
     */
    @Override
    public void processMessage(final NetworkEvent.Context messageContext) {

        messageContext.enqueueWork(() -> {

            final LocalPlayer player = Minecraft.getInstance().player;

            if (player != null && player.containerMenu instanceof ModContainer) {
                ((ModContainer)player.containerMenu).readContainerData(Objects.requireNonNull(this._buffer));
            }
        });

        messageContext.setPacketHandled(true);
    }

    //endregion
    //region internals

    final ModContainer _container;
    final FriendlyByteBuf _buffer;

    //endregion
}
