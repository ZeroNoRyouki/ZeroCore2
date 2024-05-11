/*
 *
 * ModRecipeBuilder.java
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

package it.zerono.mods.zerocore.lib.datagen.provider.recipe;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractModRecipeBuilder<Recipe extends ModRecipe, Result, RecipeResult extends IRecipeResult<Result>,
            Builder extends AbstractModRecipeBuilder<Recipe, Result, RecipeResult, Builder>> {

    protected AbstractModRecipeBuilder(RecipeResult result) {

        Preconditions.checkNotNull(result, "Result must not be null");

        this._result = result;
        this._criteria = new LinkedHashMap<>();
        this._conditions = new ObjectArrayList<>(8);
    }

    protected abstract Recipe getRecipe();

    public void build(RecipeOutput output) {
        this.build(output, this._result.getId());
    }

    public void build(RecipeOutput output, ResourceLocation id) {

        final var conditions = this._conditions.toArray(new ICondition[0]);
        final var advancementHolder = buildAdvancements(output, this._criteria, id);

        output.accept(id, this.getRecipe(), advancementHolder, conditions);
    }

    public Builder addCriterion(String name, Criterion<?> criterion) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name must not be null or empty");
        Preconditions.checkNotNull(criterion, "Criterion must not be null");

        if (null != this._criteria.put(name, criterion)) {
            throw new IllegalArgumentException("A criterion named " + name + " already exists");
        }

        return this.self();
    }

    public Builder addCondition(ICondition condition) {

        this._conditions.add(condition);
        return this.self();
    }

    //region internals

    private Builder self() {
        //noinspection unchecked
        return (Builder)this;
    }

    private static AdvancementHolder buildAdvancements(RecipeOutput output, Map<String, Criterion<?>> criteria,
                                                       ResourceLocation id) {

        final var builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        criteria.forEach(builder::addCriterion);

        return builder.build(id.withPrefix("recipes/"));
    }

    private final RecipeResult _result;
    private final List<ICondition> _conditions;
    private final Map<String, Criterion<?>> _criteria;

    //endregion
}
