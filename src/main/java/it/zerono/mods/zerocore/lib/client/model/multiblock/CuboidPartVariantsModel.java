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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class CuboidPartVariantsModel extends BlockVariantsModel {

    public CuboidPartVariantsModel(final BakedModel template, final int blocksCount, final boolean ambientOcclusion) {

        super(blocksCount, ambientOcclusion, true, false);
        this._template = template;
    }

    //region IDynamicBakedModel

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction renderSide,
                                    RandomSource rand, ModelData data, @Nullable RenderType renderType) {

        if (null != renderSide && data.has(PartProperties.OUTWARD_FACING) && PartProperties.getOutwardFacing(data).except(renderSide)) {
            return this._template.getQuads(state, renderSide, rand, data, renderType);
        }

        return super.getQuads(state, renderSide, rand, data, renderType);
    }

    //endregion
    //region internals

    private final BakedModel _template;

    //endregion
}
