/*
 *
 * IndexedFluidHandlerForwarder.java
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

package it.zerono.mods.zerocore.lib.fluid.handler;

import it.zerono.mods.zerocore.lib.data.stack.AllowedHandlerAction;
import it.zerono.mods.zerocore.lib.data.stack.IndexedStackContainer;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class IndexedFluidHandlerForwarder<Index extends Enum<Index>>
        implements IFluidHandler {

    public IndexedFluidHandlerForwarder(IndexedStackContainer<Index, Fluid, FluidStack> container, Index index) {
        this(container, index, AllowedHandlerAction.InsertExtract);
    }

    public IndexedFluidHandlerForwarder(IndexedStackContainer<Index, Fluid, FluidStack> container, Index index,
            AllowedHandlerAction allowedAction) {

        this._container = container;
        this._index = index;
        this._allowedActions = allowedAction;
    }

    public IndexedStackContainer<Index, Fluid, FluidStack> getContainer() {
        return this._container;
    }

    public Index getIndex() {
        return this._index;
    }

    public AllowedHandlerAction getAllowedActions() {
        return this._allowedActions;
    }

    //region IFluidHandler

    /**
     * Returns the number of fluid storage units ("tanks") available
     *
     * @return The number of tanks available
     */
    @Override
    public int getTanks() {
        return 1;
    }

    /**
     * Returns the FluidStack in a given tank.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
     * </p>
     *
     * @param tank Tank to query.
     * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
     */
    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.getContainer().getStackCopy(this.getIndex());
    }

    /**
     * Retrieves the maximum fluid amount for a given tank.
     *
     * @param tank Tank to query.
     * @return The maximum fluid amount held by the tank.
     */
    @Override
    public int getTankCapacity(int tank) {
        return this.getContainer().getCapacity();
    }

    /**
     * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
     * basically always return TRUE for this.
     *
     * @param tank  Tank to query for validity
     * @param stack Stack to test with for validity
     * @return TRUE if the tank can hold the FluidStack, not considering current state.
     * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
     */
    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return this.getContainer().isStackValidForIndex(this.getIndex(), stack);
    }

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param action   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    @Override
    public int fill(FluidStack resource, FluidAction action) {

        if (this.getAllowedActions().canInsert()) {
            return this.getContainer().insert(this.getIndex(), resource, OperationMode.from(action));
        } else {
            return 0;
        }
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {

        if (this.getAllowedActions().canExtract()) {
            return this.getContainer().extract(this.getIndex(), resource, OperationMode.from(action));
        } else {
            return FluidStack.EMPTY;
        }
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * <p/>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        if (this.getAllowedActions().canExtract()) {
            return this.getContainer().extract(this.getIndex(), maxDrain, OperationMode.from(action));
        } else {
            return FluidStack.EMPTY;
        }
    }

    //endregion
    //region internals

    private final IndexedStackContainer<Index, Fluid, FluidStack> _container;
    private final Index _index;
    private final AllowedHandlerAction _allowedActions;

    //endregion
}
