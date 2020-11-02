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
import it.zerono.mods.zerocore.lib.client.model.data.AbstractModelDataMap;
import it.zerono.mods.zerocore.lib.client.model.data.GenericProperties;

public class CuboidPartVariantsModelData extends AbstractModelDataMap {

    public CuboidPartVariantsModelData(final int blockId, final int variantIndex, final BlockFacings outwardFacing) {

        this.addProperty(GenericProperties.ID, blockId);
        this.addProperty(GenericProperties.VARIANT_INDEX, variantIndex);
        this.addProperty(PartProperties.OUTWARD_FACING, outwardFacing);
    }

    public static int hash(final int blockId, final int variantIndex, final BlockFacings outwardFacing) {
        return (variantIndex << 16) | ((blockId & 0xff) << 8) | outwardFacing.value();
    }

    //region Object

    @Override
    public int hashCode() {
        return hash(GenericProperties.getId(this), GenericProperties.getVariantIndex(this), PartProperties.getOutwardFacing(this));
    }

    //endregion
}
