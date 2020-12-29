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

import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public final class MultiblockRegistry<Controller extends IMultiblockController<Controller>>
        implements IMultiblockRegistry<Controller> {

    @SuppressWarnings("rawtypes")
    public static final IMultiblockRegistry INSTANCE = new MultiblockRegistry();

    //region IMultiblockRegistry

    /**
     * Register a new part in the system. The part has been created either through user action or via a chunk loading.
     *
     * @param part The part being loaded.
     */
    @Override
    public void onPartAdded(final IMultiblockPart<Controller> part) {
        CodeHelper.optionalIfPresentOrElse(part.getPartWorld(),
                world -> this.getRegistryOrDefault(world).onPartAdded(part),
                () -> Log.LOGGER.info(Log.MULTIBLOCK, "[Multiblock Registry] Found a part with no world: not adding it!"));
    }

    /**
     * Call to remove a part from world lists.
     *
     * @param part The part being removed.
     */
    @Override
    public void onPartRemovedFromWorld(final IMultiblockPart<Controller> part) {
        CodeHelper.optionalIfPresentOrElse(part.getPartWorld(),
                world -> CodeHelper.optionalIfPresentOrElse(this.getRegistry(world),
                        registry -> registry.onPartRemovedFromWorld(part),
                        () -> Log.LOGGER.error(Log.MULTIBLOCK, "[Multiblock Registry] Trying to remove a part from a world ({}) that is not tracked! Skipping.",
                                world.getDimensionKey())),
                () -> Log.LOGGER.error(Log.MULTIBLOCK, "[Multiblock Registry] Trying to remove a part with no world: Skipping!"));
    }

    /**
     * Call to mark a controller as dead. It should only be marked as dead
     * when it has no connected parts. It will be removed after the next world tick.
     * @param controller The dead controller
     */
    @Override
    public void addDeadController(final Controller controller) {

        final World world = controller.getWorld();

        CodeHelper.optionalIfPresentOrElse(this.getRegistry(world),
                registry -> registry.addDeadController(controller),
                () -> Log.LOGGER.error(Log.MULTIBLOCK, "[Multiblock Registry] Controller {} in world ({}) marked as dead, but that world is not tracked! Controller is being ignored.",
                        controller.hashCode(), world.getDimensionKey()));
    }

    /**
     * Call to mark a controller as dirty. Dirty means that parts have
     * been added or removed this tick.
     * @param controller The dirty controller
     */
    @Override
    public void addDirtyController(final Controller controller) {

        final World world = controller.getWorld();

        this.forRegistry(world,
                registry -> registry.addDirtyController(controller),
                () -> Log.LOGGER.error(Log.MULTIBLOCK, "Adding a dirty controller to a world ({}) that has no registered controllers!",
                        world.getDimensionKey()));
    }

    //endregion
    //region internals

    private MultiblockRegistry() {

        this._registries = Maps.newHashMapWithExpectedSize(2);
        MinecraftForge.EVENT_BUS.addListener(this::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldUnload);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldTick);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(this::onClientTick));
    }

    /**
     * Called before Tile Entities are ticked in the world. Do bookkeeping here.
     * @param world The world being ticked
     */
    private void tickStart(final World world) {

        world.getProfiler().startSection("Zero CORE|Multiblock|Tick");

        this.forRegistry(world, registry -> {

            registry.processMultiblockChanges();
            registry.tickStart();
        });

        world.getProfiler().endSection();
    }

    /**
     * Called when the world has finished loading a chunk.
     * @param world The world which has finished loading a chunk
     * @param chunkX The X coordinate of the chunk
     * @param chunkZ The Z coordinate of the chunk
     */
    private void onChunkLoaded(final IWorld world, final int chunkX, final int chunkZ) {
        this.forRegistry(world, registry -> registry.onChunkLoaded(chunkX, chunkZ));
    }

    /**
     * Called whenever a world is unloaded. Unload the relevant registry, if we have one.
     * @param world The world being unloaded.
     */
    private void onWorldUnloaded(final IWorld world) {

        this.forRegistry(world, registry -> {

            registry.onWorldUnloaded();
            this.removeRegistry(world);
        });
    }

    private Optional<MultiblockWorldRegistry<Controller>> getRegistry(final IWorld world) {
        return Optional.ofNullable(this._registries.get(world));
    }

    private void forRegistry(final IWorld world, final Consumer<MultiblockWorldRegistry<Controller>> consumer) {

        final MultiblockWorldRegistry<Controller> registry = this._registries.get(world);

        if (null != registry) {
            consumer.accept(registry);
        }
    }

    private void forRegistry(final IWorld world, final Consumer<MultiblockWorldRegistry<Controller>> consumer,
                             final Runnable registryNotFound) {

        final MultiblockWorldRegistry<Controller> registry = this._registries.get(world);

        if (null != registry) {
            consumer.accept(registry);
        } else {
            registryNotFound.run();
        }
    }

    private MultiblockWorldRegistry<Controller> getRegistryOrDefault(final World world) {
        return this._registries.computeIfAbsent(world, w -> new MultiblockWorldRegistry<>(world));
    }

    private void removeRegistry(final IWorld world) {
        this._registries.remove(world);
    }

    //region event handlers

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onChunkLoad(final ChunkEvent.Load event) {

        final ChunkPos pos = event.getChunk().getPos();

        this.onChunkLoaded(event.getWorld(), pos.x, pos.z);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onWorldUnload(final WorldEvent.Unload event) {
        this.onWorldUnloaded(event.getWorld());
    }

    @SubscribeEvent
    public void onWorldTick(final TickEvent.WorldTickEvent event) {

        if (TickEvent.Phase.START == event.phase) {
            this.tickStart(event.world);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {

        final World world = Minecraft.getInstance().world;

        if (TickEvent.Phase.START == event.phase && null != world) {
            this.tickStart(world);
        }
    }

    //endregion

    private final Map<IWorld, MultiblockWorldRegistry<Controller>> _registries;

    //endregion
}
