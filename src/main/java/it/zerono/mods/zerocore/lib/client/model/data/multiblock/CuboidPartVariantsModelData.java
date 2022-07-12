/*
 *
 * CuboidPartVariantsModelData.java
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

import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.client.model.data.GenericProperties;
import net.minecraftforge.client.model.data.ModelData;

public final class CuboidPartVariantsModelData {

    public static ModelData from(final int blockId, final int variantIndex, final BlockFacings outwardFacing) {
        return ModelData.builder()
                .with(GenericProperties.ID, blockId)
                .with(GenericProperties.VARIANT_INDEX, variantIndex)
                .with(PartProperties.OUTWARD_FACING, outwardFacing)
                .build();
    }

    public static int hash(final int blockId, final int variantIndex, final BlockFacings outwardFacing) {
        return (variantIndex << 16) | ((blockId & 0xff) << 8) | outwardFacing.value();
    }

    public static int hash(final ModelData data) {

        if (!data.has(GenericProperties.ID) || !data.has(GenericProperties.VARIANT_INDEX) ||
                !data.has(PartProperties.OUTWARD_FACING)) {
            throw new IllegalArgumentException("The provided model data is missing the required properties");
        }

        //noinspection ConstantConditions
        return hash(data.get(GenericProperties.ID), data.get(GenericProperties.VARIANT_INDEX),
                data.get(PartProperties.OUTWARD_FACING));
    }

    //region internals

    private CuboidPartVariantsModelData() {
    }

    //endregion
}
