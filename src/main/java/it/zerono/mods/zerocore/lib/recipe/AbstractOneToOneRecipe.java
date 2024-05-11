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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractOneToOneRecipe<Ingredient, Result, RecipeIngredient extends IRecipeIngredient<Ingredient>,
                                             RecipeResult extends IRecipeResult<Result>>
        extends ModRecipe
        implements Predicate<Ingredient> {

    protected AbstractOneToOneRecipe(final RecipeIngredient ingredient, final RecipeResult result) {

        this._ingredient = ingredient;
        this._result = result;
    }

    public static <Ingredient, Result, RecipeIngredient extends IRecipeIngredient<Ingredient>,
            RecipeResult extends IRecipeResult<Result>,
            Recipe extends AbstractOneToOneRecipe<Ingredient, Result, RecipeIngredient, RecipeResult>>
    RecipeSerializer<Recipe> createSerializer(String ingredientFieldName, Codec<RecipeIngredient> ingredientCodec,
                                              Function<FriendlyByteBuf, RecipeIngredient> ingredientFactory,
                                              String resultFieldName, Codec<RecipeResult> resultCodec,
                                              Function<FriendlyByteBuf, RecipeResult> resultFactory,
                                              BiFunction<RecipeIngredient, RecipeResult, Recipe> recipeFactory) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(ingredientFieldName), "Ingredient field name must not be null nor empty");
        Preconditions.checkNotNull(ingredientCodec, "Ingredient codec must not be null");
        Preconditions.checkNotNull(ingredientFactory, "Ingredient factory must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(resultFieldName), "Result field name must not be null nor empty");
        Preconditions.checkNotNull(resultCodec, "Result codec must not be null");
        Preconditions.checkNotNull(resultFactory, "Result factory must not be null");
        Preconditions.checkNotNull(recipeFactory, "Recipe factory must not be null");

        final Codec<Recipe> codec = RecordCodecBuilder.create(instance ->
                instance.group(
                        ingredientCodec.fieldOf(ingredientFieldName).forGetter(AbstractOneToOneRecipe::getIngredient),
                        resultCodec.fieldOf(resultFieldName).forGetter(AbstractOneToOneRecipe::getResult)
                ).apply(instance, recipeFactory));

        return new RecipeSerializer<>() {

            @Override
            public Codec<Recipe> codec() {
                return codec;
            }

            @Override
            public Recipe fromNetwork(FriendlyByteBuf buffer) {

                final RecipeIngredient ingredient = ingredientFactory.apply(buffer);
                final RecipeResult result = resultFactory.apply(buffer);

                return recipeFactory.apply(ingredient, result);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buffer, Recipe recipe) {

                recipe.getIngredient().serializeTo(buffer);
                recipe.getResult().serializeTo(buffer);
            }
        };
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
    //region ModRecipe

    @Override
    public NonNullList<net.minecraft.world.item.crafting.Ingredient> getIngredients() {
        return buildVanillaIngredientsList(this.getIngredient().asVanillaIngredients());
    }

    //endregion
    //region internals

    private final RecipeIngredient _ingredient;
    private final RecipeResult _result;

    //endregion
}
