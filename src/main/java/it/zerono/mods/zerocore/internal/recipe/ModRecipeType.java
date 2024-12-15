package it.zerono.mods.zerocore.internal.recipe;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.recipe.IModRecipe;
import it.zerono.mods.zerocore.lib.recipe.IModRecipeType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class ModRecipeType<ModRecipe extends IModRecipe>
        implements IModRecipeType<ModRecipe>, IModRecipesCache {

    ModRecipeType(ResourceLocation id) {

        Preconditions.checkNotNull(id, "Id must not be null");

        this._key = ResourceKey.create(Registries.RECIPE_TYPE, id);
        this._cache = this._immutableCache = ObjectLists.emptyList();
    }

    //region IModRecipesCache

    public void addRecipe(Recipe<?> recipe) {

        if (recipe instanceof IModRecipe modRecipe && this == modRecipe.getType()) {

            if (ObjectLists.EMPTY_LIST == this._cache) {

                this._cache = new ObjectArrayList<>(32);
                this._immutableCache = ObjectLists.unmodifiable(this._cache);
            }

            //noinspection unchecked
            this._cache.add(new RecipeHolder<>(modRecipe.getRegistrationKey(), (ModRecipe) modRecipe));
        }
    }

    @Override
    public void invalidateCache() {
        this._cache = this._immutableCache = ObjectLists.emptyList();
    }

    //endregion
    //region IModRecipeType<ModRecipe>

    @Override
    public ResourceKey<RecipeType<?>> getKey() {
        return this._key;
    }

    @Override
    public List<RecipeHolder<ModRecipe>> getRecipes() {

        if (this._cache.isEmpty()) {

            this._cache = CodeHelper.getMinecraftServer()
                    .map(MinecraftServer::getRecipeManager)
                    .map(RecipeManager::recipeMap)
                    .map(map -> map.byType(this))
                    .map(ObjectArrayList::new)
                    .map(ObjectLists::unmodifiable)
                    .orElseGet(ObjectLists::emptyList);

            this._immutableCache = ObjectLists.unmodifiable(this._cache);
        }

        return this._immutableCache;
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this.getId().toString();
    }

    //endregion
    //region internals

    private final ResourceKey<RecipeType<?>> _key;

    private ObjectList<RecipeHolder<ModRecipe>> _cache;
    private ObjectList<RecipeHolder<ModRecipe>> _immutableCache;

    //endregion
}
