/*
 *
 * NullEnergyHandlers.java
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

import javax.annotation.Nullable;

@SuppressWarnings({"WeakerAccess"})
public final class NullEnergyHandlers {

    public static final IWideEnergyHandler HANDLER = new IWideEnergyHandler() {

        @Override
        public double getEnergyStored(EnergySystem system, @Nullable Direction from) {
            return 0;
        }

        @Override
        public double getCapacity(EnergySystem system, @Nullable Direction from) {
            return 0;
        }

        @Override
        public EnergySystem getEnergySystem() {
            return EnergySystem.REFERENCE;
        }

        @Override
        public boolean canConnectEnergy(EnergySystem system, @Nullable Direction from) {
            return false;
        }
    };

    public static final IWideEnergyProvider PROVIDER = new IWideEnergyProvider() {

        @Override
        public double extractEnergy(EnergySystem system, @Nullable Direction from, double maxAmount, boolean simulate) {
            return 0;
        }

        @Override
        public double getEnergyStored(EnergySystem system, @Nullable Direction from) {
            return HANDLER.getEnergyStored(system, from);
        }

        @Override
        public double getCapacity(EnergySystem system, @Nullable Direction from) {
            return HANDLER.getCapacity(system, from);
        }

        @Override
        public EnergySystem getEnergySystem() {
            return HANDLER.getEnergySystem();
        }

        @Override
        public boolean canConnectEnergy(EnergySystem system, @Nullable Direction from) {
            return HANDLER.canConnectEnergy(system, from);
        }
    };

    public static final IWideEnergyReceiver RECEIVER = new IWideEnergyReceiver() {

        @Override
        public double receiveEnergy(EnergySystem system, @Nullable Direction from, double maxAmount, boolean simulate) {
            return 0;
        }

        @Override
        public double getEnergyStored(EnergySystem system, @Nullable Direction from) {
            return HANDLER.getEnergyStored(system, from);
        }

        @Override
        public double getCapacity(EnergySystem system, @Nullable Direction from) {
            return HANDLER.getCapacity(system, from);
        }

        @Override
        public EnergySystem getEnergySystem() {
            return HANDLER.getEnergySystem();
        }

        @Override
        public boolean canConnectEnergy(EnergySystem system, @Nullable Direction from) {
            return HANDLER.canConnectEnergy(system, from);
        }
    };

    public static final IWideEnergyStorage STORAGE = new IWideEnergyStorage() {

        @Override
        public EnergySystem getEnergySystem() {
            return EnergySystem.REFERENCE;
        }

        @Override
        public double insertEnergy(EnergySystem system, double maxAmount, boolean simulate) {
            return 0;
        }

        @Override
        public double extractEnergy(EnergySystem system, double maxAmount, boolean simulate) {
            return 0;
        }

        @Override
        public double getEnergyStored(EnergySystem system) {
            return 0;
        }

        @Override
        public double getCapacity(EnergySystem system) {
            return 0;
        }
    };

    //region internals

    private NullEnergyHandlers() {
    }
}
