/*
 *
 * GlassBlock.java
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

package it.zerono.mods.zerocore.base.multiblock.part;

import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GlassBlock<Controller extends IMultiblockController<Controller>,
                        PartType extends Enum<PartType> & IMultiblockPartType>
        extends GenericDeviceBlock<Controller, PartType>
        implements INeverCauseRenderingSkip {

    public GlassBlock(final MultiblockPartProperties<PartType> properties) {
        super(properties);
    }

    public static AbstractBlock.Properties addGlassProperties(final AbstractBlock.Properties originals) {
        return originals
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isViewBlocking((blockState, blockReader, pos) -> false);
    }

    //region Block

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return this == adjacentBlockState.getBlock();
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    //endregion
    //region ModBlock

    @Override
    protected void buildBlockState(final StateContainer.Builder<Block, BlockState> builder) {

        super.buildBlockState(builder);
        builder.add(BlockFacingsProperty.FACINGS);
    }

    @Override
    protected BlockState buildDefaultState(final BlockState state) {
        return super.buildDefaultState(state).setValue(BlockFacingsProperty.FACINGS, BlockFacingsProperty.None);
    }

    //endregion
}
