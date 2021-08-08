/*
 *
 * ServerProxy.java
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

package it.zerono.mods.zerocore.internal.proxy;

import it.zerono.mods.zerocore.internal.InternalCommand;
import it.zerono.mods.zerocore.internal.network.ErrorReportMessage;
import it.zerono.mods.zerocore.internal.network.Network;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ServerProxy implements IProxy {

    @Override
    public Optional<Level> getClientWorld() {
        return Optional.empty();
    }

    @Override
    public void markBlockRangeForRenderUpdate(BlockPos min, BlockPos max) {
        // do nothing
    }

    @Override
    public void sendPlayerStatusMessage(final Player player, final Component message) {

        if (player instanceof ServerPlayer) {
            ((ServerPlayer) player).connection.send(new ClientboundChatPacket(message, ChatType.GAME_INFO, player.getUUID()));
        }
    }

    @Override
    public void addResourceReloadListener(ISelectiveResourceReloadListener listener) {
        CodeHelper.getMinecraftServer()
                .map(MinecraftServer::getDataPackRegistries/*getResourceManager*/)
                .filter(o -> o instanceof ReloadableResourceManager)
                .map(o -> (ReloadableResourceManager)o)
                .ifPresent(rrm -> rrm.registerReloadListener(listener));
    }

    @Override
    public void reportErrorToPlayer(final @Nullable Player player, final @Nullable BlockPos position,
                                    final Component... messages) {

        if (player instanceof ServerPlayer) {
            Network.HANDLER.sendToPlayer(ErrorReportMessage.create(position, messages), (ServerPlayer)player);
        }
    }

    public void reportErrorToPlayer(final @Nullable Player player, final @Nullable BlockPos position,
                                    final List<Component> messages) {

        if (player instanceof ServerPlayer) {
            Network.HANDLER.sendToPlayer(ErrorReportMessage.create(position, messages), (ServerPlayer)player);
        }
    }

    @Override
    public void clearErrorReport() {
    }

    @Nullable
    @Override
    public RecipeManager getRecipeManager() {

        final MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);

        return null != server ? server.getRecipeManager() : null;
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public void handleInternalCommand(final InternalCommand command, final CompoundTag data, final NetworkDirection direction) {

        switch (command) {

            default:
                IProxy.super.handleInternalCommand(command, data, direction);
                break;
        }
    }

    @Override
    public void debugUngrabMouse() {
    }
}
