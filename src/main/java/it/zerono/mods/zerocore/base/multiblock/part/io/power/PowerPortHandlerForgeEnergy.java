/*
 *
 * PowerPortHandlerForgeEnergy.java
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

package it.zerono.mods.zerocore.base.multiblock.part.io.power;

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.IOPortBlockCapabilitySource;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class PowerPortHandlerForgeEnergy<Controller extends AbstractCuboidMultiblockController<Controller>,
        Port extends AbstractMultiblockEntity<Controller> & IPowerPort>
    extends AbstractPowerPortHandler<Controller, Port>
    implements IEnergyStorage {

    public PowerPortHandlerForgeEnergy(final Port port, final IoMode mode) {

        super(EnergySystem.ForgeEnergy, port, mode);
        this._remoteCapabilitySource = new IOPortBlockCapabilitySource<>(port, Capabilities.EnergyStorage.BLOCK);
    }

    //region IPowerPortHandler

    @Override
    public boolean isConnected() {
        return null != this._remoteCapabilitySource.getCapability();
    }

    @Override
    public void onPortChanged() {
        this._remoteCapabilitySource.onPortChanged();
    }

    /**
     * Send energy to the connected consumer (if there is one)
     *
     * @param amount amount of energy to send
     * @return the amount of energy accepted by the consumer
     */
    @Override
    public WideAmount outputEnergy(final WideAmount amount) {

        final var consumer = this._remoteCapabilitySource.getCapability();

        if (null == consumer || !this.isOutput() || this.isPassive()) {
            return WideAmount.ZERO;
        }

        final int maxUnits = Math.min(amount.intValue(), Integer.MAX_VALUE);

        return WideAmount.asImmutable(consumer.receiveEnergy(maxUnits, false));
    }

    //endregion
    //region IEnergyStorage

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive Maximum amount of energy to be inserted.
     * @param simulate   If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        return this.canReceive() ? this.getEnergyStorage().insertEnergy(this.getEnergySystem(),
                this.maxTransferRate(maxReceive), OperationMode.from(simulate)).intValue() : 0;
    }

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        return this.canExtract() ? this.getEnergyStorage().extractEnergy(this.getEnergySystem(),
                this.maxTransferRate(maxExtract), OperationMode.from(simulate)).intValue() : 0;
    }

    /**
     * Returns the amount of energy currently stored.
     */
    @Override
    public int getEnergyStored() {
        return this.getEnergyStorage().getEnergyStored(this.getEnergySystem()).intValue();
    }

    /**
     * Returns the maximum amount of energy that can be stored.
     */
    @Override
    public int getMaxEnergyStored() {
        return this.getEnergyStorage().getCapacity(this.getEnergySystem()).intValue();
    }

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    @Override
    public boolean canExtract() {
        return this.isOutput() && this.isPassive();
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    @Override
    public boolean canReceive() {
        return this.isInput() && this.isPassive();
    }

    //endregion
    //region internals

    private final IOPortBlockCapabilitySource<Controller, Port, IEnergyStorage> _remoteCapabilitySource;

    //endregion
}
