/*
 *
 * RecipeIngredientSourceWrapper.java
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

package it.zerono.mods.zerocore.lib.recipe.ingredient;

import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.item.inventory.IInventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public final class RecipeIngredientSourceWrapper {

    public static IRecipeIngredientSource<ItemStack> wrap(final IInventorySlot slot) {
        return new IRecipeIngredientSource<ItemStack>() {

            @Override
            public ItemStack getIngredient() {
                return slot.getStackInSlot();
            }

            @Override
            public ItemStack getMatchFrom(final IRecipeIngredient<ItemStack> ingredient) {

                final ItemStack current = this.getIngredient();

                if (current.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                return ingredient.getMatchFrom(current);
            }

            @Override
            public void consumeIngredient(final ItemStack ingredient) {

                if (!ingredient.isEmpty()) {
                    slot.decreaseStackSize(ingredient.getCount(), OperationMode.Execute);
                }
            }
        };
    }

    public static IRecipeIngredientSource<ItemStack> wrap(final IItemHandler inventory, final int slot) {
        return new IRecipeIngredientSource<ItemStack>() {

            @Override
            public ItemStack getIngredient() {
                return inventory.getStackInSlot(slot);
            }

            @Override
            public ItemStack getMatchFrom(final IRecipeIngredient<ItemStack> ingredient) {

                final ItemStack current = this.getIngredient();

                if (current.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                return ingredient.getMatchFrom(current);
            }

            @Override
            public void consumeIngredient(final ItemStack ingredient) {

                if (!ingredient.isEmpty()) {
                    inventory.extractItem(slot, ingredient.getCount(), false);
                }
            }
        };
    }

    public static IRecipeIngredientSource<FluidStack> wrap(final IFluidHandler handler, final int tank) {
        return new IRecipeIngredientSource<FluidStack>() {

            @Override
            public FluidStack getIngredient() {
                return handler.getFluidInTank(tank);
            }

            @Override
            public FluidStack getMatchFrom(final IRecipeIngredient<FluidStack> ingredient) {

                final FluidStack current = this.getIngredient();

                if (current.isEmpty()) {
                    return FluidStack.EMPTY;
                }

                return ingredient.getMatchFrom(current);
            }

            @Override
            public void consumeIngredient(final FluidStack ingredient) {

                if (!ingredient.isEmpty()) {
                    handler.drain(ingredient, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        };
    }

    //region internals

    private RecipeIngredientSourceWrapper() {
    }

    //endregion
}
