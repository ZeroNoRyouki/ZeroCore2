package it.zerono.mods.zerocore.lib.datagen.provider;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.IModDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.*;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class ModRecipeProvider
        extends RecipeProvider
        implements IModDataProvider {

    protected ModRecipeProvider(String name, PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                ResourceLocationBuilder modLocationRoot) {

        super(output);

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name must not be null or empty");
        Preconditions.checkNotNull(output, "Output must not be null");
        Preconditions.checkNotNull(registryLookup, "Registry lookup must not be null");
        Preconditions.checkNotNull(modLocationRoot, "Mod location root must not be null");

        this._settings = new ProviderSettings(name, output, registryLookup, modLocationRoot);
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

    protected ShapedRecipeBuilder shaped(RecipeCategory category, Supplier<? extends ItemLike> result, int amount) {

        validateResultAndCategory(category, result);
        Preconditions.checkArgument(amount > 0, "Amount must be greater than zero");

        return ShapedRecipeBuilder.shaped(category, result.get(), amount);
    }

    protected ShapedRecipeBuilder shaped(RecipeCategory category, Supplier<? extends ItemLike> result) {
        return this.shaped(category, result, 1);
    }

    protected ShapelessRecipeBuilder shapeless(RecipeCategory category, Supplier<? extends ItemLike> result, int amount) {

        validateResultAndCategory(category, result);
        Preconditions.checkArgument(amount > 0, "Amount must be greater than zero");

        return ShapelessRecipeBuilder.shapeless(category, result.get(), amount);
    }

    protected ShapelessRecipeBuilder shapeless(RecipeCategory category, Supplier<? extends ItemLike> result) {
        return this.shapeless(category, result, 1);
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

    protected UpgradeRecipeBuilder smithing(RecipeCategory category, Supplier<? extends ItemLike> result,
                                            Ingredient base, Ingredient addition) {

        validateResultAndCategory(category, result);
        Preconditions.checkNotNull(base, "Base must not be null");
        Preconditions.checkNotNull(addition, "Addition must not be null");

        return UpgradeRecipeBuilder.smithing(base, addition, category, result.get().asItem());
    }

    //region conditional

    protected static ICondition not(ICondition condition) {

        Preconditions.checkNotNull(condition, "Condition must not be null");

        return new NotCondition(condition);
    }

    protected static ICondition and(ICondition... conditions) {
        return new AndCondition(conditions);
    }

    protected static ICondition or(ICondition... conditions) {
        return new OrCondition(conditions);
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

    //TODO multi loader version
    protected ConditionalRecipe.Builder conditional(ICondition... conditions) {

        if (0 == conditions.length) {
            throw new IllegalArgumentException("At least one condition must be provided");
        }

        final var builder = ConditionalRecipe.builder();

        for (final var condition : conditions) {
            builder.addCondition(condition);
        }

        return builder;
    }

    protected void withFallback(Consumer<FinishedRecipe> builder, ResourceLocation name, TagKey<Item> tag,
                                @Nullable ResourceLocation fallbackName, @Nullable TagKey<Item> fallbackTag,
                                Function<TagKey<Item>, ShapedRecipeBuilder> recipe) {

        if (null == fallbackTag || null == fallbackName) {

            recipe.apply(tag).save(builder, name);

        } else {

            this.conditional(not(new TagEmptyCondition(tag.location())))
                    .addRecipe(recipe.apply(tag)::save)
                    .build(builder, name);

            this.conditional(new TagEmptyCondition(tag.location()))
                    .addRecipe(recipe.apply(fallbackTag)::save)
                    .build(builder, fallbackName);
        }
    }

    //endregion
    //region helpers

    protected void storageBlock3x3(Consumer<FinishedRecipe> builder, String name, String group,
                                   ResourceLocation toStorageId, RecipeCategory toStorageCategory,
                                   Supplier<? extends ItemLike> storage,
                                   ResourceLocation toComponentId, RecipeCategory toComponentCategory,
                                   Supplier<? extends ItemLike> component) {

        // 3x3 components -> 1 storage
        this.shapeless(toStorageCategory, storage)
                .requires(component.get(), 9)
                .group(group)
                .unlockedBy("has_item", has(component.get()))
                .save(builder, toStorageId);

        // 1 storage -> 9 components
        this.shapeless(toComponentCategory, component, 9)
                .requires(storage.get())
                .group(group)
                .unlockedBy("has_item", has(storage.get()))
                .save(builder, toComponentId);
    }

    protected void storageBlock3x3(Consumer<FinishedRecipe> builder, String name, String group,
                                   RecipeCategory toStorageCategory, Supplier<? extends ItemLike> storage,
                                   RecipeCategory toComponentCategory, Supplier<? extends ItemLike> component) {

        final var crafting = this.craftingRoot().append(name);

        this.storageBlock3x3(builder, name, group,
                crafting.buildWithSuffix("_component_to_storage"), toStorageCategory, storage,
                crafting.buildWithSuffix("_storage_to_component"), toComponentCategory, component);
    }

    protected void storageBlock2x2(Consumer<FinishedRecipe> builder, String name, String group,
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
                .save(builder, toStorageId);

        // 1 storage -> 4 components
        this.shapeless(toComponentCategory, component, 4)
                .requires(storage.get())
                .group(group)
                .unlockedBy("has_item", has(storage.get()))
                .save(builder, toComponentId);
    }

    protected void storageBlock2x2(Consumer<FinishedRecipe> builder, String name, String group,
                                   RecipeCategory toStorageCategory, Supplier<? extends ItemLike> storage,
                                   RecipeCategory toComponentCategory, Supplier<? extends ItemLike> component) {

        final var crafting = this.craftingRoot().append(name);

        this.storageBlock2x2(builder, name, group,
                crafting.buildWithSuffix("_component_to_storage2x2"), toStorageCategory, storage,
                crafting.buildWithSuffix("_storage2x2_to_component"), toComponentCategory, component);
    }

    protected void nugget(Consumer<FinishedRecipe> builder, String name, String group,
                          ResourceLocation toIngotId, RecipeCategory toIngotCategory, Supplier<? extends ItemLike> ingot,
                          ResourceLocation toNuggetId, RecipeCategory toNuggetCategory, Supplier<? extends ItemLike> nugget) {

        // 3x3 nuggets -> 1 ingot
        this.shapeless(toIngotCategory, ingot)
                .requires(nugget.get(), 9)
                .group(group)
                .unlockedBy("has_item", has(nugget.get()))
                .save(builder, toIngotId);

        // 1 ingot -> 9 nuggets
        this.shapeless(toNuggetCategory, nugget, 9)
                .requires(ingot.get())
                .group(group)
                .unlockedBy("has_item", has(ingot.get()))
                .save(builder, toNuggetId);
    }

    protected void nugget(Consumer<FinishedRecipe> builder, String name, String group,
                          RecipeCategory toIngotCategory, Supplier<? extends ItemLike> ingot,
                          RecipeCategory toNuggetCategory, Supplier<? extends ItemLike> nugget) {

        final var crafting = this.craftingRoot().append(name);

        this.nugget(builder, name, group,
                crafting.buildWithSuffix("_nugget_to_ingot"), toIngotCategory, ingot,
                crafting.buildWithSuffix("_ingot_to_nugget"), toNuggetCategory, nugget);
    }

    //endregion
    //region IModDataProvider

    @Override
    public void provideData() {
        return;
    }

    @Override
    public CompletableFuture<?> processData(CachedOutput cache, HolderLookup.Provider registryLookup) {

        return super.run(cache);

//        return CompletableFuture.completedFuture(null);
    }

    @Override
    public ProviderSettings getSettings() {
        return this._settings;
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

    private final ProviderSettings _settings;

    //endregion
}
