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

import com.google.gson.JsonObject;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class OneToOneRecipeBuilder<RecipeIngredient, RecipeResult>
    extends AbstractModRecipeBuilder<OneToOneRecipeBuilder<RecipeIngredient, RecipeResult>> {

    public OneToOneRecipeBuilder(final ResourceLocation serializerId, final IRecipeIngredient<RecipeIngredient> ingredient,
                                 final IRecipeResult<RecipeResult> result) {

        super(serializerId);
        this._ingredient = ingredient;
        this._result = result;
    }

    public void build(final Consumer<IFinishedRecipe> consumer) {
        this.build(consumer, this._result.getId());
    }

    //region AbstractModRecipeBuilder

    @Override
    protected IFinishedRecipe getFinishedRecipe(final ResourceLocation id) {
        return new OneToOneRecipeBuilderFinishedRecipe(id);
    }

    public class OneToOneRecipeBuilderFinishedRecipe
            extends AbstractFinishedRecipe {

        protected OneToOneRecipeBuilderFinishedRecipe(final ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(final JsonObject json) {

            json.add(Lib.NAME_INGREDIENT, _ingredient.serializeTo());
            json.add(Lib.NAME_RESULT, _result.serializeTo());
        }
    }

    //endregion
    //region internals

    private final IRecipeIngredient<RecipeIngredient> _ingredient;
    private final IRecipeResult<RecipeResult> _result;

    //endregion
}
