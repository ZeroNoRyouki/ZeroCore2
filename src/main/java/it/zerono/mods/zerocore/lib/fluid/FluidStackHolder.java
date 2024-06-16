/*
 *
 * FluidStackHolder.java
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

import it.zerono.mods.zerocore.lib.DebuggableHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.AbstractStackHolder;
import it.zerono.mods.zerocore.lib.data.stack.IStackHolderAccess;
import it.zerono.mods.zerocore.lib.data.stack.StackAdapters;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public class FluidStackHolder
        extends AbstractStackHolder<FluidStackHolder, FluidStack>
        implements IStackHolderAccess<FluidStackHolder, FluidStack>, IFluidHandler, INBTSerializable<CompoundTag>,
                    ISyncableEntity, IDebuggable {

    public FluidStackHolder(final int size) {
        this(NonNullList.withSize(size, FluidStack.EMPTY));
    }

    public FluidStackHolder(final int size, final BiPredicate<Integer, FluidStack> stackValidator) {
        this(NonNullList.withSize(size, FluidStack.EMPTY), stackValidator);
    }

    public FluidStackHolder(final NonNullList<FluidStack> stacks) {

        this._stacks = stacks;
        this.setMaxCapacity(Integer.MAX_VALUE);
    }

    public FluidStackHolder(final NonNullList<FluidStack> stacks, final BiPredicate<Integer, FluidStack> stackValidator) {

        super(stackValidator);
        this._stacks = stacks;
        this.setMaxCapacity(Integer.MAX_VALUE);
    }

    public void setSize(final int size) {
        this._stacks = NonNullList.withSize(size, FluidStack.EMPTY);
    }

    //region IStackHolder

    @Override
    public boolean isEmpty(final int index) {
        return this.getFluidInTank(index).isEmpty();
    }

    @Override
    public int getAmount(final int index) {
        return this.getFluidInTank(index).getAmount();
    }

    //endregion
    //region IStackHolderAccess<FluidStackHolder, FluidStack>

    @Override
    public FluidStack getStackAt(final int index) {
        return this.getFluidInTank(index);
    }

    @Override
    public void setStackAt(final int index, final FluidStack stack) {

        this.validateSlotIndex(index);
        this._stacks.set(index, stack);
    }

    //endregion
    //region IFluidHandler

    @Override
    public int getTanks() {
        return this._stacks.size();
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(final int tank) {

        this.validateSlotIndex(tank);
        return this._stacks.get(tank);
    }

    @Override
    public int getTankCapacity(final int tank) {

        this.validateSlotIndex(tank);
        return this.getMaxCapacity(tank);
    }

    @Override
    public boolean isFluidValid(final int tank, final FluidStack stack) {

        this.validateSlotIndex(tank);
        return this.isStackValid(tank, stack);
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

        if (resource.isEmpty()) {
            return 0;
        }

        final int tanks = this.getTanks();
        int filled = 0, resourceAmount = resource.getAmount(), use;

        for (int tank = 0; tank < tanks && resourceAmount > 0; ++tank) {
            if (this.isFluidValid(tank, resource)) {

                final FluidStack content = this.getFluidInTank(tank);

                if (content.isEmpty() || content.isFluidEqual(resource)) {

                    use = Math.min(this.getFreeSpace(tank), resourceAmount);
                    filled += use;
                    resourceAmount -= use;

                    if (use > 0 && action.execute()) {

                        if (content.isEmpty()) {

                            this._stacks.set(tank, FluidHelper.stackFrom(resource, use));
                            this.onChange(ChangeType.Added, tank);

                        } else if (content.isFluidEqual(resource)) {

                            content.grow(use);
                            this.onChange(ChangeType.Grown, tank);
                        }
                    }
                }
            }
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
    @NotNull
    @Override
    public FluidStack drain(final FluidStack resource, final FluidAction action) {

        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        final int tanks = this.getTanks();
        int drained = 0, maxDrainAmount = resource.getAmount(), use;

        for (int tank = 0; tank < tanks && maxDrainAmount > 0; ++tank) {

            final FluidStack content = this.getFluidInTank(tank);

            if (!content.isEmpty() && content.isFluidEqual(resource)) {

                use = Math.min(content.getAmount(), maxDrainAmount);
                drained += use;
                maxDrainAmount -= use;

                if (action.execute()) {

                    if (use == content.getAmount()) {

                        this._stacks.set(tank, FluidStack.EMPTY);
                        this.onChange(ChangeType.Removed, tank);

                    } else {

                        content.shrink(use);
                        this.onChange(ChangeType.Shrunk, tank);
                    }
                }
            }
        }

        return FluidHelper.stackFrom(resource, drained);
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
    @NotNull
    @Override
    public FluidStack drain(final int maxDrain, final FluidAction action) {

        if (maxDrain >= 0) {

            // drain the fluid present in the first non-empty tank

            final int tanks = this.getTanks();

            for (int tank = 0; tank < tanks; ++tank) {

                final FluidStack content = this.getFluidInTank(tank);

                if (!content.isEmpty()) {
                    return this.drain(FluidHelper.stackFrom(content, maxDrain), action);
                }
            }
        }

        return FluidStack.EMPTY;
    }

    //endregion
    //region INBTSerializable<CompoundTag>

    @Override
    public CompoundTag serializeNBT() {
        return this.syncDataTo(new CompoundTag(), SyncReason.FullSync);
    }

    @Override
    public void deserializeNBT(final CompoundTag nbt) {
        this.syncDataFrom(nbt, SyncReason.FullSync);
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
    public void syncDataFrom(final CompoundTag data, final SyncReason syncReason) {
        this.syncFrom(data, StackAdapters.FLUIDSTACK, size -> {

            if (size > 0) {
                this.setSize(size);
            }

            return this._stacks;
        });
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    @Override
    public CompoundTag syncDataTo(final CompoundTag data, final SyncReason syncReason) {
        return this.syncTo(data, this._stacks, StackAdapters.FLUIDSTACK);
    }

    //endregion
    //region IDebuggable

    /**
     * @param side     the LogicalSide of the caller
     * @param messages add your debug messages here
     */
    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {
        DebuggableHelper.getDebugMessagesFor(messages, this);
    }

    //endregion
    //region internals

    protected void validateSlotIndex(final int slot) {

        if (slot < 0 || slot >= this._stacks.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this._stacks.size() + ")");
        }
    }

    protected NonNullList<FluidStack> _stacks;

    //endregion
}
