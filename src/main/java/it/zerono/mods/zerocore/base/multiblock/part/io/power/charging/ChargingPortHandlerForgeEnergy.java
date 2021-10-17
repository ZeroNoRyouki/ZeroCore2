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
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class ChargingPortHandlerForgeEnergy<Controller extends AbstractCuboidMultiblockController<Controller>,
        T extends AbstractMultiblockEntity<Controller> & IIoEntity & IChargingPort>
        extends AbstractChargingPortHandler<Controller, T> {

    public ChargingPortHandlerForgeEnergy(final T part, final int inputSlotsCount, final int outputSlotsCount) {
        super(EnergySystem.ForgeEnergy, part, inputSlotsCount, outputSlotsCount);
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

            final WideAmount transfer = WideAmount.min(remaining, maxTransfer);
            final LazyOptional<IEnergyStorage> cap = this.getCapabilityFromInventory(CAPAP_FORGE_ENERGYSTORAGE, idx, true);

            if (cap.isPresent()) {

                final int transferred = cap.map(c -> recharge(c, transfer)).orElseThrow(IllegalStateException::new);

                if (0 == transferred) {
                    this.eject(idx);
                }

                accepted = accepted.add(transfer);
                remaining = remaining.subtract(transfer);

                if (remaining.isZero()) {
                    break;
                }
            }
        }

        return accepted;
    }

    //endregion
    //region internals

    private static int recharge(final IEnergyStorage cap, WideAmount energyAmount) {
        return cap.canReceive() ? cap.receiveEnergy(energyAmount.intValue(), false) : 0;
    }

    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IEnergyStorage.class)
    private static Capability<IEnergyStorage> CAPAP_FORGE_ENERGYSTORAGE = null;

    //endregion
}
