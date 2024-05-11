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

import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.network.AbstractPlayPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.NonNullConsumer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Objects;

public class ContainerDataMessage
        extends AbstractPlayPacket {

    public static final ResourceLocation ID = ZeroCore.ROOT_LOCATION.buildWithSuffix("container");

    /**
     * Construct the local message to be sent over the network.
     */
    public ContainerDataMessage(final ModContainer container) {

        super(ID);
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

        super(ID, buffer);
        this._container = null;
        this._buffer = buffer;
    }

    //region AbstractPlayPacket

    @Override
    public void write(FriendlyByteBuf buffer) {

        final NonNullConsumer<FriendlyByteBuf> writer = Objects.requireNonNull(this._container).getContainerDataWriter();

        if (null != writer) {
            writer.accept(buffer);
        }
    }

    @Override
    public void handlePacket(PlayPayloadContext context) {

        context.workHandler().execute(() -> {

            final LocalPlayer player = Minecraft.getInstance().player;

            if (player != null && player.containerMenu instanceof ModContainer) {
                ((ModContainer)player.containerMenu).readContainerData(Objects.requireNonNull(this._buffer));
            }
        });
    }

    //endregion
    //region internals

    final ModContainer _container;
    final FriendlyByteBuf _buffer;

    //endregion
}
