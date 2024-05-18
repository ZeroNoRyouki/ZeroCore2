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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.BiFunction;
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
    RecipeSerializer<Recipe> createSerializer(String ingredientFieldName,
                                              ModCodecs<RecipeIngredient, RegistryFriendlyByteBuf> ingredientCodecs,
                                              String resultFieldName,
                                              ModCodecs<RecipeResult, RegistryFriendlyByteBuf> resultCodecs,
                                              BiFunction<RecipeIngredient, RecipeResult, Recipe> recipeFactory) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(ingredientFieldName), "Ingredient field name must not be null nor empty");
        Preconditions.checkNotNull(ingredientCodecs, "Ingredient codecs must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(resultFieldName), "Result field name must not be null nor empty");
        Preconditions.checkNotNull(resultCodecs, "Result codecs must not be null");
        Preconditions.checkNotNull(recipeFactory, "Recipe factory must not be null");

        final MapCodec<Recipe> codec = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ingredientCodecs.field(ingredientFieldName, AbstractOneToOneRecipe::getIngredient),
                        resultCodecs.field(resultFieldName, AbstractOneToOneRecipe::getResult)
                ).apply(instance, recipeFactory));

        final StreamCodec<RegistryFriendlyByteBuf, Recipe> streamCodec = StreamCodec.composite(
                ingredientCodecs.streamCodec(), AbstractOneToOneRecipe::getIngredient,
                resultCodecs.streamCodec(), AbstractOneToOneRecipe::getResult,
                recipeFactory
        );

        return new RecipeSerializer<>() {

            @Override
            public MapCodec<Recipe> codec() {
                return codec;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Recipe> streamCodec() {
                return streamCodec;
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
