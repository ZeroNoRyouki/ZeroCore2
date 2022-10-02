/*
 *
 * IProxy.java
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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface IProxy {

    Optional<Level> getClientWorld();

    Optional<Player> getClientPlayer();

    void markBlockRangeForRenderUpdate(BlockPos min, BlockPos max);

    void sendPlayerStatusMessage(Player player, Component message);

    void addResourceReloadListener(PreparableReloadListener listener);

    default long getLastRenderTime() {
        return 0;
    }

    default void reportErrorToPlayer(final @Nullable Player player, final @Nullable BlockPos position,
                                    final Component... messages) {

        if (player instanceof ServerPlayer) {
            Network.HANDLER.sendToPlayer(ErrorReportMessage.create(position, messages), (ServerPlayer)player);
        }
    }

    default void reportErrorToPlayer(final @Nullable Player player, final @Nullable BlockPos position,
                                    final List<Component> messages) {

        if (player instanceof ServerPlayer) {
            Network.HANDLER.sendToPlayer(ErrorReportMessage.create(position, messages), (ServerPlayer)player);
        }
    }

    default void displayErrorToPlayer(final @Nullable BlockPos position, final Component... messages) {
    }

    default void displayErrorToPlayer(final @Nullable BlockPos position, final List<Component> messages) {
    }

    void clearErrorReport();

    @Nullable
    RecipeManager getRecipeManager();

    default void handleInternalCommand(final InternalCommand command, final CompoundTag data, final NetworkDirection direction) {
        // handle commands that are common to both distributions here
    }

    void debugUngrabMouse();
}
