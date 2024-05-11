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
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.fml.LogicalSide;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
@Deprecated //use WideEnergyBuffer
public class EnergyBuffer implements IWideEnergyStorage, ISyncableEntity, IDebuggable {

    public EnergyBuffer(EnergySystem system, double capacity) {
        this(system, capacity, capacity, capacity);
    }

    public EnergyBuffer(EnergySystem system, double capacity, double maxTransfer) {
        this(system, capacity, maxTransfer, maxTransfer);
    }

    public EnergyBuffer(final EnergySystem system, final double capacity,
                        final double maxInsert, final double maxExtract) {

        this._system = system;
        this._energy = 0d;
        this.setCapacity(capacity);
        this.setMaxInsert(maxInsert);
        this.setMaxExtract(maxExtract);
    }

    public EnergyBuffer setCapacity(double capacity) {

        this._capacity = Math.max(0d, capacity);
        this.clampEnergyToCapacity();
        return this;
    }

    public EnergyBuffer setMaxTransfer(double maxTransfer) {

        this.setMaxInsert(maxTransfer);
        this.setMaxExtract(maxTransfer);
        return this;
    }

    public EnergyBuffer setMaxInsert(double maxInsert) {

        this._maxInsert = Math.max(0d, maxInsert);
        return this;
    }

    public EnergyBuffer setMaxExtract(double maxExtract) {

        this._maxExtract = Math.max(0d, maxExtract);
        return this;
    }

    public double getMaxInsert() {
        return this._maxInsert;
    }

    public double getMaxExtract() {
        return this._maxExtract;
    }

    public double getEnergyStored() {
        return this._energy;
    }

    public EnergyBuffer setEnergyStored(double amount) {

        this._energy = Math.max(0, amount);
        this.clampEnergyToCapacity();
        return this;
    }

    public EnergyBuffer modifyEnergyStored(double amount) {

        if (Double.isNaN(amount)) {
            return this;
        }

        this._energy += amount;
        this.clampEnergyToCapacity();

        if (this._energy < 0) {
            this._energy = 0;
        }

        return this;
    }

    public double getEnergyStoredPercentage() {
        return this.getEnergyStored() / this._capacity;
    }

    //region IWideEnergyStorage

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
     * @param simulate if true, the insertion will only be simulated
     * @return amount of energy that was (or would have been, if simulated) inserted
     */
    @Override
    public double insertEnergy(EnergySystem system, double maxAmount, boolean simulate) {

        if (!this.canInsert()) {
            return 0d;
        }

        final EnergySystem localSystem = this.getEnergySystem();

        // convert the requested amount to the local energy system
        maxAmount = system.convertTo(localSystem, maxAmount);

        final double inserted = Math.min(this.getCapacity(localSystem) - this.getEnergyStored(localSystem), Math.min(this.getMaxInsert(), maxAmount));

        if (!simulate) {
            this.modifyEnergyStored(inserted);
        }

        // convert the inserted energy amount back to the original energy system
        return localSystem.convertTo(system, inserted);
    }

    /**
     * Remove energy, expressed in the specified {@code EnergySystem}, from the storage
     *
     * @param system    the {@link EnergySystem} used by the request
     * @param maxAmount maximum amount of energy to be extracted
     * @param simulate  if true, the extraction will only be simulated
     * @return amount of energy that was (or would have been, if simulated) extracted from the storage
     */
    @Override
    public double extractEnergy(EnergySystem system, double maxAmount, boolean simulate) {

        if (!this.canExtract()) {
            return 0d;
        }

        final EnergySystem localSystem = this.getEnergySystem();

        // convert the requested amount to the local energy system
        maxAmount = system.convertTo(localSystem, maxAmount);

        final double extracted = Math.min(this.getEnergyStored(localSystem), Math.min(this.getMaxExtract(), maxAmount));

        if (!simulate) {
            this.modifyEnergyStored(-extracted);
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
    public double getEnergyStored(EnergySystem system) {
        return this.convertIf(system, this._energy);
    }

    /**
     * Returns the maximum amount of energy that can be stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     */
    @Override
    public double getCapacity(EnergySystem system) {
        return this.convertIf(system, this._capacity);
    }

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    @Override
    public boolean canExtract() {
        return this.getMaxExtract() > 0;
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to insertEnergy will return 0.
     */
    @Override
    public boolean canInsert() {
        return this.getMaxInsert() > 0;
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
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        this.setCapacity(data.getDouble("capacity"));
        this.setMaxInsert(data.getDouble("maxInsert"));
        this.setMaxExtract(data.getDouble("maxExtract"));
        this.setEnergyStored(data.getDouble("energy"));
    }

    /**
     * Sync the entity data to the given NBT compound
     *
     * @param data the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        final EnergySystem localSystem = this.getEnergySystem();

        data.putDouble("capacity", this.getCapacity(localSystem));
        data.putDouble("maxInsert", this.getMaxInsert());
        data.putDouble("maxExtract", this.getMaxExtract());
        data.putDouble("energy", this.getEnergyStored(localSystem));

        return data;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        final EnergySystem sys = this.getEnergySystem();

        messages.add("Energy buffer: %1$s / %2$s; Imax: %3$s/t, Omax: %4$s/t",
                sys.asHumanReadableNumber(this.getEnergyStored(sys)),
                sys.asHumanReadableNumber(this.getCapacity(sys)),
                sys.asHumanReadableNumber(this.getMaxInsert()),
                sys.asHumanReadableNumber(this.getMaxExtract()));
    }

    //endregion
    //region Object

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {

        final EnergySystem sys = this.getEnergySystem();

        return String.format("%s / %s - Imax: %s/t, Omax: %s/t",
                sys.asHumanReadableNumber(this.getEnergyStored(sys)),
                sys.asHumanReadableNumber(this.getCapacity(sys)),
                sys.asHumanReadableNumber(this.getMaxInsert()),
                sys.asHumanReadableNumber(this.getMaxExtract()));
    }

    //endregion
    //region internals

    private void clampEnergyToCapacity() {

        final double capacity = this.getCapacity(this.getEnergySystem());

        if (this._energy > capacity) {
            this._energy = capacity;
        }
    }

    private double convertIf(EnergySystem system, double amount) {
        return this.getEnergySystem() != system ? this.getEnergySystem().convertTo(system, amount) : amount;
    }

    private final EnergySystem _system;
    private double _energy;
    private double _capacity;
    private double _maxInsert;
    private double _maxExtract;

    //endregion
}
