/*
 *
 * IRecipeHolder.java
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

import java.util.Optional;

public interface IRecipeHolder<Recipe extends ModRecipe>
        extends IRecipeProcessing {

    /**
     * @return the recipe currently held by this recipe holder if valid or create a new one if possible
     */
    default <HeldRecipe extends IHeldRecipe<Recipe>> Optional<HeldRecipe> getCurrentRecipe() {

        final Optional<HeldRecipe> current = this.getHeldRecipe();

        if (!current.isPresent() || this.shouldInvalidateRecipe() || this.hasRecipeIngredientsChanged()) {
            return this.createHeldRecipe();
        }

        return current;
    }

    /**
     * Discard the currently held recipe (if any) and load a new one (if possible)
     */
    default void refresh() {
        this.createHeldRecipe();
    }

    /**
     * @return the recipe currently held by this recipe holder
     */
    <HeldRecipe extends IHeldRecipe<Recipe>> Optional<HeldRecipe> getHeldRecipe();

    /**
     * @return a new IHeldRecipe from the currently available ingredients
     */
    <HeldRecipe extends IHeldRecipe<Recipe>> Optional<HeldRecipe> createHeldRecipe();

    /**
     * Get the number of ticks needed to complete the provided recipe.
     *
     * @param recipe the recipe
     *
     * @return the ticks needed to complete the recipe. Default is one.
     */
    int getRequiredTicks(Recipe recipe);

    /**
     * Check if the provided recipe can be processed or not.
     *
     * @param recipe the recipe
     *
     * @return return true if the holder can process a recipe, false otherwise.
     */
    boolean canProcessRecipe(Recipe recipe);

    /**
     * @return true if the available ingredients were changed by an external entity since the last check
     */
    boolean hasRecipeIngredientsChanged();

    /**
     * @return true if the currently held recipe should be discarded and a new one created in it's place, false otherwise
     */
    boolean shouldInvalidateRecipe();

    /**
     * Invalidate the currently held recipe
     */
    void invalidateRecipe();

    /**
     * Notify the holder that it should change it's status.
     * Can be used to give the player a visual feedback on the holder status.
     *
     * @param active true if the holder is active, false otherwise
     */
    void onActiveStatusChanged(boolean active);
}
