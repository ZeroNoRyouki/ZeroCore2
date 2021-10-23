/*
 *
 * EnergyHelper.java
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
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public final class EnergyHelper {

    @Deprecated
    public static double transferEnergy(final IWideEnergyStorage destination, final IWideEnergyStorage source,
                                        final double maxAmount, final OperationMode mode) {

        final EnergySystem sys = destination.getEnergySystem();

        return destination.insertEnergy(sys, source.extractEnergy(sys, maxAmount, mode.simulate()), mode.simulate());
    }

    @Deprecated
    public static int transferEnergy(final IWideEnergyStorage destination, final IWideEnergyStorage source,
                                     final int maxAmount, final OperationMode mode) {
        return (int)transferEnergy(destination, source, (double)maxAmount, mode);
    }

    @Deprecated
    public static double transferEnergy(final IWideEnergyReceiver destination, final @Nullable Direction destinationDirection,
                                        final IWideEnergyProvider source, final @Nullable Direction sourceDirection,
                                        final double maxAmount, final OperationMode mode) {

        final EnergySystem sys = destination.getEnergySystem();

        return destination.receiveEnergy(sys, destinationDirection,
                source.extractEnergy(sys, sourceDirection, maxAmount, mode.simulate()), mode.simulate());
    }

    @Deprecated
    public static int transferEnergy(final IWideEnergyReceiver destination, final @Nullable Direction destinationDirection,
                                     final IWideEnergyProvider source, final @Nullable Direction sourceDirection,
                                     final int maxAmount, final OperationMode mode) {
        return (int)transferEnergy(destination, destinationDirection, source, sourceDirection, (double)maxAmount, mode);
    }

    public static WideAmount transferEnergy(final IWideEnergyStorage2 destination, final IWideEnergyStorage2 source,
                                            final WideAmount maxAmount, final OperationMode mode) {

        final EnergySystem sys = destination.getEnergySystem();

        return destination.insertEnergy(sys, source.extractEnergy(sys, maxAmount, mode), mode);
    }

    public static int transferEnergy(final IWideEnergyStorage2 destination, final IWideEnergyStorage2 source,
                                     final int maxAmount, final OperationMode mode) {
        return transferEnergy(destination, source, WideAmount.from(maxAmount), mode).intValue();
    }

    public static WideAmount transferEnergy(final IWideEnergyReceiver2 destination, final @Nullable Direction destinationDirection,
                                            final IWideEnergyProvider2 source, final @Nullable Direction sourceDirection,
                                            final WideAmount maxAmount, final OperationMode mode) {

        final EnergySystem sys = destination.getEnergySystem();

        return destination.receiveEnergy(sys, destinationDirection,
                source.extractEnergy(sys, sourceDirection, maxAmount, mode), mode);
    }

    public static int transferEnergy(final IWideEnergyReceiver2 destination, final @Nullable Direction destinationDirection,
                                     final IWideEnergyProvider2 source, final @Nullable Direction sourceDirection,
                                     final int maxAmount, final OperationMode mode) {
        return transferEnergy(destination, destinationDirection, source, sourceDirection, WideAmount.from(maxAmount), mode).intValue();
    }

    //region internals

    private EnergyHelper() {
    }

    //endregion
}
