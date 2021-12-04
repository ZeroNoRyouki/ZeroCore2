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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ForgeModelBakery;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockVariantsModelBuilder implements ICustomModelBuilder {

    public BlockVariantsModelBuilder(final boolean ambientOcclusion, final boolean guid3D, final boolean builtInRenderer) {

        this._ambientOcclusion = ambientOcclusion;
        this._guid3D = guid3D;
        this._builtInRenderer = builtInRenderer;
        this._modelToBeReplaced = Maps.newHashMap();
        this._modelsToBeLoaded = Maps.newHashMap();
        this._variants = Maps.newHashMap();
        this._particleVariantIndex = Maps.newHashMap();
        this._hasGeneralQuads = Maps.newHashMap();
    }

    public void addBlock(final int id, final ResourceLocation modelToReplace) {
        this.addBlock(id, modelToReplace, 0, false);
    }

    public void addBlock(final int id, final ResourceLocation modelToReplace, final int particleVariantIndex) {
        this.addBlock(id, modelToReplace, particleVariantIndex, false);
    }

    public void addBlock(final int id, final ResourceLocation modelToReplace, final int particleVariantIndex, final boolean hasGeneralQuads) {

        this._modelToBeReplaced.put(id, modelToReplace);
        this._hasGeneralQuads.put(id, hasGeneralQuads);
        this._particleVariantIndex.put(id, particleVariantIndex);
    }

    /**
     * Add the provided variants model the specified block.
     * The provided models will be added to the list of models to load.
     * Note: ModelResourceLocations will not be loaded.
     *
     * This is the equivalent of calling addVariant() and loadModel() for each model provided
     *
     * @param blockId the id of the block to add the variants to
     * @param models the variants models
     */
    public void addModels(final int blockId, ResourceLocation... models) {

        Collections.addAll(this._modelsToBeLoaded.computeIfAbsent(blockId, id -> Lists.newArrayListWithCapacity(models.length)), models);
        Collections.addAll(this._variants.computeIfAbsent(blockId, id -> Lists.newArrayListWithCapacity(models.length)), models);
    }

    /**
     * Add the provided variants model the specified block.
     * The provided models will be added to the list of models to load.
     * Note: ModelResourceLocations will not be loaded.
     *
     * This is the equivalent of calling addVariant() and loadModel() for each model provided
     *
     * @param blockId the id of the block to add the variants to
     * @param models the variants models
     */
    public void addModels(final int blockId, final List<ResourceLocation> models) {

        this._modelsToBeLoaded.computeIfAbsent(blockId, id -> Lists.newArrayListWithCapacity(models.size())).addAll(models);
        this._variants.computeIfAbsent(blockId, id -> Lists.newArrayListWithCapacity(models.size())).addAll(models);
    }

    /**
     * Add the provided variant model the specified block
     *
     * @param blockId the id of the block to add the variants to
     * @param model the variants model to add
     */
    public void addVariant(final int blockId, ResourceLocation model) {
        this._variants.computeIfAbsent(blockId, id -> Lists.newArrayListWithCapacity(8)).add(model);
    }

    /**
     * Add the provided model reference to the list of models to be loaded
     * Note: ModelResourceLocations will be skipped
     *
     * @param blockId the id of the block
     * @param model the model to be loaded
     */
    public void loadModel(final int blockId, ResourceLocation model) {

        if (!(model instanceof ModelResourceLocation)) {
            this._modelsToBeLoaded.computeIfAbsent(blockId, id -> Lists.newArrayListWithCapacity(8)).add(model);
        }
    }

    protected BlockVariantsModel createReplacementModel(int blockCount, boolean ambientOcclusion, boolean guid3D, boolean builtInRenderer) {
        return new BlockVariantsModel(blockCount, ambientOcclusion, guid3D, builtInRenderer);
    }

    //region ICustomModelBuilder

    @Override
    public void onRegisterModels() {
        this._modelsToBeLoaded.values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(List::stream)
                .filter(resourceLocation -> !(resourceLocation instanceof ModelResourceLocation))
                .forEach(ForgeModelBakery::addSpecialModel);
    }

    @Override
    public void onBakeModels(final ModelBakeEvent event) {

        final Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
        final Set<Integer> ids = this._modelToBeReplaced.keySet();
        final BlockVariantsModel model = this.createReplacementModel(ids.size(), this._ambientOcclusion, this._guid3D, this._builtInRenderer);

        for (final int id : ids) {

            final List<BakedModel> variants = this._variants.getOrDefault(id, Collections.emptyList()).stream()
                    .map(location -> lookupModel(modelRegistry, location))
                    .collect(Collectors.toList());

            model.addBlock(id, this._hasGeneralQuads.get(id), /*this._particleVariantIndex.get(id),*/ variants);
            modelRegistry.put(this._modelToBeReplaced.get(id), model);
        }
    }

    //endregion
    //region internals

    private static BakedModel lookupModel(final Map<ResourceLocation, BakedModel> modelRegistry, final ResourceLocation location) {

        if (modelRegistry.containsKey(location)) {

            return modelRegistry.get(location);

        } else {

            Log.LOGGER.warn(Log.MULTIBLOCK, "Unable to find a backed model for {}", location);
            return ModRenderHelper.getMissingModel();
        }
    }

    private final boolean _ambientOcclusion;
    private final boolean _guid3D;
    private final boolean _builtInRenderer;
    private final Map<Integer, ResourceLocation> _modelToBeReplaced;
    private final Map<Integer, List<ResourceLocation>> _modelsToBeLoaded;
    private final Map<Integer, List<ResourceLocation>> _variants;
    private final Map<Integer, Integer> _particleVariantIndex;
    private final Map<Integer, Boolean> _hasGeneralQuads;

    //endregion
}
