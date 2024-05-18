/*
 *
 * AbstractMultiblockScreen.java
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

package it.zerono.mods.zerocore.base.multiblock.client.screen;

import it.zerono.mods.zerocore.base.client.screen.AbstractScreen;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractMultiblockScreen<Controller extends AbstractCuboidMultiblockController<Controller> & IMultiblockMachine,
                                                T extends AbstractMultiblockEntity<Controller> & MenuProvider,
                                                C extends ModTileContainer<T>>
        extends AbstractScreen<T, C> {

    protected AbstractMultiblockScreen(final C container, final Inventory inventory,
                                       final PlayerInventoryUsage inventoryUsage, final Component title,
                                       final Supplier<@NotNull SpriteTextureMap> mainTextureSupplier) {

        super(container, inventory, inventoryUsage, title, DEFAULT_GUI_WIDTH, DEFAULT_GUI_HEIGHT, mainTextureSupplier.get());
    }

    protected AbstractMultiblockScreen(final C container, final Inventory inventory,
                                       final PlayerInventoryUsage inventoryUsage, final Component title,
                                       final int guiWidth, final int guiHeight,
                                       final Supplier<@NotNull SpriteTextureMap> mainTextureSupplier) {

        super(container, inventory, inventoryUsage, title, guiWidth, guiHeight, mainTextureSupplier.get());
    }

    protected AbstractMultiblockScreen(final C container, final Inventory inventory,
                                       final PlayerInventoryUsage inventoryUsage, final Component title,
                                       final int guiWidth, final int guiHeight, final SpriteTextureMap mainTexture) {

        super(container, inventory, inventoryUsage, title, guiWidth, guiHeight, mainTexture);
    }

    protected Optional<Controller> getMultiblockController() {
        return this.getMenu().getTileEntity().getMultiblockController();
    }

    /**
     * Execute the given Consumer on the controller, if this part is connected to one
     * @param code the consumer
     */
    protected void executeOnController(final Consumer<Controller> code) {
        this.getMenu().getTileEntity().executeOnController(code);
    }

    /**
     * Execute the given Function on the controller returning it's result, if this part is connected to one
     * @param code the function
     * @param defaultValue the value to return if this part is not connected to a controller
     * @return the result of the function if this part is connected to a controller or defaultValue if it's not
     */
    protected <R> R evalOnController(final Function<Controller, R> code, final R defaultValue) {
        return this.getMenu().getTileEntity().evalOnController(code, defaultValue);
    }

    protected boolean testOnController(final Predicate<Controller> test) {
        return this.getMenu().getTileEntity().testOnController(test);
    }

    protected boolean isMultiblockActive() {
        return this.testOnController(c -> c instanceof IActivableMachine && ((IActivableMachine)c).isMachineActive());
    }

    protected boolean isMultiblockAssembled() {
        return this.testOnController(IMultiblockController::isAssembled);
    }
}
