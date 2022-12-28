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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface IProxy {

    Optional<World> getClientWorld();

    Optional<PlayerEntity> getClientPlayer();

    void markBlockRangeForRenderUpdate(BlockPos min, BlockPos max);

    void sendPlayerStatusMessage(PlayerEntity player, ITextComponent message);

    void addResourceReloadListener(ISelectiveResourceReloadListener listener);

    default long getLastRenderTime() {
        return 0;
    }

    default void reportErrorToPlayer(final @Nullable PlayerEntity player, final @Nullable BlockPos position,
                                    final ITextComponent... messages) {

        if (player instanceof ServerPlayerEntity) {
            Network.HANDLER.sendToPlayer(ErrorReportMessage.create(position, messages), (ServerPlayerEntity)player);
        }
    }

    default void reportErrorToPlayer(final @Nullable PlayerEntity player, final @Nullable BlockPos position,
                                    final List<ITextComponent> messages) {

        if (player instanceof ServerPlayerEntity) {
            Network.HANDLER.sendToPlayer(ErrorReportMessage.create(position, messages), (ServerPlayerEntity)player);
        }
    }

    default void displayErrorToPlayer(final @Nullable BlockPos position, final ITextComponent... messages) {
    }

    default void displayErrorToPlayer(final @Nullable BlockPos position, final List<ITextComponent> messages) {
    }

    void clearErrorReport();

    @Nullable
    RecipeManager getRecipeManager();

    default void handleInternalCommand(final InternalCommand command, final CompoundNBT data, final NetworkDirection direction) {
        // handle commands that are common to both distributions here
    }

    void debugUngrabMouse();
}
