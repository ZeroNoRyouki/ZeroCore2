/*
 *
 * IWideEnergyHandler.java
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

import net.minecraft.util.Direction;

import javax.annotation.Nullable;

/**
 * Implement this interface on entities which should handle energy, generally storing it in one or more
 * internal {@link IWideEnergyStorage} objects
 *
 * Note that {@link IWideEnergyReceiver} and {@link IWideEnergyProvider} are extensions of this
 *
 * Based upon the IEnergyHandler from King Lemming's RedstoneFlux API
 */
@Deprecated // use IWideEnergyHandler2
public interface IWideEnergyHandler extends IWideEnergyConnection {

    /**
     * Returns the amount of energy currently stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from the direction the request is coming from, or null for any directions
     */
    double getEnergyStored(EnergySystem system, @Nullable Direction from);

    /**
     * Returns the maximum amount of energy that can be stored expressed in the requested {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from the direction the request is coming from, or null for any directions
     */
    double getCapacity(EnergySystem system, @Nullable Direction from);
}
