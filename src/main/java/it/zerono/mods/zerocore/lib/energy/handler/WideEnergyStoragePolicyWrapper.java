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

import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage;

public class WideEnergyStoragePolicyWrapper {

    public static IWideEnergyStorage inputOnly(final IWideEnergyStorage original) {

        return new WideEnergyStorageForwarder(original) {

            @Override
            public double extractEnergy(EnergySystem system, double maxAmount, boolean simulate) {
                return 0;
            }
        };
    }

    public static IWideEnergyStorage outputOnly(final IWideEnergyStorage original) {
        return new WideEnergyStorageForwarder(original) {

            @Override
            public double insertEnergy(EnergySystem system, double maxAmount, boolean simulate) {
                return 0;
            }
        };
    }

    //region internals

    private WideEnergyStoragePolicyWrapper() {
    }

    //endregion
}
