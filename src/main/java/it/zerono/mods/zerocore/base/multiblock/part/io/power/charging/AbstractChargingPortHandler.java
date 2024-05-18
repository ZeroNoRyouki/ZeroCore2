/*
 *
 * AbstractChargingPortHandler.java
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

package it.zerono.mods.zerocore.base.multiblock.part.io.power.charging;

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.AbstractPowerPortHandler;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.handler.TileEntityItemStackHandler;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class AbstractChargingPortHandler<Controller extends AbstractCuboidMultiblockController<Controller>,
            Port extends AbstractMultiblockEntity<Controller> & IChargingPort>
        extends AbstractPowerPortHandler<Controller, Port>
        implements IChargingPortHandler, ISyncableEntity {

    protected AbstractChargingPortHandler(EnergySystem energySystem, Port port,
                                          int inputSlotsCount, int outputSlotsCount) {

        super(energySystem, port, IoMode.Active);
        this._input = new TileEntityItemStackHandler(port, inputSlotsCount);
        this._output = new TileEntityItemStackHandler(port, outputSlotsCount);
        this._chargingRate = WideAmount.ZERO;
    }

    protected WideAmount getChargingRate() {

        if (this._chargingRate.isZero()) {
            this._chargingRate = this.getIoEntity().getMaxTransferRate();
        }

        return this._chargingRate;
    }

    protected void eject(final int inputSlotIndex) {

        final ItemStack input = this._input.getStackInSlot(inputSlotIndex);

        if (!input.isEmpty()) {

            for (int idx = 0; idx < this.getOutputSlotsCount(); ++idx) {

                if (this._output.getStackInSlot(idx).isEmpty()) {

                    this._output.setStackInSlot(idx, input);
                    this._input.setStackInSlot(inputSlotIndex, ItemStack.EMPTY);
                    return;
                }
            }
        }
    }

    protected int getInputSlotsCount() {
        return this._input.getSlots();
    }

    protected int getOutputSlotsCount() {
        return this._output.getSlots();
    }

    //region IChargingPortHandler

    @Override
    public IItemHandlerModifiable getItemStackHandler(final IoDirection direction) {
        return direction.isInput() ? this._input : this._output;
    }

    @Override
    public void eject() {

        for (int idx = 0; idx < this.getInputSlotsCount(); ++idx) {
            this.eject(idx);
        }
    }

    //endregion
    //region IIOPortHandler

    /**
     * @return true if this handler is connected to one of it's allowed consumers, false otherwise
     */
    @Override
    public boolean isConnected() {

        for (int idx = 0; idx < this.getInputSlotsCount(); ++idx) {
            if (!this._input.getStackInSlot(idx).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onPortChanged() {
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        if (syncReason.isFullSync()) {

            syncInvFrom(data, registries, "in", this._input);
            syncInvFrom(data, registries, "out", this._output);
        }
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        if (syncReason.isFullSync()) {

            syncInvTo(data, registries, "in", this._input);
            syncInvTo(data, registries, "out", this._output);
        }

        return data;
    }

    //endregion
    //region internals

    private static void syncInvTo(CompoundTag data, HolderLookup.Provider registries, String name, ItemStackHandler inv) {
        data.put(name, inv.serializeNBT(registries));
    }

    private static void syncInvFrom(CompoundTag data, HolderLookup.Provider registries, String name, ItemStackHandler inv) {

        if (data.contains(name)) {
            inv.deserializeNBT(registries, data.getCompound(name));
        }
    }

    private final ItemStackHandler _input;
    private final ItemStackHandler _output;
    private WideAmount _chargingRate;

    //endregion
}
