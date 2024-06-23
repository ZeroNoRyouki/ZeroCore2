/*
 *
 * BlockVariantsModelBuilder.java
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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.List;
import java.util.Map;

public class BlockVariantsModelBuilder
        implements ICustomModelBuilder {

    public BlockVariantsModelBuilder(final boolean ambientOcclusion, final boolean guid3D, final boolean builtInRenderer) {

        this._ambientOcclusion = ambientOcclusion;
        this._guid3D = guid3D;
        this._builtInRenderer = builtInRenderer;
        this._blocks = new Int2ObjectOpenHashMap<>();
        this._modelsToBeLoaded = new ObjectArrayList<>(64);
    }

    /**
     * Request for the specified model(s) to be loaded when resources are (re)loaded
     *
     * @param model A model to be loaded
     * @param others Additional models to be loaded
     */
    public void loadAdditionalModel(ModelResourceLocation model, ModelResourceLocation... others) {

        this.loadAdditionalStandaloneModels(model);
        this.loadAdditionalStandaloneModels(others);
    }

    /**
     * Add a block with any additional model to this builder
     *
     * @param id A unique ID for the block
     * @param originalModel The original model of the block
     * @param hasGeneralQuads True if the original model has general quads, false otherwise
     * @param additionalVariants Any additional models to be used a variants for the added block
     */
    public void addBlock(int id, ModelResourceLocation originalModel, boolean hasGeneralQuads,
                         ModelResourceLocation... additionalVariants) {

        if (additionalVariants.length == 0) {
            additionalVariants = EMPTY_VARIANTS;
        } else {
            this.loadAdditionalStandaloneModels(additionalVariants);
        }

        this._blocks.put(id, new BlockEntrySource(id, originalModel, additionalVariants, hasGeneralQuads));
    }

    protected BlockVariantsModel createReplacementModel(int blockCount, boolean ambientOcclusion, boolean guid3D, boolean builtInRenderer) {
        return new BlockVariantsModel(blockCount, ambientOcclusion, guid3D, builtInRenderer);
    }

    //region ICustomModelBuilder

    @Override
    public void onRegisterModels(final ModelEvent.RegisterAdditional event) {
        this._modelsToBeLoaded.forEach(event::register);
    }

    @Override
    public void onBakeModels(final ModelEvent.ModifyBakingResult event) {

        // replace the existing, json-based, model with our custom one (one per builder)

        final Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();

        // - build the replacement model

        final var replacementModel = this.createReplacementModel(this._blocks.size(), this._ambientOcclusion,
                this._guid3D, this._builtInRenderer);

        for (final var block : this._blocks.values()) {

            final BakedModel[] variantsModels = new BakedModel[1 + block.variants.length];

            // add the original model of the block as the first variant
            variantsModels[0] = modelRegistry.get(block.originalModel);

            if (null == variantsModels[0]) {

                Log.LOGGER.warn(Log.CLIENT, "Unable to lookup the original model for a block variant: {}. Skipping block!",
                        block.originalModel);
                continue;
            }

            // add the additional variants...

            for (int idx = 0; idx < block.variants.length; ++idx) {

                final BakedModel variantModel;

                if (modelRegistry.containsKey(block.variants[idx])) {

                    variantModel = modelRegistry.get(block.variants[idx]);

                } else {

                    Log.LOGGER.warn(Log.CLIENT, "Unable to lookup model for a block variant: {}", block.variants[idx]);
                    variantModel = ModRenderHelper.getMissingModel();
                }

                variantsModels[1 + idx] = variantModel;
            }

            replacementModel.addBlock(block.blockId, block.hasGeneralQuads, variantsModels);

            // - replace the original model
            modelRegistry.put(block.originalModel, replacementModel);
        }
    }

    //endregion
    //region internals
    //region BlockEntrySource

    private record BlockEntrySource(int blockId, ModelResourceLocation originalModel, ModelResourceLocation[] variants,
                                    boolean hasGeneralQuads) {
    }

    //endregion

    private void loadAdditionalStandaloneModels(ModelResourceLocation... models) {

        // NeoForge only allow ModelResourceLocation with a "standalone" variant to be loaded...

        for (final var model : models) {

            if (model.variant().equals(ModelResourceLocation.STANDALONE_VARIANT)) {
                this._modelsToBeLoaded.add(model);
            }
        }
    }

    private static final ModelResourceLocation[] EMPTY_VARIANTS = new ModelResourceLocation[0];

    private final boolean _ambientOcclusion;
    private final boolean _guid3D;
    private final boolean _builtInRenderer;

    private final Int2ObjectMap<BlockEntrySource> _blocks;
    private final List<ModelResourceLocation> _modelsToBeLoaded;

    //endregion
}
