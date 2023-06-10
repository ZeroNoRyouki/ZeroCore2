/*
 *
 * ModRecipe.java
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

package it.zerono.mods.zerocore.lib.recipe;

import it.zerono.mods.zerocore.lib.item.inventory.EmptyVanillaInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class ModRecipe
    implements Recipe<EmptyVanillaInventory> {

    @SafeVarargs
    protected static NonNullList<Ingredient> buildVanillaIngredientsList(final List<Ingredient>... lists) {

        final NonNullList<Ingredient> ingredients = NonNullList.create();

        for (final List<Ingredient> list : lists) {
            if (null != list) {
                list.stream()
                        .filter(Objects::nonNull)
                        .forEach(ingredients::add);
            }
        }

        return ingredients;
    }

    //region IRecipe

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(final EmptyVanillaInventory inv, final Level world) {
        return true;
    }

    /**
     * Returns an Item that is the result of this recipe
     *
     * @param inv the inventory
     */
    @Override
    public ItemStack assemble(final EmptyVanillaInventory inv, RegistryAccess access) {
        return ItemStack.EMPTY;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     *
     * @param width grid width
     * @param height grid height
     */
    @Override
    public boolean canCraftInDimensions(final int width, final int height) {
        return true;
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this._recipeId;
    }

    /**
     * If true, this recipe does not appear in the recipe book and does not respect recipe unlocking (and the
     * doLimitedCrafting gamerule)
     */
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        throw new IllegalStateException("Override in derived class");
    }

    @Override
    public RecipeType<?> getType() {
        throw new IllegalStateException("Override in derived class");
    }

    //endregion
    //region internals

    protected ModRecipe(final ResourceLocation id) {
        this._recipeId = Objects.requireNonNull(id);
    }

    private final ResourceLocation _recipeId;

    //endregion
}
