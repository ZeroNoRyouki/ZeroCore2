/*
 *
 * IManyToOneModRecipe.java
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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
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

public interface IManyToOneModRecipe<Ingredient, Result, RecipeIngredient extends IRecipeIngredient<Ingredient>,
        RecipeResult extends IRecipeResult<Result>>
        extends IModRecipe, Predicate<List<Ingredient>> {

    static <Ingredient, Result, RecipeIngredient extends IRecipeIngredient<Ingredient>,
            RecipeResult extends IRecipeResult<Result>,
            Recipe extends IManyToOneModRecipe<Ingredient, Result, RecipeIngredient, RecipeResult>>
    RecipeSerializer<Recipe> createManyToOneSerializer(String ingredientsFieldName,
                                                       ModCodecs<RecipeIngredient, RegistryFriendlyByteBuf> ingredientsCodecs,
                                                       String resultFieldName,
                                                       ModCodecs<RecipeResult, RegistryFriendlyByteBuf> resultCodecs,
                                                       BiFunction<List<RecipeIngredient>, RecipeResult, Recipe> recipeFactory) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(ingredientsFieldName), "Ingredients field name must not be null nor empty");
        Preconditions.checkNotNull(ingredientsCodecs, "Ingredients codecs must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(resultFieldName), "Result field name must not be null nor empty");
        Preconditions.checkNotNull(resultCodecs, "Result codecs must not be null");
        Preconditions.checkNotNull(recipeFactory, "Recipe factory must not be null");

        final MapCodec<Recipe> codec = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ingredientsCodecs.listField(ingredientsFieldName, IManyToOneModRecipe::ingredients),
                        resultCodecs.field(resultFieldName, IManyToOneModRecipe::result)
                ).apply(instance, recipeFactory));

        final StreamCodec<RegistryFriendlyByteBuf, Recipe> streamCodec = StreamCodec.composite(
                ingredientsCodecs.listStreamCodec(), IManyToOneModRecipe::ingredients,
                resultCodecs.streamCodec(), IManyToOneModRecipe::result,
                recipeFactory
        );

        return new ModRecipeSerializer<>(codec, streamCodec);
    }

    int getRecipeIngredientsCount();

    List<RecipeIngredient> ingredients();

    RecipeResult result();

    //region Predicate<List<IngredientT>>

    @Override
    default boolean test(List<Ingredient> stacks) {

        List<RecipeIngredient> ingredients = this.ingredients();
        int ingredientsCount = ingredients.size();

        if (ingredientsCount != stacks.size()) {
            return false;
        }

        if (1 == ingredientsCount) {
            return ingredients.getFirst().test(stacks.getFirst());
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
    //region IModRecipe

    @Override
    default ResourceKey<Recipe<?>> getRegistrationKey() {
        return ResourceKey.create(Registries.RECIPE, this.result().getId());
    }

    @Override
    default List<RecipeDisplay> display() {

        final List<SlotDisplay> ingredients = this.ingredients().stream()
                .map(IRecipeIngredient::asSlotDisplay)
                .toList();

        return List.of(new ManyToOneRecipeDisplay(ingredients, this.result().asSlotDisplay(),
                this.getCraftingStationSlotDisplay()));
    }

    //endregion
    //region OneToOneRecipeDisplay

    record ManyToOneRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation)
            implements RecipeDisplay {

        public static final MapCodec<ManyToOneRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(ManyToOneRecipeDisplay::ingredients),
                                SlotDisplay.CODEC.fieldOf("result").forGetter(ManyToOneRecipeDisplay::result),
                                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(ManyToOneRecipeDisplay::craftingStation)
                        )
                        .apply(instance, ManyToOneRecipeDisplay::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ManyToOneRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), ManyToOneRecipeDisplay::ingredients,
                SlotDisplay.STREAM_CODEC, ManyToOneRecipeDisplay::result,
                SlotDisplay.STREAM_CODEC, ManyToOneRecipeDisplay::craftingStation,
                ManyToOneRecipeDisplay::new
        );

        public static final RecipeDisplay.Type<ManyToOneRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        //region RecipeDisplay

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flags) {

            for (final var ingredient : this.ingredients) {
                if (!ingredient.isEnabled(flags)) {
                    return false;
                }
            }

            return RecipeDisplay.super.isEnabled(flags);
        }

        //endregion
    }

    //endregion
}
