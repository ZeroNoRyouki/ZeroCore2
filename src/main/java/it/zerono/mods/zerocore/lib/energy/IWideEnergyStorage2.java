/*
 *
 * IWideEnergyStorage2.java
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

import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;

/**
 * A buffer to store a very big amount of energy
 *
 * Based upon the IEnergyStorage from King Lemming's RedstoneFlux API
 */
@SuppressWarnings("unused")
public interface IWideEnergyStorage2
        extends IEnergySystemAware {

    /**
     * Add energy, expressed in the specified {@link EnergySystem}, to the storage
     *
     * @param system the {@link EnergySystem} used by the request
     * @param maxAmount maximum amount of energy to be inserted
     * @param mode how the operation is carried out
     * @return amount of energy that was (or would have been, if simulated) inserted
     */
    WideAmount insertEnergy(EnergySystem system, WideAmount maxAmount, OperationMode mode);

    /**
     * Remove energy, expressed in the specified {@link EnergySystem}, from the storage
     *
     * @param system the {@link EnergySystem} used by the request
     * @param maxAmount maximum amount of energy to be extracted
     * @param mode how the operation is carried out
     * @return amount of energy that was (or would have been, if simulated) extracted from the storage
     */
    WideAmount extractEnergy(EnergySystem system, WideAmount maxAmount, OperationMode mode);

    /**
     * Returns the amount of energy currently stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     */
    WideAmount getEnergyStored(EnergySystem system);

    /**
     * Replace the amount of energy currently stored with the provided value expressed in the specified {@link EnergySystem}
     *
     * @param energy the new energy amount
     * @param system the {@link EnergySystem} used by the request
     */
    default void setEnergyStored(WideAmount energy, EnergySystem system) {
    }

    /**
     * Returns the maximum amount of energy that can be stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     */
    WideAmount getCapacity(EnergySystem system);

    /**
     * Return the amount of free space in the storage expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     * @return the amount of free space
     */
    default WideAmount getFreeSpace(EnergySystem system) {
        return this.getCapacity(system).subtract(this.getEnergyStored(system));
    }

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    default boolean canExtract() {
        return true;
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to insertEnergy will return 0.
     */
    default boolean canInsert() {
        return true;
    }
}
