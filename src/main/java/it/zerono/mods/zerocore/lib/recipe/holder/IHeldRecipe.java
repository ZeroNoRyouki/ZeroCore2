/*
 *
 * IHeldRecipe.java
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

public interface IHeldRecipe<Recipe extends ModRecipe>
        extends IRecipeProcessing {

    /**
     * Process the recipe
     */
    void processRecipe();

    /**
     * Get the holder of this recipe
     *
     * @return the holder
     */
    <Holder extends IRecipeHolder<Recipe>> Holder getRecipeHolder();

    /**
     * Get the current tick being processed.
     *
     * @return the current tick being processed (between 1 and getRequiredTicks()) if the recipe is being processed,
     * zero otherwise
     */
    int getCurrentTick();
}
