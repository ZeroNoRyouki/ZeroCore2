/*
 *
 * WideEnergyStorageForwarder.java
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

package it.zerono.mods.zerocore.lib.energy.handler;

import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage;

public class WideEnergyStorageForwarder
        implements IWideEnergyStorage {

    public WideEnergyStorageForwarder(final IWideEnergyStorage handler) {
        this.setHandler(handler);
    }

    public IWideEnergyStorage getHandler() {
        return this._handler;
    }

    public void setHandler(final IWideEnergyStorage handler) {
        this._handler = handler;
    }

    //region IWideEnergyStorage

    /**
     * Get the {@link EnergySystem} used by this entity
     *
     * @return the {@link EnergySystem} in use
     */
    @Override
    public EnergySystem getEnergySystem() {
        return this.getHandler().getEnergySystem();
    }

    /**
     * Add energy, expressed in the specified {@link EnergySystem}, to the storage
     *
     * @param system    the {@link EnergySystem} used by the request
     * @param maxAmount maximum amount of energy to be inserted
     * @param simulate  if true, the insertion will only be simulated
     * @return amount of energy that was (or would have been, if simulated) inserted
     */
    @Override
    public double insertEnergy(final EnergySystem system, final double maxAmount, final boolean simulate) {
        return this.getHandler().insertEnergy(system, maxAmount, simulate);
    }

    /**
     * Remove energy, expressed in the specified {@link EnergySystem}, from the storage
     *
     * @param system    the {@link EnergySystem} used by the request
     * @param maxAmount maximum amount of energy to be extracted
     * @param simulate  if true, the extraction will only be simulated
     * @return amount of energy that was (or would have been, if simulated) extracted from the storage
     */
    @Override
    public double extractEnergy(final EnergySystem system, final double maxAmount, final boolean simulate) {
        return this.getHandler().extractEnergy(system, maxAmount, simulate);
    }

    /**
     * Returns the amount of energy currently stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     */
    @Override
    public double getEnergyStored(final EnergySystem system) {
        return this.getHandler().getEnergyStored(system);
    }

    /**
     * Returns the maximum amount of energy that can be stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     */
    @Override
    public double getCapacity(final EnergySystem system) {
        return this.getHandler().getCapacity(system);
    }

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    @Override
    public boolean canExtract() {
        return this.getHandler().canExtract();
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to insertEnergy will return 0.
     */
    @Override
    public boolean canInsert() {
        return this.getHandler().canInsert();
    }

    //endregion
    //region internals

    private IWideEnergyStorage _handler;

    //endregion
}
