/*
 *
 * AbstractOneToOneRecipeBuilder.java
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
import it.zerono.mods.zerocore.lib.recipe.AbstractOneToOneRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class OneToOneRecipeBuilder<Ingredient, Result,
        RecipeIngredient extends IRecipeIngredient<Ingredient>, RecipeResult extends IRecipeResult<Result>,
        Recipe extends AbstractOneToOneRecipe<Ingredient, Result, RecipeIngredient, RecipeResult>>
    extends AbstractModRecipeBuilder<Recipe, Result, RecipeResult, OneToOneRecipeBuilder<Ingredient, Result,
        RecipeIngredient, RecipeResult, Recipe>> {

    public OneToOneRecipeBuilder(RecipeIngredient ingredient, RecipeResult result,
                                 BiFunction<RecipeIngredient, RecipeResult, Recipe> recipeFactory) {

        super(result);

        Preconditions.checkArgument(!ingredient.isEmpty(), "Ingredient cannot be empty");
        Preconditions.checkArgument(!result.isEmpty(), "Result cannot be empty");
        Preconditions.checkNotNull(recipeFactory, "Recipe factory cannot be empty");

        this._recipeFactory = () -> Objects.requireNonNull(recipeFactory.apply(ingredient, result));
    }

    @Override
    protected Recipe getRecipe() {
        return this._recipeFactory.get();
    }

    //region internals

    private final Supplier<@NotNull Recipe> _recipeFactory;

    //endregion
}
