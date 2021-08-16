/*
 *
 * SlotGeneric.java
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

package it.zerono.mods.zerocore.lib.item.inventory.container.slot.type;

import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.item.inventory.IInventorySlot;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.ISlotNotify;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.function.BiFunction;

public class SlotGeneric
        extends SlotItemHandler
        implements IInventorySlot {

    public SlotGeneric(final IItemHandler itemHandler, final SlotFactory slotFactory) {

        super(itemHandler, slotFactory.getIndex(), slotFactory.getX(), slotFactory.getY());
        this._template = slotFactory.getTemplate();
        this._factory = slotFactory;
    }

    public SlotTemplate getTemplate() {
        return this._template;
    }

    public SlotFactory getFactory() {
        return this._factory;
    }

    public void translate(final BiFunction<Integer, Integer, Point> mapper) {
        setPos(this, mapper.apply(this.getFactory().getX(), this.getFactory().getY()));
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     *
     * @param stack the stack to validate
     */
    @Override
    public boolean mayPlace(final ItemStack stack) {
        return this.getTemplate().match(this.getSlotIndex(), stack);
    }

    /**
     * Helper method to put a stack in the slot.
     *
     * @param stack the stack
     */
    @Override
    public void set(final ItemStack stack) {

        final IItemHandler itemHandler = this.getItemHandler();

        if (itemHandler instanceof IItemHandlerModifiable) {

            ((IItemHandlerModifiable)itemHandler).setStackInSlot(this.getSlotIndex(), stack);
            this.setChanged();

            if (itemHandler instanceof ISlotNotify) {
                ((ISlotNotify)itemHandler).onSlotChanged(itemHandler, this.getSlotIndex(), stack);
            }
        }
    }

    //region IInventorySlot

    /**
     * Get this slot index in the parent inventory
     */
    @Override
    public int getIndex() {
        return this.getSlotIndex();
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
        return this.getItem();
    }

    /**
     * Replace the ItemStack in the slot.
     *
     * @param stack ItemStack to set the slot to (may be empty).
     */
    @Override
    public void setStackInSlot(final ItemStack stack) {
        this.set(stack);
    }

    /**
     * Check if the ItemStack can be added to the slot, even partially
     *
     * @param stack ItemStack to check
     * @return true if the ItemStack can be fully or partially added to the slot. False otherwise
     */
    @Override
    public boolean canInsertStack(final ItemStack stack) {

        final ItemStack remainder = this.getItemHandler().insertItem(this.getIndex(), stack, true);

        return remainder.isEmpty() || remainder.getCount() < stack.getCount();
    }

    /**
     * Check if the given amount of items can be extracted from the slot
     *
     * @param amount amount to check
     * @return true if the given amount of items can be extracted from the slot. False otherwise
     */
    @Override
    public boolean canExtractStack(final int amount) {

        final ItemStack remainder = this.getItemHandler().extractItem(this.getIndex(), amount, true);

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
    public int getSlotLimit(final ItemStack stack) {
        return stack.isEmpty() ? this.getMaxStackSize() : Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
    }

    //endregion
    //region internals

    private static void setPos(final SlotGeneric slot, final Point pos) {

        try {
            s_xPosField.setInt(slot, pos.X);
        } catch (IllegalAccessException e) {
            Log.LOGGER.warn(Log.CORE, "Unable to set field xPos for a SlotGeneric");
        }

        try {
            s_yPosField.setInt(slot, pos.Y);
        } catch (IllegalAccessException e) {
            Log.LOGGER.warn(Log.CORE, "Unable to set field yPos for a SlotGeneric");
        }
    }

    private final SlotTemplate _template;
    private final SlotFactory _factory;

    private static final Field s_xPosField;
    private static final Field s_yPosField;

    static {

        s_xPosField = getPosField("f_85870_"); // x
        s_yPosField = getPosField("f_46014_"); // y
    }

    @Nullable
    private static Field getPosField(final String name) {

        try {

            return ObfuscationReflectionHelper.findField(Slot.class, name);

        } catch (ObfuscationReflectionHelper.UnableToFindFieldException ex) {

            Log.LOGGER.error(Log.CORE, "SlotGeneric - Unable to get field {} : {}", name, ex);
            return null;
        }
    }

    //endregion
}
