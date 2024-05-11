/*
 *
 * ModTileContainerScreen.java
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

package it.zerono.mods.zerocore.lib.client.gui;

import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModTileContainerScreen<T extends AbstractModBlockEntity, C extends ModTileContainer<T>>
    extends ModContainerScreen<C> {

    public T getTileEntity() {
        return this.getMenu().getTileEntity();
    }

    //region Tile commands

    /**
     * Send a command to the corresponding Tile Entity on the server side
     *
     * @param name the command name
     */
    public void sendCommandToServer(final String name) {
        this.getTileEntity().sendCommandToServer(name);
    }

    /**
     * Send a command to the corresponding Tile Entity on the server side
     *
     * @param name the command name
     * @param parameters the parameters for the command
     */
    public void sendCommandToServer(final String name, final CompoundTag parameters) {
        this.getTileEntity().sendCommandToServer(name, parameters);
    }

    /**
     * Send a command to the corresponding Tile Entity on the client side
     *
     * @param name the command name
     */
    public void sendCommandToPlayer(final ServerPlayer player, final String name) {
        this.getTileEntity().sendCommandToPlayer(player, name);
    }

    /**
     * Send a command to the corresponding Tile Entity on the client side
     *
     * @param name the command name
     * @param parameters the parameters for the command
     */
    public void sendCommandToPlayer(final ServerPlayer player, final String name, final CompoundTag parameters) {
        this.getTileEntity().sendCommandToPlayer(player, name, parameters);
    }

    //endregion
    //region internals

    protected ModTileContainerScreen(final C container, final Inventory inventory, final Component title,
                                 final int guiWidth, final int guiHeight) {
        this(container, inventory, title, guiWidth, guiHeight, true);
    }

    protected ModTileContainerScreen(final C container, final Inventory inventory, final Component title,
                                 final int guiWidth, final int guiHeight, boolean singleWindow) {

        super(container, inventory, title, guiWidth, guiHeight, singleWindow);
        this._raiseDataUpdatedHandler = container.getTileEntity().DataUpdate.subscribe(this::raiseDataUpdated);
    }

    /**
     * Called when this screen was closed.
     * Override to handle this event
     */
    @Override
    protected void onScreenClose() {

        this.getMenu().getTileEntity().DataUpdate.unsubscribe(this._raiseDataUpdatedHandler);
        super.onScreenClose();
    }

    private final Runnable _raiseDataUpdatedHandler;

    //endregion
}
