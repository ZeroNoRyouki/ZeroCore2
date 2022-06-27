/*
 *
 * FluidTank.java
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

package it.zerono.mods.zerocore.lib.fluid;

import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.AbstractStackHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.BiPredicate;

public class FluidTank
        extends AbstractStackHolder<FluidTank, FluidStack>
        implements IFluidHandler, IFluidTank, ISyncableEntity, IDebuggable {

    public FluidTank(final int capacity) {
        this._capacity = capacity;
    }

    public FluidTank(final int capacity, final BiPredicate<Integer, FluidStack> stackValidator) {

        super(stackValidator);
        this._capacity = capacity;
    }

    public void setContent(final FluidStack stack) {

        final boolean wasEmpty = this.isEmpty(0);
        final boolean isNowEmpty = stack.isEmpty();

        if (wasEmpty && isNowEmpty) {
            return;
        }

        this._content = stack;
        this.onChange(wasEmpty ? ChangeType.Added : (isNowEmpty ? ChangeType.Removed : ChangeType.Replaced), 0);
    }

    public FluidTank setCapacity(final int capacity) {

        this._capacity = Math.max(0, capacity);

        if (this._content.getAmount() > capacity) {
            this.setContent(new FluidStack(this._content, capacity));
        }

        return this;
    }

    public double getFluidAmountPercentage() {
        return (double)this.getFluidAmount() / this.getCapacity();
    }

    public boolean isEmpty() {
        return this.getFluid().isEmpty();
    }

    public int getFreeSpace() {
        return Math.max(0, this._capacity - this.getFluidAmount());
    }

    //region IFluidTank

    /**
     * @return FluidStack representing the fluid in the tank, null if the tank is empty.
     */
    @Nonnull
    @Override
    public FluidStack getFluid() {
        return this._content;
    }

    /**
     * @return Current amount of fluid in the tank.
     */
    @Override
    public int getFluidAmount() {
        return this.getFluid().getAmount();
    }

    /**
     * @return Capacity of this fluid tank.
     */
    @Override
    public int getCapacity() {
        return this._capacity;
    }

    /**
     * @param stack Fluidstack holding the Fluid to be queried.
     * @return If the tank can hold the fluid (EVER, not at the time of query).
     */
    @Override
    public boolean isFluidValid(final FluidStack stack) {
        return this.isStackValid(0, stack);
    }

    //endregion
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
    @Nonnull
    @Override
    public FluidStack getFluidInTank(final int tank) {
        return this.getFluid();
    }

    /**
     * Retrieves the maximum fluid amount for a given tank.
     *
     * @param tank Tank to query.
     * @return The maximum fluid amount held by the tank.
     */
    @Override
    public int getTankCapacity(final int tank) {
        return this.getCapacity();
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
    public boolean isFluidValid(final int tank, final FluidStack stack) {
        return this.isFluidValid(stack);
    }

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param action   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    @Override
    public int fill(final FluidStack resource, final FluidAction action) {

        if (resource.isEmpty() || !this.isFluidValid(resource)) {
            return 0;
        }

        if (action.simulate()) {

            if (this._content.isEmpty()) {
                return Math.min(this._capacity, resource.getAmount());
            }

            if (!this._content.isFluidEqual(resource)) {
                return 0;
            }

            return Math.min(this.getFreeSpace(), resource.getAmount());
        }

        if (this._content.isEmpty()) {

            this._content = new FluidStack(resource, Math.min(this._capacity, resource.getAmount()));
            this.onChange(ChangeType.Added, 0);
            return this._content.getAmount();
        }

        if (!this._content.isFluidEqual(resource)) {
            return 0;
        }

        int filled = this._capacity - this._content.getAmount();

        if (resource.getAmount() < filled) {

            this._content.grow(resource.getAmount());
            filled = resource.getAmount();

        } else {

            this._content.setAmount(this._capacity);
        }

        if (filled > 0) {
            this.onChange(ChangeType.Grown, 0);
        }

        return filled;
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
    public FluidStack drain(final FluidStack resource, final FluidAction action) {

        if (resource.isEmpty() || !resource.isFluidEqual(this._content)) {
            return FluidStack.EMPTY;
        }

        return this.drain(resource.getAmount(), action);
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
    public FluidStack drain(final int maxDrain, final FluidAction action) {

        final int drained = Math.min(this._content.getAmount(), maxDrain);
        final FluidStack stack = new FluidStack(this._content, drained);

        if (action.execute() && drained > 0) {

            this._content.shrink(drained);
            this.onChange(ChangeType.Shrunk, 0);
        }

        return stack;
    }

    //endregion
    //region IStackHolder

    @Override
    public boolean isEmpty(int index) {
        return this.getFluid().isEmpty();
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        if (data.contains("capacity")) {
            this.setCapacity(data.getInt("capacity"));
        }

        if (data.contains("content")) {
            this.setContent(FluidHelper.stackFrom(data.getCompound("content")));
        }

        this.onLoad();
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        data.putInt("capacity", this.getCapacity());
        data.put("content", FluidHelper.stackToNBT(this.getFluid()));
        return data;
    }

    //endregion
    //region IDebuggable

    /**
     * @param side     the LogicalSide of the caller
     * @param messages add your debug messages here
     */
    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        messages.addUnlocalized("Capacity: %d", this.getCapacity());
        messages.addUnlocalized(FluidHelper.toStringHelper(this._content));
    }

    //endregion
    //region internals

    protected FluidStack _content = FluidStack.EMPTY;
    protected int _capacity;

    //endregion
}
