/*
 *
 * FluidPortHandlerForge.java
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

package it.zerono.mods.zerocore.base.multiblock.part.io.fluid;

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.fluid.handler.FluidHandlerForwarder;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidPortHandlerForge<Controller extends AbstractCuboidMultiblockController<Controller>,
            T extends AbstractMultiblockEntity<Controller> & IFluidPort>
        extends AbstractFluidPortHandler<Controller, T>
        implements IFluidHandler {

    public FluidPortHandlerForge(final T part, final IoMode mode) {

        super(part, mode);
        this._capability = LazyOptional.of(() -> this);
        this._capabilityForwarder = new FluidHandlerForwarder(EmptyFluidHandler.INSTANCE);
        this._consumer = null;
    }

    //region IFluidPortHandler

    /**
     * If this is an Active Fluid Port in output mode, send fluid to the connected consumer (if there is one)
     *
     * @param stack FluidStack representing the Fluid and maximum amount of fluid to be sent out.
     * @return the amount of fluid accepted by the consumer
     */
    @Override
    public int outputFluid(final FluidStack stack) {

        if (null == this._consumer || this.isPassive() || this.getIoEntity().getIoDirection().isInput()) {
            return 0;
        }

        return this._consumer.fill(stack, FluidAction.EXECUTE);
    }

    /**
     * If this is an Active Fluid Port in input mode, try to get fluids from the connected consumer (if there is one)
     */
    @Override
    public int inputFluid(final IFluidHandler destination, final int maxAmount) {

        if (null == this._consumer || this.isPassive() || this.getIoEntity().getIoDirection().isOutput()) {
            return 0;
        }

        final FluidStack transferred = FluidUtil.tryFluidTransfer(destination, this._consumer, maxAmount, true);

        return transferred.isEmpty() ? 0 : transferred.getAmount();
    }

    /**
     * @return true if this handler is connected to one of it's allowed consumers, false otherwise
     */
    @Override
    public boolean isConnected() {
        return null != this._consumer;
    }

    /**
     * Check for connections
     *
     * @param world    the handler world
     * @param position the handler position
     */
    @Override
    public void checkConnections(@Nullable final Level world, final BlockPos position) {
        this._consumer = this.lookupConsumer(world, position, CAPAP_FORGE_FLUIDHANDLER,
                te -> te instanceof IFluidPortHandler, this._consumer);
    }

    /**
     * Get the requested capability, if supported
     *
     * @param capability the capability
     * @param direction  the direction the request is coming from
     * @return the capability (if supported) or null (if not)
     */
    @Nullable
    @Override
    public <C> LazyOptional<C> getCapability(final Capability<C> capability, final @Nullable Direction direction) {

        if (CAPAP_FORGE_FLUIDHANDLER == capability) {
            return this._capability.cast();
        }

        return null;
    }

    @Override
    public void invalidate() {
        this._capability.invalidate();
    }

    @Override
    public void update(NonNullFunction<IoDirection, IFluidHandler> handlerProvider) {
        this._capabilityForwarder.setHandler(handlerProvider.apply(this.getIoEntity().getIoDirection()));
    }

    //endregion
    //region IFluidHandler

    @Override
    public int getTanks() {
        return this._capabilityForwarder.getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return this._capabilityForwarder.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this._capabilityForwarder.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return this._capabilityForwarder.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return this.isPassive() ? this._capabilityForwarder.fill(resource, action) : 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return this.isPassive() ? this._capabilityForwarder.drain(resource, action) : FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return this.isPassive() ? this._capabilityForwarder.drain(maxDrain, action) : FluidStack.EMPTY;
    }

    //endregion
    //region internals

    @SuppressWarnings("FieldMayBeFinal")
    public static Capability<IFluidHandler> CAPAP_FORGE_FLUIDHANDLER = CapabilityManager.get(new CapabilityToken<>(){});

    private IFluidHandler _consumer;
    private final FluidHandlerForwarder _capabilityForwarder;
    private final LazyOptional<IFluidHandler> _capability;

    //endregion
}
