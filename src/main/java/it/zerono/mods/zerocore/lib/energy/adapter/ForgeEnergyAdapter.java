/*
 *
 * ForgeEnergyAdapter.java
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

package it.zerono.mods.zerocore.lib.energy.adapter;

import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyProvider;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyReceiver;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public final class ForgeEnergyAdapter {

    public static IEnergyStorage wrap(final IWideEnergyReceiver receiver) {
        return new IEnergyStorage() {

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return (int)receiver.receiveEnergy(EnergySystem.ForgeEnergy, null, maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return (int)receiver.getEnergyStored(EnergySystem.ForgeEnergy, null);
            }

            @Override
            public int getMaxEnergyStored() {
                return (int)receiver.getCapacity(EnergySystem.ForgeEnergy, null);
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }

    public static IEnergyStorage wrap(final IWideEnergyProvider provider) {
        return new IEnergyStorage() {

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return 0;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return (int)provider.extractEnergy(EnergySystem.ForgeEnergy, null, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return (int)provider.getEnergyStored(EnergySystem.ForgeEnergy, null);
            }

            @Override
            public int getMaxEnergyStored() {
                return (int)provider.getCapacity(EnergySystem.ForgeEnergy, null);
            }

            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public boolean canReceive() {
                return false;
            }
        };
    }

    public static IEnergyStorage wrap(final IWideEnergyStorage storage) {
        return new IEnergyStorage() {

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return (int)storage.insertEnergy(EnergySystem.ForgeEnergy, maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return (int)storage.extractEnergy(EnergySystem.ForgeEnergy, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return (int)storage.getEnergyStored(EnergySystem.ForgeEnergy);
            }

            @Override
            public int getMaxEnergyStored() {
                return (int)storage.getCapacity(EnergySystem.ForgeEnergy);
            }

            @Override
            public boolean canExtract() {
                return storage.canExtract();
            }

            @Override
            public boolean canReceive() {
                return storage.canInsert();
            }
        };
    }

    //region internals

    private ForgeEnergyAdapter() {
    }

    //endregion
}
