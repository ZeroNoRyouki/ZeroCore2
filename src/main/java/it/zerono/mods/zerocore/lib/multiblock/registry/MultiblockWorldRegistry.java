/*
 *
 * MultiblockWorldRegistry.java
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

package it.zerono.mods.zerocore.lib.multiblock.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * This class manages all the multiblock controllers that exist in a given world,
 * either client- or server-side.
 * You must create different registries for server and client worlds.
 *
 * @author Erogenous Beef
 */
final class MultiblockWorldRegistry<Controller extends IMultiblockController<Controller>> {

    /**
     * Use this only if you know what you're doing. You should rarely need to iterate
     * over all controllers in a world!
     *
     * @return An (unmodifiable) set of controllers which are active in this world.
     */
    public Set<Controller> getControllers() {
        return Collections.unmodifiableSet(this._controllers);
    }

    MultiblockWorldRegistry(final World world) {

        this._world = world;
        this._controllers = Sets.newHashSet();
        this._deadControllers = Sets.newHashSet();
        this._dirtyControllers = Sets.newHashSet();
        this._detachedParts = Sets.newHashSet();
        this._orphanedParts = Sets.newHashSet();
        this._partsAwaitingChunkLoad = Maps.newHashMap();
        this._partsAwaitingChunkLoadMutex = new Object();
        this._orphanedPartsMutex = new Object();
    }

    /**
     * Called before Tile Entities are ticked in the world. Run game logic.
     */
    void tickStart() {

        this._world.getProfiler().startSection("Zero CORE|Multiblock|World|Tick");

        if (!this._controllers.isEmpty()) {

            this._controllers.stream()
                    .filter(controller -> this._world == controller.getWorld())
                    .forEach(controller -> {

                        if (controller.isEmpty()) {
                            // This happens on the server when the user breaks the last block. It's fine.
                            // Mark 'er dead and move on.
                            this._deadControllers.add(controller);
                        } else {
                            // Run the game logic for this world
                            controller.updateMultiblockEntity();
                        }
                    });
        }

        this._world.getProfiler().endSection();
    }

