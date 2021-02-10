/*
 *
 * TwoToOneRecipeSerializer.java
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
import it.zerono.mods.zerocore.lib.recipe.AbstractTwoToOneRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.IntFunction;

public class TwoToOneRecipeSerializer <Ingredient1, Ingredient2, Result,
                                        RecipeIngredient1 extends IRecipeIngredient<Ingredient1>,
                                        RecipeIngredient2 extends IRecipeIngredient<Ingredient2>,
                                        RecipeResult extends IRecipeResult<Result>,
                                        Recipe extends AbstractTwoToOneRecipe<Ingredient1, Ingredient2, Result,
                                                                                RecipeIngredient1, RecipeIngredient2,
                                                                                RecipeResult>>
        extends ForgeRegistryEntry<IRecipeSerializer<?>>
        implements IRecipeSerializer<Recipe> {

    public TwoToOneRecipeSerializer(final AbstractTwoToOneRecipe.IRecipeFactory<Ingredient1, Ingredient2, Result, RecipeIngredient1, RecipeIngredient2, RecipeResult, Recipe> recipeFactory,
                                    final Function<JsonElement, RecipeIngredient1> jsonIngredient1Factory,
                                    final Function<PacketBuffer, RecipeIngredient1> packetIngredient1Factory,
                                    final Function<JsonElement, RecipeIngredient2> jsonIngredient2Factory,
                                    final Function<PacketBuffer, RecipeIngredient2> packetIngredient2Factory,
                                    final Function<JsonElement, RecipeResult> jsonResultFactory,
                                    final Function<PacketBuffer, RecipeResult> packetResultFactory,
                                    final IntFunction<String> jsonIngredientsLabelsSupplier) {

        this._recipeFactory = recipeFactory;
        this._jsonIngredient1Factory = jsonIngredient1Factory;
        this._packetIngredient1Factory = packetIngredient1Factory;
        this._jsonIngredient2Factory = jsonIngredient2Factory;
        this._packetIngredient2Factory = packetIngredient2Factory;
        this._jsonResultFactory = jsonResultFactory;
        this._packetResultFactory = packetResultFactory;
        this._jsonIngredientsLabelsSupplier = jsonIngredientsLabelsSupplier;
    }

    //region IRecipeSerializer<Recipe>

    @Override
    public Recipe read(final ResourceLocation recipeId, final JsonObject json) {

        final RecipeIngredient1 ingredient1 = this._jsonIngredient1Factory.apply(JSONHelper.jsonGetMandatoryElement(json, this._jsonIngredientsLabelsSupplier.apply(0)));
        final RecipeIngredient2 ingredient2 = this._jsonIngredient2Factory.apply(JSONHelper.jsonGetMandatoryElement(json, this._jsonIngredientsLabelsSupplier.apply(1)));
        final RecipeResult result = this._jsonResultFactory.apply(JSONHelper.jsonGetMandatoryElement(json, Lib.NAME_RESULT));

        return this.create(recipeId, ingredient1, ingredient2, result);
    }

    @Nullable
    @Override
    public Recipe read(final ResourceLocation recipeId, final PacketBuffer buffer) {

        final RecipeIngredient1 ingredient1 = this._packetIngredient1Factory.apply(buffer);
        final RecipeIngredient2 ingredient2 = this._packetIngredient2Factory.apply(buffer);
        final RecipeResult result = this._packetResultFactory.apply(buffer);

        return this.create(recipeId, ingredient1, ingredient2, result);
    }

    @Override
    public void write(final PacketBuffer buffer, final Recipe recipe) {

        recipe.getIngredient1().serializeTo(buffer);
        recipe.getIngredient2().serializeTo(buffer);
        recipe.getResult().serializeTo(buffer);
    }

    //endregion
    //region internals

    private Recipe create(final ResourceLocation id, final RecipeIngredient1 ingredient1,
                          final RecipeIngredient2 ingredient2, final RecipeResult result) {

        if (result.isEmpty()) {
            throw new IllegalArgumentException("A two-to-one recipe result cannot be empty");
        }

        return this._recipeFactory.create(id, ingredient1, ingredient2, result);
    }

    private final AbstractTwoToOneRecipe.IRecipeFactory<Ingredient1, Ingredient2, Result, RecipeIngredient1, RecipeIngredient2, RecipeResult, Recipe> _recipeFactory;
    private final Function<JsonElement, RecipeIngredient1> _jsonIngredient1Factory;
    private final Function<PacketBuffer, RecipeIngredient1> _packetIngredient1Factory;
    private final Function<JsonElement, RecipeIngredient2> _jsonIngredient2Factory;
    private final Function<PacketBuffer, RecipeIngredient2> _packetIngredient2Factory;
    private final Function<JsonElement, RecipeResult> _jsonResultFactory;
    private final Function<PacketBuffer, RecipeResult> _packetResultFactory;

    private final IntFunction<String> _jsonIngredientsLabelsSupplier;

    //endregion
}
