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

import it.zerono.mods.zerocore.lib.data.WideAmount;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

@SuppressWarnings({"WeakerAccess"})
public final class NullEnergyHandlers {

    public static final IWideEnergyHandler2 WIDE_HANDLER = new IWideEnergyHandler2() {

        @Override
        public EnergySystem getEnergySystem() {
            return EnergySystem.REFERENCE;
        }

        @Override
        public boolean canConnectEnergy(EnergySystem system, @Nullable Direction from) {
            return false;
        }

        @Override
        public WideAmount getEnergyStored(EnergySystem system, @Nullable Direction from) {
            return WideAmount.ZERO;
        }

        @Override
        public WideAmount getCapacity(EnergySystem system, @Nullable Direction from) {
            return WideAmount.ZERO;
        }
    };

    public static final IWideEnergyProvider2 WIDE_PROVIDER = new IWideEnergyProvider2() {

        @Override
        public EnergySystem getEnergySystem() {
            return EnergySystem.REFERENCE;
        }

        @Override
        public boolean canConnectEnergy(EnergySystem system, @Nullable Direction from) {
            return false;
        }

        @Override
        public WideAmount getEnergyStored(EnergySystem system, @Nullable Direction from) {
            return WideAmount.ZERO;
        }

        @Override
        public WideAmount getCapacity(EnergySystem system, @Nullable Direction from) {
            return WideAmount.ZERO;
        }

        @Override
        public WideAmount extractEnergy(EnergySystem system, @Nullable Direction from, WideAmount maxAmount, boolean simulate) {
            return WideAmount.ZERO;
        }
    };

    public static final IWideEnergyReceiver2 WIDE_RECEIVER = new IWideEnergyReceiver2() {

        @Override
        public EnergySystem getEnergySystem() {
            return EnergySystem.REFERENCE;
        }

        @Override
        public boolean canConnectEnergy(EnergySystem system, @Nullable Direction from) {
            return false;
        }

        @Override
        public WideAmount getEnergyStored(EnergySystem system, @Nullable Direction from) {
            return WideAmount.ZERO;
        }

        @Override
        public WideAmount getCapacity(EnergySystem system, @Nullable Direction from) {
            return WideAmount.ZERO;
        }

        @Override
        public WideAmount receiveEnergy(EnergySystem system, @Nullable Direction from, WideAmount maxAmount, boolean simulate) {
            return WideAmount.ZERO;
        }
    };

    public static final IWideEnergyStorage2 WIDE_STORAGE = new IWideEnergyStorage2() {

        @Override
        public EnergySystem getEnergySystem() {
            return EnergySystem.REFERENCE;
        }

        @Override
        public WideAmount insertEnergy(EnergySystem system, WideAmount maxAmount, boolean simulate) {
            return WideAmount.ZERO;
        }

        @Override
        public WideAmount extractEnergy(EnergySystem system, WideAmount maxAmount, boolean simulate) {
            return WideAmount.ZERO;
        }

        @Override
        public WideAmount getEnergyStored(EnergySystem system) {
            return WideAmount.ZERO;
        }

        @Override
        public WideAmount getCapacity(EnergySystem system) {
            return WideAmount.ZERO;
        }
    };

    @Deprecated // use WIDE_HANDLER
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

    @Deprecated // use WIDE_PROVIDER
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

    @Deprecated // use WIDE_RECEIVER
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

    @Deprecated // use WIDE_STORAGE
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

    //endregion
}
