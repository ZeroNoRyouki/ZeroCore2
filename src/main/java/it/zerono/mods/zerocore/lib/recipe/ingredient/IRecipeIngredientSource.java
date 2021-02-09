/*
 *
 * IRecipeIngredientSource.java
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

package it.zerono.mods.zerocore.lib.recipe.ingredient;

public interface IRecipeIngredientSource<T> {

    /**
     * Get the ingredient contained in the source.
     *
     * @return the ingredient from the source object. It MUST not be modified.
     */
    T getIngredient();

    /**
     * Get the ingredient from the source object matching the recipe ingredient provided
     *
     * @param ingredient the recipe ingredient
     * @return the ingredient from the source object that match the recipe ingredient
     */
    T getMatchFrom(IRecipeIngredient<T> ingredient);

    /**
     * Consume the provided ingredient in the source object
     *
     * @param ingredient the ingredient to consume
     */
    void consumeIngredient(T ingredient);
}
