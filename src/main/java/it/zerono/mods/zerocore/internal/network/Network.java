/*
 *
 * Network.java
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
import it.zerono.mods.zerocore.lib.data.nbt.NBTBuilder;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.network.IModMessage;
import it.zerono.mods.zerocore.lib.network.ModSyncableTileMessage;
import it.zerono.mods.zerocore.lib.network.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Collection;

public final class Network {

    public static final NetworkHandler HANDLER;

    public static void initialize() {

        HANDLER.registerMessage(TileCommandMessage.class, TileCommandMessage::new);
        HANDLER.registerMessage(ModSyncableTileMessage.class, ModSyncableTileMessage::new);
        HANDLER.registerMessage(ErrorReportMessage.class, ErrorReportMessage::new);
        HANDLER.registerMessage(InternalCommandMessage.class, InternalCommandMessage::new);
        HANDLER.registerMessage(ContainerDataMessage.class, ContainerDataMessage::new);
    }

    public static <Message extends IModMessage> void sendToAllPlayers(final Message message) {

        if (null != ServerLifecycleHooks.getCurrentServer()) {
            HANDLER.sendToAllPlayers(message);
        }
    }

    public static void sendClearRecipeCommand() {
        sendToAllPlayers(new InternalCommandMessage(InternalCommand.ClearRecipes));
    }

    public static void sendDebugGuiFrameCommand(final boolean enable) {
        sendToAllPlayers(new InternalCommandMessage(InternalCommand.DebugGuiFrame,
                new NBTBuilder().addBoolean("enable", enable).build()));
    }

    public static void sendServerContainerDataSync(final Collection<ServerPlayerEntity> players, final CompoundNBT data) {

        final InternalCommandMessage message = new InternalCommandMessage(InternalCommand.ContainerDataSync, data);

        players.forEach(player -> HANDLER.sendToPlayer(message, player));
    }

    public static void sendServerContainerData(final Collection<ServerPlayerEntity> players, final ModContainer container) {

        final ContainerDataMessage message = new ContainerDataMessage(container);

        players.forEach(player -> HANDLER.sendToPlayer(message, player));
    }

    static {
        HANDLER = new NetworkHandler(ZeroCore.newID("network"), "1");
    }
}
