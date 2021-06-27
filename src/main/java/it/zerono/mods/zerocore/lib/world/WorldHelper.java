/*
 *
 * WorldHelper.java
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

package it.zerono.mods.zerocore.lib.world;

import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess"})
public final class WorldHelper {

    public static final int DIMENSION_ID_OVERWORLD = 0;
    public static final int DIMENSION_ID_NETHER = -1;
    public static final int DIMENSION_ID_THEEND = 1;

    public static Optional<World> getClientWorld() {
        return ZeroCore.getProxy().getClientWorld();
    }

    public static Optional<ServerWorld> getServerWorld(final RegistryKey<World> worldKey) {

        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        return null != server ? Optional.ofNullable(server.getWorld(worldKey)) : Optional.empty();
    }

    //region Positions helpers

    public static Stream<BlockPos> getNeighboringPositions(BlockPos origin) {
        return Stream.of(Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)
                .map(origin::offset);
    }

    public static BlockPos[] getNeighboringPositionsList(final BlockPos origin, final BlockPos[] storage) {

        for (int i = 0; i < CodeHelper.DIRECTIONS.length; ++i) {
            storage[i] = origin.offset(CodeHelper.DIRECTIONS[i]);
        }

        return storage;
    }

    //endregion
    //region Block / BlockState helpers

    public static Optional<BlockState> getBlockState(World world, BlockPos position) {
        return world.isBlockPresent(position) ? Optional.of(world.getBlockState(position)) : Optional.empty();
    }

    public static Stream<BlockState> getBlockStatesFrom(World world, Stream<BlockPos> positions) {
        return getFromWorld(world, positions, WorldHelper::getBlockState);
    }

    public static Stream<Block> getBlocksFrom(World world, Stream<BlockPos> positions) {
        return getBlockStatesFrom(world, positions)
                .map(BlockState::getBlock);
    }

    /**
     * Force a block update at the given position
     *
     * @param world the world to update
     * @param position the position of the block begin updated
     */
    public static void notifyBlockUpdate(World world, BlockPos position) {
        notifyBlockUpdate(world, position, null, null);
    }

    /**
     * Force a block update at the given position
     *
     * @param world the world to update
     * @param position the position of the block begin updated
     * @param oldState the old state of the block begin updated. if null, the current state will be retrieved from the world
     * @param newState the new state for the block begin updated. if null, the final value of oldState will be used
     */
    public static void notifyBlockUpdate(World world, BlockPos position, @Nullable BlockState oldState,
                                         @Nullable BlockState newState) {

        if (null == oldState) {
            oldState = world.getBlockState(position);
        }

        if (null == newState) {
            newState = oldState;
        }

        world.notifyBlockUpdate(position, oldState, newState, 3);
    }

    public static void markBlockRangeForRenderUpdate(BlockPos min, BlockPos max) {
        ZeroCore.getProxy().markBlockRangeForRenderUpdate(min, max);
    }

    /**
     * MC-Version independent wrapper around World::notifyNeighborsOfStateChange()
     */
    public static void notifyNeighborsOfStateChange(final World world, final BlockPos pos, final Block blockType) {
        world.notifyNeighborsOfStateChange(pos, blockType);
    }

    //endregion
    //region Tile Entities

    @Nullable
    public static TileEntity getLoadedTile(final IWorldReader world, final BlockPos position) {
        return World.isValid(position) ?
                getLoadedTile((Chunk)world.getChunk(position.getX() >> 4, position.getZ() >> 4, ChunkStatus.FULL, false), position) : null;
    }
    @Nullable
    public static TileEntity getLoadedTile(final ChunkCache chunkCache, final BlockPos position) {
        return World.isValid(position) ? getLoadedTile(chunkCache.get(position), position) : null;
    }

    @Nullable
    private static TileEntity getLoadedTile(final @Nullable Chunk chunk, final BlockPos position) {
        return null != chunk ? chunk.getTileEntity(position, Chunk.CreateEntityType.CHECK) : null;
    }

    /**
     * Get a TileEntity from the given position if that position is currently loaded and within the world border
     *
     * @param world the TileEntity world
     * @param position the TileEntity position
     * @return an Optional holding the TileEntity if it exists and it was already loaded and within the world border
     */
    public static Optional<TileEntity> getTile(World world, BlockPos position) {
        return world.isBlockPresent(position) && world.getWorldBorder().contains(position) ?
                Optional.ofNullable(world.getTileEntity(position)) :
                Optional.empty();
    }

    /**
     * Get a TileEntity from the given position.
     *
     * @param world the TileEntity world
     * @param position the TileEntity position
     * @return an Optional holding the TileEntity if it exists
     */
    public static Optional<TileEntity> getTile(IBlockReader world, BlockPos position) {
        return Optional.ofNullable(world.getTileEntity(position));
    }

    /**
     * Get a TileEntity from the given, offsetted, position if the resulting position is currently loaded and within the world border
     *
     * @param world the TileEntity world
     * @param origin the starting position
     * @param direction the direction used to offset the starting position
     * @return an Optional holding the TileEntity if it exists and it was already loaded and within the world border
     */
    public static Optional<TileEntity> getTile(World world, BlockPos origin, Direction direction) {
        return getTile(world, origin.offset(direction));
    }

    /**
     * Get a TileEntity from the given, offsetted, position if the resulting position is currently loaded and within the world border
     *
     * @param origin the TileEntity defining the starting position and world
     * @param direction the direction used to offset the starting position
     * @return an Optional holding the TileEntity if it exists and it was already loaded and within the world border
     */
    public static Optional<TileEntity> getTile(TileEntity origin, Direction direction) {

        final World world = origin.getWorld();

        return null != world ? getTile(world, origin.getPos().offset(direction)) : Optional.empty();
    }

    public static Stream<TileEntity> getTilesFrom(World world, Stream<BlockPos> positions) {
        return getFromWorld(world, positions, WorldHelper::getTile);
    }

    @SuppressWarnings("unchecked")
    public static <Controller extends IMultiblockController<Controller>>
        Optional<IMultiblockPart<Controller>> getMultiblockPartFrom(World world, BlockPos position) {
        return WorldHelper.getTile(world, position)
                .filter(te -> te instanceof IMultiblockPart)
                .map(te -> (IMultiblockPart<Controller>)te);
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> Optional<T> getClientTile(final BlockPos position) {
        return getClientWorld().map(w -> (T)w.getTileEntity(position));
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends TileEntity> Optional<T> getClientTile(final BlockPos position, final Direction direction) {
        return getClientTile(position.offset(direction));
    }

    //endregion
    //region Chunk helpers

    public static int getChunkXFromBlock(int blockX) {
        return blockX >> 4;
    }

    public static int getChunkXFromBlock(BlockPos position) {
        return position.getX() >> 4;
    }

    public static int getChunkZFromBlock(int blockZ) {
        return blockZ >> 4;
    }

    public static int getChunkZFromBlock(BlockPos position) {
        return position.getZ() >> 4;
    }

    public static long getChunkXZHashFromBlock(int blockX, int blockZ) {
        return ChunkPos.asLong(WorldHelper.getChunkXFromBlock(blockX), WorldHelper.getChunkZFromBlock(blockZ));
    }

    public static long getChunkXZHashFromBlock(BlockPos position) {
        return ChunkPos.asLong(WorldHelper.getChunkXFromBlock(position), WorldHelper.getChunkZFromBlock(position));
    }

    public static boolean chunkExists(World world, BlockPos position) {
        return world.chunkExists(getChunkXFromBlock(position), getChunkZFromBlock(position));
    }

    //endregion

    public static boolean isEntityInRange(Entity entity, double x, double y, double z, double range) {
        return entity.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < (range * range);
    }

    public static boolean isEntityInRange(Entity entity, BlockPos position, double range) {
        return entity.getDistanceSq(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5) < (range * range);
    }

    public static <T extends IParticleData> void spawnVanillaParticles(final World world, final T particle,
                                                                       int minCount, int maxCount,
                                                                       int x, int y, int z,
                                                                       int offsetX, int offsetY, int offsetZ) {

        final Random rand = world.rand;
        int howMany = MathHelper.nextInt(rand, minCount, maxCount);
        double motionX, motionY, motionZ, pX, pY, pZ, px1, px2, py1, py2, pz1, pz2;

        px1 = x - offsetX + 0.5D;
        px2 = x + offsetX + 0.5D;
        py1 = y;
        py2 = y + offsetY;
        pz1 = z - offsetZ + 0.5D;
        pz2 = z + offsetZ + 0.5D;

        if (world instanceof ServerWorld) {

            final ServerWorld ws = (ServerWorld)world;

            motionX = rand.nextGaussian() * 0.02D;
            motionY = rand.nextGaussian() * 0.02D;
            motionZ = rand.nextGaussian() * 0.02D;

            pX = MathHelper.nextDouble(rand, px1, px2);
            pY = MathHelper.nextDouble(rand, py1, py2);
            pZ = MathHelper.nextDouble(rand, pz1, pz2);

            ws.spawnParticle(particle, pX, pY, pZ, howMany, motionX, motionY, motionZ, rand.nextGaussian() * 0.02D);

        } else {

            for (int i = 0; i < howMany; ++i) {

                motionX = rand.nextGaussian() * 0.02D;
                motionY = rand.nextGaussian() * 0.02D;
                motionZ = rand.nextGaussian() * 0.02D;

                pX = MathHelper.nextDouble(rand, px1, px2);
                pY = MathHelper.nextDouble(rand, py1, py2);
                pZ = MathHelper.nextDouble(rand, pz1, pz2);

                world.addParticle(particle, pX, pY, pZ, motionX, motionY, motionZ);
            }
        }
    }

    /**
     * Spawn an ItemStack in the world
     * This replace StacksHelper.spawnInWorld
     *
     * @param stack the stack
     * @param world the world to spawn in
     * @param x spawn coordinates
     * @param y spawn coordinates
     * @param z spawn coordinates
     * @param withMomentum if true, add momentum to the stack
     */
    public static void spawnItemStack(ItemStack stack, World world, double x, double y, double z, boolean withMomentum) {

        float x2, y2, z2;

        if (withMomentum) {

            x2 = world.rand.nextFloat() * 0.8F + 0.1F;
            y2 = world.rand.nextFloat() * 0.8F + 0.1F;
            z2 = world.rand.nextFloat() * 0.8F + 0.1F;

        } else {

            x2 = 0.5F;
            y2 = 0.0F;
            z2 = 0.5F;
        }

        final ItemEntity entity = new ItemEntity(world, x + x2, y + y2, z + z2, ItemHelper.stackFrom(stack));

        if (withMomentum) {
            entity.setMotion(world.rand.nextGaussian() * 0.05F,
                    world.rand.nextGaussian() * 0.05F + 0.2F,
                    world.rand.nextGaussian() * 0.05F);
        } else {
            entity.setMotion(0.0, -0.05F, 0.0);
        }

        world.addEntity(entity);
    }

    public static boolean isFluidStateTagged(IBlockReader access, BlockPos position, ITag.INamedTag<Fluid> tag) {
        return access.getFluidState(position).isTagged(tag);
    }

    public static boolean isFluidStateTagged(BlockState blockState, ITag.INamedTag<Fluid> tag) {
        return blockState.getFluidState().isTagged(tag);
    }

    //region internals

    private static <T> Stream<T> getFromWorld(World world, Stream<BlockPos> positions,
                                              BiFunction<World, BlockPos, Optional<T>> getter) {
        return positions
                .map(position -> getter.apply(world, position))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private WorldHelper() {
    }

    //endregion
}
