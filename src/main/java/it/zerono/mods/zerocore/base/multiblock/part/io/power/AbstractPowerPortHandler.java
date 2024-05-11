/*
 *
 * AbstractPowerPortHandler.java
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
import it.zerono.mods.zerocore.base.multiblock.part.io.AbstractIOPortHandler;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage2;
import it.zerono.mods.zerocore.lib.energy.NullEnergyHandlers;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;

public abstract class AbstractPowerPortHandler<Controller extends AbstractCuboidMultiblockController<Controller>,
        Port extends AbstractMultiblockEntity<Controller> & IPowerPort>
    extends AbstractIOPortHandler<Controller, Port>
    implements IPowerPortHandler {

    protected AbstractPowerPortHandler(EnergySystem energySystem, Port part, IoMode mode) {

        super(part, mode);
        this._system = energySystem;
    }

    protected IWideEnergyStorage2 getEnergyStorage() {
        return this.getIoEntity().getMultiblockController()
                .filter(Controller::isAssembled)
                .filter(c -> c instanceof IWideEnergyStorage2)
                .map(c -> (IWideEnergyStorage2)c)
                .orElse(NullEnergyHandlers.WIDE_STORAGE);
    }

    protected WideAmount maxTransferRate(final int requestedRate) {
        return WideAmount.min(this.getIoEntity().getMaxTransferRate(), WideAmount.asImmutable(requestedRate));
    }

    protected WideAmount maxTransferRate(final long requestedRate) {
        return WideAmount.min(this.getIoEntity().getMaxTransferRate(), WideAmount.asImmutable(requestedRate));
    }

    protected WideAmount maxTransferRate(final double requestedRate) {
        return WideAmount.min(this.getIoEntity().getMaxTransferRate(), WideAmount.asImmutable(requestedRate));
    }

    //region IPowerTapHandler

    /**
     * Get the {@link EnergySystem} supported by this IPowerPortHandler
     *
     * @return the supported {@link EnergySystem}
     */
    public EnergySystem getEnergySystem() {
        return this._system;
    }

    //endregion
    //region internals

    private final EnergySystem _system;

    //endregion
}
