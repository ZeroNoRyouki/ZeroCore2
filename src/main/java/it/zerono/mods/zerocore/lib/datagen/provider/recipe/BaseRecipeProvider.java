package it.zerono.mods.zerocore.lib.datagen.provider.recipe;
/*
 * BaseRecipeProvider
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
 * Do not remove or edit this header
 *
 */

import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.*;
import net.minecraftforge.common.util.NonNullFunction;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseRecipeProvider
        extends RecipeProvider {

    protected BaseRecipeProvider(final DataGenerator generator) {
        super(generator);
    }

    //region cooking / smelting

    protected void blastingAndSmelting(final Consumer<IFinishedRecipe> consumer, final String name,
                                       final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                                       final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source) {
        this.blastingAndSmelting(consumer, name, nameToIdConverter, result, source, 1f, 200);
    }

    protected void blastingAndSmelting(final Consumer<IFinishedRecipe> consumer, final String name,
                                       final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                                       final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                                       final float xp, final int smeltingTime) {

        this.blasting(consumer, name, nameToIdConverter, result, source, xp, smeltingTime / 2);
        this.smelting(consumer, name, nameToIdConverter, result, source, xp, smeltingTime);
    }

    protected void blasting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source) {
        this.blasting(consumer, name, nameToIdConverter, result, source, 1f, 100);
    }

    protected void blasting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                            final ICondition... conditions) {
        this.blasting(consumer, name, nameToIdConverter, result, source, 1f, 100, conditions);
    }

    protected void blasting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                            final float xp, final int time) {
        CookingRecipeBuilder.blasting(Ingredient.of(source.get()), result.get(), xp, time)
                .unlockedBy("has_item", has(source.get()))
                .save(consumer, nameToIdConverter.apply("blasting/" + name));
    }

    protected void blasting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                            final float xp, final int time, final ICondition... conditions) {
        conditionalBuilder(conditions)
                .addRecipe(CookingRecipeBuilder.blasting(Ingredient.of(source.get()), result.get(), xp, time)
                        .unlockedBy("has_item", has(source.get()))::save)
                .build(consumer, nameToIdConverter.apply("blasting/" + name));
    }

    protected void smelting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source) {
        this.smelting(consumer, name, nameToIdConverter, result, source, 1f, 200);
    }

    protected void smelting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                            final ICondition... conditions) {
        this.smelting(consumer, name, nameToIdConverter, result, source, 1f, 200, conditions);
    }

    protected void smelting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                            final float xp, final int time) {
        CookingRecipeBuilder.smelting(Ingredient.of(source.get()), result.get(), xp, time)
                .unlockedBy("has_item", has(source.get()))
                .save(consumer, nameToIdConverter.apply("smelting/" + name));
    }

    protected void smelting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                            final float xp, final int time, final ICondition... conditions) {
        conditionalBuilder(conditions)
                .addRecipe(CookingRecipeBuilder.smelting(Ingredient.of(source.get()), result.get(), xp, time)
                        .unlockedBy("has_item", has(source.get()))::save)
                .build(consumer, nameToIdConverter.apply("smelting/" + name));
    }

    //endregion
    //region miscellanea

    protected void storageBlock3x3(final Consumer<IFinishedRecipe> consumer, final String name,
                                   final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                                   final String group, final Supplier<? extends IItemProvider> component,
                                   final Supplier<? extends IItemProvider> storage) {

        // 3x3 components -> 1 storage
        ShapelessRecipeBuilder.shapeless(storage.get())
                .requires(component.get(), 9)
                .group(group)
                .unlockedBy(name + "_has_storage", has(component.get()))
                .save(consumer, nameToIdConverter.apply(name + "_component_to_storage"));

        // 1 storage -> 9 components
        ShapelessRecipeBuilder.shapeless(component.get(), 9)
                .requires(storage.get())
                .group(group)
                .unlockedBy("has_item", has(storage.get()))
                .save(consumer, nameToIdConverter.apply("crafting/" + name + "_storage_to_component"));
    }

    protected void storageBlock2x2(final Consumer<IFinishedRecipe> consumer, final String name,
                                   final NonNullFunction<String, ResourceLocation> nameToIdConverter,
                                   final String group, final Supplier<? extends IItemProvider> component,
                                   final Supplier<? extends IItemProvider> storage) {

        // 2x2 components -> 1 storage
        ShapedRecipeBuilder.shaped(storage.get())
                .define('X', component.get())
                .pattern("XX")
                .pattern("XX")
                .group(group)
                .unlockedBy(name + "_has_storage", has(component.get()))
                .save(consumer, nameToIdConverter.apply(name + "_component_to_storage2x2"));

        // 1 storage -> 4 components
        ShapelessRecipeBuilder.shapeless(component.get(), 4)
                .requires(storage.get())
                .group(group)
                .unlockedBy("has_item", has(storage.get()))
                .save(consumer, nameToIdConverter.apply("crafting/" + name + "_storage2x2_to_component"));
    }

    protected static void recipeWithAlternativeTag(final Consumer<IFinishedRecipe> c,
                                                   final ResourceLocation name, @Nullable final ResourceLocation alternativeName,
                                                   final ITag.INamedTag<Item> tag, @Nullable final ITag.INamedTag<Item> alternativeTag,
                                                   final Function<ITag.INamedTag<Item>, ShapedRecipeBuilder> recipe) {

        if (null == alternativeTag || null == alternativeName) {

            recipe.apply(tag).save(c, name);

        } else {

            conditionalBuilder(not(new TagEmptyCondition(tag.getName())))
                    .addRecipe(recipe.apply(tag)::save)
                    .build(c, name);

            conditionalBuilder(new TagEmptyCondition(tag.getName()))
                    .addRecipe(recipe.apply(alternativeTag)::save)
                    .build(c, alternativeName);
        }
    }

    //endregion
    //region Conditional recipes

    protected static ICondition not(final ICondition condition) {
        return new NotCondition(condition);
    }

    protected static ICondition and(final ICondition... conditions) {
        return new AndCondition(conditions);
    }

    protected static ICondition or(final ICondition... conditions) {
        return new OrCondition(conditions);
    }

    protected static ICondition modLoaded(final String modId) {
        return new ModLoadedCondition(modId);
    }

    protected static ConditionalRecipe.Builder conditionalBuilder(final ICondition... conditions) {

        if (0 == conditions.length) {
            throw new IllegalArgumentException("No conditions were provided");
        }

        final ConditionalRecipe.Builder builder = ConditionalRecipe.builder();

        for (final ICondition condition : conditions) {
            builder.addCondition(condition);
        }

        return builder;
    }

    //endregion
}
