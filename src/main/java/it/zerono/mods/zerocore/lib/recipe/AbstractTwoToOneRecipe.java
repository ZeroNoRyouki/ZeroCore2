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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.BiPredicate;
import java.util.function.Function;

public abstract class AbstractTwoToOneRecipe<Ingredient1, Ingredient2, Result,
                                             RecipeIngredient1 extends IRecipeIngredient<Ingredient1>,
                                             RecipeIngredient2 extends IRecipeIngredient<Ingredient2>,
                                             RecipeResult extends IRecipeResult<Result>>
        extends ModRecipe
        implements BiPredicate<Ingredient1, Ingredient2> {

    protected AbstractTwoToOneRecipe(final RecipeIngredient1 ingredient1, final RecipeIngredient2 ingredient2,
                                     final RecipeResult result) {

        this._ingredient1 = ingredient1;
        this._ingredient2 = ingredient2;
        this._result = result;
    }

    public static <Ingredient1, Ingredient2, Result, RecipeIngredient1 extends IRecipeIngredient<Ingredient1>,
            RecipeIngredient2 extends IRecipeIngredient<Ingredient2>, RecipeResult extends IRecipeResult<Result>,
            Recipe extends AbstractTwoToOneRecipe<Ingredient1, Ingredient2, Result, RecipeIngredient1, RecipeIngredient2,
                    RecipeResult>>
    RecipeSerializer<Recipe> createSerializer(String ingredient1FieldName, Codec<RecipeIngredient1> ingredient1Codec,
                                              Function<FriendlyByteBuf, RecipeIngredient1> ingredient1Factory,
                                              String ingredient2FieldName, Codec<RecipeIngredient2> ingredient2Codec,
                                              Function<FriendlyByteBuf, RecipeIngredient2> ingredient2Factory,
                                              String resultFieldName, Codec<RecipeResult> resultCodec,
                                              Function<FriendlyByteBuf, RecipeResult> resultFactory,
                                              Function3<RecipeIngredient1, RecipeIngredient2, RecipeResult, Recipe> recipeFactory) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(ingredient1FieldName), "Ingredient 1 field name must not be null nor empty");
        Preconditions.checkNotNull(ingredient1Codec, "Ingredient 1 codec must not be null");
        Preconditions.checkNotNull(ingredient1Factory, "Ingredient 1 factory must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(ingredient2FieldName), "Ingredient 2 field name must not be null nor empty");
        Preconditions.checkNotNull(ingredient2Codec, "Ingredient 2 codec must not be null");
        Preconditions.checkNotNull(ingredient2Factory, "Ingredient 2 factory must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(resultFieldName), "Result field name must not be null nor empty");
        Preconditions.checkNotNull(resultCodec, "Result codec must not be null");
        Preconditions.checkNotNull(resultFactory, "Result factory must not be null");
        Preconditions.checkNotNull(recipeFactory, "Recipe factory must not be null");

        final Codec<Recipe> codec = RecordCodecBuilder.create(instance ->
                instance.group(
                        ingredient1Codec.fieldOf(ingredient1FieldName).forGetter(AbstractTwoToOneRecipe::getIngredient1),
                        ingredient2Codec.fieldOf(ingredient2FieldName).forGetter(AbstractTwoToOneRecipe::getIngredient2),
                        resultCodec.fieldOf(resultFieldName).forGetter(AbstractTwoToOneRecipe::getResult)
                ).apply(instance,  recipeFactory));

        return new RecipeSerializer<>() {

            @Override
            public Codec<Recipe> codec() {
                return codec;
            }

            @Override
            public Recipe fromNetwork(FriendlyByteBuf buffer) {

                final RecipeIngredient1 ingredient1 = ingredient1Factory.apply(buffer);
                final RecipeIngredient2 ingredient2 = ingredient2Factory.apply(buffer);
                final RecipeResult result = resultFactory.apply(buffer);

                return recipeFactory.apply(ingredient1, ingredient2, result);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buffer, Recipe recipe) {

                recipe.getIngredient1().serializeTo(buffer);
                recipe.getIngredient2().serializeTo(buffer);
                recipe.getResult().serializeTo(buffer);
            }
        };
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
    //region internals

    private final RecipeIngredient1 _ingredient1;
    private final RecipeIngredient2 _ingredient2;
    private final RecipeResult _result;

    //endregion
}