    /**
     * Called prior to processing multiblock controllers. Do bookkeeping.
     */
    @SuppressWarnings("deprecation")
    void processMultiblockChanges() {

        // Merge pools - sets of adjacent machines which should be merged later on in processing

        this._world.getProfiler().startSection("Zero CORE|Multiblock|World|Merge");

        BlockPos coord;
        List<Set<Controller>> mergePools = null;

        if (!this._orphanedParts.isEmpty()) {

            Set<IMultiblockPart<Controller>> orphansToProcess = null;

            // Keep the synchronized block small. We can't iterate over orphanedParts directly
            // because the client does not know which chunks are actually loaded, so attachToNeighbors()
            // is not chunk-safe on the client, because Minecraft is stupid.
            // It's possible to polyfill this, but the polyfill is too slow for comfort.

            synchronized (this._orphanedPartsMutex) {

                if (!this._orphanedParts.isEmpty()) {

                    orphansToProcess = this._orphanedParts;
                    this._orphanedParts = Sets.newHashSet();
                }
            }

            if (null != orphansToProcess) {

                Set<Controller> compatibleControllers;

                // Process orphaned blocks
                // These are blocks that exist in a valid chunk and require a controller
                for (final IMultiblockPart<Controller> orphan : orphansToProcess) {

                    coord = orphan.getWorldPosition();
                    if (!this._world.isBlockLoaded(coord)) {
                        continue;
                    }

                    // This can occur on slow machines.
                    if (orphan.isPartInvalid()) {
                        continue;
                    }

                    if (!WorldHelper.getMultiblockPartFrom(this._world, coord)
                        .filter(mbp -> mbp == orphan)
                        .isPresent()) {
                        // This block has been replaced by another.
                        continue;
                    }

                    // THIS IS THE ONLY PLACE WHERE PARTS ATTACH TO MACHINES
                    // Try to attach to a neighbor's master controller

                    compatibleControllers = orphan.attachToNeighbors();

                    if (compatibleControllers.isEmpty()) {

                        // FOREVER ALONE! Create and register a new controller.
                        // THIS IS THE ONLY PLACE WHERE NEW CONTROLLERS ARE CREATED.

                        final Controller newController = orphan.createController();

                        newController.attachPart(orphan);
                        this._controllers.add(newController);

                    } else if (compatibleControllers.size() > 1) {

                        if (null == mergePools) {
                            mergePools = Lists.newArrayList();
                        }

                        // THIS IS THE ONLY PLACE WHERE MERGES ARE DETECTED
                        // Multiple compatible controllers indicates an impending merge.
                        // Locate the appropriate merge pool(s)

                        final List<Set<Controller>> candidatePools = Lists.newArrayList();

                        for (final Set<Controller> candidatePool : mergePools) {

                            if (!Collections.disjoint(candidatePool, compatibleControllers)) {
                                // They share at least one element, so that means they will all touch after the merge
                                candidatePools.add(candidatePool);
                            }
                        }

                        if (candidatePools.isEmpty()) {

                            // No pools nearby, create a new merge pool
                            mergePools.add(compatibleControllers);

                        } else if (candidatePools.size() == 1) {

                            // Only one pool nearby, simply add to that one
                            candidatePools.get(0).addAll(compatibleControllers);

                        } else {

                            // Multiple pools- merge into one, then add the compatible controllers
                            final Set<Controller> masterPool = candidatePools.get(0);
                            Set<Controller> consumedPool;

                            for (int i = 1; i < candidatePools.size(); ++i) {

                                consumedPool = candidatePools.get(i);
                                masterPool.addAll(consumedPool);
                                mergePools.remove(consumedPool);
                            }

                            masterPool.addAll(compatibleControllers);
                        }
                    }
                }
            }
        }

        if (null != mergePools && !mergePools.isEmpty()) {

            // Process merges - any machines that have been marked for merge should be merged
            // into the "master" machine.
            // To do this, we combine lists of machines that are touching one another and therefore
            // should voltron the fuck up.

            for (final Set<Controller> mergePool : mergePools) {

                // Search for the new master machine, which will take over all the blocks contained in the other machines
                Controller newMaster = null;

                for (final Controller controller : mergePool) {
                    if (null == newMaster || controller.shouldConsumeController(newMaster)) {
                        newMaster = controller;
                    }
                }

                if (null == newMaster) {

                    Log.LOGGER.error(Log.MULTIBLOCK, "[Multiblock Registry] Checked a merge pool of size {}, found no master candidates. This should never happen.", mergePool.size());

                } else {

                    // Merge all the other machines into the master machine, then unregister them

                    this.addDirtyController(newMaster);

                    for (final Controller controller : mergePool) {

                        if (controller != newMaster) {

                            newMaster.assimilateController(controller);
                            this.addDeadController(controller);
                            this.addDirtyController(newMaster);
                        }
                    }
                }
            }
        }

        this._world.getProfiler().endStartSection("Zero CORE|Multiblock|World|Split&Assembly");

        // Process splits and assembly
        // Any controllers which have had parts removed must be checked to see if some parts are no longer
        // physically connected to their master.

        if (!this._dirtyControllers.isEmpty()) {

            Set<IMultiblockPart<Controller>> newlyDetachedParts;

            for (final Controller controller : this._dirtyControllers) {

                // Tell the machine to check if any parts are disconnected.
                // It should return a set of parts which are no longer connected.
                // POSTCONDITION: The controller must have informed those parts that
                // they are no longer connected to this machine.

                newlyDetachedParts = controller.checkForDisconnections();

                if (!controller.isEmpty()) {

                    controller.recalculateCoords();
                    controller.checkIfMachineIsWhole();

                } else {

                    this.addDeadController(controller);
                }

                if (!newlyDetachedParts.isEmpty()) {
                    // Controller has shed some parts - add them to the detached list for delayed processing
                    this._detachedParts.addAll(newlyDetachedParts);
                }
            }

            this._dirtyControllers.clear();
        }

        // Unregister dead controllers

        this._world.getProfiler().endStartSection("Zero CORE|Multiblock|World|DeadControllers");

        if (!this._deadControllers.isEmpty()) {

            for (final Controller controller : this._deadControllers) {

                // Go through any controllers which have marked themselves as potentially dead.
                // Validate that they are empty/dead, then unregister them.

                if (!controller.isEmpty()) {

                    Log.LOGGER.error(Log.MULTIBLOCK, "[Multiblock Registry] Found a non-empty controller. Forcing it to shed its blocks and die. This should never happen!");
                    this._detachedParts.addAll(controller.detachAllParts());
                }

                // THIS IS THE ONLY PLACE WHERE CONTROLLERS ARE UNREGISTERED.
                this._controllers.remove(controller);
            }

            this._deadControllers.clear();
        }

        // Process detached blocks

        this._world.getProfiler().endStartSection("Zero CORE|Multiblock|World|DetachedParts");

        // Any blocks which have been detached this tick should be moved to the orphaned
        // list, and will be checked next tick to see if their chunk is still loaded.

        // Ensure parts know they're detached
        this._detachedParts.forEach(IMultiblockPart::assertDetached);

        this.addAllOrphanedPartsThreadsafe(this._detachedParts);
        this._detachedParts.clear();

        this._world.getProfiler().endSection();
    }

