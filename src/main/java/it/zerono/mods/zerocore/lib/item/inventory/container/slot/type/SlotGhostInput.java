/*
 *
 * SlotGhostInput.java
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

import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SlotGhostInput extends SlotGeneric {

    public SlotGhostInput(final IItemHandler itemHandler, final SlotFactory slotFactory) {
        super(itemHandler, slotFactory);
    }

    /**
     * Helper method to put a stack in the slot if the slot inventory implement IItemHandlerModifiable
     *
     * @param stack the stack to be put in the inventory
     */
    @Override
    public void putStack(ItemStack stack) {

        if (stack.isEmpty()) {
            ItemHelper.stackSetSize(stack, 1);
        }

        super.putStack(stack);
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    @Override
    public int getSlotStackLimit() {
        return 0;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     *
     * @param playerIn
     */
    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     *
     * @param amount
     */
    @Override
    public ItemStack decrStackSize(int amount) {
        return ItemHelper.stackEmpty();
    }
}
