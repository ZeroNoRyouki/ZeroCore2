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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public abstract class AbstractChargingPortHandler<Controller extends AbstractCuboidMultiblockController<Controller>,
            T extends AbstractMultiblockEntity<Controller> & IChargingPort>
        extends AbstractPowerPortHandler<Controller, T>
        implements IChargingPortHandler, ISyncableEntity {

    protected AbstractChargingPortHandler(final EnergySystem energySystem, final T part,
                                          final int inputSlotsCount, final int outputSlotsCount) {

        super(energySystem, part, IoMode.Active);
        this._input = new TileEntityItemStackHandler(part, inputSlotsCount);
        this._output = new TileEntityItemStackHandler(part, outputSlotsCount);
        this._chargingRate = WideAmount.ZERO;
    }

    protected WideAmount getChargingRate() {

        if (this._chargingRate.isZero()) {
            this._chargingRate = this.getPart().getMaxTransferRate();
        }

        return this._chargingRate;
    }

    protected <C> LazyOptional<C> getCapabilityFromInventory(final Capability<C> capability, final int inputSlotIndex,
                                                             final boolean ejectIfNotFound) {

        final ItemStack stack = this._input.getStackInSlot(inputSlotIndex);

        if (!stack.isEmpty()) {

            final LazyOptional<C> cap = stack.getCapability(capability, null);

            if (ejectIfNotFound && !cap.isPresent()) {
                this.eject(inputSlotIndex);
            }

            return cap;
        }

        return LazyOptional.empty();
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

    /**
     * Check for connections
     *
     * @param world    the handler world
     * @param position the handler position
     */
    @Override
    public void checkConnections(@Nullable World world, BlockPos position) {
        // nothing to do here
    }

    @Override
    public void invalidate() {
        // nothing to do here
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundNBT}
     *
     * @param data       the {@link CompoundNBT} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundNBT data, SyncReason syncReason) {

        if (syncReason.isFullSync()) {

            syncInvFrom(data, "in", this._input);
            syncInvFrom(data, "out", this._output);
        }
    }

    /**
     * Sync the entity data to the given {@link CompoundNBT}
     *
     * @param data       the {@link CompoundNBT} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundNBT} the data was written to (usually {@code data})
     */
    @Override
    public CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {

        if (syncReason.isFullSync()) {

            syncInvTo(data, "in", this._input);
            syncInvTo(data, "out", this._output);
        }

        return data;
    }

    //endregion
    //region internals

    private static void syncInvTo(final CompoundNBT data, final String name, final ItemStackHandler inv) {
        data.put(name, inv.serializeNBT());
    }

    private static void syncInvFrom(final CompoundNBT data, final String name, final ItemStackHandler inv) {

        if (data.contains(name)) {
            inv.deserializeNBT(data.getCompound(name));
        }
    }

    private final ItemStackHandler _input;
    private final ItemStackHandler _output;
    private WideAmount _chargingRate;

    //endregion
}
