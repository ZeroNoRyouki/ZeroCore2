/*
 *
 * AbstractCuboidMultiblockBlockStateProvider.java
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

package it.zerono.mods.zerocore.lib.datagen.provider.multiblock;

import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public abstract class AbstractCuboidMultiblockBlockStateProvider extends BlockStateProvider {

    public AbstractCuboidMultiblockBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    protected static String fullResourceName(final String resourceName, final String subFolder) {
        return ModelProvider.BLOCK_FOLDER + "/" + subFolder + "/" + resourceName;
    }

    protected void simpleBlock(final Supplier<? extends Block> block, final ModelFile model, final boolean genItemModel) {
        this.simpleBlock(block.get(), model, genItemModel);
    }

    protected void simpleBlock(final Block block, final ModelFile model, final boolean genItemModel) {

        this.simpleBlock(block, model);

        if (genItemModel) {
            this.simpleBlockItem(block, model);
        }
    }

    protected void genGlass(final Supplier<? extends Block> block, final String resourceName, final String subFolder) {
        this.genGlass(block.get(), resourceName, subFolder);
    }

    protected void genGlass(final Block block, final String resourceName, final String subFolder) {

        final BlockModelProvider mbp = this.models();
        final String fullResourceName = fullResourceName(resourceName, subFolder);

        final ResourceLocation glass0 = this.modLoc(fullResourceName + ".0");
        final ResourceLocation glass1 = this.modLoc(fullResourceName + ".1");
        final ResourceLocation glass3 = this.modLoc(fullResourceName + ".3");
        final ResourceLocation glass5 = this.modLoc(fullResourceName + ".5");
        final ResourceLocation glass7 = this.modLoc(fullResourceName + ".7");
        final ResourceLocation glass8 = this.modLoc(fullResourceName + ".8");
        final ResourceLocation glass9 = this.modLoc(fullResourceName + ".9");
        final ResourceLocation glass11 = this.modLoc(fullResourceName + ".11");
        final ResourceLocation glass12 = this.modLoc(fullResourceName + ".12");
        final ResourceLocation glass13 = this.modLoc(fullResourceName + ".13");
        final ResourceLocation glass15 = this.modLoc(fullResourceName + ".15");

        ModelFile model;

        // BlockFacingsProperty.None / glass_c0

        model = mbp.cubeAll(fullResourceName + "_c0", glass0);
        this.addGlassVariant(block, BlockFacingsProperty.None, model);
        this.simpleBlockItem(block, model);

        // BlockFacingsProperty.Face_* / glass_c1

        model = mbp.cube(fullResourceName + "_c1", glass0, glass15, glass1, glass1, glass1, glass1).texture("particle", glass0);
        this.addGlassVariant(block, BlockFacingsProperty.Face_U, model);
        this.addGlassVariant(block, BlockFacingsProperty.Face_D, model, 180, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Face_N, model, 90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Face_S, model, -90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Face_W, model, 90, -90);
        this.addGlassVariant(block, BlockFacingsProperty.Face_E, model, 90, 90);

        // BlockFacingsProperty.Angle_* / glass_c2angle

        model = mbp.cube(fullResourceName + "_c2angle", glass8, glass15, glass5, glass9, glass15, glass1).texture("particle", glass0);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_EU, model);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_UW, model, 0, 180);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_DE, model, 180, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_DW, model, 180, 180);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_NU, model, 0, -90);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_SU, model, 0, 90);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_DN, model, 180, -90);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_DS, model, 180, 90);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_EN, model, 90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_ES, model, -90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_NW, model, 90, -90);
        this.addGlassVariant(block, BlockFacingsProperty.Angle_SW, model, -90, 90);

        // BlockFacingsProperty.Opposite_* / glass_c2

        model = mbp.cube(fullResourceName + "_c2", glass15, glass15, glass3, glass3, glass3, glass3).texture("particle", glass0);
        this.addGlassVariant(block, BlockFacingsProperty.Opposite_DU, model);
        this.addGlassVariant(block, BlockFacingsProperty.Opposite_EW, model, -90, 90);
        this.addGlassVariant(block, BlockFacingsProperty.Opposite_NS, model, -90, 0);

        // BlockFacingsProperty.CShape_* / glass_c3t1/glass_c3t2

        model = mbp.cube(fullResourceName + "_c3t1", glass15, glass15, glass11, glass7, glass3, glass15).texture("particle", glass0);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_DUW, model);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_DSU, model, 0, -90);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_DEU, model, 0, -180);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_DNU, model, 0, 90);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_NSW, model, -90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_ENS, model, -90, 180);

        model = mbp.cube(fullResourceName + "_c3t2", glass12, glass15, glass13, glass13, glass15, glass15).texture("particle", glass0);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_EUW, model);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_ESW, model, -90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_DEW, model, 180, 0);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_ENW, model, 90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_NSU, model, 0, 90);
        this.addGlassVariant(block, BlockFacingsProperty.CShape_DNS, model, 180, 90);

        // BlockFacingsProperty.Corner_* / glass_c3angle

        model = mbp.cube(fullResourceName + "_c3angle", glass9, glass15, glass5, glass15, glass15, glass9).texture("particle", glass0);
        this.addGlassVariant(block, BlockFacingsProperty.Corner_ESU, model);
        this.addGlassVariant(block, BlockFacingsProperty.Corner_SUW, model, 0, 90);
        this.addGlassVariant(block, BlockFacingsProperty.Corner_ENU, model, 90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Corner_DEN, model, 180, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Corner_DES, model, 270, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Corner_DSW, model, 180, -180);
        this.addGlassVariant(block, BlockFacingsProperty.Corner_DNW, model, -90, -180);
        this.addGlassVariant(block, BlockFacingsProperty.Corner_NUW, model, 90, -90);

        // BlockFacingsProperty.Pipe_* / glass_c4x

        model = mbp.cube(fullResourceName + "_c4x", glass15, glass15, glass15, glass15, glass15, glass15).texture("particle", glass0);
        this.addGlassVariant(block, BlockFacingsProperty.Pipe_DEUW, model);
        this.addGlassVariant(block, BlockFacingsProperty.Pipe_ENSW, model, 90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Pipe_DNSU, model, 0, 90);

        // BlockFacingsProperty.Misc_* / glass_c4angle

        model = mbp.cube(fullResourceName + "_c4angle", glass15, glass15, glass15, glass7, glass11, glass15).texture("particle", glass0);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_DNUW, model);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_DNSW, model, 90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_NSUW, model, -90, 0);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_DSUW, model, 0, -90);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_DENU, model, 0, 90);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_DESU, model, 0, 180);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_ENSU, model, -90, 180);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_DENS, model, 90, 180);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_ESUW, model, -90, -90);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_DESW, model, -270, -90);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_ENUW, model, -90, 90);
        this.addGlassVariant(block, BlockFacingsProperty.Misc_DENW, model, -270, -270);

        // BlockFacingsProperty.PipeEnd_*/All / glass_c5-6

        model = mbp.cubeAll(fullResourceName + "_c5-6", glass15);
        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_DENSW, model);
        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_DESUW, model);
        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_DNSUW, model);
        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_DENSU, model);
        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_DENUW, model);
        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_ENSUW, model);
        this.addGlassVariant(block, BlockFacingsProperty.All, model);
    }

    private void addGlassVariant(Block block, BlockFacingsProperty property, ModelFile model) {
        this.addGlassVariant(block, property, model, 0, 0, false);
    }

    private void addGlassVariant(Block block, BlockFacingsProperty property, ModelFile model,
                                 int rotationX, int rotationY) {
        this.addGlassVariant(block, property, model, rotationX, rotationY, false);
    }

    private void addGlassVariant(Block block, BlockFacingsProperty property, ModelFile model,
                                 int rotationX, int rotationY, boolean uvLock) {
        this.getVariantBuilder(block)
                .partialState()
                    .with(BlockFacingsProperty.FACINGS, property)
                    .modelForState()
                        .modelFile(model)
                        .rotationX(rotationX)
                        .rotationY(rotationY)
                        .uvLock(uvLock)
                        .addModel();
    }

    protected void genFrame(final Supplier<? extends Block> block, final String resourceName, final String subFolder) {
        this.genFrame(block.get(), resourceName, subFolder);
    }

    protected void genFrame(final Block block, final String resourceName, final String subFolder) {

        final BlockModelProvider mbp = this.models();
        final String fullResourceName = fullResourceName(resourceName, subFolder);

        final ResourceLocation texturePlating = this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + subFolder + "/" + "plating");
        final ResourceLocation templateLoc = this.modLoc(fullResourceName + "_template");

        final ResourceLocation textureSingle = this.modLoc(fullResourceName + "_single");
        final ResourceLocation textureUp = this.modLoc(fullResourceName + "_up");
        final ResourceLocation textureDown = this.modLoc(fullResourceName + "_down");
        final ResourceLocation textureLeft = this.modLoc(fullResourceName + "_left");
        final ResourceLocation textureLeftDown = this.modLoc(fullResourceName + "_left_down");
        final ResourceLocation textureLeftUp = this.modLoc(fullResourceName + "_left_up");
        final ResourceLocation textureRight = this.modLoc(fullResourceName + "_right");
        final ResourceLocation textureRightDown = this.modLoc(fullResourceName + "_right_down");
        final ResourceLocation textureRightUp = this.modLoc(fullResourceName + "_right_up");

        mbp.cube(templateLoc.getPath(), texturePlating, texturePlating, texturePlating, texturePlating,
                texturePlating, texturePlating)
                .texture("particle", texturePlating);

        mbp.cubeAll(fullResourceName + "_01_face", texturePlating);

        mbp.withExistingParent(fullResourceName + "_02_frame_ds", templateLoc)
                .texture("down", textureUp)
                .texture("south", textureDown);

        mbp.withExistingParent(fullResourceName + "_03_frame_de", templateLoc)
                .texture("down", textureRight)
                .texture("east", textureDown);

        mbp.withExistingParent(fullResourceName + "_04_frame_dn", templateLoc)
                .texture("down", textureDown)
                .texture("north", textureDown);

        mbp.withExistingParent(fullResourceName + "_05_frame_dw", templateLoc)
                .texture("down", textureLeft)
                .texture("west", textureDown);

        mbp.withExistingParent(fullResourceName + "_06_frame_us", templateLoc)
                .texture("up", textureDown)
                .texture("south", textureUp);

        mbp.withExistingParent(fullResourceName + "_07_frame_ue", templateLoc)
                .texture("up", textureRight)
                .texture("east", textureUp);

        mbp.withExistingParent(fullResourceName + "_08_frame_un", templateLoc)
                .texture("up", textureUp)
                .texture("north", textureUp);

        mbp.withExistingParent(fullResourceName + "_09_frame_uw", templateLoc)
                .texture("up", textureLeft)
                .texture("west", textureUp);

        mbp.withExistingParent(fullResourceName + "_10_frame_se", templateLoc)
                .texture("south", textureRight)
                .texture("east", textureLeft);

        mbp.withExistingParent(fullResourceName + "_11_frame_ne", templateLoc)
                .texture("north", textureLeft)
                .texture("east", textureRight);

        mbp.withExistingParent(fullResourceName + "_12_frame_nw", templateLoc)
                .texture("north", textureRight)
                .texture("west", textureLeft);

        mbp.withExistingParent(fullResourceName + "_13_frame_sw", templateLoc)
                .texture("south", textureLeft)
                .texture("west", textureRight);

        mbp.withExistingParent(fullResourceName + "_14_corner_dsw", templateLoc)
                .texture("down", textureLeftUp)
                .texture("south", textureLeftDown)
                .texture("west", textureRightDown);

        mbp.withExistingParent(fullResourceName + "_15_corner_dse", templateLoc)
                .texture("down", textureRightUp)
                .texture("south", textureRightDown)
                .texture("east", textureLeftDown);

        mbp.withExistingParent(fullResourceName + "_16_corner_dne", templateLoc)
                .texture("down", textureRightDown)
                .texture("north", textureLeftDown)
                .texture("east", textureRightDown);

        mbp.withExistingParent(fullResourceName + "_17_corner_dnw", templateLoc)
                .texture("down", textureLeftDown)
                .texture("north", textureRightDown)
                .texture("west", textureLeftDown);

        mbp.withExistingParent(fullResourceName + "_18_corner_usw", templateLoc)
                .texture("up", textureLeftDown)
                .texture("south", textureLeftUp)
                .texture("west", textureRightUp);

        mbp.withExistingParent(fullResourceName + "_19_corner_use", templateLoc)
                .texture("up", textureRightDown)
                .texture("south", textureRightUp)
                .texture("east", textureLeftUp);

        mbp.withExistingParent(fullResourceName + "_20_corner_une", templateLoc)
                .texture("up", textureRightUp)
                .texture("north", textureLeftUp)
                .texture("east", textureRightUp);

        mbp.withExistingParent(fullResourceName + "_21_corner_unw", templateLoc)
                .texture("up", textureLeftUp)
                .texture("north", textureRightUp)
                .texture("west", textureLeftUp);

        this.simpleBlock(block, mbp.cubeAll(fullResourceName + "_00_single", textureSingle), true);
    }
}
