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
import com.google.gson.JsonObject;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class OneToOneRecipeBuilder<RecipeIngredient, RecipeResult>
    extends AbstractModRecipeBuilder<OneToOneRecipeBuilder<RecipeIngredient, RecipeResult>> {

    public OneToOneRecipeBuilder(ResourceLocation serializerId, IRecipeIngredient<RecipeIngredient> ingredient,
                                 IRecipeResult<RecipeResult> result) {

        super(serializerId);

        Preconditions.checkArgument(!ingredient.isEmpty(), "An ingredient cannot be empty");
        Preconditions.checkArgument(!result.isEmpty(), "A result cannot be empty");

        this._ingredient = ingredient;
        this._result = result;
    }

    public void build(Consumer<FinishedRecipe> consumer) {
        this.build(consumer, this._result.getId());
    }

    //region AbstractModRecipeBuilder

    @Override
    protected FinishedRecipe getFinishedRecipe(ResourceLocation id) {
        return new OneToOneRecipeBuilderFinishedRecipe(id);
    }

    public class OneToOneRecipeBuilderFinishedRecipe
            extends AbstractFinishedRecipe {

        protected OneToOneRecipeBuilderFinishedRecipe(ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(JsonObject json) {

            json.add(Lib.NAME_INGREDIENT, OneToOneRecipeBuilder.this._ingredient.serializeTo());
            json.add(Lib.NAME_RESULT, OneToOneRecipeBuilder.this._result.serializeTo());
        }
    }

    //endregion
    //region internals

    private final IRecipeIngredient<RecipeIngredient> _ingredient;
    private final IRecipeResult<RecipeResult> _result;

    //endregion
}
