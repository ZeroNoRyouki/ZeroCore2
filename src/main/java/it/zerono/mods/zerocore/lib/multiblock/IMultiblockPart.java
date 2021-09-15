/*
 *
 * IMultiblockPart.java
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

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemTier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Basic interface for a multiblock machine part.
 * Preferably, you should derive from {@link AbstractMultiblockPart}, which does all the hard work for you.
 * 
 * @param <Controller> the type of this part controller
 */
public interface IMultiblockPart<Controller extends IMultiblockController<Controller>> {

	/**
	 * @return True if this part is connected to a multiblock controller. False otherwise.
	 */
	boolean isConnected();

	default boolean isConnectedTo(Controller controller) {
	    return this.evalOnController(c -> c == controller, false);
    }

	/**
	 * @return True if this part is connected to a multiblock controller of an assembled machine. False otherwise.
	 */
	boolean isMachineAssembled();

	/**
	 * @return True if this part is connected to a multiblock controller of an disassembled machine. False otherwise.
	 */
	boolean isMachineDisassembled();

	/**
	 * @return True if this part is connected to a multiblock controller of an paused machine. False otherwise.
	 */
	boolean isMachinePaused();

	/**
	 * @return The attached multiblock controller for this tile entity. 
	 */
    Optional<Controller> getMultiblockController();

    /**
     * Execute the given Consumer on the controller, if this part is connected to one
     * @param code the consumer
     */
    default void executeOnController(Consumer<Controller> code) {
        this.getMultiblockController().ifPresent(code);
    }

    /**
     * Execute the given Function on the controller returning it's result, if this part is connected to one
     * @param code the function
     * @param defaultValue the value to return if this part is not connected to a controller
     * @return the result of the function if this part is connected to a controller or defaultValue if it's not
     */
    default <R> R evalOnController(Function<Controller, R> code, R defaultValue) {
        return this.getMultiblockController().map(code).orElse(defaultValue);
    }

    /**
     * Execute the given Predicate on the controller return it's result, if this part is connected to one
     *
     * @param test the predicate
     * @return true if this part is connected to a controller and the predicate match the controller, false otherwise
     */
    default boolean testOnController(Predicate<Controller> test) {
        return this.getMultiblockController().filter(test).isPresent();
    }

    /**
     * Returns the world of this part
     */
    @Deprecated // use getCurrentWorld()
    Optional<World> getPartWorld();

    default <T> T mapPartWorld(Function<World, T> mapper, T defaultValue) {
        return this.getPartWorld().map(mapper).orElse(defaultValue);
    }

    default void forPartWorld(Consumer<World> consumer) {
        this.getPartWorld().ifPresent(consumer);
    }

    default World getCurrentWorld() {
        return Objects.requireNonNull(this.getPartWorld().orElse(null));
    }

	/**
	 * Returns the location of this multiblock part in the world, in BlockPos form.
	 * @return A BlockPos set to the location of this multiblock part in the world.
	 */
	BlockPos getWorldPosition();

	default long getWorldPositionHash() {
	    return this.getWorldPosition().asLong();
    }

	boolean isPartInvalid();
	
	// Multiblock connection-logic callbacks
	
	/**
	 * Called after this block has been attached to a new multiblock controller.
	 * @param newController The new multiblock controller to which this tile entity is attached.
	 */
	void onAttached(Controller newController);
	
	/**
	 * Called after this block has been detached from a multiblock controller.
	 * @param multiblockController The multiblock controller that no longer controls this tile entity.
	 */
	void onDetached(Controller multiblockController);
	
	/**
	 * Called when this block is being orphaned. Use this to copy game-data values that
	 * should persist despite a machine being broken.
	 * This should NOT mark the part as disconnected. onDetached will be called immediately afterwards.
	 * @param oldController The controller which is orphaning this block. 
	 * @param oldControllerSize The number of connected blocks in the controller prior to shedding orphans. Deprecated: always zero
	 * @param newControllerSize The number of connected blocks in the controller after shedding orphans. Deprecated: always zero
	 */
	void onOrphaned(Controller oldController, int oldControllerSize, int newControllerSize);
	
	// Multiblock fuse/split helper methods. Here there be dragons.

	/**
	 * Factory method. Creates a new multiblock controller and returns it.
	 * Does not attach this tile entity to it.
	 * Override this in your game code!
	 * @return A new Multiblock Controller
	 */
    Controller createController();

	/**
	 * Retrieve the type of multiblock controller which governs this part.
	 * Used to ensure that incompatible multiblocks are not merged.
	 * @return The class/type of the multiblock controller which governs this type of part.
	 */
	Class<Controller> getControllerType();
	
	/**
	 * Called when this block is moved from its current controller into a new controller.
	 * A special case of attach/detach, done here for efficiency to avoid triggering
	 * lots of recalculation logic.
	 * @param newController The new controller into which this tile entity is being merged.
	 */
	void onAssimilated(Controller newController);

	// Multiblock connection data access.
	// You generally shouldn't toy with these!
	// They're for use by Multiblock Controllers.
	
	/**
	 * Set that this block has been visited by your validation algorithms.
	 */
	void setVisited();
	
	/**
	 * Set that this block has not been visited by your validation algorithms;
	 */
	void setUnvisited();
	
	/**
	 * @return True if this block has been visited by your validation algorithms since the last reset.
	 */
	boolean isVisited();

    /**
     * @return True if this block has NOT been visited by your validation algorithms since the last reset.
     */
    default boolean isNotVisited() {
        return !this.isVisited();
    }
	
