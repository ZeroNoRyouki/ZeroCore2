/*
 *
 * ITwoToOneModRecipe.java
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
import java.util.function.BiPredicate;

public interface ITwoToOneModRecipe<Ingredient1, Ingredient2, Result,
        RecipeIngredient1 extends IRecipeIngredient<Ingredient1>,
        RecipeIngredient2 extends IRecipeIngredient<Ingredient2>,
        RecipeResult extends IRecipeResult<Result>>
        extends IModRecipe, BiPredicate<Ingredient1, Ingredient2> {

    static <Ingredient1, Ingredient2, Result, RecipeIngredient1 extends IRecipeIngredient<Ingredient1>,
            RecipeIngredient2 extends IRecipeIngredient<Ingredient2>, RecipeResult extends IRecipeResult<Result>,
            Recipe extends ITwoToOneModRecipe<Ingredient1, Ingredient2, Result, RecipeIngredient1, RecipeIngredient2, 
                    RecipeResult>>
    RecipeSerializer<Recipe> createTwoToOneSerializer(String ingredient1FieldName,
                                                      ModCodecs<RecipeIngredient1, RegistryFriendlyByteBuf> ingredient1Codecs,
                                                      String ingredient2FieldName,
                                                      ModCodecs<RecipeIngredient2, RegistryFriendlyByteBuf> ingredient2Codecs,
                                                      String resultFieldName,
                                                      ModCodecs<RecipeResult, RegistryFriendlyByteBuf> resultCodecs,
                                                      Function3<RecipeIngredient1, RecipeIngredient2, RecipeResult, Recipe> recipeFactory) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(ingredient1FieldName), "Ingredient 1 field name must not be null nor empty");
        Preconditions.checkNotNull(ingredient1Codecs, "Ingredient 1 codecs must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(ingredient2FieldName), "Ingredient 2 field name must not be null nor empty");
        Preconditions.checkNotNull(ingredient2Codecs, "Ingredient 2 codecs must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(resultFieldName), "Result field name must not be null nor empty");
        Preconditions.checkNotNull(resultCodecs, "Result codecs must not be null");
        Preconditions.checkNotNull(recipeFactory, "Recipe factory must not be null");

        final MapCodec<Recipe> codec = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ingredient1Codecs.field(ingredient1FieldName, ITwoToOneModRecipe::ingredient1),
                        ingredient2Codecs.field(ingredient2FieldName, ITwoToOneModRecipe::ingredient2),
                        resultCodecs.field(resultFieldName, ITwoToOneModRecipe::result)
                ).apply(instance,  recipeFactory));

        final StreamCodec<RegistryFriendlyByteBuf, Recipe> streamCodec = StreamCodec.composite(
                ingredient1Codecs.streamCodec(), ITwoToOneModRecipe::ingredient1,
                ingredient2Codecs.streamCodec(), ITwoToOneModRecipe::ingredient2,
                resultCodecs.streamCodec(), ITwoToOneModRecipe::result,
                recipeFactory
        );

        return new ModRecipeSerializer<>(codec, streamCodec);
    }

    RecipeIngredient1 ingredient1();

    RecipeIngredient2 ingredient2();

    RecipeResult result();

    //region BiPredicate<Ingredient1, Ingredient2>

    @Override
    default boolean test(Ingredient1 stack1, Ingredient2 stack2) {
        return this.ingredient1().test(stack1) && this.ingredient2().test(stack2);
    }

    //endregion
    //region IModRecipe

    @Override
    default ResourceKey<Recipe<?>> getRegistrationKey() {
        return ResourceKey.create(Registries.RECIPE, this.result().getId());
    }

    @Override
    default List<RecipeDisplay> display() {
        return List.of(new TwoToOneRecipeDisplay(this.ingredient2().asSlotDisplay(), this.ingredient2().asSlotDisplay(),
                this.result().asSlotDisplay(), this.getCraftingStationSlotDisplay()));
    }

    //endregion
    //region OneToOneRecipeDisplay

    record TwoToOneRecipeDisplay(SlotDisplay ingredient1, SlotDisplay ingredient2, SlotDisplay result,
                                 SlotDisplay craftingStation)
            implements RecipeDisplay {

        public static final MapCodec<TwoToOneRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                SlotDisplay.CODEC.fieldOf("ingredient1").forGetter(TwoToOneRecipeDisplay::ingredient1),
                                SlotDisplay.CODEC.fieldOf("ingredient2").forGetter(TwoToOneRecipeDisplay::ingredient2),
                                SlotDisplay.CODEC.fieldOf("result").forGetter(TwoToOneRecipeDisplay::result),
                                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(TwoToOneRecipeDisplay::craftingStation)
                        )
                        .apply(instance, TwoToOneRecipeDisplay::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, TwoToOneRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC, TwoToOneRecipeDisplay::ingredient1,
                SlotDisplay.STREAM_CODEC, TwoToOneRecipeDisplay::ingredient2,
                SlotDisplay.STREAM_CODEC, TwoToOneRecipeDisplay::result,
                SlotDisplay.STREAM_CODEC, TwoToOneRecipeDisplay::craftingStation,
                TwoToOneRecipeDisplay::new
        );

        public static final RecipeDisplay.Type<TwoToOneRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        //region RecipeDisplay

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flags) {
            return this.ingredient1.isEnabled(flags) && this.ingredient2.isEnabled(flags) &&
                    RecipeDisplay.super.isEnabled(flags);
        }

        //endregion
    }

    //endregion
}
