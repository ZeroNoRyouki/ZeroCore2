/*
 *
 * IWideEnergyStorage.java
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

/**
 * A buffer to store a very big amount of energy
 *
 * Based upon the IEnergyStorage from King Lemming's RedstoneFlux API
 */
@SuppressWarnings("unused")
public interface IWideEnergyStorage {

    /**
     * Get the {@link EnergySystem} used natively by the IWideEnergyStorage
     *
     * @return the native {@link EnergySystem}
     */
    EnergySystem getEnergySystem();

    /**
     * Add energy, expressed in the specified {@link EnergySystem}, to the storage
     *
     * @param system the {@link EnergySystem} used by the request
     * @param maxAmount maximum amount of energy to be inserted
     * @param simulate if true, the insertion will only be simulated
     * @return amount of energy that was (or would have been, if simulated) inserted
     */
    double insertEnergy(EnergySystem system, double maxAmount, boolean simulate);

    /**
     * Remove energy, expressed in the specified {@link EnergySystem}, from the storage
     *
     * @param system the {@link EnergySystem} used by the request
     * @param maxAmount maximum amount of energy to be extracted
     * @param simulate if true, the extraction will only be simulated
     * @return amount of energy that was (or would have been, if simulated) extracted from the storage
     */
    double extractEnergy(EnergySystem system, double maxAmount, boolean simulate);

    /**
     * Returns the amount of energy currently stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     */
    double getEnergyStored(EnergySystem system);

    /**
     * Returns the maximum amount of energy that can be stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     */
    double getCapacity(EnergySystem system);
}
