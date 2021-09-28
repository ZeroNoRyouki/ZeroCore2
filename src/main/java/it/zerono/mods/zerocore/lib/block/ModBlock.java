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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;
import java.util.List;
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

        final Block.Properties builder = Block.Properties.of(material)
                .strength(hardness, resistance)
                .sound(soundType);

        if (randomTick) {
            builder.randomTicks();
        }

        return builder;
    }

    //region extended properties

    @FunctionalInterface
    public interface IStackStorableTooltipBuilder {

        void build(ItemStack stack, CompoundNBT data, @Nullable IBlockReader world,
                   NonNullConsumer<ITextComponent> appender, ITooltipFlag flag);
    }

    public static class ExtendedProperties<T extends ExtendedProperties<T>> {

        public ExtendedProperties() {

            this._baseProperties = createProperties(Material.STONE, SoundType.STONE, 1.5f, 6.0f, false);
            this.setAsStackStorable(false);
        }

        public T setBlockProperties(final Block.Properties properties) {

            this._baseProperties = properties;
            return this.self();
        }

        public T setAsStackStorable(final boolean storable) {

            this._stackStorable = storable;
            this._stackStorableTooltipBuilder = EMPTY_STACK_STORABLE_TOOLTIP_BUILDER;
            return this.self();
        }

        /**
         * Mark the block as having a stack-storable TileEntity (an ISyncableEntity) and set a tooltip builder for the associated ItemStack
         * @param tooltipBuilder the builder
         * @return this object
         */
        public T setAsStackStorable(final IStackStorableTooltipBuilder tooltipBuilder) {

            this._stackStorable = true;
            this._stackStorableTooltipBuilder = tooltipBuilder;
            return this.self();
        }

        //region internals

        private T self() {
            //noinspection unchecked
            return (T)this;
        }

        private Block.Properties _baseProperties;

        private boolean _stackStorable;
        private IStackStorableTooltipBuilder _stackStorableTooltipBuilder;

        //endregion
    }
    
    public static ITextComponent getNameForTranslation(final Block block) {
        return new TranslationTextComponent(block.getDescriptionId());
    }

    public static int lightValueFrom(final float value) {
        return (int)(15.0F * value);
    }

    @Deprecated // This constructor will be removed in future version in favor of the ExtendedProperties one
    public ModBlock(final Block.Properties properties) {

        super(properties);
        this._stackStorable = false;
        this._stackStorableTooltipBuilder = EMPTY_STACK_STORABLE_TOOLTIP_BUILDER;
        this.registerDefaultState(this.buildDefaultState(this.getStateDefinition().any()));
    }

    public ModBlock(final ExtendedProperties extendedProperties) {

        super(extendedProperties._baseProperties);
        this._stackStorable = extendedProperties._stackStorable;
        this._stackStorableTooltipBuilder = extendedProperties._stackStorableTooltipBuilder;
        this.registerDefaultState(this.buildDefaultState(this.getStateDefinition().any()));
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
        world.setBlock(position, this.buildUpdatedState(currentState, world, position, tileEntity), updateFlags);
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
    public VoxelShape getBlockSupportShape(BlockState state, IBlockReader reader, BlockPos pos) {
        return super.getBlockSupportShape(state, reader, pos);
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
    public boolean triggerEvent(BlockState state, World world, BlockPos position, int id, int param) {

        if (this.hasTileEntity(state)) {
            return WorldHelper.getTile(world, position)
                    .map(tile -> tile.triggerEvent(id, param))
                    .orElse(super.triggerEvent(state, world, position, id, param));

        } else {
            return super.triggerEvent(state, world, position, id, param);
        }
    }

    @Override
    public void appendHoverText(final ItemStack stack, final @Nullable IBlockReader world,
                                final List<ITextComponent> tooltip, final ITooltipFlag flag) {

        if (this._stackStorable && stack.hasTag()) {

            CompoundNBT data = stack.getTag();

            //noinspection ConstantConditions
            if (data.contains("BlockEntityTag")) {
                data = data.getCompound("BlockEntityTag");
            }

            if (data.contains("zcvase_payload")) {
                data = data.getCompound("zcvase_payload");
            }

            this._stackStorableTooltipBuilder.build(stack, data, world, tooltip::add, flag);
        }
    }

    //endregion
    //region internals

    @Override
    protected void createBlockStateDefinition(final StateContainer.Builder<Block, BlockState> builder) {
        this.buildBlockState(builder);
    }

    protected void buildBlockState(final StateContainer.Builder<Block, BlockState> builder) {
    }

    protected BlockState buildDefaultState(BlockState state) {
        return state;
    }

    private static final IStackStorableTooltipBuilder EMPTY_STACK_STORABLE_TOOLTIP_BUILDER = (stack, data, world, appender, flag) -> {};

    private final boolean _stackStorable;
    private final IStackStorableTooltipBuilder _stackStorableTooltipBuilder;

    //endregion
}
