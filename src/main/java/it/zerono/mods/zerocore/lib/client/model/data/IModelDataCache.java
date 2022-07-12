/*
 *
 * IModelDataCache.java
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

package it.zerono.mods.zerocore.lib.client.model.data;

import net.minecraftforge.client.model.data.ModelData;

/**
 * A cache for re-usable {@link IModelData} instances.
 * Each model data in the cache is associated with a generic "state", representing the state of the object the model data describe
 */
public interface IModelDataCache<State> {

    ModelData EMPTY = ModelData.EMPTY;

    /**
     * Get the {@link IModelData} of the given state from the cache.
     * If the state is not cached, EMPTY will be returned
     *
     * @param state the state associated with the requested model data
     * @return the model data associated with the provided state if any is found, EMPTY otherwise
     */
    ModelData getData(State state);

    /**
     * Put the provided {@link IModelData} in the cache associating it with the given state
     *
     * @param state the state associated with the model data
     * @param data the model data
     */
    void putData(State state, ModelData data);
}
