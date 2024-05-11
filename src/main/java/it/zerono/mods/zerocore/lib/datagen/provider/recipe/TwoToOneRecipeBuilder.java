/*
 *
 * TwoToOneRecipeBuilder.java
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

package it.zerono.mods.zerocore.lib.datagen.provider.recipe;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.recipe.AbstractTwoToOneRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.neoforged.neoforge.common.util.NonNullSupplier;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Objects;

public class TwoToOneRecipeBuilder<Ingredient1, Ingredient2, Result,
        RecipeIngredient1 extends IRecipeIngredient<Ingredient1>, RecipeIngredient2 extends IRecipeIngredient<Ingredient2>,
        RecipeResult extends IRecipeResult<Result>,
        Recipe extends AbstractTwoToOneRecipe<Ingredient1, Ingredient2, Result, RecipeIngredient1, RecipeIngredient2, RecipeResult>>
    extends AbstractModRecipeBuilder<Recipe, Result, RecipeResult, TwoToOneRecipeBuilder<Ingredient1, Ingredient2, Result,
        RecipeIngredient1, RecipeIngredient2, RecipeResult, Recipe>> {

    public TwoToOneRecipeBuilder(RecipeIngredient1 ingredient1, RecipeIngredient2 ingredient2, RecipeResult result,
                                 TriFunction<RecipeIngredient1, RecipeIngredient2, RecipeResult, Recipe> recipeFactory) {

        super(result);

        Preconditions.checkArgument(!ingredient1.isEmpty(), "Ingredient 1 cannot be empty");
        Preconditions.checkArgument(!ingredient2.isEmpty(), "Ingredient 2 cannot be empty");
        Preconditions.checkArgument(!result.isEmpty(), "Result cannot be empty");
        Preconditions.checkNotNull(recipeFactory, "Recipe factory cannot be empty");

        this._recipeFactory = () -> Objects.requireNonNull(recipeFactory.apply(ingredient1, ingredient2, result));
    }

    @Override
    protected Recipe getRecipe() {
        return this._recipeFactory.get();
    }

    //region internals

    private final NonNullSupplier<Recipe> _recipeFactory;

    //endregion
}
