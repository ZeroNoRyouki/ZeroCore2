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
import com.google.gson.JsonObject;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.IntFunction;

public class TwoToOneRecipeBuilder<RecipeIngredient1, RecipeIngredient2, RecipeResult>
        extends AbstractModRecipeBuilder<TwoToOneRecipeBuilder<RecipeIngredient1, RecipeIngredient2, RecipeResult>> {

    public TwoToOneRecipeBuilder(ResourceLocation serializerId, IRecipeIngredient<RecipeIngredient1> ingredient1,
                                 IRecipeIngredient<RecipeIngredient2> ingredient2, IRecipeResult<RecipeResult> result,
                                 IntFunction<String> jsonIngredientsLabelsSupplier) {

        super(serializerId);

        Preconditions.checkArgument(!ingredient1.isEmpty(), "An ingredient cannot be empty");
        Preconditions.checkArgument(!ingredient2.isEmpty(), "An ingredient cannot be empty");
        Preconditions.checkArgument(!result.isEmpty(), "A result cannot be empty");

        this._ingredient1 = ingredient1;
        this._ingredient2 = ingredient2;
        this._result = result;
        this._jsonIngredientsLabelsSupplier = jsonIngredientsLabelsSupplier;
    }

    public void build(Consumer<FinishedRecipe> consumer) {
        this.build(consumer, this._result.getId());
    }

    //region AbstractModRecipeBuilder

    @Override
    protected FinishedRecipe getFinishedRecipe(ResourceLocation id) {
        return new TwoToOneRecipeBuilderFinishedRecipe(id);
    }

    public class TwoToOneRecipeBuilderFinishedRecipe
            extends AbstractFinishedRecipe {

        protected TwoToOneRecipeBuilderFinishedRecipe(ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(JsonObject json) {

            json.add(TwoToOneRecipeBuilder.this._jsonIngredientsLabelsSupplier.apply(0),
                    TwoToOneRecipeBuilder.this._ingredient1.serializeTo());
            json.add(TwoToOneRecipeBuilder.this._jsonIngredientsLabelsSupplier.apply(1),
                    TwoToOneRecipeBuilder.this._ingredient2.serializeTo());
            json.add(Lib.NAME_RESULT, TwoToOneRecipeBuilder.this._result.serializeTo());
        }
    }

    //endregion
    //region internals

    private final IRecipeIngredient<RecipeIngredient1> _ingredient1;
    private final IRecipeIngredient<RecipeIngredient2> _ingredient2;
    private final IRecipeResult<RecipeResult> _result;
    private final IntFunction<String> _jsonIngredientsLabelsSupplier;

    //endregion
}
