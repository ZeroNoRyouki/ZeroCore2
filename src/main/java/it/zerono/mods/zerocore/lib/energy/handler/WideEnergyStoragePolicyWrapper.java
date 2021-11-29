/*
 *
 * WideEnergyProviderPolicyWrapper.java
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

package it.zerono.mods.zerocore.lib.energy.handler;

import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage2;

public class WideEnergyStoragePolicyWrapper {

    @Deprecated //use IWideEnergyStorage2
    public static IWideEnergyStorage inputOnly(final IWideEnergyStorage original) {

        return new WideEnergyStorageForwarder(original) {

            @Override
            public double extractEnergy(EnergySystem system, double maxAmount, boolean simulate) {
                return 0;
            }
        };
    }

    @Deprecated //use IWideEnergyStorage2
    public static IWideEnergyStorage outputOnly(final IWideEnergyStorage original) {
        return new WideEnergyStorageForwarder(original) {

            @Override
            public double insertEnergy(EnergySystem system, double maxAmount, boolean simulate) {
                return 0;
            }
        };
    }

    public static IWideEnergyStorage2 inputOnly(final IWideEnergyStorage2 original) {

        return new WideEnergyStorageForwarder2(original) {

            @Override
            public WideAmount extractEnergy(EnergySystem system, WideAmount maxAmount, OperationMode mode) {
                return WideAmount.ZERO;
            }
        };
    }

    public static IWideEnergyStorage2 outputOnly(final IWideEnergyStorage2 original) {
        return new WideEnergyStorageForwarder2(original) {

            @Override
            public WideAmount insertEnergy(EnergySystem system, WideAmount maxAmount, OperationMode mode) {
                return WideAmount.ZERO;
            }
        };
    }

    //region internals

    private WideEnergyStoragePolicyWrapper() {
    }

    //endregion
}
