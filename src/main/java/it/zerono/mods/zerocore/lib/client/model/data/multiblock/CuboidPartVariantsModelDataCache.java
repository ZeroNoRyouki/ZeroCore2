/*
 *
 * CuboidPartVariantsModelDataCache.java
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

package it.zerono.mods.zerocore.lib.client.model.data.multiblock;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.zerono.mods.zerocore.lib.block.BlockFacings;
import net.minecraftforge.client.model.data.ModelData;

public class CuboidPartVariantsModelDataCache {

    public CuboidPartVariantsModelDataCache() {
        this._cache = new Int2ObjectArrayMap<>(16);
    }

    public synchronized ModelData computeIfAbsent(final int blockId, final int variantIndex,
                                                  final BlockFacings outwardFacing) {
        return this._cache.computeIfAbsent(CuboidPartVariantsModelData.hash(blockId, variantIndex, outwardFacing),
                k -> CuboidPartVariantsModelData.from(blockId, variantIndex, outwardFacing));
    }

    //region internals

    private final Int2ObjectMap<ModelData> _cache;

    //endregion
}
