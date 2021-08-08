/*
 *
 * IWorldGenWhiteList.java
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

package it.zerono.mods.zerocore.lib.world;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Collection;

public interface IWorldGenWhiteList extends FeatureConfiguration {

    /**
     * Check if worldgen is allowed in the provided World
     *
     * @param world the World to check
     * @return true if worldgen can be performed, false otherwise
     */
    boolean shouldGenerateIn(Level world);

    /**
     * Check if worldgen is allowed in the provided dimension
     *
     * @param dimensionId the dimension ID to check
     * @return true if worldgen can be performed, false otherwise
     */
    boolean shouldGenerateIn(int dimensionId);

    /**
     * Whitelist the provided dimension ID, allowing worldgen in that dimension
     *
     * @param dimensionId the dimension to whitelist
     */
    void whiteListDimension(int dimensionId);

    /**
     * Whitelist the provided dimension IDs, allowing worldgen in those dimensions
     *
     * @param dimensionIds the dimensions to whitelist
     */
    void whiteListDimensions(int[] dimensionIds);

    /**
     * Whitelist the provided dimension IDs, allowing worldgen in those dimensions
     *
     * @param dimensionIds the dimensions to whitelist
     */
    void whiteListDimensions(Collection<Integer> dimensionIds);

    /**
     * Remove all dimension IDs from the white list
     */
    void clearWhiteList();
}
