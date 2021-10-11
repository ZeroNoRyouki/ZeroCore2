/*
 *
 * AbstractManyToOneRecipe.java
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractManyToOneRecipe<IngredientT, Result,
                                                RecipeIngredient extends IRecipeIngredient<IngredientT>,
                                                RecipeResult extends IRecipeResult<Result>>
        extends ModRecipe
        implements Predicate<List<IngredientT>>, ISerializableRecipe {

    @FunctionalInterface
    public interface IRecipeFactory<IngredientT, Result, RecipeIngredient extends IRecipeIngredient<IngredientT>,
                                        RecipeResult extends IRecipeResult<Result>,
                                        Recipe extends AbstractManyToOneRecipe<IngredientT, Result, RecipeIngredient, RecipeResult>> {

        Recipe create(ResourceLocation id, List<RecipeIngredient> ingredients, RecipeResult result);
    }

    protected AbstractManyToOneRecipe(final ResourceLocation id, final List<RecipeIngredient> ingredients,
                                      final RecipeResult result, final IntFunction<String> jsonIngredientsLabelsSupplier) {

        super(id);

        Preconditions.checkArgument(!ingredients.isEmpty(), "Trying to create a recipe without ingredients");

        final ObjectList<RecipeIngredient> copy = ingredients.size() == 1 ? ObjectLists.singleton(ingredients.get(0)) :
                new ObjectArrayList<>(ingredients);

        this._ingredients = ObjectLists.unmodifiable(copy);
        this._result = result;
        this._jsonIngredientsLabelsSupplier = jsonIngredientsLabelsSupplier;
    }

    public int getRecipeIngredientsCount() {
        return this._ingredients.size();
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return this._ingredients;
    }

    public RecipeResult getResult() {
        return this._result;
    }

    //region Predicate<List<IngredientT>>

    @Override
    public boolean test(final List<IngredientT> stacks) {

        List<RecipeIngredient> ingredients = this.getRecipeIngredients();
        int ingredientsCount = ingredients.size();

        if (ingredientsCount != stacks.size()) {
            return false;
        }

        if (1 == ingredientsCount) {
            return ingredients.get(0).test(stacks.get(0));
        }

        ingredients = new ObjectArrayList<>(ingredients);
        ingredientsCount = ingredients.size();

        boolean found;

        for (final IngredientT stack : stacks) {

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
    //region ModRecipe

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return buildVanillaIngredientsList(this.getRecipeIngredients().stream()
                .flatMap(i -> i.asVanillaIngredients().stream())
                .collect(Collectors.toList()));
    }

    //endregion
    //region ISerializableRecipe

    @Override
    public void serializeTo(final PacketBuffer buffer) {

        final List<RecipeIngredient> ingredients = this.getRecipeIngredients();
        final int count = ingredients.size();

        buffer.writeInt(count);

        //noinspection ForLoopReplaceableByForEach
        for (int idx = 0; idx < count; ++idx) {
            ingredients.get(idx).serializeTo(buffer);
        }

        this._result.serializeTo(buffer);
    }

    @Override
    public JsonElement serializeTo() {

        final JsonObject json = new JsonObject();
        final List<RecipeIngredient> ingredients = this.getRecipeIngredients();
        final int count = ingredients.size();

        for (int idx = 0; idx < count; ++idx) {
            json.add(this._jsonIngredientsLabelsSupplier.apply(idx), ingredients.get(idx).serializeTo());
        }

        json.add(Lib.NAME_RESULT, this._result.serializeTo());
        return json;
    }

    //endregion
    //region internals

    private final List<RecipeIngredient> _ingredients;
    private final RecipeResult _result;
    private final IntFunction<String> _jsonIngredientsLabelsSupplier;

    //endregion
}
