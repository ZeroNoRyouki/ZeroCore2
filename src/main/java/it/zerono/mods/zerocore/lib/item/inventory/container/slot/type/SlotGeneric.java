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
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.ISlotNotify;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.function.BiFunction;

public class SlotGeneric extends SlotItemHandler {

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
     * @param stack
     */
    @Override
    public boolean isItemValid(final ItemStack stack) {
        return this.getTemplate().match(this.getSlotIndex(), stack);
    }

    /**
     * Helper method to put a stack in the slot.
     *
     * @param stack
     */
    @Override
    public void putStack(final ItemStack stack) {

        final IItemHandler itemHandler = this.getItemHandler();

        if (itemHandler instanceof IItemHandlerModifiable) {

            ((IItemHandlerModifiable)itemHandler).setStackInSlot(this.getSlotIndex(), stack);
            this.onSlotChanged();

            if (itemHandler instanceof ISlotNotify) {
                ((ISlotNotify)itemHandler).onSlotChanged(itemHandler, this.getSlotIndex(), stack);
            }
        }
    }

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

        s_xPosField = getPosField("field_75223_e"); // xPos
        s_yPosField = getPosField("field_75221_f"); // yPos
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
