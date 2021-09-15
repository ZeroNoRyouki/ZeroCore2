/*
 *
 * IRecipeResultTarget.java
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

package it.zerono.mods.zerocore.lib.recipe.result;

import it.zerono.mods.zerocore.lib.data.stack.OperationMode;

public interface IRecipeResultTarget<T extends IRecipeResult<?>> {

    /**
     * Set the recipe result in the target.
     *
     * @param result The recipe result.
     * @param mode How the operation is carried out.
     * @return The portion of the result that was not inserted in the target, if any.
     */
    long setResult(T result, OperationMode mode);

    /**
     * Return how many times the provided result can be stored in the target, based the size of the result,
     * the capacity of the target and what, if anything, it already contains.
     *
     * @param result The recipe result.
     * @return How many times the result can be stored in the target.
     */
    long countStorableResults(T result);
}
