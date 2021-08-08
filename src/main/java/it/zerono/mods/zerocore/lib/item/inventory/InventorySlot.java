/*
 *
 * InventorySlot.java
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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

@SuppressWarnings({"WeakerAccess"})
public class InventorySlot
        implements IInventorySlot {

    public InventorySlot(IItemHandler inventory, int slotIndex) {

        this._inventory = inventory;
        this._index = slotIndex;
    }

    //region IInventorySlot

    /**
     * Get this slot index in the parent inventory
     */
    @Override
    public int getIndex() {
        return this._index;
    }

    /**
     * Returns the ItemStack in the slot.
     * <p>
     * The result's stack size may be greater than the ItemStack max size.
     * <p>
     * If the result is EMPTY, then the slot is empty.
     * If the result is not EMPTY but the stack size is zero, then it represents
     * an empty slot that will only accept a specific ItemStack.
     * <p>
     * IMPORTANT: This ItemStack MUST NOT be modified. This method is not for
     * altering an inventories contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * <p>
     * SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK
     *
     * @return ItemStack in given slot.
     **/
    @Override
    public ItemStack getStackInSlot() {
        return this._inventory.getStackInSlot(this.getIndex());
    }

    /**
     * Replace the ItemStack in the slot.
     *
     * @param stack ItemStack to set the slot to (may be empty).
     */
    @Override
    public void setStackInSlot(final ItemStack stack) {

        if (this._inventory instanceof IItemHandlerModifiable) {
            ((IItemHandlerModifiable)this._inventory).setStackInSlot(this.getIndex(), stack);
        }
    }

    /**
     * Inserts an ItemStack into the slot and return the remainder.
     * The ItemStack should not be modified in this function!
     *
     * @param stack ItemStack to insert.
     * @param mode  How the operation is carried out.
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty stack).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    @Override
    public ItemStack insertStack(ItemStack stack, OperationMode mode) {
        return this._inventory.insertItem(this.getIndex(), stack, mode.simulate());
    }

    /**
     * Extracts an ItemStack from the slot. The returned value must be EMPTY if nothing is extracted,
     * otherwise it's stack size must not be greater than amount or the ItemStack getMaxStackSize().
     *
     * @param amount Amount to extract (may be greater than the current stacks max limit)
     * @param mode   How the operation is carried out.
     * @return ItemStack extracted from the slot, must be EMPTY, if nothing can be extracted
     **/
    @Override
    public ItemStack extractStack(int amount, OperationMode mode) {
        return this._inventory.extractItem(this.getIndex(), amount, mode.simulate());
    }

    /**
     * Check if the ItemStack can be added to the slot, even partially
     *
     * @param stack ItemStack to check
     * @return true if the ItemStack can be fully or partially added to the slot. False otherwise
     */
    @Override
    public boolean canInsertStack(final ItemStack stack) {

        final ItemStack remainder = this._inventory.insertItem(this.getIndex(), stack, true);

        return remainder.isEmpty() || remainder.getCount() < stack.getCount();
    }

    /**
     * Check if the given amount of items can be extracted from the slot
     *
     * @param amount amount to check
     * @return true if the given amount of items can be extracted from the slot. False otherwise
     */
    @Override
    public boolean canExtractStack(int amount) {

        final ItemStack remainder = this._inventory.extractItem(this.getIndex(), amount, true);

        return !remainder.isEmpty() && amount == remainder.getCount();
    }

    /**
     * Retrieves the maximum stack size allowed to exist in the slot.
     *
     * @param stack A stack to use to check for the stack limit. Can be EMPTY.
     * @return The maximum stack size allowed in the slot. If the stack parameter is not the EMPTY stack,
     * it's maximum size will be taken into account.
     */
    @Override
    public int getSlotLimit(ItemStack stack) {

        final ItemStack current = this.getStackInSlot();

        return current.isEmpty() ? stack.getMaxStackSize() : current.getMaxStackSize();
    }

    //region internals

    private final IItemHandler _inventory;
    private final int _index;

    //endregion
}
