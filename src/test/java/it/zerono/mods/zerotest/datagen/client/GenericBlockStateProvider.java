package it.zerono.mods.zerotest.datagen.client;

/*
 * GenericBlockStateProvider
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
 * Do not remove or edit this header
 *
 */

import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.client.state.BlockStateDataProvider;
import it.zerono.mods.zerotest.test.content.TestContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class GenericBlockStateProvider
        extends BlockStateDataProvider {

    public GenericBlockStateProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ResourceLocationBuilder modLocationRoot) {
        super("Generic blockstate provider", output, lookupProvider, modLocationRoot);
    }

    //region BlockStateDataProvider

    @Override
    public void provideData() {

        this.testBlocks();


    }

    //endregion
    //region internals

    private void testBlocks() {

        final var blue = this.blocksRoot().buildWithSuffix("blue_block_side");
        final var red = this.blocksRoot().buildWithSuffix("red_block_side");
        final var green = this.blocksRoot().buildWithSuffix("green_block_side");
        final var grey = this.blocksRoot().buildWithSuffix("grey_block_side");
        final var purple = this.blocksRoot().buildWithSuffix("purple_block_side");
        final var yellow = this.blocksRoot().buildWithSuffix("yellow_block_side");
        final var blue_log = this.blocksRoot().buildWithSuffix("blue_log");
        final var blue_plank = this.blocksRoot().buildWithSuffix("blue_plank");

        this.cube(TestContent.Blocks.TEST_BLOCK, blue);

        this.singleVariant(TestContent.Blocks.TEST_BLOCK2)
                .model(this.models()
                        .block(TestContent.Blocks.TEST_BLOCK2)
                        .delegateFor(TestContent.Blocks.TEST_BLOCK2)
                        .cube(blue, blue, red, green, grey,
                                purple, yellow));

        this.wood(TestContent.Blocks.TEST_WOOD, blue_log);
        this.woodLog(TestContent.Blocks.TEST_WOOD_LOG, green, purple);
        this.woodPlanks(TestContent.Blocks.TEST_WOOD_PLANK, blue_plank);
        this.leaves(TestContent.Blocks.TEST_LEAVES, new ResourceLocation("minecraft:block/acacia_leaves"));

        this.button(TestContent.Blocks.TEST_BUTTON, blue);
        this.door(TestContent.Blocks.TEST_DOOR, blue, red, blue);
        this.fence(TestContent.Blocks.TEST_FENCE, blue);
        this.fenceGate(TestContent.Blocks.TEST_FENCEGATE, blue);
        this.wall(TestContent.Blocks.TEST_WALL, blue);
        this.pressurePlate(TestContent.Blocks.TEST_PRESSURE_PLATE, blue);

        final var doubleSlabModel = this.models()
                .block(TestContent.Blocks.TEST_SLAB, "_double")
                .cube(blue, blue, blue, blue, blue, red, red);

        this.slab(TestContent.Blocks.TEST_SLAB, doubleSlabModel, red, red, blue);
        this.stairs(TestContent.Blocks.TEST_STAIRS, blue);
        this.trapdoor(TestContent.Blocks.TEST_TRAPDOOR, false, purple);
        this.trapdoor(TestContent.Blocks.TEST_TRAPDOOR_ORIENTABLE, true, green);

        final var glass = new ResourceLocation("minecraft:block/blue_stained_glass");
        final var glassPaneEdge = new ResourceLocation("minecraft:block/blue_stained_glass_pane_top");

        this.glass(TestContent.Blocks.TEST_GLASS, glass);
        this.glassPane(TestContent.Blocks.TEST_GLASS_PANE, glass, glassPaneEdge);
    }

    //endregion
}
