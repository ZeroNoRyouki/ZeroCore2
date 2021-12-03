/*
 *
 * ItemStackHolder.java
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

package it.zerono.mods.zerocore.lib.item.inventory;

import it.zerono.mods.zerocore.lib.DebuggableHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.AbstractStackHolder;
import it.zerono.mods.zerocore.lib.data.stack.IStackHolderAccess;
import it.zerono.mods.zerocore.lib.data.stack.StackAdapters;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;

public class ItemStackHolder
        extends AbstractStackHolder<ItemStackHolder, ItemStack>
        implements IStackHolderAccess<ItemStackHolder, ItemStack>, IItemHandler, IItemHandlerModifiable,
                    INBTSerializable<CompoundTag>, ISyncableEntity, IDebuggable {

    public ItemStackHolder(final int size) {
        this(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    public ItemStackHolder(final int size, final BiPredicate<Integer, ItemStack> stackValidator) {
        this(NonNullList.withSize(size, ItemStack.EMPTY), stackValidator);
    }

    public ItemStackHolder(final NonNullList<ItemStack> stacks) {

        this._stacks = stacks;
        this.setMaxCapacity(this::getSlotMaxCapacityFromStack);
    }

    public ItemStackHolder(final NonNullList<ItemStack> stacks, final BiPredicate<Integer, ItemStack> stackValidator) {

        super(stackValidator);
        this._stacks = stacks;
        this.setMaxCapacity(this::getSlotMaxCapacityFromStack);
    }

    public void setSize(final int size) {
        this._stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    //region IStackHolder

    @Override
    public boolean isEmpty(final int index) {
        return this.getStackInSlot(index).isEmpty();
    }

    @Override
    public int getAmount(final int index) {
        return this.getStackInSlot(index).getCount();
    }

    //endregion
    //region IStackHolderAccess<ItemStackHolder, ItemStack>

    @Override
    public ItemStack getStackAt(final int index) {
        return this.getStackInSlot(index);
    }

    @Override
    public void setStackAt(final int index, final ItemStack stack) {
        this.setStackInSlot(index, stack);
    }

    //endregion
    //region IItemHandler

    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    @Override
    public int getSlots() {
        return this._stacks.size();
    }

    /**
     * Returns the ItemStack in a given slot.
     * <p>
     * The result's stack size may be greater than the itemstack's max size.
     * <p>
     * If the result is empty, then the slot is empty.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This ItemStack <em>MUST NOT</em> be modified. This method is not for
     * altering an inventory's contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * </p>
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK</em></strong>
     * </p>
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. Empty Itemstack if the slot is empty.
     **/
    @Nonnull
    @Override
    public ItemStack getStackInSlot(final int slot) {

        this.validateSlotIndex(slot);
        return this._stacks.get(slot);
    }

    /**
     * <p>
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack <em>should not</em> be modified in this function!
     * </p>
     * Note: This behaviour is subtly different from {@link net.minecraftforge.fluids.capability.IFluidHandler#fill(FluidStack, IFluidHandler.FluidAction)}  fill(FluidStack, boolean)}
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert. This must not be modified by the item handler.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     * The returned ItemStack can be safely modified after.
     **/
    @Nonnull
    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!this.isItemValid(slot, stack)) {
            return stack;
        }

        this.validateSlotIndex(slot);

        final ItemStack existing = this._stacks.get(slot);

        int limit = this.getStackLimit(slot, stack);

        if (!existing.isEmpty()) {

            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                return stack;
            }

            limit -= existing.getCount();
        }

        if (limit <= 0) {
            return stack;
        }

        final boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {

            if (existing.isEmpty()) {

                this._stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                this.onChange(ChangeType.Added, slot);

            } else {

                existing.grow(reachedLimit ? limit : stack.getCount());
                this.onChange(ChangeType.Grown, slot);
            }
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    /**
     * Extracts an ItemStack from the given slot.
     * <p>
     * The returned value must be empty if nothing is extracted,
     * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxStackSize()}.
     * </p>
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stack's max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
     * The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     **/
    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {

        if (0 == amount) {
            return ItemStack.EMPTY;
        }

        this.validateSlotIndex(slot);

        final ItemStack existing = this._stacks.get(slot);

        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {

            if (!simulate) {

                this._stacks.set(slot, ItemStack.EMPTY);
                this.onChange(ChangeType.Removed, slot);
                return existing;

            } else {
                return existing.copy();
            }

        } else {

            if (!simulate) {

                this._stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                this.onChange(ChangeType.Shrunk, slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    /**
     * Retrieves the maximum stack size allowed to exist in the given slot.
     *
     * @param slot Slot to query.
     * @return The maximum stack size allowed in the slot.
     */
    @Override
    public int getSlotLimit(final int slot) {
        return this.getMaxCapacity(slot);
    }

    /**
     * <p>
     * This function re-implements the vanilla function {@link net.minecraft.world.Container#canPlaceItem(int, ItemStack)}.
     * It should be used instead of simulated insertions in cases where the contents and state of the inventory are
     * irrelevant, mainly for the purpose of automation and logic (for instance, testing if a minecart can wait
     * to deposit its items into a full inventory, or if the items in the minecart can never be placed into the
     * inventory and should move on).
     * </p>
     * <ul>
     * <li>isItemValid is false when insertion of the item is never valid.</li>
     * <li>When isItemValid is true, no assumptions can be made and insertion must be simulated case-by-case.</li>
     * <li>The actual items in the inventory, its fullness, or any other state are <strong>not</strong> considered by isItemValid.</li>
     * </ul>
     *
     * @param slot  Slot to query for validity
     * @param stack Stack to test with for validity
     * @return true if the slot can insert the ItemStack, not considering the current state of the inventory.
     * false if the slot can never insert the ItemStack in any situation.
     */
    @Override
    public boolean isItemValid(final int slot, final ItemStack stack) {
        return this.isStackValid(slot, stack);
    }

    //endregion
    //region IItemHandlerModifiable

    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be empty).
     * @throws RuntimeException if the handler is called in a way that the handler
     *                          was not expecting.
     **/
    @Override
    public void setStackInSlot(final int slot, final ItemStack stack) {

        this.validateSlotIndex(slot);

        final boolean wasEmpty = this.isEmpty(slot);
        final boolean isNowEmpty = stack.isEmpty();

        if (wasEmpty && isNowEmpty) {
            return;
        }

        this._stacks.set(slot, stack);
        this.onChange(wasEmpty ? ChangeType.Added : (isNowEmpty ? ChangeType.Removed : ChangeType.Replaced), slot);
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
        this.syncFrom(data, StackAdapters.ITEMSTACK, size -> {

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
        return this.syncTo(data, this._stacks, StackAdapters.ITEMSTACK);
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

    protected int getStackLimit(final int slot, final ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    private int getSlotMaxCapacityFromStack(final int slot) {
        return this.getStackAt(slot).getMaxStackSize();
    }

    protected NonNullList<ItemStack> _stacks;

    //endregion
}
