/*
 *
 * AbstractMultiblockController.java
 *
 * A multiblock library for making irregularly-shaped multiblock machines
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * Original author: Erogenous Beef
 * https://github.com/erogenousbeef/BeefCore
 *
 * Minecraft 1.9+ port and further development: ZeroNoRyouki
 * https://github.com/ZeroNoRyouki/ZeroCore2
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 "ZeroNoRyouki"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package it.zerono.mods.zerocore.lib.multiblock;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox;
import it.zerono.mods.zerocore.lib.data.nbt.INestedSyncableEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.multiblock.registry.MultiblockRegistry;
import it.zerono.mods.zerocore.lib.multiblock.storage.EmptyPartStorage;
import it.zerono.mods.zerocore.lib.multiblock.storage.IPartStorage;
import it.zerono.mods.zerocore.lib.multiblock.storage.PartStorage;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.multiblock.validation.ValidationError;
import it.zerono.mods.zerocore.lib.network.INetworkTileEntitySyncProvider;
import it.zerono.mods.zerocore.lib.network.NetworkTileEntitySyncProvider;
import it.zerono.mods.zerocore.lib.world.ChunkCache;
import it.zerono.mods.zerocore.lib.world.NeighboringPositions;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * This class contains the base logic for "multiblock controllers". Conceptually, they are
 * meta-TileEntities. They govern the logic for an associated group of TileEntities.
 * 
 * Subordinate TileEntities implement the IMultiblockPart class and, generally, should not have an update() loop.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractMultiblockController<Controller extends AbstractMultiblockController<Controller>>
        implements IMultiblockController<Controller>, IMultiblockValidator, INestedSyncableEntity, INetworkTileEntitySyncProvider {

    /**
     * Raised when the multiblock data was loaded from disk or from the network.
     * Subscribe when the multiblock is Assembled.
     * Subscribers are automatically removed when the multiblock is Disassembled or Paused.
     */
    public final IEvent<Runnable> DataUpdated;

	//region IMultiblockController

    /**
     * Sync the controller state from the save-delegate data
     *
     * @param data the data
     */
    @Override
    public void syncFromSaveDelegate(final CompoundNBT data, final SyncReason syncReason) {

        this.syncDataFrom(data, syncReason);
        this.requestDataUpdateNotification();
    }

    /**
     * @return True if this controller has no associated blocks, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return this._connectedParts.isEmpty();
    }

    /**
     * @return The number of blocks connected to this controller.
     */
    @Override
    public int getPartsCount() {
        return this._connectedParts.size();
    }

    /**
     * Check if a multiblock part is being tracked by this machine.
     * @param part The part to check.
     * @return True if the multiblock part is being tracked by this machine, false otherwise.
     */
    @Override
    public boolean containsPart(final IMultiblockPart<Controller> part) {
        return this.isPartCompatible(part) && this._connectedParts.contains(part);
    }

    /**
     * Check if this controller contains at least one valid part at one of the given coordinates.
     *
     * @param positions the coordinates to check
     * @return True if at least one part exists at one of the given coordinates
     */
    @Override
    public boolean containsPartsAt(final NeighboringPositions positions) {
        return this._connectedParts.contains(positions);
    }

    @Override
    public boolean containsPartsAt(final BlockPos[] positions) {
        return this._connectedParts.contains(positions);
    }

    /**
     * Attach a new part to this machine.
     * @param part The part to add.
     */
    @Override
    public void attachPart(final IMultiblockPart<Controller> part) {

        final Controller mySelf = this.castSelf();

        this._connectedParts.addOrReplace(part);
        part.onAttached(mySelf);
        this.onPartAdded(part);

        if (part.hasMultiblockSaveData()) {

            part.forMultiblockSaveData(data -> {

                this.syncFromSaveDelegate(data, SyncReason.FullSync);
                part.onMultiblockDataAssimilated();
            });
        }

        this.getReferenceTracker().accept(part);
        this._boundingBox = this._boundingBox.add(part.getWorldPosition());
        this.getRegistry().addDirtyController(mySelf);

        this.callOnLogicalClient(CodeHelper::clearErrorReport);
    }

    /**
     * Call to detach a block from this machine. Generally, this should be called
     * when the tile entity is being released, e.g. on block destruction.
     * @param part The part to detach from this machine.
     * @param chunkUnloading Is this entity detaching due to the chunk unloading? If true, the multiblock will be paused instead of broken.
     */
    @Override
    public void detachPart(final IMultiblockPart<Controller> part, final boolean chunkUnloading) {

        final Controller mySelf = this.castSelf();

        if (chunkUnloading && this._assemblyState.isAssembled()) {

            this._assemblyState.setPaused();
            this.clearDataUpdatedSubscribers();
            this.onMachinePaused();
        }

        // Strip out this part

        this.onDetachPart(part);
        this._connectedParts.remove(part);

        if (this._connectedParts.isEmpty()) {

            this._boundingBox = CuboidBoundingBox.EMPTY;

            // Destroy/unregister
            this.getRegistry().addDeadController(mySelf);
            return;
        }

        this._needBuildingBoxRebuild = true;

        if (null == this._detachedParts) {
            this._detachedParts = this.createPartStorage();
        }

        this._detachedParts.addOrReplace(part);

        this.getRegistry().addDirtyController(mySelf);

        this.callOnLogicalClient(CodeHelper::clearErrorReport);
    }

    /**
     * Detach all parts. Return a collection of all parts which still
     * have a valid tile entity. Chunk-safe.
     *
     * @return A collection of all parts which still have a valid tile entity.
     */
    @Override
    public IPartStorage<Controller> detachAll() {

        final IPartStorage<Controller> detachedParts = this._connectedParts;

        this._connectedParts.forEach(this::onDetachPart, part -> this.getWorld().hasChunkAt(part.getWorldPosition()));
        this._connectedParts = this.createPartStorage();
        this._boundingBox = CuboidBoundingBox.EMPTY;
        return detachedParts;
    }

    /**
     * Assimilate another controller into this controller.
     * Acquire all of the other controller's blocks and attach them
     * to this one.
     *
     * @param other The controller to merge into this one.
     */
    @Override
    public void assimilateController(final Controller other) {

        if (!this.isControllerCompatible(other)) {
            return;
        }

        // should I be the one consuming the other controller?

        if (this.compareTo(other) >= 0) {
            throw new IllegalArgumentException("The controller with the lowest minimum-coord value must consume the one with the higher coords");
        }

        final IPartStorage<Controller> otherParts = other._connectedParts;
        final int otherPartsCount = otherParts.size();

        if (1 == otherPartsCount) {

            final IMultiblockPart<Controller> acquiredPart = Objects.requireNonNull(otherParts.getFirst());

            other.prepareAssimilation(this);
            this._connectedParts.addOrReplace(acquiredPart);
            acquiredPart.onAssimilated(this.castSelf());
            this.onPartAdded(acquiredPart);

        } else {

            // save a reference to them and then releases all blocks and references gently so they can be incorporated
            // into another multiblock (prepareAssimilation() will invalidate _connectedParts)

            final Controller mySelf = this.castSelf();
            final boolean export = this._connectedParts.size() < otherPartsCount;

            other.prepareAssimilation(this);
            otherParts.forEachValidPart(acquiredPart -> {

                acquiredPart.onAssimilated(mySelf);
                this.onPartAdded(acquiredPart);
            });

            if (export) {

                otherParts.addAll(this._connectedParts);
                this._connectedParts = otherParts;

            } else {

                this._connectedParts.addAll(otherParts);
            }
        }

        this.onAssimilate(other);
        other.onAssimilated(this);
    }

    /**
     * Tests whether this multiblock should consume the other multiblock
     * and become the new multiblock master when the two multiblocks
     * are adjacent. Assumes both multiblocks are the same type.
     * @param other The other multiblock controller.
     * @return True if this multiblock should consume the other, false otherwise.
     */
    @Override
    public boolean shouldConsumeController(final Controller other) {

        if (!this.isControllerCompatible(other)) {
            throw new IllegalArgumentException("Attempting to merge two multiblocks with different master classes - this should never happen!");
        }

        if (this == other) {
            // Don't be silly, don't eat yourself.
            return false;
        }

        int res = this.compareTo(other);

        if (res < 0) {

            return true;

        } else if (res > 0) {

            return false;

        } else {

            // Strip dead parts from both and retry
            Log.LOGGER.warn(Log.MULTIBLOCK, "[{}] Encountered two controllers with the same reference coordinate. Auditing connected parts and retrying.",
                    CodeHelper.getWorldSideName(this.getWorld()));

            final ChunkCache chunkCache = ChunkCache.getOrCreate(this.getWorld());
            this.auditParts(chunkCache);
            other.auditParts(chunkCache);
            chunkCache.clear();

            // check again...

            res = this.compareTo(other);

            if (res < 0) {

                return true;

            } else if(res > 0) {

                return false;

            } else {

                //noinspection AutoBoxing
                Log.LOGGER.error(Log.MULTIBLOCK, "My Controller ({}): size ({})", this.hashCode(), this.getPartsCount());
                //noinspection AutoBoxing
                Log.LOGGER.error(Log.MULTIBLOCK, "Other Controller ({}): size ({})", other.hashCode(), other.getPartsCount());
                throw new IllegalArgumentException("[" + CodeHelper.getWorldSideName(this.getWorld()) + "] Two controllers with the same reference coord that somehow both have valid parts - this should never happen!");
            }
        }
    }

    /**
     * Called when this machine may need to check for blocks that are no
     * longer physically connected to the reference coordinate.
     */
    @Override
    public IPartStorage<Controller> checkForDisconnections() {

        if (!this._shouldCheckForDisconnections || null == this._detachedParts || this._detachedParts.isEmpty()) {
            return EmptyPartStorage.getInstance();
        }

        // Invalidate our reference coordinate, we'll recalculate it shortly

        final ReferencePartTracker<Controller> reference = this.getReferenceTracker();

        reference.invalidate();

        // Reset visitations and find the reference coordinate

        this._connectedParts.forEach(part -> {

            part.setUnvisited();
            reference.accept(part);
        });

        final Controller mySelf = this.castSelf();

        if (reference.isInvalid() || this.isEmpty()) {

            // There are no valid parts remaining. The entire multiblock was unloaded during a chunk unload. Halt.

            this._shouldCheckForDisconnections = false;
            this.getRegistry().addDeadController(mySelf);
            return EmptyPartStorage.getInstance();
        }

        // Release the detached parts

        this._detachedParts = null;

        // Now visit all connected parts

        this.visitAllLoadedParts();

        // Finally, remove all parts that remain disconnected.

        final IPartStorage<Controller> removedParts = this.createPartStorage();

        final List<IMultiblockPart<Controller>> deadParts = new ObjectArrayList<>(1024);

        this._connectedParts.forEachNotVisitedPart(orphanCandidate -> {

            deadParts.add(orphanCandidate);
            orphanCandidate.onOrphaned(mySelf, /*originalSize*/0, /*visitedParts*/0);
            this.onDetachPart(orphanCandidate);
            removedParts.addOrReplace(orphanCandidate);
        });

        // Trim any blocks that were invalid, or were removed.

        if (!deadParts.isEmpty()) {

            this._connectedParts.removeAll(deadParts);
            this._needBuildingBoxRebuild = true;
        }

        // We've run the checks from here on out.
        this._shouldCheckForDisconnections = false;

        return removedParts;
    }

    /**
     * Request to be notified when the multiblock data was loaded from disk or from the network.
     * Subscribe when the multiblock is Assembled.
     * Subscribers are automatically removed when the multiblock is Disassembled or Paused.
     *
     * @param handler a {@link Runnable} that's called when the multiblock data is loaded
     */
    @Override
    public Runnable listenForDataUpdate(Runnable handler) {

        this.DataUpdated.subscribe(handler);
        return handler;
    }

    /**
     * Stop listening for multiblock data updates
     *
     * @param handler the value returned by listenForDataUpdate()
     */
    @Override
    public void unlistenForDataUpdate(Runnable handler) {
        this.DataUpdated.unsubscribe(handler);
    }

    /**
     * Check if the machine is whole or not.
     * If the machine was not whole, but now is, assemble the machine.
     * If the machine was whole, but no longer is, disassemble the machine.
     */
    @Override
    public void checkIfMachineIsWhole() {

        this._lastValidationError = null;

        if (this.isMachineWhole(this)) {

            // This will alter assembly state
            this.assembleMachine(this._assemblyState.isPaused());

        } else if (this._assemblyState.isAssembled()) {

            // This will alter assembly state
            this.disassembleMachine();
        }
        // Else Paused, do nothing

        this._detachedParts = null;

        this.callOnLogicalClient(CodeHelper::clearErrorReport);
    }
    /**
     * @return True if this multiblock machine is considered assembled and ready to go.
     */
    @Override
    public boolean isAssembled() {
        return this._assemblyState.isAssembled();
    }

    /**
     * @return True if this multiblock machine is disassembled.
     */
    @Override
    public boolean isDisassembled() {
        return this._assemblyState.isDisassembled();
    }

    /**
     * @return True if this multiblock machine is paused.
     */
    @Override
    public boolean isPaused() {
        return this._assemblyState.isPaused();
    }

    /**
     * Driver for the update loop. If the machine is assembled, runs
     * the game logic update method.
     */
    @SuppressWarnings("deprecation")
    @Override
    public final void updateMultiblockEntity() {

        if (this.isEmpty()) {

            // This shouldn't happen, but just in case...
            this.getRegistry().addDeadController(this.castSelf());
            return;
        }

        if (!this.isAssembled()) {
            // Not assembled - don't run game logic
            return;
        }

        if (this.calledByLogicalClient()) {

            this.updateClient();

            if (this._requestDataUpdateNotification) {
                this.raiseDataUpdated();
            }

        } else if (this.updateServer()) {

            this.raiseDataUpdated();

            // If this returns true, the server has changed its internal data.
            // If our chunks are loaded (they should be), we must mark our chunks as dirty.

            final World myWorld = this.getWorld();
            final BlockPos min = this._boundingBox.getMin();
            final BlockPos max = this._boundingBox.getMax();

            if (myWorld.hasChunksAt(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ())) {

                final int minChunkX = WorldHelper.getChunkXFromBlock(min);
                final int minChunkZ = WorldHelper.getChunkZFromBlock(min);
                final int maxChunkX = WorldHelper.getChunkXFromBlock(max);
                final int maxChunkZ = WorldHelper.getChunkZFromBlock(max);

                for (int x = minChunkX; x <= maxChunkX; ++x) {
                    for(int z = minChunkZ; z <= maxChunkZ; ++z) {
                        // Ensure that we save our data, even if our save delegate has no TEs.
                        myWorld.getChunk(x, z).markUnsaved();
                    }
                }
            }
        }
        // Else: Server, but no need to save data.
    }

    /**
     * @return The reference coordinate, the block with the lowest x, y, z coordinates, evaluated in that order.
     */
    @Override
    public Optional<BlockPos> getReferenceCoord() {
        return this.getReferenceTracker().getPosition();
    }

    /**
     * Get the World associated to this controller
     *
     * @return the world
     */
    @Override
    public World getWorld() {
        return this._world;
    }

    /**
     * Force this multiblock to recalculate its minimum and maximum coordinates
     * from the list of connected parts.
     */
    @Override
    public void recalculateCoords() {

        if (this._needBuildingBoxRebuild) {

            this._boundingBox = this.isEmpty() ? CuboidBoundingBox.EMPTY : this.buildBoundingBox();
            this._needBuildingBoxRebuild = false;
        }
    }

    /**
     * @return The minimum bounding-box coordinate containing this machine's blocks.
     */
    @Override
    @Deprecated // use getBoundingBox()
    public Optional<BlockPos> getMinimumCoord() {
        return Optional.of(this._boundingBox.getMin());
    }

    /**
     * @return The bounding-box encompassing this machine's blocks.
     */
    @Override
    public CuboidBoundingBox getBoundingBox() {
        return this._boundingBox;
    }

    @Override
    @Deprecated // use getBoundingBox()
    public <T> T mapBoundingBoxCoordinates(final BiFunction<BlockPos, BlockPos, T> minMaxCoordMapper, final T defaultValue) {
        return minMaxCoordMapper.apply(this._boundingBox.getMin(), this._boundingBox.getMax());
    }

    @Override
    @Deprecated // use getBoundingBox()
    public <T> T mapBoundingBoxCoordinates(final BiFunction<BlockPos, BlockPos, T> minMaxCoordMapper, final T defaultValue,
                                           final Function<BlockPos, BlockPos> minRemapper, final Function<BlockPos, BlockPos> maxRemapper) {
        return minMaxCoordMapper.apply(minRemapper.apply(this._boundingBox.getMin()), maxRemapper.apply(this._boundingBox.getMax()));
    }

    @Override
    @Deprecated // use getBoundingBox()
    public void forBoundingBoxCoordinates(final BiConsumer<BlockPos, BlockPos> minMaxCoordConsumer) {
        minMaxCoordConsumer.accept(this._boundingBox.getMin(), this._boundingBox.getMax());
    }

    @Override
    @Deprecated // use getBoundingBox()
    public void forBoundingBoxCoordinates(final BiConsumer<BlockPos, BlockPos> minMaxCoordConsumer,
                                          final Function<BlockPos, BlockPos> minRemapper, final Function<BlockPos, BlockPos> maxRemapper) {
        minMaxCoordConsumer.accept(minRemapper.apply(this._boundingBox.getMin()), minRemapper.apply(this._boundingBox.getMax()));
    }

    @Override
    public void forceStructureUpdate(World world) {
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param other the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(final Controller other) {

        final int sizeCmp = Integer.compare(other.getPartsCount(), this.getPartsCount());

        return 0 != sizeCmp ? sizeCmp : this.getReferenceTracker().compareTo(other.getReferenceTracker());
    }

    //endregion
    //region IMultiblockValidator

    @Override
    public boolean hasLastError() {
        return null != this._lastValidationError;
    }

    @Override
    public boolean isLastErrorEmpty() {
        return null == this._lastValidationError;
    }

    /**
     * @return the last validation error encountered when trying to assemble the multiblock, or null if there is no error.
     */
    @Override
    public Optional<ValidationError> getLastError() {
        return Optional.ofNullable(this._lastValidationError);
    }

    /**
     * Set a validation error
     * @param error the error
     */
    @Override
    public void setLastError(final ValidationError error) {
        this._lastValidationError = error;
    }

    /**
     * Set a validation error
     * @param messageFormatStringResourceKey a translation key for a message or a message format string
     * @param messageParameters optional parameters for a message format string
     */
    @Override
    public void setLastError(final String messageFormatStringResourceKey, final Object... messageParameters) {
        this._lastValidationError = new ValidationError(null, messageFormatStringResourceKey, messageParameters);
    }

    /**
     * Set a validation error
     *
     * @param position                       the in-world position of the error
     * @param messageFormatStringResourceKey a translation key for a message or a message format string
     * @param messageParameters              optional parameters for a message format string
     */
    @Override
    public void setLastError(final BlockPos position, final String messageFormatStringResourceKey,
                             final Object... messageParameters) {
        this._lastValidationError = new ValidationError(position, messageFormatStringResourceKey, messageParameters);
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundNBT}
     *
     * @param data       the {@link CompoundNBT} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundNBT data, SyncReason syncReason) {
        this.requestDataUpdateNotification();
    }

    //endregion
    //region INestedSyncableEntity

    @Override
    public Optional<ISyncableEntity> getNestedSyncableEntity() {
        return Optional.of(this);
    }

    //endregion
    //region INetworkTileEntitySyncProvider

    /**
     * Add the player to the update queue.
     *
     * @param player    the player to send updates to.
     * @param updateNow if true, send an update to the player immediately.
     */
    @Override
    public void enlistForUpdates(ServerPlayerEntity player, boolean updateNow) {
        this._syncProvider.enlistForUpdates(player, updateNow && this.calledByLogicalServer());
    }

    /**
     * Remove the player for the update queue.
     *
     * @param player the player to be removed from the update queue.
     */
    @Override
    public void delistFromUpdates(ServerPlayerEntity player) {
        this._syncProvider.delistFromUpdates(player);
    }

    /**
     * Send an update to all enlisted players
     */
    @Override
    public void sendUpdates() {
        this.callOnLogicalServer(this._syncProvider::sendUpdates);
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        //noinspection AutoBoxing
        return String.format("%d parts", this._connectedParts.size());
    }

    //endregion
    //region AbstractMultiblockController

	protected AbstractMultiblockController(final World world) {

        this._assemblyState = new AssemblyState();
        this._connectedParts = this.createPartStorage();
        this._world = world;
        this._lastValidationError = null;
        this._reference = new ReferencePartTracker<>();
        this._boundingBox = CuboidBoundingBox.EMPTY;
        this._shouldCheckForDisconnections = false;
        this._syncProvider = NetworkTileEntitySyncProvider.create(
                () -> this.getReferenceCoord().orElseGet(() -> new BlockPos(0, 0, 0)), this);
        this._requestDataUpdateNotification = false;
        this._needBuildingBoxRebuild = false;

        this.DataUpdated = new Event<>();
	}

	/**
	 * Called when a new part is added to the machine. Good time to register things into lists.
	 * @param newPart The part being added.
	 */
	protected abstract void onPartAdded(IMultiblockPart<Controller> newPart);

	/**
	 * Called when a part is removed from the machine. Good time to clean up lists.
	 * @param oldPart The part being removed.
	 */
	protected abstract void onPartRemoved(IMultiblockPart<Controller> oldPart);
	
	/**
	 * Called when a machine is assembled from a disassembled state.
	 */
	protected void onMachineAssembled() {

        if (CodeHelper.isDevEnv()) {
            //noinspection AutoBoxing
            Log.LOGGER.info(Log.MULTIBLOCK, "Multiblock assembled at {}", System.nanoTime());
        }
    }
	
	/**
	 * Called when a machine is restored to the assembled state from a paused state.
	 */
	protected abstract void onMachineRestored();

	/**
	 * Called when a machine is paused from an assembled state
	 * This generally only happens due to chunk-loads and other "system" events.
	 */
	protected abstract void onMachinePaused();
	
	/**
	 * Called when a machine is disassembled from an assembled state.
	 * This happens due to user or in-game actions (e.g. explosions)
	 */
	protected abstract void onMachineDisassembled();

	/**
	 * Helper method so we don't check for a whole machine until we have enough parts
	 * to actually assemble it. This isn't as simple as xmax*ymax*zmax for non-cubic machines
	 * or for machines with hollow/complex interiors.
	 * @return The minimum number of parts connected to the machine for it to be assembled.
	 */
	protected abstract int getMinimumNumberOfPartsForAssembledMachine();

	/**
	 * Returns the maximum X dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
	 * dimension checking in X. (This is not recommended.)
	 * @return The maximum X dimension size of the machine, or -1 
	 */
	protected abstract int getMaximumXSize();

	/**
	 * Returns the maximum Z dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
	 * dimension checking in X. (This is not recommended.)
	 * @return The maximum Z dimension size of the machine, or -1 
	 */
	protected abstract int getMaximumZSize();

	/**
	 * Returns the maximum Y dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
	 * dimension checking in X. (This is not recommended.)
	 * @return The maximum Y dimension size of the machine, or -1 
	 */
	protected abstract int getMaximumYSize();
	
	/**
	 * Returns the minimum X dimension size of the machine. Must be at least 1, because nothing else makes sense.
	 * @return The minimum X dimension size of the machine
	 */
	protected int getMinimumXSize() {
	    return 1;
	}

	/**
	 * Returns the minimum Y dimension size of the machine. Must be at least 1, because nothing else makes sense.
	 * @return The minimum Y dimension size of the machine
	 */
	protected int getMinimumYSize() {
	    return 1;
	}

	/**
	 * Returns the minimum Z dimension size of the machine. Must be at least 1, because nothing else makes sense.
	 * @return The minimum Z dimension size of the machine
	 */
	protected int getMinimumZSize() {
	    return 1;
	}

	/**
	 * Checks if a machine is whole. If not, set a validation error using IMultiblockValidator.
	 */
	protected abstract boolean isMachineWhole(IMultiblockValidator validatorCallback);

	/**
	 * Callback. Called after this controller assimilates all the blocks
	 * from another controller.
	 * Use this to absorb that controller's game data.
	 * @param assimilated The controller whose uniqueness was added to our own.
	 */
	protected abstract void onAssimilate(IMultiblockController<Controller> assimilated);
	
	/**
	 * Callback. Called after this controller is assimilated into another controller.
	 * All blocks have been stripped out of this object and handed over to the
	 * other controller.
	 * This is intended primarily for cleanup.
	 * @param assimilator The controller which has assimilated this controller.
	 */
	protected abstract void onAssimilated(IMultiblockController<Controller> assimilator);

	/**
	 * The server-side update loop! Use this similarly to a TileEntity's update loop.
	 * You do not need to call your superclass' update() if you're directly
	 * derived from AbstractMultiblockController. This is a callback.
	 * Note that this will only be called when the machine is assembled.
	 * @return True if the multiblock should save data, i.e. its internal game state has changed. False otherwise.
	 */
	protected abstract boolean updateServer();
	
	/**
	 * Client-side update loop. Generally, this shouldn't do anything, but if you want
	 * to do some interpolation or something, do it here.
	 */
	protected abstract void updateClient();

	/**
	 * The "frame" consists of the outer edges of the machine, plus the corners.
	 * 
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 */
	protected abstract boolean isBlockGoodForFrame(World world, int x, int y, int z, IMultiblockValidator validatorCallback);

	/**
	 * The top consists of the top face, minus the edges.
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 */
	protected abstract boolean isBlockGoodForTop(World world, int x, int y, int z, IMultiblockValidator validatorCallback);
	
	/**
	 * The bottom consists of the bottom face, minus the edges.
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 */
	protected abstract boolean isBlockGoodForBottom(World world, int x, int y, int z, IMultiblockValidator validatorCallback);
	
	/**
	 * The sides consists of the N/E/S/W-facing faces, minus the edges.
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 */
	protected abstract boolean isBlockGoodForSides(World world, int x, int y, int z, IMultiblockValidator validatorCallback);
	
	/**
	 * The interior is any block that does not touch blocks outside the machine.
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 */
	protected abstract boolean isBlockGoodForInterior(World world, int x, int y, int z, IMultiblockValidator validatorCallback);

    /**
     * Marks the reference coord dirty & updateable.
     *
     * On the server, this will mark the for a data-update, so that
     * nearby clients will receive an updated description packet from the server
     * after a short time. The block's chunk will also be marked dirty and the
     * block's chunk will be saved to disk the next time chunks are saved.
     *
     * On the client, this will mark the block for a rendering update.
     */
    protected void markReferenceCoordForUpdate() {
        this.getReferenceCoord().ifPresent(pos -> WorldHelper.notifyBlockUpdate(this.getWorld(), pos));
    }

    /**
     * Marks the reference coord dirty.
     *
     * On the server, this marks the reference coord's chunk as dirty; the block (and chunk)
     * will be saved to disk the next time chunks are saved. This does NOT mark it dirty for
     * a description-packet update.
     *
     * On the client, does nothing.
     */
    protected void markReferenceCoordDirty() {

        this.callOnLogicalServer(() -> this.getReferenceTracker().consume((part, position) -> {

            this.getWorld().blockEntityChanged(position, (TileEntity)part);
            WorldHelper.notifyBlockUpdate(this.getWorld(), position);
        }));
    }

    protected CuboidBoundingBox buildBoundingBox() {
        return this._connectedParts.boundingBox();
    }

    /*
	/ **
     * Called on the server to fill in the structure data for a validation request about to be sent to the client
	 * @param data the buffer to write the data to
	 * /
    public void fillClientValidationRequest(final ByteBuf data) {

        data.writeInt(this._minimumCoord.getX());
        data.writeInt(this._minimumCoord.getY());
        data.writeInt(this._minimumCoord.getZ());

        data.writeInt(this._maximumCoord.getX());
        data.writeInt(this._maximumCoord.getY());
        data.writeInt(this._maximumCoord.getZ());
    }

	/ **
     * Called on the client to process a structure validation request from the server
	 * @param serverReferenceCoord the reference coordinates on the server
	 * @param data the structure data
	 * @return return true if the client structure match the server one. false otherwise
	 * /
    public boolean processClientValidationRequest(final BlockPos serverReferenceCoord, final ByteBuf data) {

        if (!this._referenceCoord.equals(serverReferenceCoord))
            return false;

        final BlockPos serverMinCoord = new BlockPos(data.readInt(), data.readInt(), data.readInt());
        final BlockPos serverMaxCoord = new BlockPos(data.readInt(), data.readInt(), data.readInt());

        return this._minimumCoord.equals(serverMinCoord) && this._maximumCoord.equals(serverMaxCoord);
    }

	/ **
     * Called on the client to fill in the structure data for a validation response about to be sent back to the server
	 * @param data the buffer to write the data to
	 * /
    public void fillClientValidationResponse(final ByteBuf data) {

        // client reference coordinates
        data.writeInt(this._referenceCoord.getX());
        data.writeInt(this._referenceCoord.getY());
        data.writeInt(this._referenceCoord.getZ());

        // client _minimumCoord
        data.writeInt(this._minimumCoord.getX());
        data.writeInt(this._minimumCoord.getY());
        data.writeInt(this._minimumCoord.getZ());

        // client _maximumCoord
        data.writeInt(this._maximumCoord.getX());
        data.writeInt(this._maximumCoord.getY());
        data.writeInt(this._maximumCoord.getZ());
    }

	/ **
     * Called on the server to process a structure validation response from the client. Override in the multiblock shape
	 * @param data the structure data
	 * /
    public void processClientValidationResponse(final ByteBuf data) {
    }
    */

    @SuppressWarnings("unchecked")
    protected Controller castSelf() {
        return (Controller)this;
    }

    protected IPartStorage<Controller> createPartStorage() {
        return new PartStorage<>();
    }

    /**
     * @return an unmodifiable Set containing all the parts connected to this controller
     */
    protected Collection<IMultiblockPart<Controller>> getConnectedParts() {
        return this._connectedParts.unmodifiable();
    }

    protected Stream<IMultiblockPart<Controller>> getConnectedParts(final Predicate<IMultiblockPart<Controller>> test) {
        return this.getConnectedParts().stream()
                .filter(test);
    }

    protected void forEachConnectedParts(final Consumer<IMultiblockPart<Controller>> action) {
        this._connectedParts.forEach(action);
    }

    /**
     * @return The number of blocks connected to this controller that match the given Predicate
     */
    protected int getPartsCount(final Predicate<IMultiblockPart<Controller>> test) {
        return (int)this._connectedParts.stream()
                .filter(test)
                .count();
    }

    /**
     * @return True if there is at least one connected part that match the given Predicate
     */
    protected boolean isAnyPartConnected(final Predicate<IMultiblockPart<Controller>> test) {
        return this._connectedParts.stream().anyMatch(test);
    }

    protected NeighboringPositions getNeighboringPositionsToVisit() {
        return new NeighboringPositions();
    }

    protected void visitAllLoadedParts() {

        if (this._connectedParts.size() < 32 * 32 * 64) {

            this.visitLoadedNeighboringParts(Objects.requireNonNull(this.getReferenceTracker().get()));
            return;
        }

        final IMultiblockPart<Controller> firstPart = Objects.requireNonNull(this.getReferenceTracker().get());
        final NeighboringPositions positions = this.getNeighboringPositionsToVisit();
        final List<IMultiblockPart<Controller>> nearbyParts = new ReferenceArrayList<>(positions.size());

        firstPart.setVisited();
        positions.setTo(firstPart.getWorldPosition());
        this._connectedParts.get(positions, nearbyParts);
        nearbyParts.parallelStream().forEach(this::visitLoadedNeighboringParts);
    }

    /**
     * Visit all loaded neighboring parts starting from the provided part
     *
     * @param firstPart the starting part
     */
    protected void visitLoadedNeighboringParts(final IMultiblockPart<Controller> firstPart) {

        final LinkedList<IMultiblockPart<Controller>> partsToCheck = Lists.newLinkedList();
        final NeighboringPositions positions = this.getNeighboringPositionsToVisit();
        final List<IMultiblockPart<Controller>> nearbyParts = new ReferenceArrayList<>(positions.size());

        partsToCheck.add(firstPart);

        do {

            final IMultiblockPart<Controller> part = partsToCheck.removeFirst();

            part.setVisited();
            positions.setTo(part.getWorldPosition());

            this._connectedParts.get(positions, nearbyParts);

            for (final IMultiblockPart<Controller> nearbyPart : nearbyParts) {

                if (nearbyPart.isNotVisited()) {

                    nearbyPart.setVisited();
                    partsToCheck.add(nearbyPart);
                }
            }

            nearbyParts.clear();

        } while (!partsToCheck.isEmpty());
    }

    /**
     * @return the ReferencePartTracker of this Controller
     */
    protected ReferencePartTracker<Controller> getReferenceTracker() {

        if (this._reference.isInvalid()) {
            this._reference.accept(this._connectedParts);
        }

        return this._reference;
    }

    //region Logical sides and deferred execution helpers

    /**
     * Test if we were called by the Server thread or by another thread in a server environment
     */
    public boolean calledByLogicalServer() {
        return !this._world.isClientSide;
    }

    /**
     * Test if we were called by the Client thread or by another thread in a client-only or combined environment
     */
    public boolean calledByLogicalClient() {
        return this._world.isClientSide;
    }

    public void callOnLogicalSide(final Runnable serverCode, final Runnable clientCode) {
        CodeHelper.callOnLogicalSide(this._world, serverCode, clientCode);
    }

    public <T> T callOnLogicalSide(final Supplier<T> serverCode, final Supplier<T> clientCode) {
        return CodeHelper.callOnLogicalSide(this._world, serverCode, clientCode);
    }

    public boolean callOnLogicalSide(final BooleanSupplier serverCode, final BooleanSupplier clientCode) {
        return CodeHelper.callOnLogicalSide(this._world, serverCode, clientCode);
    }

    public int callOnLogicalSide(final IntSupplier serverCode, final IntSupplier clientCode) {
        return CodeHelper.callOnLogicalSide(this._world, serverCode, clientCode);
    }

    public long callOnLogicalSide(final LongSupplier serverCode, final LongSupplier clientCode) {
        return CodeHelper.callOnLogicalSide(this._world, serverCode, clientCode);
    }

    public double callOnLogicalSide(final DoubleSupplier serverCode, final DoubleSupplier clientCode) {
        return CodeHelper.callOnLogicalSide(this._world, serverCode, clientCode);
    }

    public void callOnLogicalServer(final Runnable code) {
        CodeHelper.callOnLogicalServer(this._world, code);
    }

    public <T> T callOnLogicalServer(final Supplier<T> code, final Supplier<T> invalidSideReturnValue) {
        return CodeHelper.callOnLogicalServer(this._world, code, invalidSideReturnValue);
    }

    public boolean callOnLogicalServer(final BooleanSupplier code) {
        return CodeHelper.callOnLogicalServer(this._world, code);
    }

    public int callOnLogicalServer(final IntSupplier code, final int invalidSideReturnValue) {
        return CodeHelper.callOnLogicalServer(this._world, code, invalidSideReturnValue);
    }

    public long callOnLogicalServer(final LongSupplier code, final long invalidSideReturnValue) {
        return CodeHelper.callOnLogicalServer(this._world, code, invalidSideReturnValue);
    }

    public double callOnLogicalServer(final DoubleSupplier code, final double invalidSideReturnValue) {
        return CodeHelper.callOnLogicalServer(this._world, code, invalidSideReturnValue);
    }

    public void callOnLogicalClient(final Runnable code) {
        CodeHelper.callOnLogicalClient(this._world, code);
    }

    public <T> T callOnLogicalClient(final Supplier<T> code, final Supplier<T> invalidSideReturnValue) {
        return CodeHelper.callOnLogicalClient(this._world, code, invalidSideReturnValue);
    }

    public boolean callOnLogicalClient(final BooleanSupplier code) {
        return CodeHelper.callOnLogicalClient(this._world, code);
    }

    public int callOnLogicalClient(final IntSupplier code, final int invalidSideReturnValue) {
        return CodeHelper.callOnLogicalClient(this._world, code, invalidSideReturnValue);
    }

    public long callOnLogicalClient(final LongSupplier code, final long invalidSideReturnValue) {
        return CodeHelper.callOnLogicalClient(this._world, code, invalidSideReturnValue);
    }

    public double callOnLogicalClient(final DoubleSupplier code, final double invalidSideReturnValue) {
        return CodeHelper.callOnLogicalClient(this._world, code, invalidSideReturnValue);
    }

    //endregion
    //endregion
    //region internals

    /**
     * Called when a machine becomes "whole" and should begin
     * functioning as a game-logically finished machine.
     * Calls onMachineAssembled on all attached parts.
     */
    private void assembleMachine(final boolean currentlyPaused) {

        final Controller mySelf = this.castSelf();

        this._connectedParts.forEach(part -> part.onPreMachineAssembled(mySelf));

        this._assemblyState.setAssembled();
        this.clearDataUpdatedSubscribers();

        if (currentlyPaused) {
            this.onMachineRestored();
        } else {
            this.onMachineAssembled();
        }

        this._connectedParts.forEach(part -> part.onPostMachineAssembled(mySelf));
//        this.onUpdateBlockState();
    }

    /**
     * Called when the machine needs to be disassembled.
     * It is not longer "whole" and should not be functional, usually
     * as a result of a block being removed.
     * Calls onMachineBroken on all attached parts.
     */
    private void disassembleMachine() {

        this._connectedParts.forEach(IMultiblockPart::onPreMachineBroken);

        this._assemblyState.setDisassembled();
        this.clearDataUpdatedSubscribers();
        this.onMachineDisassembled();

        this._connectedParts.forEach(IMultiblockPart::onPostMachineBroken);
//        this.onUpdateBlockState();
    }

    /**
     * Callback whenever a part is removed (or will very shortly be removed) from a controller.
     * Do housekeeping/callbacks, also nulls min/max coords.
     * @param part The part being removed.
     */
    private void onDetachPart(final IMultiblockPart<Controller> part) {

        // Strip out this part
        part.onDetached(this.castSelf());
        this.onPartRemoved(part);
        part.forfeitMultiblockSaveDelegate();

        this._boundingBox = CuboidBoundingBox.EMPTY;

        // access the reference tracker directly to avoid updating it every time a part is detached
        if (this._reference.test(part)) {
            this._reference.invalidate();
        }

        this._shouldCheckForDisconnections = true;
    }

    /**
     * Called when this machine is consumed by another controller.
     * Essentially, forcibly tear down this object.
     * @param otherController The controller consuming this controller.
     */
    @SuppressWarnings({"unused"})
    protected void prepareAssimilation(IMultiblockController<Controller> otherController) {

        final ReferencePartTracker<Controller> reference = this.getReferenceTracker();

        reference.forfeitSaveDelegate();
        reference.invalidate();

        // abandon the current set of connected parts - avoid the need to copy it in assimilateController()
        this._connectedParts = this.createPartStorage();
    }

    /**
     * Checks all of the parts in the controller. If any are dead or do not exist in the world, they are removed.
     */
    protected void auditParts(final ChunkCache chunkCache) {

        final Set<IMultiblockPart<Controller>> deadParts = new ReferenceOpenHashSet<>(Math.min(64, this._connectedParts.size()));

        for (final IMultiblockPart<Controller> part : this._connectedParts) {

            if (part.isPartInvalid() || part != WorldHelper.getLoadedTile(chunkCache, part.getWorldPosition())) {

                this.onDetachPart(part);
                deadParts.add(part);
            }
        }

        this._connectedParts.removeAll(deadParts);

        //noinspection AutoBoxing
        Log.LOGGER.warn(Log.MULTIBLOCK, "[{}] Controller found {} dead parts during an audit, {} parts remain attached",
                CodeHelper.getWorldSideName(this.getWorld()), deadParts.size(), this.getPartsCount());
    }

    /*
     * Marks the whole multiblock for a render update on the client. On the server, this does nothing
     */
	protected void markMultiblockForRenderUpdate() {
	    this.forBoundingBoxCoordinates(WorldHelper::markBlockRangeForRenderUpdate);
	}

    /*
	protected void requestClientValidation() {
		this._clientValidationRequested = true;
	}

	protected boolean isClientValidationRequested() {
		return this._clientValidationRequested;
	}

	void beginClientValidation(final ServerPlayerEntity player) {

		this._clientValidationRequested = false;
		PacketHandler.INSTANCE.sendTo(new ClientMultiblockValidationRequest(this), player);
	}
	*/

    @SuppressWarnings("unchecked")
    private IMultiblockRegistry<Controller> getRegistry() {
        return (IMultiblockRegistry<Controller>) MultiblockRegistry.INSTANCE;
    }

    private void requestDataUpdateNotification() {
        this._requestDataUpdateNotification = true;
    }

    private void raiseDataUpdated() {

        this.DataUpdated.raise(Runnable::run);
        this._requestDataUpdateNotification = false;
    }

    private void clearDataUpdatedSubscribers() {
        this.DataUpdated.unsubscribeAll();
    }

    /**
     * The parts tracked by this controller
     */
    protected IPartStorage<Controller> _connectedParts;

    /**
     * The parts that were detached from this controller
     */
    protected IPartStorage<Controller> _detachedParts;

    /**
     * Machine state
     */
    private final AssemblyState _assemblyState;

    /**
     * The World associated to this controller
     */
    private final World _world;

    /** This is a deterministically-picked coordinate that identifies this
     * multiblock uniquely in its dimension.
     * Currently, this is the coord with the lowest X, Y and Z coordinates, in that order of evaluation.
     * i.e. If something has a lower X but higher Y/Z coordinates, it will still be the reference.
     * If something has the same X but a lower Y coordinate, it will be the reference. Etc.
     */
    private final ReferencePartTracker<Controller> _reference;

    private CuboidBoundingBox _boundingBox;

    /**
     * Set to true whenever a part is removed from this controller.
     */
    protected boolean _shouldCheckForDisconnections;

    /**
     * Set whenever we validate the multiblock
     */
    private ValidationError _lastValidationError;

    private final INetworkTileEntitySyncProvider _syncProvider;
    private boolean _requestDataUpdateNotification;
    private boolean _needBuildingBoxRebuild;

    //endregion
}
