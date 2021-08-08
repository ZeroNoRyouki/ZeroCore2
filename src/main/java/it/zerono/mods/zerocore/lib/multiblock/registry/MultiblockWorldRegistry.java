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

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.storage.IPartStorage;
import it.zerono.mods.zerocore.lib.multiblock.storage.PartStorage;
import it.zerono.mods.zerocore.lib.world.NeighboringPositions;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

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

    MultiblockWorldRegistry(final Level world) {

        this._world = world;
        this._controllers = new ReferenceOpenHashSet<>(64);
        this._deadControllers = new ReferenceOpenHashSet<>(64);
        this._dirtyControllers = new ReferenceOpenHashSet<>(64);
        this._orphanedParts = this.createPartStorage();
        this._detachedParts = this.createPartStorage();
        this._neighborsIterator = new NeighboringPositions();
        this._multiblockChangesDelay = CodeHelper.tickCountdown(20);

        if (CodeHelper.isDevEnv()) {
            //noinspection AutoBoxing
            Log.LOGGER.info(Log.MULTIBLOCK, "MultiblockWorldRegistry created at {}", System.nanoTime());
        }
    }

    /**
     * Called before Tile Entities are ticked in the world. Run game logic.
     */
    void tickStart() {

        final ProfilerFiller profiler = this._world.getProfiler();

        profiler.push("Zero CORE|Multiblock|World|Tick");

        for (final Controller controller : this._controllers) {

            if (controller.isEmpty()) {

                // This happens on the server when the user breaks the last block. It's fine.
                // Mark 'er dead and move on.
                this._deadControllers.add(controller);

            } else {

                // Run the game logic for this world
                controller.updateMultiblockEntity();
            }
        }

        profiler.pop();
    }

    /**
     * Called prior to processing multiblock controllers. Do bookkeeping.
     */
    void processMultiblockChanges() {

        final boolean process = this._multiblockChangesDelay.getAsBoolean();

        if ((this._orphanedParts.isEmpty() && this._dirtyControllers.isEmpty() &&
                this._deadControllers.isEmpty() && this._detachedParts.isEmpty()) ||
                !process) {
            return;
        }

        final ProfilerFiller profiler = this._world.getProfiler();

        // Merge pools - sets of adjacent machines which should be merged later on in processing

        profiler.push("Zero CORE|Multiblock|World|Merge");

        if (!this._orphanedParts.isEmpty()) {

            final IPartStorage<Controller> orphansToProcess = this._orphanedParts;
            List<Set<Controller>> mergePools = null;

            this._orphanedParts = this.createPartStorage();

            // Process orphaned blocks
            // These are blocks that exist in a valid chunk and require a controller
            orphans:
            for (final IMultiblockPart<Controller> orphan : orphansToProcess) {

                // This can occur on slow machines.
                if (orphan.isPartInvalid()) {
                    continue;
                }

                // THIS IS THE ONLY PLACE WHERE PARTS ATTACH TO CONTROLLERS
                // Try to attach to a neighbor's master controller

                final Set<Controller> compatibleControllers = orphan.attachToNeighbors(this::findControllersFor);
                final int compatibleControllersSize = compatibleControllers.size();

                switch (compatibleControllersSize) {

                    case 1:

                        // only 1 controller found in the neighborhood, and the part had already attached itself to it in attachToNeighbors()
                        break;

                    case 0:

                        // FOREVER ALONE! Create and register a new controller.
                        // THIS IS THE ONLY PLACE WHERE NEW CONTROLLERS ARE CREATED.

                        final Controller newController = orphan.createController();

                        newController.attachPart(orphan);
                        this._controllers.add(newController);
                        break;

                    default:

                        // THIS IS THE ONLY PLACE WHERE MERGES ARE DETECTED
                        // Multiple compatible controllers indicates an impending merge.
                        // Locate the appropriate merge pool(s)

                        if (null == mergePools) {

                            // No pools nearby, create a new merge pool
                            mergePools = new ReferenceArrayList<>(16);
                            mergePools.add(compatibleControllers);

                        } else {

                            if (1 == mergePools.size()) {

                                final Set<Controller> pool = mergePools.get(0);

                                for (final Controller controller : compatibleControllers) {

                                    if (pool.contains(controller)) {

                                        // At least one compatible controller is in this merge pool, so that means they
                                        // will all touch after the merge

                                        pool.addAll(compatibleControllers);
                                        continue orphans;
                                    }
                                }

                                // No pools nearby, create a new merge pool
                                mergePools.add(compatibleControllers);

                            } else {

                                final List<Set<Controller>> candidatePools = new ReferenceArrayList<>(16);

                                for (final Set<Controller> pool : mergePools) {

                                    for (final Controller controller : compatibleControllers) {

                                        if (pool.contains(controller)) {

                                            // At least one compatible controller is in this merge pool, so that means they
                                            // will all touch after the merge
                                            candidatePools.add(pool);
                                            break;
                                        }
                                    }
                                }

                                if (candidatePools.isEmpty()) {

                                    // No pools nearby, create a new merge pool
                                    mergePools.add(compatibleControllers);

                                } else if (1 == candidatePools.size()) {

                                    // Only one pool nearby, simply add to that one
                                    candidatePools.get(0).addAll(compatibleControllers);

                                } else {

                                    // Multiple pools - merge into one, then add the compatible controllers

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

            // Orphan parts processed. Process merge pools...

            if (null != mergePools && !mergePools.isEmpty()) {

                // Process merges - any machines that have been marked for merge should be merged into the "master" machine.
                // To do this, we combine lists of machines that are touching one another and therefore should Golion the fuck up.

                for (final Set<Controller> mergePool : mergePools) {

                    // Search for the new master machine, which will take over all the blocks contained in the other machines
                    Controller newMaster = null;

                    for (final Controller controller : mergePool) {

                        if (null == newMaster || controller.shouldConsumeController(newMaster)) {
                            newMaster = controller;
                        }
                    }

                    if (null == newMaster) {

                        //noinspection AutoBoxing
                        Log.LOGGER.error(Log.MULTIBLOCK, "[Multiblock Registry] Checked a merge pool of size {}, found no master candidates. This should never happen.", mergePool.size());

                    } else {

                        // Merge all the other machines into the master machine, then unregister them

                        this._dirtyControllers.add(newMaster);

                        for (final Controller controller : mergePool) {

                            if (controller != newMaster) {

                                newMaster.assimilateController(controller);
                                this._deadControllers.add(controller);
                            }
                        }
                    }
                }
            }
        } // orphaned parts / merge processing complete

        profiler.popPush("Zero CORE|Multiblock|World|Split&Assembly");

        // Process splits and assembly
        // Any controllers which have had parts removed (by the player or by chunk-unloading) must be checked to see if
        // some parts are no longer physically connected to their master.

        if (!this._dirtyControllers.isEmpty()) {

            IPartStorage<Controller> newlyDetachedParts;

            for (final Controller controller : this._dirtyControllers) {

                // Tell the machine to check if any parts are disconnected.
                // It should return a set of parts which are no longer connected.
                // POSTCONDITION: The controller must have informed those parts that they are no longer connected to this machine.

                if (controller.isEmpty()) {

                    this._deadControllers.add(controller);
                    continue;
                }

                newlyDetachedParts = controller.checkForDisconnections();

                if (!controller.isEmpty()) {

                    controller.recalculateCoords();
                    controller.checkIfMachineIsWhole();

                } else {

                    this._deadControllers.add(controller);
                }

                if (!newlyDetachedParts.isEmpty()) {

                    // Controller has shed some parts - add them to the detached list for delayed processing
                    this._detachedParts.addAll(newlyDetachedParts);
                }
            }

            this._dirtyControllers.clear();
        }

        // Unregister dead controllers

        profiler.popPush("Zero CORE|Multiblock|World|DeadControllers");

        if (!this._deadControllers.isEmpty()) {

            for (final Controller controller : this._deadControllers) {

                // Go through any controllers which have marked themselves as potentially dead.
                // Validate that they are empty/dead, then unregister them.

                if (!controller.isEmpty()) {

                    Log.LOGGER.error(Log.MULTIBLOCK, "[Multiblock Registry] Found a non-empty controller. Forcing it to shed its blocks and die. This should never happen!");
                    this._detachedParts.addAll(controller.detachAll());
                }

                // THIS IS THE ONLY PLACE WHERE CONTROLLERS ARE UNREGISTERED.
                this._controllers.remove(controller);
            }

            this._deadControllers.clear();
        }

        // Process detached blocks

        profiler.popPush("Zero CORE|Multiblock|World|DetachedParts");

        // Any blocks which have been detached this tick should be moved to the orphaned
        // list, and will be checked next tick to see if their chunk is still loaded.

        if (!this._detachedParts.isEmpty()) {

            this._detachedParts.forEach(p -> {

                // Ensure parts know they're detached
                p.assertDetached();
                this._orphanedParts.addOrReplace(p);
            });

            this._detachedParts = this.createPartStorage();
        }

        profiler.pop();
    }

    /**
     * Called when a multiblock part is added to the world, either via chunk-load or user action.
     * If its chunk is loaded, it will be processed during the next tick.
     * If the chunk is not loaded, it will be added to a list of objects waiting for a chunkload.
     * @param part The part which is being added to this world.
     */
    void onPartAdded(final IMultiblockPart<Controller> part) {

        final ProfilerFiller profiler = this._world.getProfiler();

        profiler.push("Zero CORE|Multiblock|World|PartAdded");
        this._orphanedParts.addOrReplace(part);
        profiler.pop();
    }

    /**
     * Called when a part is removed from the world, via user action or via chunk unloads.
     * This part is removed from any lists in which it may be, and its machine is marked for recalculation.
     * @param part The part which is being removed.
     */
    void onPartRemovedFromWorld(final IMultiblockPart<Controller> part) {

        final ProfilerFiller profiler = this._world.getProfiler();

        profiler.push("Zero CORE|Multiblock|World|PartRemoved");

        this._detachedParts.remove(part);

        if (this._orphanedParts.contains(part)) {
            this._orphanedParts.remove(part);
        }

        part.assertDetached();

        profiler.pop();
    }

    /**
     * Called when the world which this World Registry represents is fully unloaded from the system.
     * Does some housekeeping just to be nice.
     */
    void onWorldUnloaded() {

        final ProfilerFiller profiler = this._world.getProfiler();

        profiler.push("Zero CORE|Multiblock|World|WorldUnloaded");

        this._controllers.clear();
        this._deadControllers.clear();
        this._dirtyControllers.clear();
        this._orphanedParts = null;
        this._detachedParts = null;
        this._world = null;

        profiler.pop();
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

    private Set<Controller> findControllersFor(final IMultiblockPart<Controller> orphan) {

        final Class<? extends IMultiblockController<Controller>> targetControllerType = orphan.getControllerType();
        final Set<Controller> controllers = new ReferenceArraySet<>(6);

        this._neighborsIterator.setTo(orphan.getWorldPosition());

        for (final Controller controller : this._controllers) {

            if (targetControllerType.equals(controller.getClass()) &&
                    controller.isPartCompatible(orphan) &&
                    controller.containsPartsAt(this._neighborsIterator)) {
                controllers.add(controller);
            }
        }

        return controllers;
    }

    private IPartStorage<Controller> createPartStorage() {
        return new PartStorage<>();
    }

	//region internals

    private Level _world;

    // Active controllers
    private final Set<Controller> _controllers;

    // Controllers whose parts lists have changed
    private final Set<Controller> _dirtyControllers;

    // Controllers which are empty
    private final Set<Controller> _deadControllers;

    // Orphan parts: parts which currently have no master, but should seek one this tick
    private IPartStorage<Controller> _orphanedParts;

    // Detached parts: parts which have been detached during internal operations
    private IPartStorage<Controller> _detachedParts;

    private final NeighboringPositions _neighborsIterator;

    private final BooleanSupplier _multiblockChangesDelay;

    //endregion
}
