/*
 *
 * IWideEnergyConnection.java
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

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * Implement this interface on entities which should connect to energy transportation blocks.
 * This is intended for blocks which generate energy but do not accept it; otherwise just use IWideEnergyHandler.
 *
 * Note that {@link IWideEnergyHandler2} is an extension of this.
 *
 * Based upon the IEnergyConnection from King Lemming's RedstoneFlux API
 */
public interface IWideEnergyConnection
        extends IEnergySystemAware {

    /**
     * Returns true if the entity can connect on a given side and support with the provided {@link EnergySystem}.
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from the direction the request is coming from, or null for any directions
     */
    boolean canConnectEnergy(EnergySystem system, @Nullable Direction from);
}
