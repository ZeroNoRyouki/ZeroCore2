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
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess"})
public final class WorldHelper {

    public static final int DIMENSION_ID_OVERWORLD = 0;
    public static final int DIMENSION_ID_NETHER = -1;
    public static final int DIMENSION_ID_THEEND = 1;

    public static Optional<Level> getClientWorld() {
        return ZeroCore.getProxy().getClientWorld();
    }

    public static Optional<ServerLevel> getServerWorld(final ResourceKey<Level> worldKey) {

        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        return null != server ? Optional.ofNullable(server.getLevel(worldKey)) : Optional.empty();
    }

    //region Positions helpers

    public static Stream<BlockPos> getNeighboringPositions(BlockPos origin) {
        return Stream.of(Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)
                .map(origin::relative);
    }

    public static BlockPos[] getNeighboringPositionsList(final BlockPos origin, final BlockPos[] storage) {

        for (int i = 0; i < CodeHelper.DIRECTIONS.length; ++i) {
            storage[i] = origin.relative(CodeHelper.DIRECTIONS[i]);
        }

        return storage;
    }

    //endregion
    //region Block / BlockState helpers

    public static Optional<BlockState> getBlockState(Level world, BlockPos position) {
        return world.isLoaded(position) ? Optional.of(world.getBlockState(position)) : Optional.empty();
    }

    public static Stream<BlockState> getBlockStatesFrom(Level world, Stream<BlockPos> positions) {
        return getFromWorld(world, positions, WorldHelper::getBlockState);
    }

    public static Stream<Block> getBlocksFrom(Level world, Stream<BlockPos> positions) {
        return getBlockStatesFrom(world, positions)
                .map(BlockState::getBlock);
    }

