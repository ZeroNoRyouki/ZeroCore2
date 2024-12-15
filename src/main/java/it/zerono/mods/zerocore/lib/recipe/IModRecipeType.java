package it.zerono.mods.zerocore.lib.recipe;

import it.zerono.mods.zerocore.internal.recipe.ModRecipeTypeRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IModRecipeType<ModRecipe extends IModRecipe>
        extends RecipeType<ModRecipe> {

    static <ModRecipe extends IModRecipe> IModRecipeType<ModRecipe> create(ResourceLocation id) {
        return ModRecipeTypeRegistry.createType(id);
    }

    ResourceKey<RecipeType<?>> getKey();

    default ResourceLocation getId() {
        return this.getKey().location();
    }

    List<RecipeHolder<ModRecipe>> getRecipes();

    default Stream<RecipeHolder<ModRecipe>> stream() {
        return this.getRecipes().stream();
    }

    default List<RecipeHolder<ModRecipe>> getRecipes(Predicate<ModRecipe> filter) {
        return this.stream()
                .filter(holder -> filter.test(holder.value()))
                .collect(Collectors.toList());
    }

    default <R extends ModRecipe> List<R> getRecipes(Predicate<ModRecipe> filter,
                                                     Function<@NotNull ModRecipe, @NotNull R> mapping) {
        return this.stream()
                .map(RecipeHolder::value)
                .filter(filter)
                .map(mapping::apply)
                .collect(Collectors.toList());
    }

    default Optional<RecipeHolder<ModRecipe>> findFirst(Predicate<ModRecipe> predicate) {
        return this.stream().filter(holder -> predicate.test(holder.value())).findFirst();
    }

    default boolean contains(Predicate<ModRecipe> predicate) {
        return this.stream()
                .map(RecipeHolder::value)
                .anyMatch(predicate);
    }
}
