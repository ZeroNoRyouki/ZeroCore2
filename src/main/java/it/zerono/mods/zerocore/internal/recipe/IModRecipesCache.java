package it.zerono.mods.zerocore.internal.recipe;

import net.minecraft.world.item.crafting.Recipe;

public interface IModRecipesCache {

    void addRecipe(Recipe<?> recipe);

    void invalidateCache();
}
