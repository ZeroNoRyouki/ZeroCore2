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
import it.zerono.mods.zerocore.base.multiblock.part.io.IOPortBlockCapabilitySource;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.fluid.handler.FluidHandlerForwarder;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class FluidPortHandlerForge<Controller extends AbstractCuboidMultiblockController<Controller>,
            Port extends AbstractMultiblockEntity<Controller> & IFluidPort>
        extends AbstractFluidPortHandler<Controller, Port>
        implements IFluidHandler {

    public FluidPortHandlerForge(final Port port, final IoMode mode) {

        super(port, mode);
        this._capabilityForwarder = new FluidHandlerForwarder(EmptyFluidHandler.INSTANCE);
        this._remoteCapabilitySource = new IOPortBlockCapabilitySource<>(port, Capabilities.FluidHandler.BLOCK);
    }

    //region IFluidPortHandler

    @Override
    public boolean isConnected() {
        return null != this._remoteCapabilitySource.getCapability();
    }

    @Override
    public void onPortChanged() {
        this._remoteCapabilitySource.onPortChanged();
    }

    /**
     * If this is an Active Fluid Port in output mode, send fluid to the connected consumer (if there is one)
     *
     * @param stack FluidStack representing the Fluid and maximum amount of fluid to be sent out.
     * @return the amount of fluid accepted by the consumer
     */
    @Override
    public int outputFluid(final FluidStack stack) {

        final var consumer = this._remoteCapabilitySource.getCapability();

        if (null == consumer || this.isPassive() || this.getIoEntity().getIoDirection().isInput()) {
            return 0;
        }

        return consumer.fill(stack, FluidAction.EXECUTE);
    }

    /**
     * If this is an Active Fluid Port in input mode, try to get fluids from the connected consumer (if there is one)
     */
    @Override
    public int inputFluid(final IFluidHandler destination, final int maxAmount) {

        final var consumer = this._remoteCapabilitySource.getCapability();

        if (null == consumer || this.isPassive() || this.getIoEntity().getIoDirection().isOutput()) {
            return 0;
        }

        final FluidStack transferred = FluidUtil.tryFluidTransfer(destination, consumer, maxAmount, true);

        return transferred.isEmpty() ? 0 : transferred.getAmount();
    }

    @Override
    public void update(Function<@NotNull IoDirection, IFluidHandler> handlerProvider) {
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

    private final FluidHandlerForwarder _capabilityForwarder;
    private final IOPortBlockCapabilitySource<Controller, Port, IFluidHandler> _remoteCapabilitySource;

    //endregion
}