    /**
     * Called when a multiblock part is added to the world, either via chunk-load or user action.
     * If its chunk is loaded, it will be processed during the next tick.
     * If the chunk is not loaded, it will be added to a list of objects waiting for a chunkload.
     * @param part The part which is being added to this world.
     */
    @SuppressWarnings("deprecation")
    void onPartAdded(final IMultiblockPart<Controller> part) {

        this._world.getProfiler().startSection("Zero CORE|Multiblock|World|PartAdded");

        final BlockPos worldLocation = part.getWorldPosition();

        if (!this._world.isBlockLoaded(worldLocation)) {

            // Part goes into the waiting-for-chunk-load list
            Set<IMultiblockPart<Controller>> partSet;
            final long chunkHash = WorldHelper.getChunkXZHashFromBlock(worldLocation);

            synchronized (this._partsAwaitingChunkLoadMutex) {

                if (!this._partsAwaitingChunkLoad.containsKey(chunkHash)) {

                    partSet = Sets.newHashSet();
                    this._partsAwaitingChunkLoad.put(chunkHash, partSet);

                } else {

                    partSet = this._partsAwaitingChunkLoad.get(chunkHash);
                }

                partSet.add(part);
            }

        } else {

            // Part goes into the orphan queue, to be checked this tick
            this.addOrphanedPartThreadsafe(part);
        }

        this._world.getProfiler().endSection();
    }

    /**
     * Called when a part is removed from the world, via user action or via chunk unloads.
     * This part is removed from any lists in which it may be, and its machine is marked for recalculation.
     * @param part The part which is being removed.
     */
    void onPartRemovedFromWorld(final IMultiblockPart<Controller> part) {

        this._world.getProfiler().startSection("Zero CORE|Multiblock|World|PartRemoved");

        final BlockPos coord = part.getWorldPosition();
        final long hash = WorldHelper.getChunkXZHashFromBlock(coord);

        if (this._partsAwaitingChunkLoad.containsKey(hash)) {

            synchronized (this._partsAwaitingChunkLoadMutex) {

                if (this._partsAwaitingChunkLoad.containsKey(hash)) {

                    this._partsAwaitingChunkLoad.get(hash).remove(part);

                    if (this._partsAwaitingChunkLoad.get(hash).size() <= 0) {
                        this._partsAwaitingChunkLoad.remove(hash);
                    }
                }
            }
        }

        this._detachedParts.remove(part);

        if (this._orphanedParts.contains(part)) {

            synchronized (this._orphanedPartsMutex) {
                this._orphanedParts.remove(part);
            }
        }

        part.assertDetached();

        this._world.getProfiler().endSection();
    }