    /**
     * Force a block update at the given position
     *
     * @param world the world to update
     * @param position the position of the block begin updated
     */
    public static void notifyBlockUpdate(Level world, BlockPos position) {
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
    public static void notifyBlockUpdate(Level world, BlockPos position, @Nullable BlockState oldState,
                                         @Nullable BlockState newState) {

        if (null == oldState) {
            oldState = world.getBlockState(position);
        }

        if (null == newState) {
            newState = oldState;
        }

        world.sendBlockUpdated(position, oldState, newState, 3);
    }

    public static void markBlockRangeForRenderUpdate(BlockPos min, BlockPos max) {
        ZeroCore.getProxy().markBlockRangeForRenderUpdate(min, max);
    }

    /**
     * MC-Version independent wrapper around World::notifyNeighborsOfStateChange()
     */
    public static void notifyNeighborsOfStateChange(final Level world, final BlockPos pos, final Block blockType) {
        world.updateNeighborsAt(pos, blockType);
    }

    //endregion
    //region Tile Entities

    @Nullable
    public static BlockEntity getLoadedTile(final Level world, final BlockPos position) {
        return world.isInWorldBounds(position) ?
                getLoadedTile((LevelChunk)world.getChunk(position.getX() >> 4, position.getZ() >> 4, ChunkStatus.FULL, false), position) : null;
    }
    @Nullable
    public static BlockEntity getLoadedTile(final ChunkCache chunkCache, final BlockPos position) {
        return chunkCache.getWorld().isInWorldBounds(position) ? getLoadedTile(chunkCache.get(position), position) : null;
    }

    @Nullable
    private static BlockEntity getLoadedTile(final @Nullable LevelChunk chunk, final BlockPos position) {
        return null != chunk ? chunk.getBlockEntity(position, LevelChunk.EntityCreationType.CHECK) : null;
    }

    /**
     * Get a TileEntity from the given position if that position is currently loaded and within the world border
     *
     * @param world the TileEntity world
     * @param position the TileEntity position
     * @return an Optional holding the TileEntity if it exists and it was already loaded and within the world border
     */
    public static Optional<BlockEntity> getTile(Level world, BlockPos position) {
        return world.getWorldBorder().isWithinBounds(position) ? Optional.ofNullable(getLoadedTile(world, position)) : Optional.empty();
    }

    /**
     * Get a TileEntity from the given position.
     *
     * @param world the TileEntity world
     * @param position the TileEntity position
     * @return an Optional holding the TileEntity if it exists
     */
    @Deprecated
    public static Optional<BlockEntity> getTile(BlockGetter world, BlockPos position) {

        if (world instanceof Level) {
            return Optional.ofNullable(getLoadedTile((Level)world, position));
        }

        return Optional.ofNullable(world.getBlockEntity(position));
    }

    /**
     * Get a TileEntity from the given, offsetted, position if the resulting position is currently loaded and within the world border
     *
     * @param world the TileEntity world
     * @param origin the starting position
     * @param direction the direction used to offset the starting position
     * @return an Optional holding the TileEntity if it exists and it was already loaded and within the world border
     */
    public static Optional<BlockEntity> getTile(Level world, BlockPos origin, Direction direction) {
        return getTile(world, origin.relative(direction));
    }

    /**
     * Get a TileEntity from the given, offsetted, position if the resulting position is currently loaded and within the world border
     *
     * @param origin the TileEntity defining the starting position and world
     * @param direction the direction used to offset the starting position
     * @return an Optional holding the TileEntity if it exists and it was already loaded and within the world border
     */
    public static Optional<BlockEntity> getTile(BlockEntity origin, Direction direction) {

        final Level world = origin.getLevel();

        return null != world ? getTile(world, origin.getBlockPos().relative(direction)) : Optional.empty();
    }

    public static Stream<BlockEntity> getTilesFrom(Level world, Stream<BlockPos> positions) {
        return getFromWorld(world, positions, WorldHelper::getTile);
    }

    @SuppressWarnings("unchecked")
    public static <Controller extends IMultiblockController<Controller>>
        Optional<IMultiblockPart<Controller>> getMultiblockPartFrom(Level world, BlockPos position) {
        return WorldHelper.getTile(world, position)
                .filter(te -> te instanceof IMultiblockPart)
                .map(te -> (IMultiblockPart<Controller>)te);
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> Optional<T> getClientTile(final BlockPos position) {
        return getClientWorld().map(w -> (T)getLoadedTile(w, position));
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends BlockEntity> Optional<T> getClientTile(final BlockPos position, final Direction direction) {
        return getClientTile(position.relative(direction));
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

    public static boolean chunkExists(Level world, BlockPos position) {
        return world.hasChunk(getChunkXFromBlock(position), getChunkZFromBlock(position));
    }

    //endregion

    public static boolean isEntityInRange(Entity entity, double x, double y, double z, double range) {
        return entity.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) < (range * range);
    }

    public static boolean isEntityInRange(Entity entity, BlockPos position, double range) {
        return entity.distanceToSqr(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5) < (range * range);
    }

    public static <T extends ParticleOptions> void spawnVanillaParticles(final Level world, final T particle,
                                                                       int minCount, int maxCount,
                                                                       int x, int y, int z,
                                                                       int offsetX, int offsetY, int offsetZ) {

        final RandomSource rand = world.random;
        int howMany = Mth.nextInt(rand, minCount, maxCount);
        double motionX, motionY, motionZ, pX, pY, pZ, px1, px2, py1, py2, pz1, pz2;

        px1 = x - offsetX + 0.5D;
        px2 = x + offsetX + 0.5D;
        py1 = y;
        py2 = y + offsetY;
        pz1 = z - offsetZ + 0.5D;
        pz2 = z + offsetZ + 0.5D;

        if (world instanceof ServerLevel) {

            final ServerLevel ws = (ServerLevel)world;

            motionX = rand.nextGaussian() * 0.02D;
            motionY = rand.nextGaussian() * 0.02D;
            motionZ = rand.nextGaussian() * 0.02D;

            pX = Mth.nextDouble(rand, px1, px2);
            pY = Mth.nextDouble(rand, py1, py2);
            pZ = Mth.nextDouble(rand, pz1, pz2);

            ws.sendParticles(particle, pX, pY, pZ, howMany, motionX, motionY, motionZ, rand.nextGaussian() * 0.02D);

        } else {

            for (int i = 0; i < howMany; ++i) {

                motionX = rand.nextGaussian() * 0.02D;
                motionY = rand.nextGaussian() * 0.02D;
                motionZ = rand.nextGaussian() * 0.02D;

                pX = Mth.nextDouble(rand, px1, px2);
                pY = Mth.nextDouble(rand, py1, py2);
                pZ = Mth.nextDouble(rand, pz1, pz2);

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
    public static void spawnItemStack(ItemStack stack, Level world, double x, double y, double z, boolean withMomentum) {

        float x2, y2, z2;

        if (withMomentum) {

            x2 = world.random.nextFloat() * 0.8F + 0.1F;
            y2 = world.random.nextFloat() * 0.8F + 0.1F;
            z2 = world.random.nextFloat() * 0.8F + 0.1F;

        } else {

            x2 = 0.5F;
            y2 = 0.0F;
            z2 = 0.5F;
        }

        final ItemEntity entity = new ItemEntity(world, x + x2, y + y2, z + z2, stack.copy());

        if (withMomentum) {
            entity.setDeltaMovement(world.random.nextGaussian() * 0.05F,
                    world.random.nextGaussian() * 0.05F + 0.2F,
                    world.random.nextGaussian() * 0.05F);
        } else {
            entity.setDeltaMovement(0.0, -0.05F, 0.0);
        }

        world.addFreshEntity(entity);
    }

    public static boolean isFluidStateTagged(BlockGetter access, BlockPos position, TagKey<Fluid> tag) {
        return access.getFluidState(position).is(tag);
    }

    public static boolean isFluidStateTagged(BlockState blockState, TagKey<Fluid> tag) {
        return blockState.getFluidState().is(tag);
    }

    //region internals

    private static <T> Stream<T> getFromWorld(Level world, Stream<BlockPos> positions,
                                              BiFunction<Level, BlockPos, Optional<T>> getter) {
        return positions
                .map(position -> getter.apply(world, position))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private WorldHelper() {
    }

    //endregion
}
