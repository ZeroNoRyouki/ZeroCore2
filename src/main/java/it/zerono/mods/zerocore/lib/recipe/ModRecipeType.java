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
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ModRecipeType<Recipe extends ModRecipe>
        implements IRecipeType<Recipe> {

    public static <Recipe extends ModRecipe> ModRecipeType<Recipe> create(final ResourceLocation id) {

        final ModRecipeType<Recipe> type = new ModRecipeType<>(id);

        s_types.add(type);
        return type;
    }

    public static void invalidate() {
        s_types.forEach(ModRecipeType::invalidateCache);
    }

    public static void onRegisterRecipes() {
        s_types.forEach(type -> Registry.register(Registry.RECIPE_TYPE, type._id, type));
    }

    public List<Recipe> getRecipes() {

        if (this._cache.isEmpty()) {

            final RecipeManager manager = CodeHelper.getRecipeManager();

            if (null != manager) {
                this._cache = manager.getAllRecipesFor(this);
            }
        }

        return this._cache;
    }

    public Stream<Recipe> stream() {
        return this.getRecipes().stream();
    }

    public Optional<Recipe> findFirst(final Predicate<Recipe> predicate) {
        return this.stream().filter(predicate).findFirst();
    }

    public boolean contains(final Predicate<Recipe> predicate) {
        return this.stream().anyMatch(predicate);
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
        this._cache = Collections.emptyList();
    }

    protected void invalidateCache() {
        this._cache = Collections.emptyList();
    }

    private static final List<ModRecipeType<? extends ModRecipe>> s_types = Lists.newLinkedList();

    private final ResourceLocation _id;
    private List<Recipe> _cache;

    //endregion
}
