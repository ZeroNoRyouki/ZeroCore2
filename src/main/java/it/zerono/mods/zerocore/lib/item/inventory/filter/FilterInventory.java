/*
 *
 * FilterInventory.java
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

package it.zerono.mods.zerocore.lib.item.inventory.filter;

import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * An inventory based Filter with ItemStack defined conditions
 *
 * Each inventory "slot" is represented by an ItemStackFilterCondition
 */
@SuppressWarnings({"WeakerAccess"})
public class FilterInventory extends Filter implements IItemHandlerModifiable {

    public FilterInventory(int size) {
        this._size = size;
    }

    //region IItemHandlerModifiable

    /**
     * <p>
     * This function re-implements the vanilla function IInventory#isItemValidForSlot(int, ItemStack).
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
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public ResourceLocation getComponentId() {
        return COMPONENT_ID;
    }

    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    @Override
    public int getSlots() {
        return this._size;
    }

    /**
     * Returns the filter ItemStack for the ItemStackFilterCondition in the given slot
     *
     * DO NOT MODIFY THIS ITEMSTACK
     *
     * @param slot Slot to query
     * @return the filter ItemStack for the ItemStackFilterCondition in the given slot
     **/
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.getFilterStack(slot)
                .map(ItemStackFilterCondition::getFilterStack)
                .orElse(ItemHelper.stackEmpty());
    }

    /**
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack should not be modified in this function!
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return null).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

        if (stack.isEmpty()) {
            return ItemHelper.stackEmpty();
        }

        this.validateSlotIndex(slot);

        final Optional<ItemStackFilterCondition> itemStackFilter = this.getFilterStack(slot);

        if (!itemStackFilter.isPresent()) {

            if (!simulate) {
                this.addFilterStack(slot, stack);
            }

            return ItemHelper.stackEmpty();
        }

        final ItemStack existing = itemStackFilter.get().getFilterStack();

        // check if the filter stack exist (this will always be a null check)
        if (null == existing) {

            // the filter is missing it's filter stack ...
            if (!simulate) {
                itemStackFilter.get().setFilterStack(stack);
            }

            return ItemHelper.stackEmpty();
        }

        int limit = stack.getMaxStackSize();

        if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
            return stack;
        }

        limit -= existing.getCount();

        if (limit <= 0) {
            return stack;
        }

        final boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            existing.grow(reachedLimit ? limit : stack.getCount());
        }

        return reachedLimit ? ItemHelper.stackFrom(stack, stack.getCount() - limit) : ItemHelper.stackEmpty();
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        if (amount == 0) {
            return ItemHelper.stackEmpty();
        }

        this.validateSlotIndex(slot);

        final Optional<ItemStackFilterCondition> itemStackFilter = this.getFilterStack(slot);

        if (!itemStackFilter.isPresent()) {
            return ItemHelper.stackEmpty();
        }

        final ItemStack existing = itemStackFilter.get().getFilterStack();

        if (existing == null) {
            return ItemHelper.stackEmpty();
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {

            if (!simulate) {
                // remove the filter completely
                this.removeFilterStack(slot);
            }

            return existing;

        } else {

            if (!simulate) {
                itemStackFilter.get().setFilterStack(existing, existing.getCount() - toExtract);
            }

            return ItemHelper.stackFrom(existing, toExtract);
        }
    }

    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be null)
     * @throws RuntimeException if the handler is called in a way that the handler
     * was not expecting.
     **/
    @Override
    public void setStackInSlot(int slot, ItemStack stack) {

        this.validateSlotIndex(slot);
        CodeHelper.optionalIfPresentOrElse(this.getFilterStack(slot),
                filter -> {

                    final ItemStack existing = filter.getFilterStack();

                    if (null == existing || !ItemStack.matches(existing, stack)) {
                        filter.setFilterStack(stack);
                    }
                },
                () -> this.addFilterStack(slot, stack));
    }

    /**
     * Retrieves the maximum stack size allowed to exist in the given slot.
     *
     * @param slot Slot to query.
     * @return The maximum stack size allowed in the slot.
     */
    @Override
    public int getSlotLimit(int slot) {

        final ItemStack stack = this.getStackInSlot(slot);

        return stack.isEmpty() ? 0 : stack.getMaxStackSize();
    }

    //region internals

    private boolean isSlotIndexValid(int slot) {
        return slot >= 0 && slot < this._size;
    }

    private void validateSlotIndex(int slot) {

        if (!this.isSlotIndexValid(slot)) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this._size + ")");
        }
    }

    private Optional<ItemStackFilterCondition> getFilterStack(int slot) {

        if (!this.isSlotIndexValid(slot)) {
            return Optional.empty();
        }

        return this.getCondition(String.valueOf(slot))
                .filter(c -> c instanceof ItemStackFilterCondition)
                .map(c -> (ItemStackFilterCondition) c);
    }

    private void addFilterStack(int slot, final ItemStack stack) {
        this.addCondition(String.valueOf(slot), new ItemStackFilterCondition(stack));
    }

    private void removeFilterStack(int slot) {
        this.removeCondition(String.valueOf(slot));
    }

    private int _size;

    private static final ResourceLocation COMPONENT_ID;

    static {

        COMPONENT_ID = ZeroCore.newID("inventory.filter.FilterInventory");

        final FilterManager<FilterInventory> fm = FilterManager.getInstance();

        fm.registerFactory(COMPONENT_ID, new IFilterComponentFactory<FilterInventory>() {

            @Override
            public Optional<FilterInventory> createComponent(@Nonnull ResourceLocation componentId) {
                return Optional.of(new FilterInventory(1));
            }

            @Override
            public Optional<FilterInventory> createComponent(@Nonnull ResourceLocation componentId, CompoundNBT nbt) {
                return Optional.empty();
            }
        });
    }
}
