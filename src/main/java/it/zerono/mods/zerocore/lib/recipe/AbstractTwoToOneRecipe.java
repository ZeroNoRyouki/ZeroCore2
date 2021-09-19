/*
 *
 * AbstractTwoToOneRecipe.java
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
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.BiPredicate;
import java.util.function.IntFunction;

public abstract class AbstractTwoToOneRecipe<Ingredient1, Ingredient2, Result,
                                             RecipeIngredient1 extends IRecipeIngredient<Ingredient1>,
                                             RecipeIngredient2 extends IRecipeIngredient<Ingredient2>,
                                             RecipeResult extends IRecipeResult<Result>>
        extends ModRecipe
        implements BiPredicate<Ingredient1, Ingredient2>, ISerializableRecipe {

    @FunctionalInterface
    public interface IRecipeFactory<Ingredient1, Ingredient2, Result,
                                    RecipeIngredient1 extends IRecipeIngredient<Ingredient1>,
                                    RecipeIngredient2 extends IRecipeIngredient<Ingredient2>,
                                    RecipeResult extends IRecipeResult<Result>,
                                    Recipe extends AbstractTwoToOneRecipe<Ingredient1, Ingredient2, Result,
                                                                            RecipeIngredient1, RecipeIngredient2,
                                                                            RecipeResult>> {

        Recipe create(ResourceLocation id, RecipeIngredient1 ingredient1, RecipeIngredient2 ingredient2, RecipeResult result);
    }

    protected AbstractTwoToOneRecipe(final ResourceLocation id, final RecipeIngredient1 ingredient1,
                                     final RecipeIngredient2 ingredient2, final RecipeResult result,
                                     final IntFunction<String> jsonIngredientsLabelsSupplier) {

        super(id);
        this._ingredient1 = ingredient1;
        this._ingredient2 = ingredient2;
        this._result = result;
        this._jsonIngredientsLabelsSupplier = jsonIngredientsLabelsSupplier;
    }

    public RecipeIngredient1 getIngredient1() {
        return this._ingredient1;
    }

    public RecipeIngredient2 getIngredient2() {
        return this._ingredient2;
    }

    public RecipeResult getResult() {
        return this._result;
    }

    //region Predicate<RecipeIngredient>

    @Override
    public boolean test(final Ingredient1 stack1, final Ingredient2 stack2) {
        return this.getIngredient1().test(stack1) && this.getIngredient2().test(stack2);
    }

    //endregion
    //region ModRecipe

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return buildVanillaIngredientsList(this.getIngredient1().asVanillaIngredients(), this.getIngredient2().asVanillaIngredients());
    }

    //endregion
    //region ISerializableRecipe

    @Override
    public void serializeTo(final FriendlyByteBuf buffer) {

        this._ingredient1.serializeTo(buffer);
        this._ingredient2.serializeTo(buffer);
        this._result.serializeTo(buffer);
    }

    @Override
    public JsonElement serializeTo() {

        final JsonObject json = new JsonObject();

        json.add(this._jsonIngredientsLabelsSupplier.apply(0), this._ingredient1.serializeTo());
        json.add(this._jsonIngredientsLabelsSupplier.apply(1), this._ingredient2.serializeTo());
        json.add(Lib.NAME_RESULT, this._result.serializeTo());
        return json;
    }

    //endregion
    //region internals

    private final RecipeIngredient1 _ingredient1;
    private final RecipeIngredient2 _ingredient2;
    private final RecipeResult _result;

    private final IntFunction<String> _jsonIngredientsLabelsSupplier;

    //endregion
}
