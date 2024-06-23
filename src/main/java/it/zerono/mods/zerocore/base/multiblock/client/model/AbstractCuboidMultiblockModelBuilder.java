package it.zerono.mods.zerocore.base.multiblock.client.model;

import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.client.model.multiblock.CuboidPartVariantsModelBuilder;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;

public abstract class AbstractCuboidMultiblockModelBuilder
        extends CuboidPartVariantsModelBuilder
        implements IMultiblockModelBuilder {

    protected AbstractCuboidMultiblockModelBuilder(String multiblockShortName, String templateModelName,
                                                   boolean ambientOcclusion, ResourceLocationBuilder modRoot) {
        this(templateModelName, ambientOcclusion, modRoot.appendPath("block", multiblockShortName));
    }

    protected AbstractCuboidMultiblockModelBuilder(String templateModelName, boolean ambientOcclusion,
                                                   ResourceLocationBuilder modelRoot) {

        super(IMultiblockModelBuilder.getModelResourceLocation(modelRoot, templateModelName), ambientOcclusion);

        this._modelRoot = modelRoot;
        this.build();
    }

    protected void addCasing(MultiblockPartBlock<?, ?> part) {
        this.addBlock(part, false,
                "casing_01_face",
                "casing_02_frame_ds",
                "casing_03_frame_de",
                "casing_04_frame_dn",
                "casing_05_frame_dw",
                "casing_06_frame_us",
                "casing_07_frame_ue",
                "casing_08_frame_un",
                "casing_09_frame_uw",
                "casing_10_frame_se",
                "casing_11_frame_ne",
                "casing_12_frame_nw",
                "casing_13_frame_sw",
                "casing_14_corner_dsw",
                "casing_15_corner_dse",
                "casing_16_corner_dne",
                "casing_17_corner_dnw",
                "casing_18_corner_usw",
                "casing_19_corner_use",
                "casing_20_corner_une",
                "casing_21_corner_unw");
    }

    protected void addDevice(MultiblockPartBlock<?, ?> part) {
        this.addBlock(part, false);
    }

    protected void addController(MultiblockPartBlock<?, ?> part) {
        this.addBlock(part, false, "controller_on", "controller_off");
    }

    protected void addIoPort(MultiblockPartBlock<?, ?> part) {
        this.addBlock(part, false);
    }

    protected void addIoPort(MultiblockPartBlock<?, ?> part, String... models) {
        this.addBlock(part, false, models);
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
