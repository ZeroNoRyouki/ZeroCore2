/*
 *
 * PowerPortHandlerForgeEnergy.java
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
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class PowerPortHandlerForgeEnergy<Controller extends AbstractCuboidMultiblockController<Controller>,
            T extends AbstractMultiblockEntity<Controller> & IPowerPort>
        extends AbstractPowerPortHandler<Controller, T>
        implements IEnergyStorage {

    public PowerPortHandlerForgeEnergy(final T part, final IoMode mode) {

        super(EnergySystem.ForgeEnergy, part, mode);
        this._consumer = null;
        this._capability = LazyOptional.of(() -> this);
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

        if (null == this._consumer || !this.isOutput() || this.isPassive()) {
            return WideAmount.ZERO;
        }

        final int maxUnits = Math.min(amount.intValue(), Integer.MAX_VALUE);

        return WideAmount.asImmutable(this._consumer.receiveEnergy(maxUnits, false));
    }

    /**
     * @return true if there is a consumer connected, false otherwise
     */
    public boolean isConnected() {
        return null != this._consumer;
    }

    /**
     * Check for connections
     *
     * @param world    the IPowerPort world
     * @param position the IPowerPort position
     */
    public void checkConnections(@Nullable Level world, BlockPos position) {
        this._consumer = this.lookupConsumer(world, position, CAPAP_FORGE_ENERGYSTORAGE, 
                te -> te instanceof IPowerPortHandler, this._consumer);
    }

    @Override
    public void invalidate() {
        this._capability.invalidate();
    }

    /**
     * Get the requested capability if supported
     *
     * @param capability the capability
     * @param direction  the direction the request is coming from
     * @param <C>        the type of the capability
     * @return the capability (if supported) or null (if not)
     */
    @Override
    @Nullable
    public <C> LazyOptional<C> getCapability(final Capability<C> capability, final @Nullable Direction direction) {

        if (CAPAP_FORGE_ENERGYSTORAGE == capability) {
            return this._capability.cast();
        }

        return null;
    }

    //endregion
    //region IEnergyStorage

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive Maximum amount of energy to be inserted.
     * @param simulate   If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        return this.canReceive() ? this.getEnergyStorage().insertEnergy(this.getEnergySystem(),
                this.maxTransferRate(maxReceive), OperationMode.from(simulate)).intValue() : 0;
    }

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        return this.canExtract() ? this.getEnergyStorage().extractEnergy(this.getEnergySystem(),
                this.maxTransferRate(maxExtract), OperationMode.from(simulate)).intValue() : 0;
    }

    /**
     * Returns the amount of energy currently stored.
     */
    @Override
    public int getEnergyStored() {
        return this.getEnergyStorage().getEnergyStored(this.getEnergySystem()).intValue();
    }

    /**
     * Returns the maximum amount of energy that can be stored.
     */
    @Override
    public int getMaxEnergyStored() {
        return this.getEnergyStorage().getCapacity(this.getEnergySystem()).intValue();
    }

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    @Override
    public boolean canExtract() {
        return this.isOutput() && this.isPassive();
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    @Override
    public boolean canReceive() {
        return this.isInput() && this.isPassive();
    }

    //endregion
    //region internals

    @SuppressWarnings("FieldMayBeFinal")
    private static Capability<IEnergyStorage> CAPAP_FORGE_ENERGYSTORAGE = CapabilityManager.get(new CapabilityToken<>(){});

    private IEnergyStorage _consumer;
    private final LazyOptional<IEnergyStorage> _capability;

    //endregion
}
