/*
 *
 * ManyToOneRecipeSerializer.java
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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import it.zerono.mods.zerocore.lib.recipe.AbstractManyToOneRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

public class ManyToOneRecipeSerializer<IngredientT, Result, RecipeIngredient extends IRecipeIngredient<IngredientT>,
                                        RecipeResult extends IRecipeResult<Result>,
                                        Recipe extends AbstractManyToOneRecipe<IngredientT, Result, RecipeIngredient, RecipeResult>>
        implements RecipeSerializer<Recipe> {

    public ManyToOneRecipeSerializer(final AbstractManyToOneRecipe.IRecipeFactory<IngredientT, Result, RecipeIngredient, RecipeResult, Recipe> recipeFactory,
                                    final Function<JsonElement, RecipeIngredient> jsonIngredient1Factory,
                                    final Function<FriendlyByteBuf, RecipeIngredient> packetIngredient1Factory,
                                    final Function<JsonElement, RecipeResult> jsonResultFactory,
                                    final Function<FriendlyByteBuf, RecipeResult> packetResultFactory,
                                    final IntFunction<String> jsonIngredientsLabelsSupplier) {

        this._recipeFactory = recipeFactory;
        this._jsonIngredientFactory = jsonIngredient1Factory;
        this._packetIngredientFactory = packetIngredient1Factory;
        this._jsonResultFactory = jsonResultFactory;
        this._packetResultFactory = packetResultFactory;
        this._jsonIngredientsLabelsSupplier = jsonIngredientsLabelsSupplier;
    }

    //region IRecipeSerializer<Recipe>

    @Override
    public Recipe fromJson(final ResourceLocation recipeId, final JsonObject json) {

        final List<RecipeIngredient> ingredients = new ObjectArrayList<>(5);
        String elementName;
        int idx = 0;

        while (json.has(elementName = this._jsonIngredientsLabelsSupplier.apply(idx))) {

            ingredients.add(this._jsonIngredientFactory.apply(json.get(elementName)));
            ++idx;
        }

        final RecipeResult result = this._jsonResultFactory.apply(JSONHelper.jsonGetMandatoryElement(json, Lib.NAME_RESULT));

        return this.create(recipeId, ingredients, result);
    }

    @Nullable
    @Override
    public Recipe fromNetwork(final ResourceLocation recipeId, final FriendlyByteBuf buffer) {

        final int count = buffer.readInt();
        final List<RecipeIngredient> ingredients = new ObjectArrayList<>(count);

        for (int i = 0; i < count; ++i) {
            ingredients.add(this._packetIngredientFactory.apply(buffer));
        }

        final RecipeResult result = this._packetResultFactory.apply(buffer);

        return this.create(recipeId, ingredients, result);
    }

    @Override
    public void toNetwork(final FriendlyByteBuf buffer, final Recipe recipe) {
        recipe.serializeTo(buffer);
    }

    //endregion
    //region internals

    private Recipe create(final ResourceLocation id, final List<RecipeIngredient> ingredients, final RecipeResult result) {

        if (result.isEmpty()) {
            throw new IllegalArgumentException("A many-to-one recipe result cannot be empty");
        }

        return this._recipeFactory.create(id, ingredients, result);
    }

    private final AbstractManyToOneRecipe.IRecipeFactory<IngredientT, Result, RecipeIngredient, RecipeResult, Recipe> _recipeFactory;
    private final Function<JsonElement, RecipeIngredient> _jsonIngredientFactory;
    private final Function<FriendlyByteBuf, RecipeIngredient> _packetIngredientFactory;
    private final Function<JsonElement, RecipeResult> _jsonResultFactory;
    private final Function<FriendlyByteBuf, RecipeResult> _packetResultFactory;

    private final IntFunction<String> _jsonIngredientsLabelsSupplier;

    //endregion
}
