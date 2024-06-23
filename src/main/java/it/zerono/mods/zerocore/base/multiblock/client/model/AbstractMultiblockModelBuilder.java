package it.zerono.mods.zerocore.base.multiblock.client.model;

import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import it.zerono.mods.zerocore.lib.client.model.BlockVariantsModelBuilder;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

public abstract class AbstractMultiblockModelBuilder
        extends BlockVariantsModelBuilder
        implements IMultiblockModelBuilder {

    protected AbstractMultiblockModelBuilder(String multiblockShortName, ResourceLocationBuilder modRoot) {
        this(modRoot.appendPath("block", multiblockShortName));
    }

    protected AbstractMultiblockModelBuilder(ResourceLocationBuilder modelRoot) {

        super(true, true, false);

        this._modelRoot = modelRoot;
        this.build();
    }

    protected void addGlass(MultiblockPartBlock<?, ?> part) {

        final var blockId = BuiltInRegistries.BLOCK.getKey(part);
        final var originalModel = new ModelResourceLocation(blockId, BlockFacingsProperty.None.asVariantString());
        final BlockFacingsProperty[] properties = BlockFacingsProperty.values();
        final ModelResourceLocation[] additionalModels = new ModelResourceLocation[properties.length - 1];

        for (int idx = 1; idx < properties.length; ++idx) {
            additionalModels[idx - 1] = new ModelResourceLocation(blockId, properties[idx].asVariantString());
        }

        this.addBlock(part.getPartType().getByteHashCode(), originalModel, false, additionalModels);
    }

    //region IMultiblockModelBuilder

    @Override
    public ResourceLocationBuilder getModelRoot() {
        return this._modelRoot;
    }

    //endregion
    //region internals

    private final ResourceLocationBuilder _modelRoot;

    //endregion
}
