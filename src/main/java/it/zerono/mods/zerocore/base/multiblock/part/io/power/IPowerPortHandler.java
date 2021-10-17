/*
 *
 * IPowerPortHandler.java
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
import it.zerono.mods.zerocore.base.multiblock.part.io.IIOPortHandler;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;

public interface IPowerPortHandler
        extends IIOPortHandler {

    static <Controller extends AbstractCuboidMultiblockController<Controller>,
            T extends AbstractMultiblockEntity<Controller> & IPowerPort>
    IPowerPortHandler create(final EnergySystem system, final IoMode mode, final T part) {

        switch (system) {

            case ForgeEnergy:
                return new PowerPortHandlerForgeEnergy<>(part, mode);

            default:
                throw new IllegalArgumentException("Unsupported energy system: " + system);
        }
    }

    /**
     * Get the {@link EnergySystem} supported by this IPowerPortHandler
     *
     * @return the supported {@link EnergySystem}
     */
    EnergySystem getEnergySystem();

    /**
     * Send energy to the connected consumer (if there is one)
     *
     * @param amount amount of energy to send
     * @return the amount of energy accepted by the consumer
     */
    WideAmount outputEnergy(WideAmount amount);
}
