/*
 *
 * EnergyBuffer.java
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

package it.zerono.mods.zerocore.lib.energy;

import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.LogicalSide;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class WideEnergyBuffer
        implements IWideEnergyStorage2, ISyncableEntity, IDebuggable {

    public WideEnergyBuffer(EnergySystem system, WideAmount capacity) {
        this(system, capacity, capacity, capacity);
    }

    public WideEnergyBuffer(EnergySystem system, WideAmount capacity, WideAmount maxTransfer) {
        this(system, capacity, maxTransfer, maxTransfer);
    }

    public WideEnergyBuffer(final EnergySystem system, final WideAmount capacity,
                            final WideAmount maxInsert, final WideAmount maxExtract) {

        this._system = system;
        this._energy = WideAmount.ZERO;
        this._capacity = capacity;
        this._maxInsert = maxInsert;
        this._maxExtract = maxExtract;
        this._modified = false;
    }

    public boolean isEmpty() {
        return this._energy.isZero();
    }

    public boolean modified() {

        final boolean m = this._modified;

        this._modified = false;
        return m;
    }

    public WideEnergyBuffer setCapacity(final WideAmount capacity) {

        this._capacity = this._capacity.set(capacity);

        if (this._energy.greaterThan(capacity)) {

            this._energy = this._energy.set(capacity);
            this._modified = true;
        }

        return this;
    }

    public WideEnergyBuffer setMaxTransfer(final WideAmount maxTransfer) {

        this.setMaxInsert(maxTransfer);
        this.setMaxExtract(maxTransfer);
        return this;
    }

    public WideEnergyBuffer setMaxInsert(final WideAmount maxInsert) {

        this._maxInsert = this._maxInsert.set(WideAmount.clamp(maxInsert, WideAmount.ZERO, WideAmount.MAX_VALUE));
        return this;
    }

    public WideEnergyBuffer setMaxExtract(final WideAmount maxExtract) {

        this._maxExtract = this._maxExtract.set(WideAmount.clamp(maxExtract, WideAmount.ZERO, WideAmount.MAX_VALUE));
        return this;
    }

    public WideAmount getMaxInsert() {
        return this._maxInsert.copy();
    }

    public WideAmount getMaxExtract() {
        return this._maxExtract.copy();
    }

    public WideAmount getEnergyStored() {
        return this._energy.copy();
    }

    public WideEnergyBuffer grow(final WideAmount amount) {
        return this.setEnergyStoredInternal(this._energy.add(amount));
    }

    public WideEnergyBuffer grow(final double amount) {
        return this.setEnergyStoredInternal(this._energy.add(amount));
    }

    public WideEnergyBuffer shrink(final WideAmount amount) {
        return this.setEnergyStoredInternal(this._energy.subtract(amount));
    }

    public WideEnergyBuffer shrink(final double amount) {
        return this.setEnergyStoredInternal(this._energy.subtract(amount));
    }

    public void merge(final WideEnergyBuffer other) {

        if (!other.isEmpty()) {

            this._energy = this._energy.add(other._system.convertTo(this._system, other._energy));
            this._modified = true;
        }
    }

    public void empty() {
        this.setEnergyStoredInternal(WideAmount.ZERO);
    }

    //region IWideEnergyStorage2

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    @Override
    public boolean canExtract() {
        return this._maxExtract.greaterThan(WideAmount.ZERO);
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to insertEnergy will return 0.
     */
    @Override
    public boolean canInsert() {
        return this._maxInsert.greaterThan(WideAmount.ZERO);
    }

    /**
     * Get the {@code EnergySystem} used natively the the IWideEnergyStorage
     *
     * @return the native {@code EnergySystem}
     */
    public EnergySystem getEnergySystem() {
        return this._system;
    }

    /**
     * Add energy, expressed in the specified {@code EnergySystem}, to the storage
     *
     * @param system the {@link EnergySystem} used by the request
     * @param maxAmount maximum amount of energy to be inserted
     * @param mode how the operation is carried out
     * @return amount of energy that was (or would have been, if simulated) inserted
     */
    @Override
    public WideAmount insertEnergy(final EnergySystem system, WideAmount maxAmount, final OperationMode mode) {

        if (!this.canInsert()) {
            return WideAmount.ZERO;
        }

        final EnergySystem localSystem = this.getEnergySystem();

        // convert the requested amount to the local energy system
        maxAmount = system.convertTo(localSystem, maxAmount);

        final WideAmount inserted = WideAmount.min(this.getCapacity(localSystem).subtract(this._energy),
                WideAmount.min(this._maxInsert, maxAmount)).copy();

        if (mode.execute()) {

            this._energy = this._energy.set(WideAmount.min(this.getEnergyStored(localSystem).add(inserted), this._capacity));
            this._modified = true;
        }

        // convert the inserted energy amount back to the original energy system
        return localSystem.convertTo(system, inserted);
    }

    /**
     * Remove energy, expressed in the specified {@code EnergySystem}, from the storage
     *
     * @param system    the {@link EnergySystem} used by the request
     * @param maxAmount maximum amount of energy to be extracted
     * @param mode how the operation is carried out
     * @return amount of energy that was (or would have been, if simulated) extracted from the storage
     */
    @Override
    public WideAmount extractEnergy(final EnergySystem system, WideAmount maxAmount, final OperationMode mode) {

        if (!this.canExtract()) {
            return WideAmount.ZERO;
        }

        final EnergySystem localSystem = this.getEnergySystem();

        // convert the requested amount to the local energy system
        maxAmount = system.convertTo(localSystem, maxAmount);

        final WideAmount extracted = WideAmount.min(this._energy, WideAmount.min(this._maxExtract, maxAmount)).copy();

        if (mode.execute()) {

            this._energy = this._energy.subtract(extracted);
            this._modified = true;
        }

        // convert the extracted energy amount back to the original energy system
        return localSystem.convertTo(system, extracted);
    }

    /**
     * Returns the amount of energy currently stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     */
    @Override
    public WideAmount getEnergyStored(final EnergySystem system) {
        return this.convertIf(system, this._energy);
    }

    /**
     * Replace the amount of energy currently stored with the provided value expressed in the specified {@link EnergySystem}
     *
     * @param energy the new energy amount
     * @param system the {@link EnergySystem} used by the request
     */
    @Override
    public void setEnergyStored(final WideAmount energy, final EnergySystem system) {

        final EnergySystem localSystem = this.getEnergySystem();
        final WideAmount newAmount = localSystem == system ? energy : system.convertTo(localSystem, energy);

        this._energy = WideAmount.clamp(newAmount, WideAmount.ZERO, this._capacity).copy();
        this._modified = true;
    }

    /**
     * Returns the maximum amount of energy that can be stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     */
    @Override
    public WideAmount getCapacity(final EnergySystem system) {
        return this.convertIf(system, this._capacity);
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given NBT compound
     *
     * @param data the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(final CompoundTag data, final SyncReason syncReason) {

        if (data.contains("wide")) {

            this._capacity = WideAmount.from(data.getCompound("capacity"));
            this._maxInsert = WideAmount.from(data.getCompound("maxInsert"));
            this._maxExtract = WideAmount.from(data.getCompound("maxExtract"));
            this._energy = WideAmount.from(data.getCompound("energy"));

        } else {

            // load and convert data generated by the old EnergyBuffer class

            this.setMaxInsert(WideAmount.from(data.getDouble("maxInsert")));
            this.setMaxExtract(WideAmount.from(data.getDouble("maxExtract")));
            this.setEnergyStoredInternal(WideAmount.from(data.getDouble("energy")));
            this.setCapacity(WideAmount.from(data.getDouble("capacity")));
        }

        this._modified = true;
    }

    /**
     * Sync the entity data to the given NBT compound
     *
     * @param data the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public CompoundTag syncDataTo(final CompoundTag data, final SyncReason syncReason) {

        data.putByte("wide", (byte)1);
        data.put("capacity", this._capacity.serializeTo(new CompoundTag()));
        data.put("maxInsert", this._maxInsert.serializeTo(new CompoundTag()));
        data.put("maxExtract", this._maxExtract.serializeTo(new CompoundTag()));
        data.put("energy", this._energy.serializeTo(new CompoundTag()));
        return data;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        final EnergySystem sys = this.getEnergySystem();

        messages.add("Energy buffer: %1$s / %2$s; Imax: %3$s/t, Omax: %4$s/t",
                sys.asHumanReadableNumber(this._energy.doubleValue()),
                sys.asHumanReadableNumber(this._capacity.doubleValue()),
                sys.asHumanReadableNumber(this._maxInsert.doubleValue()),
                sys.asHumanReadableNumber(this._maxExtract.doubleValue()));
    }

    //endregion
    //region Object

    @Override
    public String toString() {

        final EnergySystem sys = this.getEnergySystem();

        return String.format("%s / %s - Imax: %s/t, Omax: %s/t",
                sys.asHumanReadableNumber(this._energy.doubleValue()),
                sys.asHumanReadableNumber(this._capacity.doubleValue()),
                sys.asHumanReadableNumber(this._maxInsert.doubleValue()),
                sys.asHumanReadableNumber(this._maxExtract.doubleValue()));
    }

    //endregion
    //region internals

    private WideAmount convertIf(final EnergySystem system, final WideAmount amount) {
        return this.getEnergySystem() != system ? this.getEnergySystem().convertTo(system, amount.copy()) : amount.copy();
    }

    private WideEnergyBuffer setEnergyStoredInternal(final WideAmount amount) {

        this.setEnergyStored(amount, this.getEnergySystem());
        return this;
    }

    private final EnergySystem _system;
    private WideAmount _energy;
    private WideAmount _capacity;
    private WideAmount _maxInsert;
    private WideAmount _maxExtract;
    private boolean _modified;

    //endregion
}
