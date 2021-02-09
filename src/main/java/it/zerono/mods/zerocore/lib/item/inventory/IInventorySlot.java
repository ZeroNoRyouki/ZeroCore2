/*
 *
 * IInventorySlot.java
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

import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public interface IInventorySlot {

    /**
     Get this slot index in the parent inventory
     */
    int getIndex();

    /**
     * Returns the ItemStack in the slot.
     *
     * The result's stack size may be greater than the ItemStack max size.
     *
     * If the result is EMPTY, then the slot is empty.
     * If the result is not EMPTY but the stack size is zero, then it represents
     * an empty slot that will only accept a specific ItemStack.
     *
     * IMPORTANT: This ItemStack MUST NOT be modified. This method is not for
     * altering an inventories contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     *
     * SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK
     *
     * @return ItemStack in given slot.
     **/
    ItemStack getStackInSlot();

    /**
     * Replace the ItemStack in the slot.
     *
     * @param stack ItemStack to set the slot to (may be empty).
     */
    void setStackInSlot(ItemStack stack);

    /**
     * Replace the ItemStack in the slot with the EMPTY ItemStack.
     */
    default void setToEmpty() {
        this.setStackInSlot(ItemStack.EMPTY);
    }

    /**
     * Check if the slot is empty.
     *
     * @return true if the slot is empty, false otherwise.
     */
    default boolean isSlotEmpty() {
        return this.getStackInSlot().isEmpty();
    }

    /**
     * Returns the size of the ItemStack in the slot.
     *
     * @return The size of the stored stack, or zero is the stack is empty.
     */
    default int getStackSize() {
        return this.getStackInSlot().getCount();
    }

    /**
     * Modify the size of the ItemStack in the slot.
     *
     * If there is a stack in the slot, set the size of it to the given amount, capping it at the item's max stack size
     * and the limit of the slot. If the amount is less than or equal to zero, then this instead sets the stack to EMPTY.
     *
     * @param amount The desired size to set the stack to.
     * @param mode How the operation is carried out.
     *
     * @return Actual size the stack was set to.
     */
    default int setStackSize(int amount, final OperationMode mode) {

        if (this.isSlotEmpty()) {

            return 0;

        } else if (amount <= 0) {

            if (mode.execute()) {
                this.setToEmpty();
            }

            return 0;
        }

        final ItemStack stack = this.getStackInSlot();
        final int maxStackSize = this.getSlotLimit(stack);

        if (amount > maxStackSize) {
            amount = maxStackSize;
        }

        if (stack.getCount() == amount || mode.simulate()) {
            return amount;
        }

        final ItemStack newStack = stack.copy();

        newStack.setCount(amount);
        this.setStackInSlot(newStack);

        return amount;
    }

    /**
     * Increase the size of the ItemStack in the slot.
     *
     * If there is an ItemStack stored in this slot, increase its size by the given amount, capping it at the item's max
     * stack size and the limit of the slot. If the stack shrinks to an amount of less than or equal to zero, then this
     * instead sets the stack to EMPTY.
     *
     * @param amount The desired amount to increase the stack size by.
     * @param mode How the operation is carried out.
     *
     * @return Actual amount the stack was increased.
     */
    default int increaseStackSize(int amount, final OperationMode mode) {

        if (amount > 0) {
            amount = Math.min(amount, this.getSlotLimit(this.getStackInSlot()));
        }

        final int currentSize = this.getStackSize();

        return this.setStackSize(currentSize + amount, mode) - currentSize;
    }

    /**
     * Decrease the size of the ItemStack in the slot.
     *
     * If there is an ItemStack stored in this slot, decrease its size by the given amount. If this causes its size to
     * become less than or equal to zero, then the stack is set to EMPTY. If this method is used to grow the
     * stack the size gets capped at the item's max stack size and the limit of this slot.
     *
     * @param amount The desired amount to shrink the stack by.
     * @param mode How the operation is carried out.
     *
     * @return Actual amount the stack was decreased.
     */
    default int decreaseStackSize(final int amount, final OperationMode mode) {
        return -this.increaseStackSize(-amount, mode);
    }

    /**
     * Inserts an ItemStack into the slot and return the remainder.
     * The ItemStack should not be modified in this function!
     *
     * @param stack ItemStack to insert.
     * @param mode How the operation is carried out.
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an EMPTY stack).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    default ItemStack insertStack(final ItemStack stack, final OperationMode mode) {

        if (stack.isEmpty() || this.canInsertStack(stack)) {
            return stack;
        }

        final int needed = this.getSlotLimit(stack) - this.getStackSize();

        if (needed <= 0) {
            return stack;
        }

        boolean typeMatch = false;

        if (this.isSlotEmpty() || (typeMatch = ItemHandlerHelper.canItemStacksStack(this.getStackInSlot(), stack))) {

            int toAdd = Math.min(stack.getCount(), needed);

            if (mode.execute()) {

                if (typeMatch) {

                    this.increaseStackSize(toAdd, mode);

                } else {

                    final ItemStack toSet = stack.copy();

                    toSet.setCount(toAdd);
                    this.setStackInSlot(toSet);
                }
            }

            final ItemStack remainder = stack.copy();

            remainder.setCount(stack.getCount() - toAdd);
            return remainder;
        }

        return stack;
    }

    /**
     * Extracts an ItemStack from the slot. The returned value must be EMPTY if nothing is extracted,
     * otherwise it's stack size must not be greater than amount or the ItemStack getMaxStackSize().
     *
     * @param amount Amount to extract (may be greater than the current stacks max limit)
     * @param mode How the operation is carried out.
     * @return ItemStack extracted from the slot, must be EMPTY, if nothing can be extracted
     **/
    default ItemStack extractStack(int amount, final OperationMode mode) {

        if (this.isSlotEmpty() || amount < 1) {
            return ItemStack.EMPTY;
        }

        final ItemStack current = this.getStackInSlot();
        final int currentAmount = Math.min(getStackSize(), current.getMaxStackSize());

        if (currentAmount < amount) {
            amount = currentAmount;
        }

        final ItemStack result = current.copy();

        result.setCount(amount);

        if (mode.execute()) {
            this.decreaseStackSize(amount, mode);
        }

        return result;
    }

    /**
     * Check if the ItemStack can be added to the slot, even partially
     *
     * @param stack ItemStack to check
     * @return true if the ItemStack can be fully or partially added to the slot. False otherwise
     */
    boolean canInsertStack(ItemStack stack);

    /**
     * Check if the given amount of items can be extracted from the slot
     *
     * @param amount amount to check
     * @return true if the given amount of items can be extracted from the slot. False otherwise
     */
    boolean canExtractStack(int amount);

    /**
     * Retrieves the maximum stack size allowed to exist in the slot.
     *
     * @param stack A stack to use to check for the stack limit. Can be EMPTY.
     * @return The maximum stack size allowed in the slot. If the stack parameter is not the EMPTY stack,
     * it's maximum size will be taken into account.
     */
    int getSlotLimit(ItemStack stack);

    /**
     * Return the free space available in the slot.
     *
     * @param stack A stack to use to check for the stack limit. Can be EMPTY.
     * @return The free space in the slot. If the stack parameter is not the EMPTY stack,
     * it's maximum size will be taken into account.
     */
    default int getSlotFreeSpace(final ItemStack stack) {
        return this.isSlotEmpty() ? this.getSlotLimit(stack) : this.getSlotLimit(stack) - this.getStackInSlot().getCount();
    }
}
