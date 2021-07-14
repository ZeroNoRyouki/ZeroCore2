/*
 *
 * ModBlock.java
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

package it.zerono.mods.zerocore.lib.block;

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.*;

public class ModBlock
        extends Block
        implements IBlockStateUpdater {

    public static Block.Properties createProperties(final Material material, final SoundType soundType,
                                                    final float hardnessAndResistance) {
        return createProperties(material,soundType, hardnessAndResistance, hardnessAndResistance, false);
    }

    public static Block.Properties createProperties(final Material material, final SoundType soundType,
                                                    final float hardness, final float resistance,
                                                    final boolean randomTick) {

        final Block.Properties builder = Block.Properties.create(material)
                .hardnessAndResistance(hardness, resistance)
                .sound(soundType);

        if (randomTick) {
            builder.tickRandomly();
        }

        return builder;
    }
    
    public static ITextComponent getNameForTranslation(final Block block) {
        return new TranslationTextComponent(block.getTranslationKey());
    }

    public static int lightValueFrom(final float value) {
        return (int)(15.0F * value);
    }

    public ModBlock(final Block.Properties properties) {

        super(properties);
        this.setDefaultState(this.buildDefaultState(this.getStateContainer().getBaseState()));
    }

    public ItemStack createItemStack() {
            return ItemHelper.stackFrom(this, 1);
    }

    public ItemStack createItemStack(final int amount) {
        return ItemHelper.stackFrom(this, amount);
    }

    @SuppressWarnings("ConstantConditions")
    public BlockItem createBlockItem(final Item.Properties properties) {
        return new BlockItem(this, properties);
    }

    //region Logical sides and deferred execution helpers

    public void callOnLogicalSide(final World world, final Runnable serverCode, final Runnable clientCode) {
        CodeHelper.callOnLogicalSide(world, serverCode, clientCode);
    }

    public <T> T callOnLogicalSide(final World world, final Supplier<T> serverCode, final Supplier<T> clientCode) {
        return CodeHelper.callOnLogicalSide(world, serverCode, clientCode);
    }

    public boolean callOnLogicalSide(final World world, final BooleanSupplier serverCode, final BooleanSupplier clientCode) {
        return CodeHelper.callOnLogicalSide(world, serverCode, clientCode);
    }

    public int callOnLogicalSide(final World world, final IntSupplier serverCode, final IntSupplier clientCode) {
        return CodeHelper.callOnLogicalSide(world, serverCode, clientCode);
    }

    public long callOnLogicalSide(final World world, final LongSupplier serverCode, final LongSupplier clientCode) {
        return CodeHelper.callOnLogicalSide(world, serverCode, clientCode);
    }

    public double callOnLogicalSide(final World world, final DoubleSupplier serverCode, final DoubleSupplier clientCode) {
        return CodeHelper.callOnLogicalSide(world, serverCode, clientCode);
    }

    public void callOnLogicalServer(final World world, final Runnable code) {
        CodeHelper.callOnLogicalServer(world, code);
    }

    public void callOnLogicalServer(final World world, final Consumer<World> code) {

        if (CodeHelper.calledByLogicalServer(world)) {
            code.accept(world);
        }
    }

    public <T> T callOnLogicalServer(final World world, final Supplier<T> code, final Supplier<T> invalidSideReturnValue) {
        return CodeHelper.callOnLogicalServer(world, code, invalidSideReturnValue);
    }

    public boolean callOnLogicalServer(final World world, final BooleanSupplier code) {
        return CodeHelper.callOnLogicalServer(world, code);
    }

    public int callOnLogicalServer(final World world, final IntSupplier code, final int invalidSideReturnValue) {
        return CodeHelper.callOnLogicalServer(world, code, invalidSideReturnValue);
    }

    public long callOnLogicalServer(final World world, final LongSupplier code, final long invalidSideReturnValue) {
        return CodeHelper.callOnLogicalServer(world, code, invalidSideReturnValue);
    }

    public double callOnLogicalServer(final World world, final DoubleSupplier code, final double invalidSideReturnValue) {
        return CodeHelper.callOnLogicalServer(world, code, invalidSideReturnValue);
    }

    public void callOnLogicalClient(final World world, final Runnable code) {
        CodeHelper.callOnLogicalClient(world, code);
    }

    public void callOnLogicalClient(final World world, final Consumer<World> code) {

        if (CodeHelper.calledByLogicalClient(world)) {
            code.accept(world);
        }
    }

    public <T> T callOnLogicalClient(final World world, final Supplier<T> code, final Supplier<T> invalidSideReturnValue) {
        return CodeHelper.callOnLogicalClient(world, code, invalidSideReturnValue);
    }

    public boolean callOnLogicalClient(final World world, final BooleanSupplier code) {
        return CodeHelper.callOnLogicalClient(world, code);
    }

    public int callOnLogicalClient(final World world, final IntSupplier code, final int invalidSideReturnValue) {
        return CodeHelper.callOnLogicalClient(world, code, invalidSideReturnValue);
    }

    public long callOnLogicalClient(final World world, final LongSupplier code, final long invalidSideReturnValue) {
        return CodeHelper.callOnLogicalClient(world, code, invalidSideReturnValue);
    }

    public double callOnLogicalClient(final World world, final DoubleSupplier code, final double invalidSideReturnValue) {
        return CodeHelper.callOnLogicalClient(world, code, invalidSideReturnValue);
    }

    //endregion
    //region IBlockStateUpdater

    @Override
    public void updateBlockState(BlockState currentState, IWorld world, BlockPos position,
                                 @Nullable TileEntity tileEntity, int updateFlags) {
        world.setBlockState(position, this.buildUpdatedState(currentState, world, position, tileEntity), updateFlags);
    }

    @Override
    public BlockState buildUpdatedState(BlockState currentState, IBlockReader reader,
                                        BlockPos position, @Nullable TileEntity tileEntity) {
        return currentState;
    }

    //endregion
    //region INeighborChangeListener support

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos blockPosition, Block block, BlockPos neighborPosition, boolean isMoving) {

        super.neighborChanged(state, world, blockPosition, block, neighborPosition, isMoving);

        if (this instanceof INeighborChangeListener.Notifier && this.hasTileEntity(state)) {

            WorldHelper.getTile(world, blockPosition)
                    .filter(te -> te instanceof INeighborChangeListener)
                    .map(te -> (INeighborChangeListener)te)
                    .ifPresent(listener -> listener.onNeighborBlockChanged(state, neighborPosition, isMoving));
        }
    }

    /**
     * Called when a tile entity on a side of this block changes is created or is destroyed.
     *
     * @param state
     * @param world The world
     * @param blockPosition Block position in world
     * @param neighborPosition Block position of neighbor
     */
    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos blockPosition, BlockPos neighborPosition) {

        super.onNeighborChange(state, world, blockPosition, neighborPosition);

        if (this instanceof INeighborChangeListener.Notifier && this.hasTileEntity(state)) {

            final TileEntity te = WorldHelper.getLoadedTile(world, blockPosition);

            if (te instanceof INeighborChangeListener) {
                ((INeighborChangeListener)te).onNeighborTileChanged(state, neighborPosition);
            }
        }
    }

    //endregion
    //region Block


    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos) {
        return super.getCollisionShape(state, reader, pos);
    }

    /**
     * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
     * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
     * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
     *
     * @param state
     * @param world
     * @param position
     * @param id
     * @param param
     */
    @Override
    public boolean eventReceived(BlockState state, World world, BlockPos position, int id, int param) {

        if (this.hasTileEntity(state)) {
            return WorldHelper.getTile(world, position)
                    .map(tile -> tile.receiveClientEvent(id, param))
                    .orElse(super.eventReceived(state, world, position, id, param));

        } else {
            return super.eventReceived(state, world, position, id, param);
        }
    }

    //endregion
    //region internals

    @Override
    protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
        this.buildBlockState(builder);
    }

    protected void buildBlockState(final StateContainer.Builder<Block, BlockState> builder) {
    }

    protected BlockState buildDefaultState(BlockState state) {
        return state;
    }

    //endregion
}
