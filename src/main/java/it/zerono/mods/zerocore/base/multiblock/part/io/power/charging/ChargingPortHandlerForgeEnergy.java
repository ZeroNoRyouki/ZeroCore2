/*
 *
 * ChargingPortHandlerForgeEnergy.java
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

package it.zerono.mods.zerocore.base.multiblock.part.io.power.charging;

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.capability.ItemHandlerCapabilitySource;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class ChargingPortHandlerForgeEnergy<Controller extends AbstractCuboidMultiblockController<Controller>,
        Port extends AbstractMultiblockEntity<Controller> & IIoEntity & IChargingPort>
    extends AbstractChargingPortHandler<Controller, Port> {

    public ChargingPortHandlerForgeEnergy(final Port port, final int inputSlotsCount, final int outputSlotsCount) {

        super(EnergySystem.ForgeEnergy, port, inputSlotsCount, outputSlotsCount);

        final var handler = this.getItemStackHandler(IoDirection.Input);

        this._remoteCapabilitySources = new ArrayList<>(this.getInputSlotsCount());

        for (int idx = 0; idx < this.getInputSlotsCount(); ++idx) {
            this._remoteCapabilitySources.add(new ItemHandlerCapabilitySource<>(handler, idx, Capabilities.EnergyStorage.ITEM));
        }
    }

    //region IPowerPortHandler

    /**
     * Send energy to the connected consumer (if there is one)
     *
     * @param amount amount of energy to send
     * @return the amount of energy accepted by the consumer
     */
    @Override
    public WideAmount outputEnergy(final WideAmount amount) {

        final WideAmount maxTransfer = this.getChargingRate();

        if (amount.isZero() || maxTransfer.isZero()) {
            return WideAmount.ZERO;
        }

        WideAmount remaining = amount.copy();
        WideAmount accepted = WideAmount.ZERO;

        for (int idx = 0; idx < this.getInputSlotsCount(); ++idx) {

            final var capability = this._remoteCapabilitySources.get(idx).getCapability(null);

            if (null == capability || !capability.canReceive()) {
                continue;
            }

            final WideAmount transfer = WideAmount.min(remaining, maxTransfer);
            final int transferred = capability.receiveEnergy(transfer.intValue(), false);

            if (0 == transferred) {
                this.eject(idx);
            }

            accepted = accepted.add(transfer);
            remaining = remaining.subtract(transfer);

            if (remaining.isZero()) {
                break;
            }
        }

        return accepted;
    }

    //endregion
    //region internals

    private final List<ItemHandlerCapabilitySource<IEnergyStorage, Void>> _remoteCapabilitySources;

    //endregion
}
