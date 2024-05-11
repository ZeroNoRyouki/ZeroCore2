/*
 *
 * AbstractManyToOneRecipe.java
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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractManyToOneRecipe<Ingredient, Result,
        RecipeIngredient extends IRecipeIngredient<Ingredient>,
        RecipeResult extends IRecipeResult<Result>>
    extends ModRecipe
    implements Predicate<List<Ingredient>> {

    protected AbstractManyToOneRecipe(final List<RecipeIngredient> ingredients, final RecipeResult result) {

        Preconditions.checkArgument(!ingredients.isEmpty(), "Trying to create a recipe without ingredients");

        final ObjectList<RecipeIngredient> copy = ingredients.size() == 1 ? ObjectLists.singleton(ingredients.get(0)) :
                new ObjectArrayList<>(ingredients);

        this._ingredients = ObjectLists.unmodifiable(copy);
        this._result = result;
    }

    public static <Ingredient, Result, RecipeIngredient extends IRecipeIngredient<Ingredient>,
            RecipeResult extends IRecipeResult<Result>,
            Recipe extends AbstractManyToOneRecipe<Ingredient, Result, RecipeIngredient, RecipeResult>>
    RecipeSerializer<Recipe> createSerializer(String ingredientsFieldName, Codec<RecipeIngredient> ingredientsCodec,
                                              Function<FriendlyByteBuf, RecipeIngredient> ingredientFactory,
                                              String resultFieldName, Codec<RecipeResult> resultCodec,
                                              Function<FriendlyByteBuf, RecipeResult> resultFactory,
                                              BiFunction<List<RecipeIngredient>, RecipeResult, Recipe> recipeFactory) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(ingredientsFieldName), "Ingredients field name must not be null nor empty");
        Preconditions.checkNotNull(ingredientsCodec, "Ingredients codec must not be null");
        Preconditions.checkNotNull(ingredientFactory, "Ingredient factory must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(resultFieldName), "Result field name must not be null nor empty");
        Preconditions.checkNotNull(resultCodec, "Result codec must not be null");
        Preconditions.checkNotNull(resultFactory, "Result factory must not be null");
        Preconditions.checkNotNull(recipeFactory, "Recipe factory must not be null");

        final Codec<Recipe> codec = RecordCodecBuilder.create(instance ->
                instance.group(
                        ingredientsCodec.listOf().fieldOf(ingredientsFieldName).forGetter(AbstractManyToOneRecipe::getRecipeIngredients),
                        resultCodec.fieldOf(resultFieldName).forGetter(AbstractManyToOneRecipe::getResult)
                ).apply(instance,  recipeFactory));

        return new RecipeSerializer<>() {

            @Override
            public Codec<Recipe> codec() {
                return codec;
            }

            @Override
            public Recipe fromNetwork(FriendlyByteBuf buffer) {

                final int count = buffer.readVarInt();
                final List<RecipeIngredient> ingredients = new ObjectArrayList<>(count);

                for (int idx = 0; idx < count; ++idx) {
                    ingredients.add(ingredientFactory.apply(buffer));
                }

                final RecipeResult result = resultFactory.apply(buffer);

                return recipeFactory.apply(ingredients, result);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buffer, Recipe recipe) {

                final var ingredients = recipe.getRecipeIngredients();

                buffer.writeVarInt(ingredients.size());
                ingredients.forEach(ingredient -> ingredient.serializeTo(buffer));
                recipe.getResult().serializeTo(buffer);
            }
        };
    }

    public int getRecipeIngredientsCount() {
        return this._ingredients.size();
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return this._ingredients;
    }

    public RecipeResult getResult() {
        return this._result;
    }

    //region Predicate<List<IngredientT>>

    @Override
    public boolean test(final List<Ingredient> stacks) {

        List<RecipeIngredient> ingredients = this.getRecipeIngredients();
        int ingredientsCount = ingredients.size();

        if (ingredientsCount != stacks.size()) {
            return false;
        }

        if (1 == ingredientsCount) {
            return ingredients.get(0).test(stacks.get(0));
        }

        ingredients = new ObjectArrayList<>(ingredients);
        ingredientsCount = ingredients.size();

        boolean found;

        for (final Ingredient stack : stacks) {

            found = false;

            for (int idx = 0; idx < ingredientsCount; ++idx) {
                if (ingredients.get(idx).test(stack)) {

                    ingredients.remove(idx);
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        return true;
    }

    //endregion
    //region ModRecipe

    @Override
    public NonNullList<net.minecraft.world.item.crafting.Ingredient> getIngredients() {
        return buildVanillaIngredientsList(this.getRecipeIngredients().stream()
                .flatMap(i -> i.asVanillaIngredients().stream())
                .collect(Collectors.toList()));
    }

    //endregion
    //region internals

    private final List<RecipeIngredient> _ingredients;
    private final RecipeResult _result;

    //endregion
}
