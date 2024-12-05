/*
 *
 * IOneToOneModRecipe.java
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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface IOneToOneModRecipe<Ingredient, Result, RecipeIngredient extends IRecipeIngredient<Ingredient>,
        RecipeResult extends IRecipeResult<Result>>
        extends IModRecipe, Predicate<Ingredient> {

    static <Ingredient, Result, RecipeIngredient extends IRecipeIngredient<Ingredient>,
            RecipeResult extends IRecipeResult<Result>,
            Recipe extends IOneToOneModRecipe<Ingredient, Result, RecipeIngredient, RecipeResult>>
    RecipeSerializer<Recipe> createOneToOneSerializer(String ingredientFieldName,
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
                        ingredientCodecs.field(ingredientFieldName, IOneToOneModRecipe::ingredient),
                        resultCodecs.field(resultFieldName, IOneToOneModRecipe::result)
                ).apply(instance, recipeFactory));

        final StreamCodec<RegistryFriendlyByteBuf, Recipe> streamCodec = StreamCodec.composite(
                ingredientCodecs.streamCodec(), IOneToOneModRecipe::ingredient,
                resultCodecs.streamCodec(), IOneToOneModRecipe::result,
                recipeFactory
        );

        return new ModRecipeSerializer<>(codec, streamCodec);
    }

    RecipeIngredient ingredient();

    RecipeResult result();

    //region Predicate<RecipeIngredient>

    @Override
    default boolean test(Ingredient stack) {
        return this.ingredient().test(stack);
    }

    //endregion
    //region IModRecipe

    @Override
    default ResourceKey<Recipe<?>> getRegistrationKey() {
        return ResourceKey.create(Registries.RECIPE, this.result().getId());
    }

    @Override
    default List<RecipeDisplay> display() {
        return List.of(new OneToOneRecipeDisplay(this.ingredient().asSlotDisplay(), this.result().asSlotDisplay(),
                this.getCraftingStationSlotDisplay()));
    }

    //endregion
    //region OneToOneRecipeDisplay
    
    record OneToOneRecipeDisplay(SlotDisplay ingredient, SlotDisplay result, SlotDisplay craftingStation)
            implements RecipeDisplay {

        public static final MapCodec<OneToOneRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                SlotDisplay.CODEC.fieldOf("ingredient").forGetter(OneToOneRecipeDisplay::ingredient),
                                SlotDisplay.CODEC.fieldOf("result").forGetter(OneToOneRecipeDisplay::result),
                                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(OneToOneRecipeDisplay::craftingStation)
                        )
                        .apply(instance, OneToOneRecipeDisplay::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, OneToOneRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC, OneToOneRecipeDisplay::ingredient,
                SlotDisplay.STREAM_CODEC, OneToOneRecipeDisplay::result,
                SlotDisplay.STREAM_CODEC, OneToOneRecipeDisplay::craftingStation,
                OneToOneRecipeDisplay::new
        );

        public static final RecipeDisplay.Type<OneToOneRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        //region RecipeDisplay

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flags) {
            return this.ingredient.isEnabled(flags) && RecipeDisplay.super.isEnabled(flags);
        }

        //endregion
    }

    //endregion
}
