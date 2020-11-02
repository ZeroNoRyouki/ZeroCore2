/*
 *
 * CuboidPartVariantsModel.java
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

package it.zerono.mods.zerocore.lib.client.model.multiblock;

import it.zerono.mods.zerocore.lib.client.model.BlockVariantsModel;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.PartProperties;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class CuboidPartVariantsModel extends BlockVariantsModel {

    public CuboidPartVariantsModel(final IBakedModel template, final int blocksCount, final boolean ambientOcclusion) {

        super(blocksCount, ambientOcclusion, true, false);
        this._template = template;
    }

    //region IDynamicBakedModel

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction renderSide,
                                    Random rand, IModelData data) {

        if (null != renderSide && data.hasProperty(PartProperties.OUTWARD_FACING) && PartProperties.getOutwardFacing(data).except(renderSide) /*.isSet(renderSide)*/) {
            return this._template.getQuads(state, renderSide, rand, data);
        }

        return super.getQuads(state, renderSide, rand, data);
    }

    //endregion
    //region internals

    private final IBakedModel _template;

    //endregion
}