    /**
     * Called when the world which this World Registry represents is fully unloaded from the system.
     * Does some housekeeping just to be nice.
     */
    void onWorldUnloaded() {

        this._world.getProfiler().startSection("Zero CORE|Multiblock|World|WorldUnloaded");

        this._controllers.clear();
        this._deadControllers.clear();
        this._dirtyControllers.clear();
        this._detachedParts.clear();

        synchronized (this._partsAwaitingChunkLoadMutex) {
            this._partsAwaitingChunkLoad.clear();
        }

        synchronized (this._orphanedPartsMutex) {
            this._orphanedParts.clear();
        }

        this._world.getProfiler().endSection();
        this._world = null;
    }

    /**
     * Called when a chunk has finished loading. Adds all of the parts which are awaiting
     * load to the list of parts which are orphans and therefore will be added to machines
     * after the next world tick.
     *
     * @param chunkX Chunk X coordinate (world coordate >> 4) of the chunk that was loaded
     * @param chunkZ Chunk Z coordinate (world coordate >> 4) of the chunk that was loaded
     */
    void onChunkLoaded(final int chunkX, final int chunkZ) {

        this._world.getProfiler().startSection("Zero CORE|Multiblock|World|ChunkUnloaded");

        final long chunkHash = ChunkPos.asLong(chunkX, chunkZ);

        if (this._partsAwaitingChunkLoad.containsKey(chunkHash)) {

            synchronized (this._partsAwaitingChunkLoadMutex) {

                if (this._partsAwaitingChunkLoad.containsKey(chunkHash)) {

                    this.addAllOrphanedPartsThreadsafe(this._partsAwaitingChunkLoad.get(chunkHash));
                    this._partsAwaitingChunkLoad.remove(chunkHash);
                }
            }
        }

        this._world.getProfiler().endSection();
    }

    /**
     * Registers a controller as dead. It will be cleaned up at the end of the next world tick.
     * Note that a controller must shed all of its blocks before being marked as dead, or the system
     * will complain at you.
     *
     * @param deadController The controller which is dead.
     */
    void addDeadController(Controller deadController) {
        this._deadControllers.add(deadController);
    }

    /**
     * Registers a controller as dirty - its list of attached blocks has changed, and it
     * must be re-checked for assembly and, possibly, for orphans.
     *
     * @param dirtyController The dirty controller.
     */
    void addDirtyController(Controller dirtyController) {
        this._dirtyControllers.add(dirtyController);
    }

	//region internals

    private void addOrphanedPartThreadsafe(final IMultiblockPart<Controller> part) {

        synchronized (this._orphanedPartsMutex) {
            this._orphanedParts.add(part);
        }
    }

    private void addAllOrphanedPartsThreadsafe(final Collection<? extends IMultiblockPart<Controller>> parts) {

        synchronized (this._orphanedPartsMutex) {
            this._orphanedParts.addAll(parts);
        }
    }

    private World _world;

    // Active controllers
    private final Set<Controller> _controllers;

    // Controllers whose parts lists have changed
    private final Set<Controller> _dirtyControllers;

    // Controllers which are empty
    private final Set<Controller> _deadControllers;

    // A list of orphan parts - parts which currently have no master, but should seek one this tick
    // Indexed by the hashed chunk coordinate
    // This can be added-to asynchronously via chunk loads!
    private Set<IMultiblockPart<Controller>> _orphanedParts;

    // A list of parts which have been detached during internal operations
    private final Set<IMultiblockPart<Controller>> _detachedParts;

    // A list of parts whose chunks have not yet finished loading
    // They will be added to the orphan list when they are finished loading.
    // Indexed by the hashed chunk coordinate
    // This can be added-to asynchronously via chunk loads!
    private final Map<Long, Set<IMultiblockPart<Controller>>> _partsAwaitingChunkLoad;

    // Mutexes to protect lists which may be changed due to asynchronous events, such as chunk loads
    private final Object _partsAwaitingChunkLoadMutex;
    private final Object _orphanedPartsMutex;
}
