/*
 *
 * RecipeResultTargetWrapper.java
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

package it.zerono.mods.zerocore.lib.recipe.result;

import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.item.inventory.IInventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class RecipeResultTargetWrapper {

    public static IRecipeResultTarget<ItemStackRecipeResult> wrap(final IInventorySlot slot) {

        return new IRecipeResultTarget<ItemStackRecipeResult>() {

            @Override
            public long setResult(final ItemStackRecipeResult result, final OperationMode mode) {

                final ItemStack stack = result.getResult();

                return stack.isEmpty() ? 0 : slot.insertStack(stack, mode).getCount();
            }

            @Override
            public long countStorableResults(final ItemStackRecipeResult result) {
                return slot.getSlotFreeSpace(result.getResult()) / result.getAmount();
            }
        };
    }

    public static IRecipeResultTarget<ItemStackRecipeResult> wrap(final IItemHandler inventory, final int slot) {

        return new IRecipeResultTarget<ItemStackRecipeResult>() {

            @Override
            public long setResult(final ItemStackRecipeResult result, final OperationMode mode) {
                return inventory.insertItem(slot, result.getResult(), mode.simulate()).getCount();
            }

            @Override
            public long countStorableResults(final ItemStackRecipeResult result) {

                final ItemStack stack = result.getResult();

                if (stack.isEmpty()) {
                    return 0;
                }

                final int max = stack.getMaxStackSize();

                stack.setCount(max);
                return (max - inventory.insertItem(slot, stack, true).getCount()) / result.getAmount();
            }
        };
    }

    //region internals

    private RecipeResultTargetWrapper() {
    }

    //endregion
}
