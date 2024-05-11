/*
 *
 * SlotCraftingResult.java
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

import it.zerono.mods.zerocore.lib.item.inventory.container.ICrafter;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class SlotCraftingResult extends SlotGeneric {

    public SlotCraftingResult(final IItemHandler itemHandler, final SlotFactory slotFactory,
                              final ICrafter crafter) {

        super(itemHandler, slotFactory);
        this._crafter = crafter;
    }

    @SuppressWarnings("WeakerAccess")
    public ICrafter getCrafter() {
        return this._crafter;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     *
     * @param stack
     */
    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(Player playerIn, ItemStack stack) {

        this.getCrafter().craft();
        super.onTake(playerIn, stack);
    }

    private final ICrafter _crafter;
}
