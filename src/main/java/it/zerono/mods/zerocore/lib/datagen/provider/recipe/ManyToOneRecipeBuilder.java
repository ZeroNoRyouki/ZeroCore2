/*
 *
 * ManyToOneRecipeBuilder.java
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
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class ManyToOneRecipeBuilder<IngredientT, Result, RecipeIngredient extends IRecipeIngredient<IngredientT>,
                                    RecipeResult extends IRecipeResult<Result>>
        extends AbstractModRecipeBuilder<ManyToOneRecipeBuilder<IngredientT, Result, RecipeIngredient, RecipeResult>> {

    public ManyToOneRecipeBuilder(final ResourceLocation serializerId, final RecipeResult result,
                                  final IntFunction<String> jsonIngredientsLabelsSupplier) {

        super(serializerId);
        Preconditions.checkArgument(!result.isEmpty(), "A result cannot be empty");
        this._ingredients = new ObjectArrayList<>(4);
        this._result = result;
        this._jsonIngredientsLabelsSupplier = jsonIngredientsLabelsSupplier;
    }

    public void build(final Consumer<IFinishedRecipe> consumer) {
        this.build(consumer, this._result.getId());
    }

    public void addIngredient(final RecipeIngredient ingredient) {

        Preconditions.checkArgument(!ingredient.isEmpty(), "An ingredient cannot be empty");
        this._ingredients.add(ingredient);
    }

    //region AbstractModRecipeBuilder

    @Override
    protected IFinishedRecipe getFinishedRecipe(final ResourceLocation id) {
        return new ManyToOneRecipeBuilderFinishedRecipe(id);
    }

    public class ManyToOneRecipeBuilderFinishedRecipe
            extends AbstractFinishedRecipe {

        protected ManyToOneRecipeBuilderFinishedRecipe(final ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(final JsonObject json) {

            final int count = _ingredients.size();

            for (int idx = 0; idx < count; ++idx) {
                json.add(_jsonIngredientsLabelsSupplier.apply(idx), _ingredients.get(idx).serializeTo());
            }

            json.add(Lib.NAME_RESULT, _result.serializeTo());
        }
    }

    //endregion
    //region internals

    private final List<RecipeIngredient> _ingredients;
    private final RecipeResult _result;

    private final IntFunction<String> _jsonIngredientsLabelsSupplier;

    //endregion
}
