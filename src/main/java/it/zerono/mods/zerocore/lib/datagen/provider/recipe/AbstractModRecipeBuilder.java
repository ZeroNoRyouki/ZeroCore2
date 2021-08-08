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

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractModRecipeBuilder<Builder extends AbstractModRecipeBuilder<Builder>> {

    protected AbstractModRecipeBuilder(final ResourceLocation serializerId) {

        this._serializerId = serializerId;
        this._advancementBuilder = Advancement.Builder.advancement();
        this._conditions = Lists.newArrayList();
    }

    protected abstract FinishedRecipe getFinishedRecipe(ResourceLocation id);

    public void build(final Consumer<FinishedRecipe> consumer, final ResourceLocation id) {

        if (this.hasCriteria()) {
            this._advancementBuilder
                    .parent(new ResourceLocation("recipes/root"))
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(RequirementsStrategy.OR);
        }

        consumer.accept(this.getFinishedRecipe(id));
    }

    public Builder addCriterion(final String name, final CriterionTriggerInstance criterionIn) {

        this._advancementBuilder.addCriterion(name, criterionIn);
        return this.self();
    }

    public boolean hasCriteria() {
        return !this._advancementBuilder.getCriteria().isEmpty();
    }

    public Builder addCondition(final ICondition condition) {

        this._conditions.add(condition);
        return this.self();
    }

    //region RecipeResult

    protected abstract class AbstractFinishedRecipe
            implements FinishedRecipe {

        public AbstractFinishedRecipe(final ResourceLocation id) {
            this._id = id;
        }

        //region IFinishedRecipe

        /**
         * Gets the JSON for the recipe.
         */
        @Override
        public JsonObject serializeRecipe() {

            final JsonObject json = new JsonObject();

            JSONHelper.jsonSetResourceLocation(json, Lib.NAME_TYPE, _serializerId);

            if (!_conditions.isEmpty()) {
                json.add(Lib.NAME_CONDITIONS, _conditions.stream()
                        .map(CraftingHelper::serialize)
                        .collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
            }

            this.serializeRecipeData(json);
            return json;
        }

        /**
         * Gets the ID for the recipe.
         */
        @Override
        public ResourceLocation getId() {
            return this._id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(_serializerId),
                    () -> "Unknown recipe serializer: " + _serializerId);
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return hasCriteria() ? _advancementBuilder.serializeToJson() : null;
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson} is
         * non-null.
         */
        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return new ResourceLocation(_id.getNamespace(), "recipes/" + _id.getPath());
        }

        //endregion
        //region internals

        private final ResourceLocation _id;

        //endregion
    }

    //endregion
    //region internals

    private Builder self() {
        //noinspection unchecked
        return (Builder)this;
    }

    private final List<ICondition> _conditions;
    private final Advancement.Builder _advancementBuilder;
    private final ResourceLocation _serializerId;

    //endregion
}
