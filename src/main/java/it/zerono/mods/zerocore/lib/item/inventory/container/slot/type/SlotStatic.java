/*
 *
 * SlotStatic.java
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

import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;

public class SlotStatic
        extends SlotGeneric {

    public SlotStatic(final IItemHandler itemHandler, final SlotFactory slotFactory) {
        super(itemHandler, slotFactory);
    }

    //region SlotGeneric

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     *
     * @param stack the stack to validate
     */
    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

    /**
     * Check if the ItemStack can be added to the slot, even partially
     *
     * @param stack ItemStack to check
     * @return true if the ItemStack can be fully or partially added to the slot. False otherwise
     */
    @Override
    public boolean canInsertStack(ItemStack stack) {
        return false;
    }

    /**
     * Check if the given amount of items can be extracted from the slot
     *
     * @param amount amount to check
     * @return true if the given amount of items can be extracted from the slot. False otherwise
     */
    @Override
    public boolean canExtractStack(final int amount) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    @Override
    public int getMaxStackSize() {
        return 64;
    }

    /**
     * If this method return false the slot content is not rendered
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isActive() {
        return true;
    }

    //endregion
}