	/**
	 * Called when this block becomes the designated block for saving data and
	 * transmitting data across the wire.
	 */
	void becomeMultiblockSaveDelegate();
	
	/**
	 * Called when this block is no longer the designated block for saving data
	 * and transmitting data across the wire.
	 */
	void forfeitMultiblockSaveDelegate();

	/**
	 * Is this block the designated save/load & network delegate?
	 */
	boolean isMultiblockSaveDelegate();

	/**
	 * Returns a {@link List} of neighboring {@link IMultiblockPart}s.
	 * Primarily a utility method. Only works after tileentity construction, so it cannot be used in
	 * IMultiblockController::attachPart.
	 * 
	 * This method is chunk-safe on the server; it will not query for parts in chunks that are unloaded.
	 * Note that no method is chunk-safe on the client, because ChunkProviderClient is stupid.
     *
	 * @return A {@link List} of neighboring {@link IMultiblockPart}s if any is found or an empty {@link List} otherwise.
	 */
    List<IMultiblockPart<Controller>> getNeighboringParts();

	// Multiblock business-logic callbacks - implement these!

	/**
	 * Called immediately BEFORE a machine is fully assembled from the disassembled state, meaning
	 * it was broken by a player/entity action, not by chunk unloads.
	 * Note that, for non-square machines, the min/max coordinates may not actually be part
	 * of the machine! They form an outer bounding box for the whole machine itself.
	 * @param multiblockController The controller to which this part is being assembled.
	 */
	void onPreMachineAssembled(Controller multiblockController);

	/**
	 * Called immediately AFTER a machine was fully assembled from the disassembled state, meaning
	 * it was broken by a player/entity action, not by chunk unloads.
	 * Note that, for non-square machines, the min/max coordinates may not actually be part
	 * of the machine! They form an outer bounding box for the whole machine itself.
	 * @param multiblockController The controller to which this part is being assembled.
	 */
	void onPostMachineAssembled(Controller multiblockController);

	/**
	 * Called immediately BEFORE the machine is broken for game reasons, e.g. a player removed a block
	 * or an explosion occurred.
	 */
	void onPreMachineBroken();

	/**
	 * Called immediately AFTER the machine is broken for game reasons, e.g. a player removed a block
	 * or an explosion occurred.
	 */
	void onPostMachineBroken();
	
	/**
	 * Called when the user activates the machine. This is not called by default, but is included
	 * as most machines have this game-logical concept.
	 */
	@SuppressWarnings("unused")
	void onMachineActivated();

	/**
	 * Called when the user deactivates the machine. This is not called by default, but is included
	 * as most machines have this game-logical concept.
	 */
    @SuppressWarnings("unused")
	void onMachineDeactivated();

	/**
	 * Called when this part should check its neighbors.
	 * This method MUST NOT cause additional chunks to load.
	 * ALWAYS check to see if a chunk is loaded before querying for its tile entity
	 * This part should inform the controller that it is attaching at this time.
     *
	 * @return A {@link Set} containing multiblock controllers to which this object would like to attach. It should
     * have attached to one of the controllers in this list. Return an empty {@link Set} if there are no compatible
     * controllers nearby
	 */
	@Deprecated
    default Set<Controller> attachToNeighbors() {
        return Collections.emptySet();
    }

    /**
     * Called when this part should check its neighbors.
     * This method MUST NOT cause additional chunks to load.
     * ALWAYS check to see if a chunk is loaded before querying for its tile entity
     * This part should inform the controller that it is attaching at this time.
     *
     * @return A {@link Set} containing multiblock controllers to which this object would like to attach. It should
     * have attached to one of the controllers in this list. Return an empty {@link Set} if there are no compatible
     * controllers nearby
     */
    Set<Controller> attachToNeighbors(Function<IMultiblockPart<Controller>, Set<Controller>> controllersLookup);

	/**
	 * Assert that this part is detached. If not, log a warning and set the part's controller to null.
	 * Do NOT fire the full disconnection logic.
	 */
	void assertDetached();

	/**
	 * @return True if a part has multiblock game-data saved inside it.
	 */
	boolean hasMultiblockSaveData();
	
	/**
	 * @return The part's saved multiblock game-data in NBT format, or null if there isn't any.
	 */
	Optional<CompoundNBT> getMultiblockSaveData();

	default <T> T mapMultiblockSaveData(Function<CompoundNBT, T> mapper, T defaultValue) {
	    return this.getMultiblockSaveData().map(mapper).orElse(defaultValue);
    }

	default void forMultiblockSaveData(Consumer<CompoundNBT> consumer) {
	    this.getMultiblockSaveData().ifPresent(consumer);
    }

	/**
	 * Called after a block is added and the controller has incorporated the part's saved
	 * multiblock game-data into itself. Generally, you should clear the saved data here.
	 */
	void onMultiblockDataAssimilated();

    /**
     * Subscribe to be notified by a controller data update.
     */
    void listenForControllerDataUpdates();

    /**
     * @return a set of default block properties for a multiblock part
     */
    static Block.Properties getDefaultBlockProperties() {
        return Block.Properties.of(Material.METAL, MaterialColor.METAL)
                .sound(SoundType.METAL)
                .strength(5.0F, 6.0F)
                .harvestLevel(ItemTier.IRON.getLevel())
                .harvestTool(ToolType.PICKAXE)
                .requiresCorrectToolForDrops()
                .lightLevel(blockState -> 0)
                .isValidSpawn((blockState, blockReader, pos, entity) -> false)
                .isViewBlocking((blockState, blockReader, pos) -> true);
    }
}
