/*
 *
 * AbstractMultiblockPart.java
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

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.multiblock.registry.MultiblockRegistry;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Base logic class for Multiblock-connected tile entities. Most multiblock machines
 * should derive from this and implement their game logic in certain abstract methods.
 */
@SuppressWarnings({"WeakerAccess"})
public abstract class AbstractMultiblockPart<Controller extends IMultiblockController<Controller>>
        extends AbstractModBlockEntity
        implements IMultiblockPart<Controller>, IDebuggable {

    //region AbstractMultiblockPart

	public AbstractMultiblockPart(final TileEntityType<?> type) {

		super(type);
		this._controller = null;
        this._unvisited = true;
        this._saveMultiblockData = false;
        this._cachedMultiblockData = null;
        this._positionHash = this.worldPosition.asLong();
	}

	public World getPartWorldOrFail() {

	    final World world = this.getLevel();

	    if (null == world) {
	        throw new IllegalStateException("Found a multiblock part without a world!");
        }

	    return world;
    }

	//endregion
    //region IMultiblockPart

    @Override
    public boolean isConnected() {
        return null != this._controller;
    }

    @Override
    public boolean isMachineAssembled() {
        return this.isConnected() && this._controller.isAssembled();
    }

    @Override
    public boolean isMachineDisassembled() {
        return this.isConnected() && this._controller.isDisassembled();
    }

    @Override
    public boolean isMachinePaused() {
        return this.isConnected() && this._controller.isPaused();
    }

    @Override
    public Optional<Controller> getMultiblockController() {
        return Optional.ofNullable(this._controller);
    }

    /**
     * Execute the given Consumer on the controller, if this part is connected to one
     * @param code the consumer
     */
    @Override
    public void executeOnController(final Consumer<Controller> code) {

        if (null != this._controller) {
            code.accept(this._controller);
        }
    }

    /**
     * Execute the given Function on the controller returning it's result, if this part is connected to one
     * @param code the function
     * @param defaultValue the value to return if this part is not connected to a controller
     * @return the result of the function if this part is connected to a controller or defaultValue if it's not
     */
    @Override
    public <R> R evalOnController(final Function<Controller, R> code, final R defaultValue) {
        return null != this._controller ? code.apply(this._controller) : defaultValue;
    }

    @Override
    public boolean testOnController(Predicate<Controller> test) {
        return null != this._controller && test.test(this._controller);
    }

    @Override
    public Optional<World> getPartWorld() {
        return Optional.ofNullable(this.getLevel());
    }

    @Override
    public <T> T mapPartWorld(Function<World, T> mapper, T defaultValue) {

        final World world = this.getLevel();

        return null != world ? mapper.apply(world) : defaultValue;
    }

    @Override
    public void forPartWorld(Consumer<World> consumer) {

        final World world = this.getLevel();

        if (null != world) {
            consumer.accept(world);
        }
    }

    @Override
    public BlockPos getWorldPosition() {
        return this.getBlockPos();
    }

    @Override
    public long getWorldPositionHash() {
        return this._positionHash;
    }

    @Override
    public boolean isPartInvalid() {
        return this.isRemoved();
    }

    @Override
    public void onAttached(Controller newController) {
        this._controller = newController;
    }

    @Override
    public void onDetached(Controller oldController) {
        this._controller = null;
    }

    @Override
    public void onOrphaned(Controller controller, int oldSize, int newSize) {

        this.setChanged();
//        this.forPartWorld(w -> w.markChunkDirty(this.getWorldPosition(), this));
    }

    @Override
    public void onAssimilated(Controller newController) {

        assert(this._controller != newController);
        this._controller = newController;
    }

    @Override
    public void setVisited() {
        this._unvisited = false;
    }

    @Override
    public void setUnvisited() {
        this._unvisited = true;
    }

    @Override
    public boolean isVisited() {
        return !this._unvisited;
    }

    @Override
    public boolean isNotVisited() {
        return this._unvisited;
    }

    @Override
    public void becomeMultiblockSaveDelegate() {
        this._saveMultiblockData = true;
    }

    @Override
    public void forfeitMultiblockSaveDelegate() {
        this._saveMultiblockData = false;
    }

    @Override
    public boolean isMultiblockSaveDelegate() {
	    return this._saveMultiblockData;
	}

    @Override
    public List<IMultiblockPart<Controller>> getNeighboringParts() {

        final World world = this.getLevel();

        if (null == world) {
            return Collections.emptyList();
        }

        final List<IMultiblockPart<Controller>> parts = new ReferenceArrayList<>(6);
        final BlockPos[] positions = WorldHelper.getNeighboringPositionsList(this.getWorldPosition(), new BlockPos[6]);

        for (BlockPos position : positions) {

            final TileEntity te = WorldHelper.getLoadedTile(world, position);

            if (te instanceof IMultiblockPart) {
                //noinspection unchecked
                parts.add((IMultiblockPart<Controller>) te);
            }
        }

        return parts;
    }

    @Override
    public Set<Controller> attachToNeighbors(final Function<IMultiblockPart<Controller>, Set<Controller>> controllersLookup) {

        final Set<Controller> foundControllers = controllersLookup.apply(this);

        switch (foundControllers.size()) {

            case 0:
                return Collections.emptySet();

            case 1:

                // attachPart will call onAttached, which will set the controller.
                this._controller = foundControllers.iterator().next();
                this._controller.attachPart(this);
                return foundControllers;

            default:

                final Set<Controller> candidateControllers = new ReferenceArraySet<>(6);
                Controller bestController = null;

                for (final Controller controller : foundControllers) {

                    if (null == bestController || (!candidateControllers.contains(controller) && controller.shouldConsumeController(bestController))) {
                        bestController = controller;
                    }

                    candidateControllers.add(controller);
                }

                // If we've located a valid neighboring controller, attach to it.

                if (null != bestController) {

                    // attachPart will call onAttached, which will set the controller.
                    this._controller = bestController;
                    bestController.attachPart(this);
                }

                return candidateControllers;
        }
    }

    @Override
    public void assertDetached() {

        if (null != this._controller) {

            final BlockPos coord = this.getWorldPosition();

            //noinspection AutoBoxing
            Log.LOGGER.info(Log.MULTIBLOCK, "[assert] Part @ ({}, {}, {}) should be detached already, but detected that it was not. This is not a fatal error, and will be repaired, but is unusual.",
                    coord.getX(), coord.getY(), coord.getZ());
            this._controller = null;
        }
    }

    @Override
    public boolean hasMultiblockSaveData() {
        return null != this._cachedMultiblockData;
    }

    @Override
    public Optional<CompoundNBT> getMultiblockSaveData() {
        return Optional.ofNullable(this._cachedMultiblockData);
    }

    @Override
    public <T> T mapMultiblockSaveData(final Function<CompoundNBT, T> mapper, final T defaultValue) {
        return null != this._cachedMultiblockData ? mapper.apply(this._cachedMultiblockData) : defaultValue;
    }

    @Override
    public void forMultiblockSaveData(final Consumer<CompoundNBT> consumer) {

        if (null != this._cachedMultiblockData) {
            consumer.accept(this._cachedMultiblockData);
        }
    }

    @Override
    public void onMultiblockDataAssimilated() {
        this._cachedMultiblockData = null;
    }

    /**
     * Subscribe to be notified by a controller data update.
     */
    public void listenForControllerDataUpdates() {
        this.onDataUpdateHandler = this.getMultiblockController()
                .map(controller -> controller.listenForDataUpdate(this::onDataUpdate))
                .orElse(null);
    }

    /**
     * Unsubscribe from the notification of controller data updates.
     */
    public void unlistenForControllerDataUpdates() {

        if (null != this.onDataUpdateHandler) {

            this.getMultiblockController().ifPresent(controller -> controller.unlistenForDataUpdate(this.onDataUpdateHandler));
            this.onDataUpdateHandler = null;
        }
    }

    private Runnable onDataUpdateHandler;

    //endregion
	//region ISyncableEntity

	@Override
	public void syncDataFrom(CompoundNBT data, SyncReason syncReason) {

        this._positionHash = this.worldPosition.asLong();

        if (data.contains("multiblockData")) {

            final CompoundNBT multiblockData = data.getCompound("multiblockData");

            switch (syncReason) {

                case FullSync:
                    // We can't directly initialize a multiblock controller yet, so we cache the data here until
                    // we receive a validate() call, which creates the controller and hands off the cached data.
                    this._cachedMultiblockData = multiblockData;
                    break;

                case NetworkUpdate:
                    CodeHelper.optionalIfPresentOrElse(this.getMultiblockController(),
                            // This part is connected to a machine, sync it
                            c -> c.syncFromSaveDelegate(multiblockData, syncReason),
                            // This part hasn't been added to a machine yet, so cache the data
                            () -> this._cachedMultiblockData = multiblockData);
                    break;
            }
        }
	}

	@Override
    public CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {

        if (this.isMultiblockSaveDelegate()) {
            this.getMultiblockController()
                    .ifPresent(c -> data.put("multiblockData", c.syncDataTo(new CompoundNBT(), syncReason)));
        }

		return data;
	}

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        super.getDebugMessages(side, messages);
        CodeHelper.optionalIfPresentOrElse(this.getMultiblockController(),
                (controller) -> this.getControllerDebugMessages(side, controller, messages),
                () -> messages.addUnlocalized("Part not attached to a controller"));
    }

    private void getControllerDebugMessages(final LogicalSide side, final Controller controller, final IDebugMessages messages) {

        messages.addUnlocalized("Multiblock controller class: %1$s", controller.getClass().getSimpleName());
        //noinspection AutoBoxing
        messages.addUnlocalized("Attached parts: %1$d; Assembled: %2$s", controller.getPartsCount(), controller.isAssembled());

        controller.getReferenceCoord().ifPresent(position -> messages.addUnlocalized("Reference coordinates %s", position.toString()));

        if (controller instanceof IActivableMachine) {
            //noinspection AutoBoxing
            messages.addUnlocalized("Active: %1$s", ((IActivableMachine)controller).isMachineActive());
        }

        controller.getLastError().ifPresent(error -> messages.add(side, error, "Last validation error: "));

        if (controller instanceof IDebuggable) {
            ((IDebuggable)controller).getDebugMessages(side, messages);
        }
    }

    //endregion
	//region TileEntity

	/**
	 * validates a tile entity
	 */
	@Override
	public void clearRemoved() {

		super.clearRemoved();
        this.getRegistry().onPartAdded(this);
	}

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved() {

        super.setRemoved();
        this.detachSelf(false);
    }

    @Override
    public void onChunkUnloaded() {

        super.onChunkUnloaded();
        this.detachSelf(true);
    }

    @Override
    public void setLevelAndPosition(final World world, final BlockPos pos) {

        super.setLevelAndPosition(world, pos);
        this._positionHash = this.worldPosition.asLong();
    }

    @Override
    public void setPosition(final BlockPos posIn) {

        super.setPosition(posIn);
        this._positionHash = this.worldPosition.asLong();
    }

    //endregion
    //region internals

	@Deprecated // not implemented yet
	protected void notifyNeighborsOfTileChange() {
		//WORLD.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
	}

	/*
	 * Detaches this block from its controller. Calls detachPart() and clears the controller member.
	 */
	@SuppressWarnings("WeakerAccess")
    protected void detachSelf(boolean chunkUnloading) {

		if (this._controller != null) {

			// Clean part out of controller
			this._controller.detachPart(this, chunkUnloading);

			// The above should call onDetached, but, just in case...
			this._controller = null;
		}

		// Clean part out of lists in the registry
        this.getRegistry().onPartRemovedFromWorld(this);
	}

    @SuppressWarnings("unchecked")
    private IMultiblockRegistry<Controller> getRegistry() {
        return (IMultiblockRegistry<Controller>) MultiblockRegistry.INSTANCE;
    }

    private Controller _controller;
    private boolean _unvisited;
    private boolean _saveMultiblockData;
    private CompoundNBT _cachedMultiblockData;
    private long _positionHash;

    //endregion
}
