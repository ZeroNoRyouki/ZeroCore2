/*
 *
 * ItemStackToItemStackRecipe.java
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public abstract class AbstractOneToOneRecipe<Ingredient, Result, RecipeIngredient extends IRecipeIngredient<Ingredient>,
                                             RecipeResult extends IRecipeResult<Result>>
        extends ModRecipe
        implements Predicate<Ingredient>, ISerializableRecipe {

    @FunctionalInterface
    public interface IRecipeFactory<Ingredient, Result, RecipeIngredient extends IRecipeIngredient<Ingredient>,
                                    RecipeResult extends IRecipeResult<Result>,
                                    Recipe extends AbstractOneToOneRecipe<Ingredient, Result, RecipeIngredient, RecipeResult>> {

        Recipe create(ResourceLocation id, RecipeIngredient ingredient, RecipeResult result);
    }

    protected AbstractOneToOneRecipe(final ResourceLocation id, final RecipeIngredient ingredient, final RecipeResult result) {

        super(id);
        this._ingredient = ingredient;
        this._result = result;
    }

    public RecipeIngredient getIngredient() {
        return this._ingredient;
    }

    public RecipeResult getResult() {
        return this._result;
    }

    //region Predicate<RecipeIngredient>

    @Override
    public boolean test(final Ingredient stack) {
        return this.getIngredient().test(stack);
    }

    //endregion
    //region ISerializableRecipe

    @Override
    public void serializeTo(final FriendlyByteBuf buffer) {

        this._ingredient.serializeTo(buffer);
        this._result.serializeTo(buffer);
    }

    @Override
    public JsonElement serializeTo() {

        final JsonObject json = new JsonObject();

        json.add(Lib.NAME_INGREDIENT, this._ingredient.serializeTo());
        json.add(Lib.NAME_RESULT, this._result.serializeTo());
        return json;
    }

    //endregion
    //region internals

    private final RecipeIngredient _ingredient;
    private final RecipeResult _result;

    //endregion
}
