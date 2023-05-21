package it.zerono.mods.zerocore.lib.datagen.provider.client.state.multiblock;

import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.client.model.ParentModel;
import it.zerono.mods.zerocore.lib.datagen.provider.client.state.BlockStateDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class CuboidMultiblockStateDataProvider
        extends BlockStateDataProvider {

    public CuboidMultiblockStateDataProvider(String name, PackOutput output,
                                             CompletableFuture<HolderLookup.Provider> lookupProvider,
                                             ResourceLocationBuilder modLocationRoot) {
        super(name, output, lookupProvider, modLocationRoot);
    }

    public <B extends Block> void multiblockGlass(Supplier<B> block, String modelBaseName, String textureBaseName,
                                                  String subDirectory) {

        var idBuilder = this.blocksRoot().appendPath(subDirectory).append(textureBaseName);

        final var texture0 = idBuilder.buildWithSuffix(".0");
        final var texture1 = idBuilder.buildWithSuffix(".1");
        final var texture3 = idBuilder.buildWithSuffix(".3");
        final var texture5 = idBuilder.buildWithSuffix(".5");
        final var texture7 = idBuilder.buildWithSuffix(".7");
        final var texture8 = idBuilder.buildWithSuffix(".8");
        final var texture9 = idBuilder.buildWithSuffix(".9");
        final var texture11 = idBuilder.buildWithSuffix(".11");
        final var texture12 = idBuilder.buildWithSuffix(".12");
        final var texture13 = idBuilder.buildWithSuffix(".13");
        final var texture15 = idBuilder.buildWithSuffix(".15");

        idBuilder = this.blocksRoot().appendPath(subDirectory).append(modelBaseName);

        final var c0 = this.models()
                .model(idBuilder.buildWithSuffix("_c0"))
                .delegateFor(block)
                .cube(texture0);
        final var c1 = this.models()
                .model(idBuilder.buildWithSuffix("_c1"))
                .cube(texture0, texture1, texture1, texture1, texture1, texture15, texture0);
        final var c2Angle = this.models()
                .model(idBuilder.buildWithSuffix("_c2angle"))
                .cube(texture0, texture5, texture9, texture15, texture1, texture15, texture8);
        final var c2 = this.models()
                .model(idBuilder.buildWithSuffix("_c2"))
                .cube(texture0, texture3, texture3, texture3, texture3, texture15, texture15);
        final var c3T1 = this.models()
                .model(idBuilder.buildWithSuffix("_c3t1"))
                .cube(texture0, texture11, texture7, texture3, texture15, texture15, texture15);
        final var c3T2 = this.models()
                .model(idBuilder.buildWithSuffix("_c3t2"))
                .cube(texture0, texture13, texture13, texture15, texture15, texture15, texture12);
        final var c3Angle = this.models()
                .model(idBuilder.buildWithSuffix("_c3angle"))
                .cube(texture0, texture5, texture15, texture15, texture9, texture15, texture9);
        final var c4X = this.models()
                .model(idBuilder.buildWithSuffix("_c4x"))
                .cube(texture0, texture15, texture15, texture15, texture15, texture15, texture15);
        final var c4Angle = this.models()
                .model(idBuilder.buildWithSuffix("_c4angle"))
                .cube(texture0, texture15, texture7, texture11, texture15, texture15, texture15);
        final var c5_6 = this.models()
                .model(idBuilder.buildWithSuffix("_c5-6"))
                .cube(texture15);

        this.multiVariant(block)
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.None, variant -> variant.model(c0))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Face_U, variant -> variant.model(c1))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Face_D, variant -> variant
                        .model(c1)
                        .xRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Face_N, variant -> variant
                        .model(c1)
                        .xRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Face_S, variant -> variant
                        .model(c1)
                        .xRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Face_W, variant -> variant
                        .model(c1)
                        .xRotation(90)
                        .yRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Face_E, variant -> variant
                        .model(c1)
                        .xRotation(90)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_EU, variant -> variant.model(c2Angle))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_UW, variant -> variant
                        .model(c2Angle)
                        .yRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_DE, variant -> variant
                        .model(c2Angle)
                        .xRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_DW, variant -> variant
                        .model(c2Angle)
                        .xRotation(180)
                        .yRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_NU, variant -> variant
                        .model(c2Angle)
                        .yRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_SU, variant -> variant
                        .model(c2Angle)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_DN, variant -> variant
                        .model(c2Angle)
                        .xRotation(180)
                        .yRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_DS, variant -> variant
                        .model(c2Angle)
                        .xRotation(180)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_EN, variant -> variant
                        .model(c2Angle)
                        .xRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_ES, variant -> variant
                        .model(c2Angle)
                        .xRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_NW, variant -> variant
                        .model(c2Angle)
                        .xRotation(90)
                        .yRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Angle_SW, variant -> variant
                        .model(c2Angle)
                        .xRotation(270)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Opposite_DU, variant -> variant.model(c2))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Opposite_EW, variant -> variant
                        .model(c2)
                        .xRotation(270)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Opposite_NS, variant -> variant
                        .model(c2)
                        .xRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_DUW, variant -> variant.model(c3T1))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_DSU, variant -> variant
                        .model(c3T1)
                        .yRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_DEU, variant -> variant
                        .model(c3T1)
                        .yRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_DNU, variant -> variant
                        .model(c3T1)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_NSW, variant -> variant
                        .model(c3T1)
                        .xRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_ENS, variant -> variant
                        .model(c3T1)
                        .xRotation(270)
                        .yRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_EUW, variant -> variant.model(c3T2))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_ESW, variant -> variant
                        .model(c3T2)
                        .xRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_DEW, variant -> variant
                        .model(c3T2)
                        .xRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_ENW, variant -> variant
                        .model(c3T2)
                        .xRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_NSU, variant -> variant
                        .model(c3T2)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.CShape_DNS, variant -> variant
                        .model(c3T2)
                        .xRotation(180)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Corner_ESU, variant -> variant.model(c3Angle))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Corner_SUW, variant -> variant
                        .model(c3Angle)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Corner_ENU, variant -> variant
                        .model(c3Angle)
                        .xRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Corner_DEN, variant -> variant
                        .model(c3Angle)
                        .xRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Corner_DES, variant -> variant
                        .model(c3Angle)
                        .xRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Corner_DSW, variant -> variant
                        .model(c3Angle)
                        .xRotation(180)
                        .yRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Corner_DNW, variant -> variant
                        .model(c3Angle)
                        .xRotation(270)
                        .yRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Corner_NUW, variant -> variant
                        .model(c3Angle)
                        .xRotation(90)
                        .yRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Pipe_DEUW, variant -> variant.model(c4X))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Pipe_ENSW, variant -> variant
                        .model(c4X)
                        .xRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Pipe_DNSU, variant -> variant
                        .model(c4X)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_DNUW, variant -> variant.model(c4Angle))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_DNSW, variant -> variant
                        .model(c4Angle)
                        .xRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_NSUW, variant -> variant
                        .model(c4Angle)
                        .xRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_DSUW, variant -> variant
                        .model(c4Angle)
                        .yRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_DENU, variant -> variant
                        .model(c4Angle)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_DESU, variant -> variant
                        .model(c4Angle)
                        .yRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_ENSU, variant -> variant
                        .model(c4Angle)
                        .xRotation(270)
                        .yRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_DENS, variant -> variant
                        .model(c4Angle)
                        .xRotation(90)
                        .yRotation(180))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_ESUW, variant -> variant
                        .model(c4Angle)
                        .xRotation(270)
                        .yRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_DESW, variant -> variant
                        .model(c4Angle)
                        .xRotation(90)
                        .yRotation(270))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_ENUW, variant -> variant
                        .model(c4Angle)
                        .xRotation(270)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.Misc_DENW, variant -> variant
                        .model(c4Angle)
                        .xRotation(90)
                        .yRotation(90))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.PipeEnd_DENSW, variant -> variant.model(c5_6))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.PipeEnd_DESUW, variant -> variant.model(c5_6))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.PipeEnd_DNSUW, variant -> variant.model(c5_6))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.PipeEnd_DENSU, variant -> variant.model(c5_6))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.PipeEnd_DENUW, variant -> variant.model(c5_6))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.PipeEnd_ENSUW, variant -> variant.model(c5_6))
                .selector(BlockFacingsProperty.FACINGS, BlockFacingsProperty.All, variant -> variant.model(c5_6));
    }

    public <B extends Block> void multiblockGlass(Supplier<B> block, String baseName, String subDirectory) {
        this.multiblockGlass(block, baseName, baseName, subDirectory);
    }

    public <B extends Block> void multiblockFrame(Supplier<B> block, String modelBaseName, String textureBaseName,
                                                  String subDirectory) {

        var idBuilder = this.blocksRoot().appendPath(subDirectory);

        final var texturePlating = idBuilder.buildWithSuffix("plating");

        idBuilder = idBuilder.append(textureBaseName);

        final var textureSingle = idBuilder.buildWithSuffix("_single");
        final var textureUp = idBuilder.buildWithSuffix("_up");
        final var textureDown = idBuilder.buildWithSuffix("_down");
        final var textureLeft = idBuilder.buildWithSuffix("_left");
        final var textureLeftDown = idBuilder.buildWithSuffix("_left_down");
        final var textureLeftUp = idBuilder.buildWithSuffix("_left_up");
        final var textureRight = idBuilder.buildWithSuffix("_right");
        final var textureRightDown = idBuilder.buildWithSuffix("_right_down");
        final var textureRightUp = idBuilder.buildWithSuffix("_right_up");

        final var template = idBuilder.buildWithSuffix("_template");

        this.models()
                .model(template)
                .cube(texturePlating);

        this.models()
                .model(idBuilder.buildWithSuffix("_01_face"))
                .cube(texturePlating);

        this.models()
                .model(idBuilder.buildWithSuffix("_02_frame_ds"))
                .parent(ParentModel.of(template, TextureSlot.DOWN, TextureSlot.SOUTH))
                .texture(TextureSlot.DOWN, textureUp)
                .texture(TextureSlot.SOUTH, textureDown)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_03_frame_de"))
                .parent(ParentModel.of(template, TextureSlot.DOWN, TextureSlot.EAST))
                .texture(TextureSlot.DOWN, textureRight)
                .texture(TextureSlot.EAST, textureDown)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_04_frame_dn"))
                .parent(ParentModel.of(template, TextureSlot.DOWN, TextureSlot.NORTH))
                .texture(TextureSlot.DOWN, textureDown)
                .texture(TextureSlot.NORTH, textureDown)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_05_frame_dw"))
                .parent(ParentModel.of(template, TextureSlot.DOWN, TextureSlot.WEST))
                .texture(TextureSlot.DOWN, textureLeft)
                .texture(TextureSlot.WEST, textureDown)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_06_frame_us"))
                .parent(ParentModel.of(template, TextureSlot.UP, TextureSlot.SOUTH))
                .texture(TextureSlot.UP, textureDown)
                .texture(TextureSlot.SOUTH, textureUp)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_07_frame_ue"))
                .parent(ParentModel.of(template, TextureSlot.UP, TextureSlot.EAST))
                .texture(TextureSlot.UP, textureRight)
                .texture(TextureSlot.EAST, textureUp)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_08_frame_un"))
                .parent(ParentModel.of(template, TextureSlot.UP, TextureSlot.NORTH))
                .texture(TextureSlot.UP, textureUp)
                .texture(TextureSlot.NORTH, textureUp)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_09_frame_uw"))
                .parent(ParentModel.of(template, TextureSlot.UP, TextureSlot.WEST))
                .texture(TextureSlot.UP, textureLeft)
                .texture(TextureSlot.WEST, textureUp)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_10_frame_se"))
                .parent(ParentModel.of(template, TextureSlot.SOUTH, TextureSlot.EAST))
                .texture(TextureSlot.SOUTH, textureRight)
                .texture(TextureSlot.EAST, textureLeft)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_11_frame_ne"))
                .parent(ParentModel.of(template, TextureSlot.NORTH, TextureSlot.EAST))
                .texture(TextureSlot.NORTH, textureLeft)
                .texture(TextureSlot.EAST, textureRight)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_12_frame_nw"))
                .parent(ParentModel.of(template, TextureSlot.NORTH, TextureSlot.WEST))
                .texture(TextureSlot.NORTH, textureRight)
                .texture(TextureSlot.WEST, textureLeft)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_13_frame_sw"))
                .parent(ParentModel.of(template, TextureSlot.SOUTH, TextureSlot.WEST))
                .texture(TextureSlot.SOUTH, textureLeft)
                .texture(TextureSlot.WEST, textureRight)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_14_corner_dsw"))
                .parent(ParentModel.of(template, TextureSlot.DOWN, TextureSlot.SOUTH, TextureSlot.WEST))
                .texture(TextureSlot.DOWN, textureLeftUp)
                .texture(TextureSlot.SOUTH, textureLeftDown)
                .texture(TextureSlot.WEST, textureRightDown)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_15_corner_dse"))
                .parent(ParentModel.of(template, TextureSlot.DOWN, TextureSlot.SOUTH, TextureSlot.EAST))
                .texture(TextureSlot.DOWN, textureRightUp)
                .texture(TextureSlot.SOUTH, textureRightDown)
                .texture(TextureSlot.EAST, textureLeftDown)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_16_corner_dne"))
                .parent(ParentModel.of(template, TextureSlot.DOWN, TextureSlot.NORTH, TextureSlot.EAST))
                .texture(TextureSlot.DOWN, textureRightDown)
                .texture(TextureSlot.NORTH, textureLeftDown)
                .texture(TextureSlot.EAST, textureRightDown)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_17_corner_dnw"))
                .parent(ParentModel.of(template, TextureSlot.DOWN, TextureSlot.NORTH, TextureSlot.WEST))
                .texture(TextureSlot.DOWN, textureLeftDown)
                .texture(TextureSlot.NORTH, textureRightDown)
                .texture(TextureSlot.WEST, textureLeftDown)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_18_corner_usw"))
                .parent(ParentModel.of(template, TextureSlot.UP, TextureSlot.SOUTH, TextureSlot.WEST))
                .texture(TextureSlot.UP, textureLeftDown)
                .texture(TextureSlot.SOUTH, textureLeftUp)
                .texture(TextureSlot.WEST, textureRightUp)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_19_corner_use"))
                .parent(ParentModel.of(template, TextureSlot.UP, TextureSlot.SOUTH, TextureSlot.EAST))
                .texture(TextureSlot.UP, textureRightDown)
                .texture(TextureSlot.SOUTH, textureRightUp)
                .texture(TextureSlot.EAST, textureLeftUp)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_20_corner_une"))
                .parent(ParentModel.of(template, TextureSlot.UP, TextureSlot.NORTH, TextureSlot.EAST))
                .texture(TextureSlot.UP, textureRightUp)
                .texture(TextureSlot.NORTH, textureLeftUp)
                .texture(TextureSlot.EAST, textureRightUp)
                .build();

        this.models()
                .model(idBuilder.buildWithSuffix("_21_corner_unw"))
                .parent(ParentModel.of(template, TextureSlot.UP, TextureSlot.NORTH, TextureSlot.WEST))
                .texture(TextureSlot.UP, textureLeftUp)
                .texture(TextureSlot.NORTH, textureRightUp)
                .texture(TextureSlot.WEST, textureLeftUp)
                .build();

        final var single = idBuilder.buildWithSuffix("_00_single");

        this.singleVariant(block)
                .variant(variant -> variant.model(model -> model
                        .model(single)
                        .delegateFor(block)
                        .cube(textureSingle)));
    }

    public <B extends Block> void multiblockFrame(Supplier<B> block, String baseName, String subDirectory) {
        this.multiblockFrame(block, baseName, baseName, subDirectory);
    }
}
