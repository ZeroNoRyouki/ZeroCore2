/*
 *
 * AbstractHeldRecipe.java
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

import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import net.minecraft.util.Mth;

import java.util.Objects;

public abstract class AbstractHeldRecipe<Recipe extends ModRecipe>
        implements IHeldRecipe<Recipe> {

    protected <Holder extends IRecipeHolder<Recipe>> AbstractHeldRecipe(final Recipe recipe, final Holder holder) {

        this._recipe = Objects.requireNonNull(recipe);
        this._holder = Objects.requireNonNull(holder);
        this._currentTick = 0;
    }

    //region IHeldRecipe

    /**
     * Process the recipe
     */
    @Override
    public boolean processRecipe() {

        final Recipe recipe = this.getRecipe();
        final IRecipeHolder<Recipe> holder = this.getRecipeHolder();
        final int requiredTicks = holder.getRequiredTicks(recipe);

        if (!holder.canProcessRecipe(recipe) || requiredTicks < 1) {

            this._currentTick = 0;
            holder.onActiveStatusChanged(false);
            return false;
        }

        if (0 == this._currentTick) {

            holder.onActiveStatusChanged(true);
            this.onBeginRecipeProcessing();
            holder.onBeginRecipeProcessing();
        }

        ++this._currentTick;
        this.onRecipeTickProcessed(this._currentTick);
        holder.onRecipeTickProcessed(this._currentTick);

        if (requiredTicks <= this._currentTick) {

            this._currentTick = 0;
            this.onRecipeProcessed();
            holder.onRecipeProcessed();
            holder.onActiveStatusChanged(false);
        }

        return true;
    }

    /**
     * Get the currently held recipe
     *
     * @return the recipe
     */
    public Recipe getRecipe() {
        return this._recipe;
    }

    /**
     * @return the holder of this recipe
     */
    @Override
    public <Holder extends IRecipeHolder<Recipe>> Holder getRecipeHolder() {
        //noinspection unchecked
        return (Holder)this._holder;
    }

    /**
     * Get the current tick being processed.
     *
     * @return the current tick being processed (between 1 and getRequiredTicks()) if the recipe is being processed,
     * zero otherwise
     */
    @Override
    public int getCurrentTick() {
        return this._currentTick;
    }

    /**
     * Load a previously saved processing state back in the recipe
     *
     * @param tick the new current tick
     */
    @Override
    public void loadCurrentTick(int tick) {
        this._currentTick = Mth.clamp(tick, 0, this.getRecipeHolder().getRequiredTicks(this.getRecipe()));
    }

    /**
     * Get the current completion progress of this recipe
     *
     * @return the completion progress
     */
    @Override
    public double getProgress() {
        return (double)this.getCurrentTick() / this.getRecipeHolder().getRequiredTicks(this.getRecipe());
    }

    //endregion
    //region internals

    private final Recipe _recipe;
    private final IRecipeHolder<Recipe> _holder;

    private int _currentTick;

    //endregion
}
