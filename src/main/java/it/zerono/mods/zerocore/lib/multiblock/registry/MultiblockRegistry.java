/*
 *
 * MultiblockRegistry.java
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

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockRegistry;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

public class MultiblockRegistry<Controller extends IMultiblockController<Controller>>
        implements IMultiblockRegistry<Controller> {

    @SuppressWarnings("rawtypes")
    public static final IMultiblockRegistry INSTANCE = Lib.createMultiblockRegistry();

    //region IMultiblockRegistry

    /**
     * Register a new part in the system. The part has been created either through user action or via a chunk loading.
     *
     * @param part The part being loaded.
     */
    @Override
    public void onPartAdded(final IMultiblockPart<Controller> part) {
        this._registries.computeIfAbsent(part.getCurrentWorld(), MultiblockWorldRegistry::new).onPartAdded(part);
    }

    /**
     * Call to remove a part from world lists.
     *
     * @param part The part being removed.
     */
    @Override
    public void onPartRemovedFromWorld(final IMultiblockPart<Controller> part) {

        final MultiblockWorldRegistry<Controller> registry = this._registries.get(part.getCurrentWorld());

        if (null != registry) {
            registry.onPartRemovedFromWorld(part);
        } else {
            Log.LOGGER.error(Log.MULTIBLOCK, "Trying to remove a part from a world ({}) that is not tracked! Skipping.",
                    part.getCurrentWorld().dimension());
        }
    }

    /**
     * Call to mark a controller as dead. It should only be marked as dead
     * when it has no connected parts. It will be cleaned up at the end of the next world tick.
     * Note that a controller must shed all of its blocks before being marked as dead, or the system
     * will complain at you.
     *
     * @param controller The dead controller
     */
    @Override
    public void addDeadController(final Controller controller) {

        final MultiblockWorldRegistry<Controller> registry = this._registries.get(controller.getWorld());

        if (null != registry) {
            registry.addDeadController(controller);
        } else {
            //noinspection AutoBoxing
            Log.LOGGER.error(Log.MULTIBLOCK, "Controller {} in world ({}) marked as dead, but that world is not tracked! Controller is being ignored.",
                    controller.hashCode(), controller.getWorld().dimension());
        }
    }

    /**
     * Call to mark a controller as dirty. Dirty means that parts have
     * been added or removed this tick.
     * @param controller The dirty controller
     */
    @Override
    public void addDirtyController(final Controller controller) {

        final MultiblockWorldRegistry<Controller> registry = this._registries.get(controller.getWorld());

        if (null != registry) {
            registry.addDirtyController(controller);
        } else {
            Log.LOGGER.error(Log.MULTIBLOCK, "Adding a dirty controller to a world ({}) that has no registered controllers!",
                    controller.getWorld().dimension());
        }
    }

    //endregion
    //region internals

    public MultiblockRegistry() {

        this._registries = new Reference2ObjectArrayMap<>(2 * 8);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldUnload);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldTick);
    }

    /**
     * Called before Tile Entities are ticked in the world. Do bookkeeping here.
     * @param world The world being ticked
     */
    protected void tickStart(final Level world) {

        final ProfilerFiller profiler = world.getProfiler();

        profiler.push("Zero CORE|Multiblock|Tick");

        final MultiblockWorldRegistry<Controller> registry = this._registries.get(world);

        if (null != registry) {

            registry.processMultiblockChanges();
            registry.tickStart();
        }

        profiler.pop();
    }

    //region event handlers

    /**
     * Called whenever a world is unloaded. Unload the relevant registry, if we have one.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onWorldUnload(final WorldEvent.Unload event) {

        final LevelAccessor world = event.getWorld();

        if (world instanceof Level) {

            final MultiblockWorldRegistry<Controller> registry = this._registries.get(world);

            if (null != registry) {

                registry.onWorldUnloaded();
                this._registries.remove(world);
            }
        } else {

            Log.LOGGER.error(Log.MULTIBLOCK, "Trying to unload a world that's not a World!");
        }
    }

    @SubscribeEvent
    public void onWorldTick(final TickEvent.WorldTickEvent event) {

        if (TickEvent.Phase.START == event.phase) {
            this.tickStart(event.world);
        }
    }

    //endregion

    private final Map<Level, MultiblockWorldRegistry<Controller>> _registries;

    //endregion
}
