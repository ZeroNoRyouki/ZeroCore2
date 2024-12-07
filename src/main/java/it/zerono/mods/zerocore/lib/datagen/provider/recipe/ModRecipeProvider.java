package it.zerono.mods.zerocore.lib.datagen.provider.recipe;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.recipe.IManyToOneModRecipe;
import it.zerono.mods.zerocore.lib.recipe.IOneToOneModRecipe;
import it.zerono.mods.zerocore.lib.recipe.ITwoToOneModRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.*;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class ModRecipeProvider
        extends RecipeProvider {

    protected ModRecipeProvider(ModRecipeProviderRunner<? extends ModRecipeProvider> mainProvider,
                                HolderLookup.Provider registryLookupProvider, RecipeOutput output) {

        super(registryLookupProvider, output);

        Preconditions.checkNotNull(mainProvider, "Main provider must not be null");

        this._mainProvider = mainProvider;
        this._holderGettersCache = new Object2ObjectArrayMap<>(8);
    }

    protected <T> HolderGetter<T> holderGetterOf(ResourceKey<? extends Registry<? extends T>> registryKey) {
        //noinspection unchecked
        return (HolderGetter<T>) this._holderGettersCache.computeIfAbsent(registryKey, this.registries::lookupOrThrow);
    }

    protected ResourceLocationBuilder root() {
        return this._mainProvider.root();
    }

    protected ResourceLocationBuilder craftingRoot() {
        return this.root().appendPath("crafting");
    }

    protected ResourceLocationBuilder blastingRoot() {
        return this.root().appendPath("blasting");
    }

    protected ResourceLocationBuilder smeltingRoot() {
        return this.root().appendPath("smelting");
    }

    protected ResourceLocationBuilder cookingRoot() {
        return this.root().appendPath("cooking");
    }

    protected ResourceLocationBuilder smokingRoot() {
        return this.root().appendPath("smoking");
    }

    protected ResourceLocationBuilder stonecuttingRoot() {
        return this.root().appendPath("stonecutting");
    }

    protected ResourceLocationBuilder smithingRoot() {
        return this.root().appendPath("smithing");
    }

    protected ResourceLocationBuilder miscRoot() {
        return this.root().appendPath("misc");
    }

    protected static ResourceKey<Recipe<?>> recipeKeyFrom(ResourceLocation id) {

        Preconditions.checkNotNull(id, "Id must not be null");

        return ResourceKey.create(Registries.RECIPE, id);
    }

    protected String group(String name) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name must not be null or empty");

        return this.root().namespace() + ":" + name;
    }

    protected ShapedRecipeBuilder shaped(RecipeCategory category, Supplier<? extends ItemLike> result, int amount) {

        validateResultAndCategory(category, result);
        Preconditions.checkArgument(amount > 0, "Amount must be greater than zero");

        return super.shaped(category, result.get(), amount);
    }

    protected ShapedRecipeBuilder shaped(RecipeCategory category, Supplier<? extends ItemLike> result) {
        return this.shaped(category, result, 1);
    }

    protected ShapedRecipeBuilder shaped(RecipeCategory category, ItemStack result) {
        return ShapedRecipeBuilder.shaped(this.holderGetterOf(Registries.ITEM), category, result);
    }

    protected ShapelessRecipeBuilder shapeless(RecipeCategory category, Supplier<? extends ItemLike> result, int amount) {

        validateResultAndCategory(category, result);
        Preconditions.checkArgument(amount > 0, "Amount must be greater than zero");

        return super.shapeless(category, result.get(), amount);
    }

    protected ShapelessRecipeBuilder shapeless(RecipeCategory category, Supplier<? extends ItemLike> result) {
        return this.shapeless(category, result, 1);
    }

    protected ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemStack result) {
        return ShapelessRecipeBuilder.shapeless(this.holderGetterOf(Registries.ITEM), category, result);
    }

    protected SimpleCookingRecipeBuilder campfireCooking(RecipeCategory category, Supplier<? extends ItemLike> result,
                                                         Ingredient ingredient, float experience, int cookingTime) {

        validateResultAndCategory(category, result);
        validateCookingParameters(ingredient, experience, cookingTime);

        return SimpleCookingRecipeBuilder.campfireCooking(ingredient, category, result.get(), experience, cookingTime);
    }

    protected SimpleCookingRecipeBuilder blasting(RecipeCategory category, Supplier<? extends ItemLike> result,
                                                  Ingredient ingredient, float experience, int cookingTime) {

        validateResultAndCategory(category, result);
        validateCookingParameters(ingredient, experience, cookingTime);

        return SimpleCookingRecipeBuilder.blasting(ingredient, category, result.get(), experience, cookingTime);
    }

    protected SimpleCookingRecipeBuilder smelting(RecipeCategory category, Supplier<? extends ItemLike> result,
                                                  Ingredient ingredient, float experience, int cookingTime) {

        validateResultAndCategory(category, result);
        validateCookingParameters(ingredient, experience, cookingTime);

        return SimpleCookingRecipeBuilder.smelting(ingredient, category, result.get(), experience, cookingTime);
    }

    protected SimpleCookingRecipeBuilder smoking(RecipeCategory category, Supplier<? extends ItemLike> result,
                                                 Ingredient ingredient, float experience, int cookingTime) {

        validateResultAndCategory(category, result);
        validateCookingParameters(ingredient, experience, cookingTime);

        return SimpleCookingRecipeBuilder.smoking(ingredient, category, result.get(), experience, cookingTime);
    }

    protected SingleItemRecipeBuilder stonecutting(RecipeCategory category, Supplier<? extends ItemLike> result,
                                                   Ingredient ingredient, int amount) {

        validateResultAndCategory(category, result);
        Preconditions.checkArgument(amount > 0, "Amount must be greater than zero");

        return SingleItemRecipeBuilder.stonecutting(ingredient, category, result.get(), amount);
    }

    protected SingleItemRecipeBuilder stonecutting(RecipeCategory category, Supplier<? extends ItemLike> result,
                                                   Ingredient ingredient) {
        return this.stonecutting(category, result, ingredient, 1);
    }

    protected SmithingTransformRecipeBuilder smithing(RecipeCategory category, Supplier<? extends ItemLike> result,
                                                      Ingredient template, Ingredient base, Ingredient addition) {

        validateResultAndCategory(category, result);
        Preconditions.checkNotNull(template, "Template must not be null");
        Preconditions.checkNotNull(base, "Base must not be null");
        Preconditions.checkNotNull(addition, "Addition must not be null");

        return SmithingTransformRecipeBuilder.smithing(template, base, addition, category, result.get().asItem());
    }

    protected SmithingTrimRecipeBuilder smithingTrim(RecipeCategory category, Ingredient template, Ingredient base,
                                                     Ingredient addition) {

        Preconditions.checkNotNull(category, "Category must not be null");
        Preconditions.checkNotNull(template, "Template must not be null");
        Preconditions.checkNotNull(base, "Base must not be null");
        Preconditions.checkNotNull(addition, "Addition must not be null");

        return SmithingTrimRecipeBuilder.smithingTrim(template, base, addition, category);
    }

    protected <Ingredient1, Result, RecipeIngredient1 extends IRecipeIngredient<Ingredient1>,
            RecipeResult extends IRecipeResult<Result>,
            OneToOneRecipe extends IOneToOneModRecipe<Ingredient1, Result, RecipeIngredient1, RecipeResult>>
    OneToOneRecipeBuilder<Ingredient1, Result, RecipeIngredient1, RecipeResult, OneToOneRecipe>
    oneToOne(RecipeIngredient1 ingredient, RecipeResult result,
             BiFunction<RecipeIngredient1, RecipeResult, @NotNull OneToOneRecipe> recipeFactory) {
        return new OneToOneRecipeBuilder<>(this::holderGetterOf, ingredient, result, recipeFactory);
    }

    protected <Ingredient1, Ingredient2, Result, RecipeIngredient1 extends IRecipeIngredient<Ingredient1>,
            RecipeIngredient2 extends IRecipeIngredient<Ingredient2>, RecipeResult extends IRecipeResult<Result>,
            TwoToOneRecipe extends ITwoToOneModRecipe<Ingredient1, Ingredient2, Result, RecipeIngredient1, RecipeIngredient2, RecipeResult>>
    TwoToOneRecipeBuilder<Ingredient1, Ingredient2, Result, RecipeIngredient1, RecipeIngredient2, RecipeResult, TwoToOneRecipe>
    twoToOne(RecipeIngredient1 ingredient1, RecipeIngredient2 ingredient2, RecipeResult result,
             TriFunction<@NotNull RecipeIngredient1, @NotNull RecipeIngredient2, @NotNull RecipeResult, @NotNull TwoToOneRecipe> recipeFactory) {
        return new TwoToOneRecipeBuilder<>(this::holderGetterOf, ingredient1, ingredient2, result, recipeFactory);
    }

    protected <Ingredient1, Result, RecipeIngredient1 extends IRecipeIngredient<Ingredient1>,
            RecipeResult extends IRecipeResult<Result>,
            ManyToOneRecipe extends IManyToOneModRecipe<Ingredient1, Result, RecipeIngredient1, RecipeResult>>
    ManyToOneRecipeBuilder<Ingredient1, Result, RecipeIngredient1, RecipeResult, ManyToOneRecipe>
    manyToOne(RecipeResult result,
              BiFunction<@NotNull List<RecipeIngredient1>, @NotNull RecipeResult, @NotNull ManyToOneRecipe> recipeFactory) {
        return new ManyToOneRecipeBuilder<>(this::holderGetterOf, result, recipeFactory);
    }

    //region conditional

    protected static ICondition not(ICondition condition) {

        Preconditions.checkNotNull(condition, "Condition must not be null");

        return new NotCondition(condition);
    }

    protected static ICondition and(ICondition... conditions) {
        return new AndCondition(Lists.newArrayList(conditions));
    }

    protected static ICondition or(ICondition... conditions) {
        return new OrCondition(Lists.newArrayList(conditions));
    }

    protected static ICondition modLoaded(String modId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(modId), "Mod ID must not be null or empty");

        return new ModLoadedCondition(modId);
    }

    protected static ICondition allModsLoaded(String... modIds) {
        return and(Stream.of(modIds)
                .map(ModRecipeProvider::modLoaded)
                .toArray(ICondition[]::new));
    }

    protected static ICondition anyModsLoaded(String... modIds) {
        return or(Stream.of(modIds)
                .map(ModRecipeProvider::modLoaded)
                .toArray(ICondition[]::new));
    }

    protected void withFallback(RecipeOutput output, ResourceLocation name, TagKey<Item> tag,
                                @Nullable ResourceLocation fallbackName, @Nullable TagKey<Item> fallbackTag,
                                Function<TagKey<Item>, RecipeBuilder> recipe) {

        if (null == fallbackTag || null == fallbackName) {

            recipe.apply(tag).save(output, recipeKeyFrom(name));

        } else {

            final var tagCondition = new TagEmptyCondition(tag.location());

            recipe.apply(tag).save(output.withConditions(not(tagCondition)), recipeKeyFrom(name));
            recipe.apply(fallbackTag).save(output.withConditions(tagCondition), recipeKeyFrom(fallbackName));
        }
    }

    //endregion
    //region helpers

    protected void storageBlock3x3(RecipeOutput output, String group,
                                   ResourceLocation toStorageId, RecipeCategory toStorageCategory,
                                   Supplier<? extends ItemLike> storage,
                                   ResourceLocation toComponentId, RecipeCategory toComponentCategory,
                                   Supplier<? extends ItemLike> component) {

        // 3x3 components -> 1 storage
        this.shapeless(toStorageCategory, storage)
                .requires(component.get(), 9)
                .group(group)
                .unlockedBy("has_item", has(component.get()))
                .save(output, recipeKeyFrom(toStorageId));

        // 1 storage -> 9 components
        this.shapeless(toComponentCategory, component, 9)
                .requires(storage.get())
                .group(group)
                .unlockedBy("has_item", has(storage.get()))
                .save(output, recipeKeyFrom(toComponentId));
    }

    protected void storageBlock3x3(RecipeOutput output, String name, String group,
                                   RecipeCategory toStorageCategory, Supplier<? extends ItemLike> storage,
                                   RecipeCategory toComponentCategory, Supplier<? extends ItemLike> component) {

        final var crafting = this.craftingRoot().append(name);

        this.storageBlock3x3(output, group,
                crafting.buildWithSuffix("_component_to_storage"), toStorageCategory, storage,
                crafting.buildWithSuffix("_storage_to_component"), toComponentCategory, component);
    }

    protected void storageBlock2x2(RecipeOutput output, String group,
                                   ResourceLocation toStorageId, RecipeCategory toStorageCategory,
                                   Supplier<? extends ItemLike> storage,
                                   ResourceLocation toComponentId, RecipeCategory toComponentCategory,
                                   Supplier<? extends ItemLike> component) {

        // 2x2 components -> 1 storage
        this.shaped(toStorageCategory, storage)
                .define('X', component.get())
                .pattern("XX")
                .pattern("XX")
                .group(group)
                .unlockedBy("has_item", has(component.get()))
                .save(output, recipeKeyFrom(toStorageId));

        // 1 storage -> 4 components
        this.shapeless(toComponentCategory, component, 4)
                .requires(storage.get())
                .group(group)
                .unlockedBy("has_item", has(storage.get()))
                .save(output, recipeKeyFrom(toComponentId));
    }

    protected void storageBlock2x2(RecipeOutput output, String name, String group,
                                   RecipeCategory toStorageCategory, Supplier<? extends ItemLike> storage,
                                   RecipeCategory toComponentCategory, Supplier<? extends ItemLike> component) {

        final var crafting = this.craftingRoot().append(name);

        this.storageBlock2x2(output, group,
                crafting.buildWithSuffix("_component_to_storage2x2"), toStorageCategory, storage,
                crafting.buildWithSuffix("_storage2x2_to_component"), toComponentCategory, component);
    }

    protected void nugget(RecipeOutput output, String group,
                          ResourceLocation toIngotId, RecipeCategory toIngotCategory, Supplier<? extends ItemLike> ingot,
                          ResourceLocation toNuggetId, RecipeCategory toNuggetCategory, Supplier<? extends ItemLike> nugget) {

        // 3x3 nuggets -> 1 ingot
        this.shapeless(toIngotCategory, ingot)
                .requires(nugget.get(), 9)
                .group(group)
                .unlockedBy("has_item", has(nugget.get()))
                .save(output, recipeKeyFrom(toIngotId));

        // 1 ingot -> 9 nuggets
        this.shapeless(toNuggetCategory, nugget, 9)
                .requires(ingot.get())
                .group(group)
                .unlockedBy("has_item", has(ingot.get()))
                .save(output, recipeKeyFrom(toNuggetId));
    }

    protected void nugget(RecipeOutput output, String name, String group,
                          RecipeCategory toIngotCategory, Supplier<? extends ItemLike> ingot,
                          RecipeCategory toNuggetCategory, Supplier<? extends ItemLike> nugget) {

        final var crafting = this.craftingRoot().append(name);

        this.nugget(output, group,
                crafting.buildWithSuffix("_nugget_to_ingot"), toIngotCategory, ingot,
                crafting.buildWithSuffix("_ingot_to_nugget"), toNuggetCategory, nugget);
    }

    //endregion
    //region internals

    private static void validateResultAndCategory(RecipeCategory category, Supplier<? extends ItemLike> result) {

        Preconditions.checkNotNull(category, "Category must not be null");
        Preconditions.checkNotNull(result, "Result must not be null");
    }

    private static void validateCookingParameters(Ingredient ingredient, float experience, int cookingTime) {

        Preconditions.checkNotNull(ingredient, "ingredient must not be null");
        Preconditions.checkArgument(experience >= 0, "Experience must be greater than or equal to zero");
        Preconditions.checkArgument(cookingTime >= 0, "Amount must be greater than or equal to zero");
    }

    private final ModRecipeProviderRunner<? extends ModRecipeProvider> _mainProvider;
    private final Map<ResourceKey<? extends Registry<?>>, HolderGetter<?>> _holderGettersCache;

    //endregion
}
