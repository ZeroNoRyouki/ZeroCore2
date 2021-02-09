/*
 *
 * RecipeHolder.java
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

package it.zerono.mods.zerocore.lib.recipe.holder;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.recipe.ModRecipe;

import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

public class RecipeHolder<Recipe extends ModRecipe>
        implements IRecipeHolder<Recipe> {

    /**
     * Return a builder to construct a new RecipeHolder
     *
     * @param factory a Supplier of a new recipe instance based on the available ingredients. May return null.
     */
    public static <Recipe extends ModRecipe> Builder<Recipe> builder(final Function<IRecipeHolder<Recipe>, IHeldRecipe<Recipe>> factory,
                                                                     final ToIntFunction<Recipe> requiredTicksSupplier) {
        return new Builder<>(factory, requiredTicksSupplier);
    }

    //region IRecipeHolder<HeldRecipe>

    /**
     * @return the recipe currently held by this recipe holder
     */
    @Override
    public <HeldRecipe extends IHeldRecipe<Recipe>> Optional<HeldRecipe> getHeldRecipe() {
        //noinspection unchecked
        return Optional.ofNullable((HeldRecipe)this._recipe);
    }

    /**
     * @return a new IHeldRecipe from the currently available ingredients
     */
    @Override
    public <HeldRecipe extends IHeldRecipe<Recipe>> Optional<HeldRecipe> createHeldRecipe() {

        this._recipe = this._factory.apply(this);

        //noinspection unchecked
        return Optional.ofNullable((HeldRecipe)this._recipe);
    }

    /**
     * Get the number of ticks needed to complete the provided recipe.
     *
     * @param recipe the recipe
     * @return the ticks needed to complete the recipe. Default is one.
     */
    @Override
    public int getRequiredTicks(Recipe recipe) {
        return this._requiredTicks.applyAsInt(recipe);
    }

    /**
     * Check if the recipe can be processed or not.
     *
     * @return return true if the holder can process a recipe, false otherwise.
     */
    @Override
    public boolean canProcessRecipe() {
        return this._canProcess.getAsBoolean();
    }

    /**
     * @return true if the available ingredients were changed by an external entity since the last check
     */
    @Override
    public boolean hasRecipeIngredientsChanged() {
        return this._ingredientsChanged.getAsBoolean();
    }

    /**
     * @return true if the currently held recipe should be discarded and a new one created in it's place, false otherwise
     */
    @Override
    public boolean shouldInvalidateRecipe() {
        return CodeHelper.shouldInvalidateResourceCache();
    }

    /**
     * Invalidate the currently held recipe
     */
    @Override
    public void invalidateRecipe() {
        this._recipe = null;
    }

    /**
     * Notify the holder that it should change it's status.
     * Can be used to give the player a visual feedback on the holder status.
     *
     * @param active true if the holder is active, false otherwise
     */
    @Override
    public void onActiveStatusChanged(boolean active) {
        this._statusChanged.accept(active);
    }

    /**
     * Called at the beginning of the recipe processing
     */
    @Override
    public void onBeginRecipeProcessing() {
        this._beginRecipeProcessing.run();
    }

    /**
     * Called at the beginning of every processing tick
     *
     * @param tick the tick begin processed
     */
    @Override
    public void onRecipeTickProcessed(int tick) {
        this._recipeTickProcessed.accept(tick);
    }

    /**
     * Called after the recipe was completely processed.
     */
    @Override
    public void onRecipeProcessed() {
        this._recipeProcessed.run();
    }

    //endregion
    //region builder

    @SuppressWarnings("unused")
    public static class Builder<Recipe extends ModRecipe> {

        public Builder(final Function<IRecipeHolder<Recipe>, IHeldRecipe<Recipe>> factory,
                       final ToIntFunction<Recipe> requiredTicks) {

            this._factory = factory;
            this._requiredTicks = requiredTicks;
            this._statusChanged = CodeHelper.VOID_BOOL_CONSUMER;
            this._canProcess = CodeHelper.TRUE_SUPPLIER;
            this._ingredientsChanged = CodeHelper.FALSE_SUPPLIER;
            this._beginRecipeProcessing = this._recipeProcessed = CodeHelper.VOID_RUNNABLE;
            this._recipeTickProcessed = CodeHelper.VOID_INT_CONSUMER;
        }

        public RecipeHolder<Recipe> build() {
            return new RecipeHolder<>(this);
        }

        public Builder<Recipe> onCanProcess(final BooleanSupplier supplier) {

            this._canProcess = Objects.requireNonNull(supplier);
            return this;
        }

        public Builder<Recipe> onHasIngredientsChanged(final BooleanSupplier supplier) {

            this._ingredientsChanged = Objects.requireNonNull(supplier);
            return this;
        }

        public Builder<Recipe> onActiveStatusChanged(final BooleanConsumer consumer) {

            this._statusChanged = Objects.requireNonNull(consumer);
            return this;
        }

        public Builder<Recipe> onBeginRecipeProcessing(final Runnable runnable) {

            this._beginRecipeProcessing = Objects.requireNonNull(runnable);
            return this;
        }

        public Builder<Recipe> onRecipeProcessed(final Runnable runnable) {

            this._recipeProcessed = Objects.requireNonNull(runnable);
            return this;
        }

        public Builder<Recipe> onRecipeTickProcessed(final IntConsumer consumer) {

            this._recipeTickProcessed = Objects.requireNonNull(consumer);
            return this;
        }

        //region internals

        private final Function<IRecipeHolder<Recipe>, IHeldRecipe<Recipe>> _factory;
        private final ToIntFunction<Recipe> _requiredTicks;

        private BooleanConsumer _statusChanged;
        private BooleanSupplier _canProcess;
        private BooleanSupplier _ingredientsChanged;
        private Runnable _beginRecipeProcessing;
        private Runnable _recipeProcessed;
        private IntConsumer _recipeTickProcessed;

        //endregion
    }

    //endregion
    //region internals

    private RecipeHolder(final Builder<Recipe> builder) {

        this._factory = Objects.requireNonNull(builder._factory);
        this._requiredTicks = Objects.requireNonNull(builder._requiredTicks);
        this._canProcess = Objects.requireNonNull(builder._canProcess);
        this._ingredientsChanged = Objects.requireNonNull(builder._ingredientsChanged);
        this._statusChanged = Objects.requireNonNull(builder._statusChanged);
        this._beginRecipeProcessing = Objects.requireNonNull(builder._beginRecipeProcessing);
        this._recipeProcessed = Objects.requireNonNull(builder._recipeProcessed);
        this._recipeTickProcessed = Objects.requireNonNull(builder._recipeTickProcessed);
    }

    private final Function<IRecipeHolder<Recipe>, IHeldRecipe<Recipe>> _factory;
    private final ToIntFunction<Recipe> _requiredTicks;
    private final BooleanConsumer _statusChanged;
    private final BooleanSupplier _canProcess;
    private final BooleanSupplier _ingredientsChanged;
    private final Runnable _beginRecipeProcessing;
    private final Runnable _recipeProcessed;
    private final IntConsumer _recipeTickProcessed;

    private IHeldRecipe<Recipe> _recipe;

    //endregion
}
