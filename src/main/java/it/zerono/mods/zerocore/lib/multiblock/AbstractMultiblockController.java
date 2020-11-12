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
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.IBlockStateUpdater;
import it.zerono.mods.zerocore.lib.data.nbt.INestedSyncableEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.multiblock.registry.MultiblockRegistry;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.multiblock.validation.ValidationError;
import it.zerono.mods.zerocore.lib.network.INetworkTileEntitySyncProvider;
import it.zerono.mods.zerocore.lib.network.NetworkTileEntitySyncProvider;
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
    public void syncFromSaveDelegate(CompoundNBT data, SyncReason syncReason) {

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
    public boolean containsPart(IMultiblockPart<Controller> part) {
        return this.isPartCompatible(part) && this._connectedParts.contains(part);
    }

    /**
     * Attach a new part to this machine.
     * @param part The part to add.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void attachPart(IMultiblockPart<Controller> part) {

        final BlockPos coord = part.getWorldPosition();

        if (!this._connectedParts.add(part)) {
            Log.LOGGER.warn(Log.MULTIBLOCK, "[{}] Controller {} is double-adding part {} @ {}. This is unusual. If you encounter odd behavior, please tear down the machine and rebuild it.",
                    CodeHelper.getWorldSideName(this.getWorld()), hashCode(), part.hashCode(), coord);
        }

        part.onAttached(this.castSelf());
        this.onPartAdded(part);

        if (part.hasMultiblockSaveData()) {

            part.getMultiblockSaveData().ifPresent(data -> {

//                this.onAttachedPartWithMultiblockData(part, data);
                this.syncFromSaveDelegate(data, SyncReason.FullSync);
                part.onMultiblockDataAssimilated();
            });
        }

        if (null == this._referenceCoord) {

            this._referenceCoord = coord;
            part.becomeMultiblockSaveDelegate();

        } else if (coord.compareTo(this._referenceCoord) < 0) {

            this.getReferenceTile()
                    .filter(tile -> tile instanceof IMultiblockPart)
                    .map(tile -> (IMultiblockPart<Controller>)tile)
                    .ifPresent(IMultiblockPart::forfeitMultiblockSaveDelegate);

            this._referenceCoord = coord;
            part.becomeMultiblockSaveDelegate();

        } else {

            part.forfeitMultiblockSaveDelegate();
        }

        final BlockPos partPos = part.getWorldPosition();
        int curX, curY, curZ;
        int newX, newY, newZ;
        int partCoord;

        if (this._minimumCoord != null) {

            curX = this._minimumCoord.getX();
            curY = this._minimumCoord.getY();
            curZ = this._minimumCoord.getZ();

            partCoord = partPos.getX();
            newX = Math.min(partCoord, curX);

            partCoord = partPos.getY();
            newY = Math.min(partCoord, curY);

            partCoord = partPos.getZ();
            newZ = Math.min(partCoord, curZ);

            if ((newX != curX) || (newY != curY) || (newZ != curZ)) {
                this._minimumCoord = new BlockPos(newX, newY, newZ);
            }
        }

        if (this._maximumCoord != null) {

            curX = this._maximumCoord.getX();
            curY = this._maximumCoord.getY();
            curZ = this._maximumCoord.getZ();

            partCoord = partPos.getX();
            newX = Math.max(partCoord, curX);

            partCoord = partPos.getY();
            newY = Math.max(partCoord, curY);

            partCoord = partPos.getZ();
            newZ = Math.max(partCoord, curZ);

            if ((newX != curX) || (newY != curY) || (newZ != curZ)) {
                this._maximumCoord = new BlockPos(newX, newY, newZ);
            }
        }

        this.getRegistry().addDirtyController(this.castSelf());
    }

    /**
     * Call to detach a block from this machine. Generally, this should be called
     * when the tile entity is being released, e.g. on block destruction.
     * @param part The part to detach from this machine.
     * @param chunkUnloading Is this entity detaching due to the chunk unloading? If true, the multiblock will be paused instead of broken.
     */
    @Override
    public void detachPart(IMultiblockPart<Controller> part, boolean chunkUnloading) {

        if (chunkUnloading && this._assemblyState.isAssembled()) {

            this._assemblyState.setPaused();
            this.clearDataUpdatedSubscribers();
            this.onMachinePaused();
        }

        // Strip out this part

        this.onDetachPart(part);

        if (!this._connectedParts.remove(part)) {

            final BlockPos position = part.getWorldPosition();

            Log.LOGGER.warn(Log.MULTIBLOCK, "[{}] Double-removing part ({}) @ {}, {}, {}, this is unexpected and may cause problems. If you encounter anomalies, please tear down the reactor and rebuild it.",
                    CodeHelper.getWorldSideName(this.getWorld()), part.hashCode(), position.getX(), position.getY(), position.getZ());
        }

        if (this._connectedParts.isEmpty()) {

            // Destroy/unregister
            this.getRegistry().addDeadController(this.castSelf());
            return;
        }

        this.getRegistry().addDirtyController(this.castSelf());

        // Find new save delegate if we need to.

        if (null == this._referenceCoord) {
            this.selectNewReferenceCoord();
        }
    }

    /**
     * Detach all parts. Return a set of all parts which still
     * have a valid tile entity. Chunk-safe.
     * @return A set of all parts which still have a valid tile entity.
     */
    @SuppressWarnings("deprecation")
    @Override
    public Set<IMultiblockPart<Controller>> detachAllParts() {

        this._connectedParts.stream()
                .filter(part -> this.getWorld().isBlockLoaded(part.getWorldPosition()))
                .forEach(this::onDetachPart);

        final Set<IMultiblockPart<Controller>> detachedParts = this._connectedParts;

        this._connectedParts = new ObjectOpenHashSet<>();
        this._connectedPartsUnmodifiable = null;
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
    public void assimilateController(/*IMultiblockController<Controller>*/Controller other) {

        if (!this.isControllerCompatible(other)) {
            return;
        }

        // should I be the one consuming the other controller?
        if (this.shouldConsume(other) >= 0) {
            throw new IllegalArgumentException("The controller with the lowest minimum-coord value must consume the one with the higher coords");
        }

        final Set<IMultiblockPart<Controller>> partsToAcquire = new ObjectOpenHashSet<>(other.getConnectedParts());

        // releases all blocks and references gently so they can be incorporated into another multiblock
        other.prepareAssimilation(this);

        // By definition, none of these can be the minimum block.
        partsToAcquire.stream()
                .filter(acquiredPart -> !acquiredPart.isPartInvalid())
                .forEach(acquiredPart -> {
                    this._connectedParts.add(acquiredPart);
                    acquiredPart.onAssimilated(this.castSelf());
                    this.onPartAdded(acquiredPart);
                });

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
    public boolean shouldConsumeController(/*IMultiblockController<Controller>*/Controller other) {

        if (!this.isControllerCompatible(other)) {
            throw new IllegalArgumentException("Attempting to merge two multiblocks with different master classes - this should never happen!");
        }

        if (this == other) {
            // Don't be silly, don't eat yourself.
            return false;
        }

        int res = this.shouldConsume(other);

        if (res < 0) {

            return true;

        } else if (res > 0) {

            return false;

        } else {

            // Strip dead parts from both and retry
            Log.LOGGER.warn(Log.MULTIBLOCK, "[{}] Encountered two controllers with the same reference coordinate. Auditing connected parts and retrying.",
                    CodeHelper.getWorldSideName(this.getWorld()));

            this.auditParts();
            other.auditParts();

            // check again...

            res = this.shouldConsume(other);

            if (res < 0) {

                return true;

            } else if(res > 0) {

                return false;

            } else {

                Log.LOGGER.error(Log.MULTIBLOCK, "My Controller ({}): size ({}), parts: {}", hashCode(), this.getPartsCount(), this.getPartsListString());
                Log.LOGGER.error(Log.MULTIBLOCK, "Other Controller ({}): size ({}), coords: {}", other.hashCode(), other.getPartsCount(), other.getPartsListString());
                throw new IllegalArgumentException("[" + CodeHelper.getWorldSideName(this.getWorld()) + "] Two controllers with the same reference coord that somehow both have valid parts - this should never happen!");
            }
        }
    }

    /**
     * Called when this machine may need to check for blocks that are no
     * longer physically connected to the reference coordinate.
     */
    @SuppressWarnings("deprecation")
    @Override
    public Set<IMultiblockPart<Controller>> checkForDisconnections() {

        if (!this._shouldCheckForDisconnections) {
            return Collections.emptySet();
        }

        if (this.isEmpty()) {

            this.getRegistry().addDeadController(this.castSelf());
            return Collections.emptySet();
        }

        // Invalidate our reference coord, we'll recalculate it shortly
        this._referenceCoord = null;

        // Reset visitations and find the minimum coordinate

        final World myWorld = this.getWorld();
        final Set<IMultiblockPart<Controller>> deadParts = Sets.newHashSet();
        IMultiblockPart<Controller> referencePart = null;

        for (final IMultiblockPart<Controller> part : this._connectedParts) {

            final BlockPos position = part.getWorldPosition();

            // This happens during chunk unload.
            if (!myWorld.isBlockLoaded(position) || part.isPartInvalid()) {

                deadParts.add(part);
                this.onDetachPart(part);
                continue;
            }

            if (!WorldHelper.getTile(myWorld, position)
                    .filter(tile -> tile == part)
                    .isPresent()) {

                deadParts.add(part);
                this.onDetachPart(part);
                continue;
            }

            part.setUnvisited();
            part.forfeitMultiblockSaveDelegate();

            if (null == this._referenceCoord) {

                this._referenceCoord = position;
                referencePart = part;

            } else if (position.compareTo(this._referenceCoord) < 0) {

                this._referenceCoord = position;
                referencePart = part;
            }
        }

        final int originalSize = this._connectedParts.size();

        this._connectedParts.removeAll(deadParts);
        deadParts.clear();

        if (null == referencePart || this.isEmpty()) {

            // There are no valid parts remaining. The entire multiblock was unloaded during a chunk unload. Halt.

            this._shouldCheckForDisconnections = false;
            this.getRegistry().addDeadController(this.castSelf());
            return Collections.emptySet();

        } else {

            referencePart.becomeMultiblockSaveDelegate();
        }

        // Now visit all connected parts, breadth-first, starting from reference coord's part
        final LinkedList<IMultiblockPart<Controller>> partsToCheck = Lists.newLinkedList();
        List<IMultiblockPart<Controller>> nearbyParts;
        IMultiblockPart<Controller> part;
        int visitedParts = 0;

        partsToCheck.add(referencePart);

        while (!partsToCheck.isEmpty()) {

            part = partsToCheck.removeFirst();

            part.setVisited();
            ++visitedParts;

            nearbyParts = part.getNeighboringParts(); // Chunk-safe on server, but not on client

            for (final IMultiblockPart<Controller> nearbyPart : nearbyParts) {

                // Ignore different machines
                if (!nearbyPart.getMultiblockController().map(this::isControllerCompatible).orElse(false)) {
                    continue;
                }

                if (!nearbyPart.isVisited()) {

                    nearbyPart.setVisited();
                    partsToCheck.add(nearbyPart);
                }
            }
        }

        // Finally, remove all parts that remain disconnected.
        final Set<IMultiblockPart<Controller>> removedParts = Sets.newHashSet();

        for (final IMultiblockPart<Controller> orphanCandidate : this._connectedParts) {

            if (!orphanCandidate.isVisited()) {

                deadParts.add(orphanCandidate);
                orphanCandidate.onOrphaned(this.castSelf(), originalSize, visitedParts);
                this.onDetachPart(orphanCandidate);
                removedParts.add(orphanCandidate);
            }
        }

        // Trim any blocks that were invalid, or were removed.
        this._connectedParts.removeAll(deadParts);

        // Cleanup. Not necessary, really.
        deadParts.clear();

        // Juuuust in case.
        if (null == this._referenceCoord) {
            selectNewReferenceCoord();
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

            if (null != this._minimumCoord && null != this._maximumCoord &&
                    myWorld.isAreaLoaded(this._minimumCoord, this._maximumCoord)) {

                final int minChunkX = WorldHelper.getChunkXFromBlock(this._minimumCoord);
                final int minChunkZ = WorldHelper.getChunkZFromBlock(this._minimumCoord);
                final int maxChunkX = WorldHelper.getChunkXFromBlock(this._maximumCoord);
                final int maxChunkZ = WorldHelper.getChunkZFromBlock(this._maximumCoord);

                for (int x = minChunkX; x <= maxChunkX; ++x) {
                    for( int z = minChunkZ; z <= maxChunkZ; ++z) {
                        // Ensure that we save our data, even if our save delegate has no TEs.
                        myWorld.getChunk(x, z).markDirty();
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

        if (null == this._referenceCoord) {
            this.selectNewReferenceCoord();
        }

        return Optional.ofNullable(this._referenceCoord);
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

        if (this.isEmpty()) {

            // if the multiblock has no parts in it, there will be no minimum or maximum coordinates
            this._minimumCoord = this._maximumCoord = null;
            return;
        }

        int minX, minY, minZ, maxX, maxY, maxZ;

        minX = minY = minZ = Integer.MAX_VALUE;
        maxX = maxY = maxZ = Integer.MIN_VALUE;

        for (final IMultiblockPart<Controller> part : this._connectedParts) {

            final BlockPos partPos = part.getWorldPosition();
            int partCoord;

            partCoord = partPos.getX();
            if (partCoord < minX) minX = partCoord;
            if (partCoord > maxX) maxX = partCoord;

            partCoord = partPos.getY();
            if (partCoord < minY) minY = partCoord;
            if (partCoord > maxY) maxY = partCoord;

            partCoord = partPos.getZ();
            if (partCoord < minZ) minZ = partCoord;
            if (partCoord > maxZ) maxZ = partCoord;
        }

        this._minimumCoord = new BlockPos(minX, minY, minZ);
        this._maximumCoord = new BlockPos(maxX, maxY, maxZ);
    }

    /**
     * @return The minimum bounding-box coordinate containing this machine's blocks.
     */
    @Override
    public Optional<BlockPos> getMinimumCoord() {

        if (null == this._minimumCoord) {
            this.recalculateCoords();
        }

        return Optional.ofNullable(this._minimumCoord);
    }

    /**
     * @return The maximum bounding-box coordinate containing this machine's blocks.
     */
    @Override
    public Optional<BlockPos> getMaximumCoord() {

        if (null == this._maximumCoord) {
            this.recalculateCoords();
        }

        return Optional.ofNullable(this._maximumCoord);
    }

    @Override
    public <T> T mapBoundingBoxCoordinates(final BiFunction<BlockPos, BlockPos, T> minMaxCoordMapper, final T defaultValue) {
        return null != this._minimumCoord && null != this._maximumCoord ? minMaxCoordMapper.apply(this._minimumCoord, this._maximumCoord) : defaultValue;
    }

    @Override
    public <T> T mapBoundingBoxCoordinates(final BiFunction<BlockPos, BlockPos, T> minMaxCoordMapper, final T defaultValue,
                                           final Function<BlockPos, BlockPos> minRemapper, final Function<BlockPos, BlockPos> maxRemapper) {
        return null != this._minimumCoord && null != this._maximumCoord ?
                minMaxCoordMapper.apply(minRemapper.apply(this._minimumCoord), maxRemapper.apply(this._maximumCoord)) : defaultValue;
    }

    @Override
    public void forBoundingBoxCoordinates(final BiConsumer<BlockPos, BlockPos> minMaxCoordConsumer) {

        if (null != this._minimumCoord && null != this._maximumCoord) {
            minMaxCoordConsumer.accept(this._minimumCoord, this._maximumCoord);
        }
    }

    @Override
    public void forBoundingBoxCoordinates(final BiConsumer<BlockPos, BlockPos> minMaxCoordConsumer,
                                          final Function<BlockPos, BlockPos> minRemapper, final Function<BlockPos, BlockPos> maxRemapper) {

        if (null != this._minimumCoord && null != this._maximumCoord) {
            minMaxCoordConsumer.accept(minRemapper.apply(this._minimumCoord), maxRemapper.apply(this._maximumCoord));
        }
    }

    @Override
    public void forceStructureUpdate(World world) {
    }

    //endregion
    //region IMultiblockValidator

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
    public void setLastError(ValidationError error) {
        this._lastValidationError = error;
    }

    /**
     * Set a validation error
     * @param messageFormatStringResourceKey a translation key for a message or a message format string
     * @param messageParameters optional parameters for a message format string
     */
    @Override
    public void setLastError(String messageFormatStringResourceKey, Object... messageParameters) {
        this._lastValidationError = new ValidationError(messageFormatStringResourceKey, messageParameters);
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
//        this._syncProvider.enlistForUpdates(player, updateNow && CodeHelper.calledByLogicalServer(this.getWorld()));
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
    //region AbstractMultiblockController

	protected AbstractMultiblockController(final World world) {

        this._assemblyState = new AssemblyState();
        this._connectedParts = new ObjectOpenHashSet<>();
        this._connectedPartsUnmodifiable = null;
        this._world = world;
        this._lastValidationError = null;
        this._referenceCoord = this._minimumCoord = this._maximumCoord = null;
        this._shouldCheckForDisconnections = true;
        this._syncProvider = NetworkTileEntitySyncProvider.create(
                () -> this.getReferenceCoord().orElseGet(() -> new BlockPos(0, 0, 0)), this);
        this._requestDataUpdateNotification = false;

        this.DataUpdated = new Event<>();
	}

    /**
	 * Call when a block with cached save-delegate data is added to the multiblock.
	 * The part will be notified that the data has been used after this call completes.
	 * @param part The NBT tag containing this controller's data.
	 */
//    protected abstract void onAttachedPartWithMultiblockData(IMultiblockPart<Controller> part, CompoundNBT data);

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
        ((ObjectOpenHashSet<IMultiblockPart<Controller>>)this._connectedParts).trim();
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
//
//        if (CodeHelper.calledByLogicalServer(this.getWorld())) {
//            CodeHelper.optionalIfPresent(this.getReferenceCoord(), this.getReferenceTile(),
//                    (coord, tile) -> this.getWorld().markChunkDirty(coord, tile));
//        }
//
        this.callOnLogicalServer(() ->
                CodeHelper.optionalIfPresent(this.getReferenceCoord(), this.getReferenceTile(),
                    (coord, tile) -> this.getWorld().markChunkDirty(coord, tile)));
    }

    /***
     * Get a TileEntity at _referenceCoord from the world associated to this controller
     * @return the TileEntity, or an empty Optional
     */
    protected Optional<TileEntity> getReferenceTile() {
        return this.getReferenceCoord().flatMap(position -> WorldHelper.getTile(this.getWorld(), position));
    }

    //TODO reevaluate
    protected void onUpdateBlockState() {

        final World myWorld = this.getWorld();

        this._connectedParts.stream()
                .filter(part -> part instanceof IBlockStateUpdater && part instanceof TileEntity)
                .forEach(part -> ((IBlockStateUpdater) part).updateBlockState(myWorld.getBlockState(part.getWorldPosition()),
                    myWorld, part.getWorldPosition(), (TileEntity) part, 1 | 2));
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

    /**
     * @return an unmodifiable Set containing all the parts connected to this controller
     */
    protected Set<IMultiblockPart<Controller>> getConnectedParts() {

        if (null == this._connectedPartsUnmodifiable) {
            this._connectedPartsUnmodifiable = Collections.unmodifiableSet(this._connectedParts);
        }

        return this._connectedPartsUnmodifiable;
    }

    protected Stream<IMultiblockPart<Controller>> getConnectedParts(final Predicate<IMultiblockPart<Controller>> test) {
        return this.getConnectedParts().stream()
                .filter(test);
    }

    /**
     * @return The number of blocks connected to this controller that match the given Predicate
     */
    protected int getPartsCount(final Predicate<IMultiblockPart<Controller>> test) {
        return (int)this.getConnectedParts(test).count();
    }

    /**
     * @return True if there is at least one connected part that match the given Predicate
     */
    protected boolean isAnyPartConnected(final Predicate<IMultiblockPart<Controller>> test) {
        return this.getConnectedParts().stream()
                .anyMatch(test);
    }


    //region Logical sides and deferred execution helpers

    /**
     * Test if we were called by the Server thread or by another thread in a server environment
     */
    public boolean calledByLogicalServer() {
        return !this._world.isRemote;
    }

    /**
     * Test if we were called by the Client thread or by another thread in a client-only or combined environment
     */
    public boolean calledByLogicalClient() {
        return this._world.isRemote;
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

    private int shouldConsume(IMultiblockController<Controller> other) {
        // Always consume other controllers if their reference coordinate is null - this means they're empty and can be assimilated on the cheap
        return other.getReferenceCoord()
                .map(theirCoord -> this.getReferenceCoord().map(myCoord -> myCoord.compareTo((theirCoord)))
                        .orElse(1))
                .orElse(-1);
    }

    /**
     * Called when a machine becomes "whole" and should begin
     * functioning as a game-logically finished machine.
     * Calls onMachineAssembled on all attached parts.
     */
    private void assembleMachine(final boolean currentlyPaused) {

        this._connectedParts.forEach(part -> part.onPreMachineAssembled(this.castSelf()));

        this._assemblyState.setAssembled();
        this.clearDataUpdatedSubscribers();

        if (currentlyPaused) {
            this.onMachineRestored();
        } else {
            this.onMachineAssembled();
        }

        this._connectedParts.forEach(part -> part.onPostMachineAssembled(this.castSelf()));
        this.onUpdateBlockState();
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
        this.onUpdateBlockState();
    }

    @SuppressWarnings("deprecation")
    private void selectNewReferenceCoord() {

        final World myWorld = this.getWorld();
        IMultiblockPart<Controller> theChosenOne = null;

        this._referenceCoord = null;

        for (final IMultiblockPart<Controller> part : this._connectedParts) {

            final BlockPos position = part.getWorldPosition();

            if (part.isPartInvalid() || !myWorld.isBlockLoaded(position)) {
                // Chunk is unloading, skip this coord to prevent chunk thrashing
                continue;
            }

            if (null == this._referenceCoord || this._referenceCoord.compareTo(position) > 0) {

                this._referenceCoord = position;
                theChosenOne = part;
            }
        }

        if (null != theChosenOne) {
            theChosenOne.becomeMultiblockSaveDelegate();
        }
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

        this._minimumCoord = this._maximumCoord = null;

        if (null != this._referenceCoord && this._referenceCoord.equals(part.getWorldPosition())) {
            this._referenceCoord = null;
        }

        this._shouldCheckForDisconnections = true;
    }

    /**
     * Called when this machine is consumed by another controller.
     * Essentially, forcibly tear down this object.
     * @param otherController The controller consuming this controller.
     */
    @SuppressWarnings({"unused", "unchecked"})
    protected void prepareAssimilation(IMultiblockController<Controller> otherController) {

        if (null != this._referenceCoord) {

            this.getReferenceTile()
                    .filter(tile -> tile instanceof IMultiblockPart)
                    .map(tile -> (IMultiblockPart<Controller>)tile)
                    .ifPresent(IMultiblockPart::forfeitMultiblockSaveDelegate);

            this._referenceCoord = null;
        }

        this._connectedParts.clear();
    }

    /**
     * Checks all of the parts in the controller. If any are dead or do not exist in the world, they are removed.
     */
    protected void auditParts() {

        final Set<IMultiblockPart<Controller>> deadParts = Sets.newHashSet();

        this._connectedParts.stream()
                .filter(part -> part.isPartInvalid() ||
                        !WorldHelper.getTile(this.getWorld(), part.getWorldPosition())
                                .filter(tile -> tile == part)
                                .isPresent())
                .forEach(part -> {
                    this.onDetachPart(part);
                    deadParts.add(part);
                });

        this._connectedParts.removeAll(deadParts);
        Log.LOGGER.warn(Log.MULTIBLOCK, "[{}] Controller found {} dead parts during an audit, {} parts remain attached",
                CodeHelper.getWorldSideName(this.getWorld()), deadParts.size(), this.getPartsCount());
    }

    protected String getPartsListString() {

        final StringBuilder sb = new StringBuilder();
        boolean notFirst = false;

        for (final IMultiblockPart<Controller> part : this._connectedParts) {

            if(notFirst) {
                sb.append(", ");
            }

            final BlockPos partPos = part.getWorldPosition();

            sb.append(String.format("(%d: %d, %d, %d)", part.hashCode(), partPos.getX(), partPos.getY(), partPos.getZ()));
            notFirst = true;
        }

        return sb.toString();
    }

    /*
     * Marks the whole multiblock for a render update on the client. On the server, this does nothing
     */
	protected void markMultiblockForRenderUpdate() {
        CodeHelper.optionalIfPresent(this.getMinimumCoord(), this.getMaximumCoord(), WorldHelper::markBlockRangeForRenderUpdate);
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
        ((Event<Runnable>)this.DataUpdated).unsubscribeAll();
    }

    /**
     * The parts tracked by this controller
     */
    private Set<IMultiblockPart<Controller>> _connectedParts;
    private Set<IMultiblockPart<Controller>> _connectedPartsUnmodifiable;

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
    private BlockPos _referenceCoord;

    /**
     * Minimum bounding box coordinate. Blocks do not necessarily exist at this coord if your machine
     * is not a cube/rectangular prism.
     */
    private BlockPos _minimumCoord;

    /**
     * Maximum bounding box coordinate. Blocks do not necessarily exist at this coord if your machine
     * is not a cube/rectangular prism.
     */
    private BlockPos _maximumCoord;

    /**
     * Set to true whenever a part is removed from this controller.
     */
    private boolean _shouldCheckForDisconnections;

    /**
     * Set whenever we validate the multiblock
     */
    private ValidationError _lastValidationError;

    private final INetworkTileEntitySyncProvider _syncProvider;
    private boolean _requestDataUpdateNotification;

    //private boolean _clientValidationRequested;

    //endregion
}
