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
import net.minecraft.entity.player.PlayerEntity;
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

    void markBlockRangeForRenderUpdate(BlockPos min, BlockPos max);

    void sendPlayerStatusMessage(PlayerEntity player, ITextComponent message);

    void addResourceReloadListener(ISelectiveResourceReloadListener listener);

    default long getLastRenderTime() {
        return 0;
    }

    void reportErrorToPlayer(@Nullable PlayerEntity player, @Nullable BlockPos position, ITextComponent... messages);

    void reportErrorToPlayer(@Nullable PlayerEntity player, @Nullable BlockPos position, List<ITextComponent> messages);

    void clearErrorReport();

    @Nullable
    RecipeManager getRecipeManager();

    default void handleInternalCommand(final InternalCommand command, final CompoundNBT data, final NetworkDirection direction) {
        // handle commands that are common to both distributions here
    }
}
