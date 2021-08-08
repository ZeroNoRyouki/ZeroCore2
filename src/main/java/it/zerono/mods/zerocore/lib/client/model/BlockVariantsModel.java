/*
 *
 * BlockVariantsModel.java
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

package it.zerono.mods.zerocore.lib.client.model;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.zerono.mods.zerocore.lib.client.model.data.GenericProperties;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class BlockVariantsModel
        extends AbstractDynamicBakedModel {

    public BlockVariantsModel(final int blocksCount, final boolean ambientOcclusion, final boolean guid3D, final boolean builtInRenderer) {

        super(ambientOcclusion, guid3D, builtInRenderer);
        this._entries = new Int2ObjectArrayMap<>(blocksCount);
    }

    @SuppressWarnings("unused")
    public void addBlock(int blockId, boolean hasGeneralQuads, /*int particlesModelIndex,*/ BakedModel... variants) {
        this._entries.put(blockId, new BlockEntry(/*particlesModelIndex, */hasGeneralQuads, variants));
    }

    @SuppressWarnings("unused")
    public void addBlock(int blockId, boolean hasGeneralQuads, /*int particlesModelIndex,*/ List<BakedModel> variants) {
        this._entries.put(blockId, new BlockEntry(/*particlesModelIndex, */hasGeneralQuads, variants));
    }

    //region IDynamicBakedModel

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction renderSide,
                                    Random rand, IModelData data) {

        if (data.hasProperty(GenericProperties.ID) && data.hasProperty(GenericProperties.VARIANT_INDEX) && this.containsBlock(data)) {
            return this.getBlock(data).getQuads(GenericProperties.getVariantIndex(data), state, renderSide, rand, data);
        }

        return ModRenderHelper.getMissingModel().getQuads(state, renderSide, rand, EmptyModelData.INSTANCE);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(final IModelData data) {

        if (data.hasProperty(GenericProperties.ID) && data.hasProperty(GenericProperties.VARIANT_INDEX) && this.containsBlock(data)) {
            return this.getBlock(data).getParticleTexture(GenericProperties.getVariantIndex(data), data);
        }

        return ModRenderHelper.getMissingModel().getParticleTexture(EmptyModelData.INSTANCE);
    }

    //endregion
    //region internals

    private boolean containsBlock(final IModelData data) {
        return data.hasProperty(GenericProperties.ID) && this._entries.containsKey(GenericProperties.getId(data));
    }

    private BlockEntry getBlock(final IModelData data) {
        return this._entries.get(GenericProperties.getId(data));
    }

    private static class BlockEntry {

        BlockEntry(/*final int particlesModelIndex,*/ final boolean hasGeneralQuads, final BakedModel... variants) {

            this._variants = ImmutableList.copyOf(variants);
            //this._particlesModelIndex = particlesModelIndex;
            this._noGeneralQuads = !hasGeneralQuads;
        }

        BlockEntry(/*final int particlesModelIndex,*/ final boolean hasGeneralQuads, final List<BakedModel> variants) {

            this._variants = ImmutableList.copyOf(variants);
            //this._particlesModelIndex = particlesModelIndex;
            this._noGeneralQuads = !hasGeneralQuads;
        }

        List<BakedQuad> getQuads(final int variantIndex, @Nullable BlockState state, @Nullable Direction renderSide,
                                 Random rand, IModelData data) {

            if (null == renderSide && this._noGeneralQuads) {
                return Collections.emptyList();
            } else {
                return this._variants.get(variantIndex).getQuads(state, renderSide, rand, data);
            }
        }

        TextureAtlasSprite getParticleTexture(final int variantIndex, IModelData data) {
            return this._variants.get(variantIndex).getParticleTexture(data);
        }

        //region internals

        private final List<BakedModel> _variants;
        //private final int _particlesModelIndex;
        private final boolean _noGeneralQuads;

        //endregion
    }

    private final Int2ObjectMap<BlockEntry> _entries;

    //endregion
}
