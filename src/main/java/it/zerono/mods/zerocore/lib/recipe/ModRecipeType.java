/*
 *
 * ModRecipeType.java
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

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModRecipeType<Recipe extends IModRecipe>
        implements RecipeType<Recipe> {

    public static <Recipe extends IModRecipe> ModRecipeType<Recipe> create(final ResourceLocation id) {

        final ModRecipeType<Recipe> type = new ModRecipeType<>(id);

        s_types.add(type);
        return type;
    }

    public static void invalidate() {
        s_types.forEach(ModRecipeType::invalidateCache);
    }

    public ResourceLocation getId() {
        return this._id;
    }

    public List<RecipeHolder<Recipe>> getRecipes() {

        if (this._cache.isEmpty()) {
            this._cache = CodeHelper.getMinecraftServer()
                    .map(MinecraftServer::getRecipeManager)
                    .map(RecipeManager::recipeMap)
                    .map(map -> map.byType(this))
                    .map(ObjectArrayList::new)
                    .map(ObjectLists::unmodifiable)
                    .orElseGet(ObjectLists::emptyList);
        }

        return this._cache;
    }

    public List<RecipeHolder<Recipe>> getRecipes(final Predicate<Recipe> filter) {
        return this.stream()
                .filter(holder -> filter.test(holder.value()))
                .collect(Collectors.toList());
    }

    public <R extends Recipe> List<R> getRecipes(final Predicate<Recipe> filter, final Function<@NotNull Recipe, @NotNull R> mapping) {
        return this.stream()
                .map(RecipeHolder::value)
                .filter(filter)
                .map(mapping::apply)
                .collect(Collectors.toList());
    }

    public Stream<RecipeHolder<Recipe>> stream() {
        return this.getRecipes().stream();
    }

    public Optional<RecipeHolder<Recipe>> findFirst(final Predicate<Recipe> predicate) {
        return this.stream().filter(holder -> predicate.test(holder.value())).findFirst();
    }

    public boolean contains(final Predicate<Recipe> predicate) {
        return this.stream()
                .map(RecipeHolder::value)
                .anyMatch(predicate);
    }

    //region Object

    @Override
    public String toString() {
        return this._id.toString();
    }

    //endregion
    //region internals

    protected ModRecipeType(final ResourceLocation id) {

        this._id = id;
        this._cache = ObjectLists.emptyList();
    }

    protected void invalidateCache() {
        this._cache = ObjectLists.emptyList();
    }

    private static final List<ModRecipeType<? extends IModRecipe>> s_types = Lists.newLinkedList();

    private final ResourceLocation _id;
    private ObjectList<RecipeHolder<Recipe>> _cache;

    //endregion
}
