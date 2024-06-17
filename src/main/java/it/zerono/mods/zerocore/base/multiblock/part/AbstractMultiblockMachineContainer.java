/*
 *
 * AbstractMultiblockMachineContainer.java
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

package it.zerono.mods.zerocore.base.multiblock.part;

import it.zerono.mods.zerocore.base.multiblock.AbstractMultiblockMachineController;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockDimensionVariant;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class AbstractMultiblockMachineContainer<Controller extends AbstractMultiblockMachineController<Controller, V>,
                                                V extends IMultiblockDimensionVariant,
                                                T extends AbstractMultiblockMachineEntity<Controller, V>>
        extends ModTileContainer<T> {

    public final BooleanData ACTIVE;

    public AbstractMultiblockMachineContainer(boolean isClientSide, int ticksBetweenUpdates, ContainerFactory factory,
                                              MenuType<? extends AbstractMultiblockMachineContainer<Controller, V, T>> type,
                                              int windowId, Inventory playerInventory, T tile) {

        super(isClientSide, ticksBetweenUpdates, factory, type, windowId, playerInventory, tile);
        this.ACTIVE = this.createActiveSyncData();
    }

    public AbstractMultiblockMachineContainer(boolean isClientSide, int ticksBetweenUpdates, ContainerFactory factory,
                                              MenuType<? extends AbstractMultiblockMachineContainer<Controller, V, T>> type,
                                              int windowId, T tile, ServerPlayer player) {

        super(isClientSide, ticksBetweenUpdates, factory, type, windowId, tile, player);
        this.ACTIVE = this.createActiveSyncData();
    }

    //region internals

    private BooleanData createActiveSyncData() {

        final T tile = this.getTileEntity();

        return BooleanData.of(this, tile::isMachineActive, tile::setMachineActive);
    }

    //endregion
}
