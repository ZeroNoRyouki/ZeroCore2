package it.zerono.mods.zerotest.datagen;

/*
 * BlockTagsDataProvider
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

import it.zerono.mods.zerocore.lib.datagen.provider.tag.IIntrinsicTagDataProvider;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.ModIntrinsicTagAppender;
import it.zerono.mods.zerotest.ZeroTest;
import it.zerono.mods.zerotest.test.content.TestContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.common.util.NonNullFunction;

public class BlockTagsDataProvider
        implements IIntrinsicTagDataProvider<Block> {

    @Override
    public String getName() {
        return ZeroTest.MOD_NAME + " blocks tags";
    }

    @Override
    public void build(HolderLookup.Provider registryLookup,
                      NonNullFunction<TagKey<Block>, ModIntrinsicTagAppender<Block>> builder) {

        builder.apply(BlockTags.BUTTONS).add(TestContent.Blocks.TEST_BUTTON);
        builder.apply(BlockTags.DOORS).add(TestContent.Blocks.TEST_DOOR);
        builder.apply(BlockTags.FENCES).add(TestContent.Blocks.TEST_FENCE);
        builder.apply(BlockTags.FENCE_GATES).add(TestContent.Blocks.TEST_FENCEGATE);
        builder.apply(BlockTags.PRESSURE_PLATES).add(TestContent.Blocks.TEST_PRESSURE_PLATE);
        builder.apply(BlockTags.STAIRS).add(TestContent.Blocks.TEST_STAIRS);
        builder.apply(BlockTags.WALLS).add(TestContent.Blocks.TEST_WALL);

        TestContent.Blocks.getAll().forEach(s -> {

            if (!(s.get() instanceof LiquidBlock)) {

                builder.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(s);
                builder.apply(BlockTags.NEEDS_IRON_TOOL).add(s);
            }
        });
    }
}
