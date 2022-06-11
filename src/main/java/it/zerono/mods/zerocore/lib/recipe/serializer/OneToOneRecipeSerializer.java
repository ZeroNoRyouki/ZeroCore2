/*
 *
 * OneToOneRecipeSerializer.java
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

package it.zerono.mods.zerocore.lib.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import it.zerono.mods.zerocore.lib.recipe.AbstractOneToOneRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.function.Function;

public class OneToOneRecipeSerializer<Ingredient, Result, RecipeIngredient extends IRecipeIngredient<Ingredient>,
                                      RecipeResult extends IRecipeResult<Result>,
                                      Recipe extends AbstractOneToOneRecipe<Ingredient, Result, RecipeIngredient, RecipeResult>>
        implements RecipeSerializer<Recipe> {

    public OneToOneRecipeSerializer(final AbstractOneToOneRecipe.IRecipeFactory<Ingredient, Result, RecipeIngredient, RecipeResult, Recipe> recipeFactory,
                                    final Function<JsonElement, RecipeIngredient> jsonIngredientFactory,
                                    final Function<FriendlyByteBuf, RecipeIngredient> packetIngredientFactory,
                                    final Function<JsonElement, RecipeResult> jsonResultFactory,
                                    final Function<FriendlyByteBuf, RecipeResult> packetResultFactory) {

        this._recipeFactory = recipeFactory;
        this._jsonIngredientFactory = jsonIngredientFactory;
        this._packetIngredientFactory = packetIngredientFactory;
        this._jsonResultFactory = jsonResultFactory;
        this._packetResultFactory = packetResultFactory;
    }

    //region IRecipeSerializer<Recipe>

    @Override
    public Recipe fromJson(final ResourceLocation recipeId, final JsonObject json) {

        final RecipeIngredient ingredient = this._jsonIngredientFactory.apply(JSONHelper.jsonGetMandatoryElement(json, Lib.NAME_INGREDIENT));
        final RecipeResult result = this._jsonResultFactory.apply(JSONHelper.jsonGetMandatoryElement(json, Lib.NAME_RESULT));

        return this.create(recipeId, ingredient, result);
    }

    @Nullable
    @Override
    public Recipe fromNetwork(final ResourceLocation recipeId, final FriendlyByteBuf buffer) {

        final RecipeIngredient ingredient = this._packetIngredientFactory.apply(buffer);
        final RecipeResult result = this._packetResultFactory.apply(buffer);

        return this.create(recipeId, ingredient, result);
    }

    @Override
    public void toNetwork(final FriendlyByteBuf buffer, final Recipe recipe) {

        recipe.getIngredient().serializeTo(buffer);
        recipe.getResult().serializeTo(buffer);
    }

    //endregion
    //region internals

    private Recipe create(final ResourceLocation id, final RecipeIngredient ingredient, final RecipeResult result) {

        if (result.isEmpty()) {
            throw new IllegalArgumentException("A one-to-one recipe result cannot be empty");
        }

        return this._recipeFactory.create(id, ingredient, result);
    }

    private final AbstractOneToOneRecipe.IRecipeFactory<Ingredient, Result, RecipeIngredient, RecipeResult, Recipe> _recipeFactory;
    private final Function<JsonElement, RecipeIngredient> _jsonIngredientFactory;
    private final Function<FriendlyByteBuf, RecipeIngredient> _packetIngredientFactory;
    private final Function<JsonElement, RecipeResult> _jsonResultFactory;
    private final Function<FriendlyByteBuf, RecipeResult> _packetResultFactory;

    //endregion
}
