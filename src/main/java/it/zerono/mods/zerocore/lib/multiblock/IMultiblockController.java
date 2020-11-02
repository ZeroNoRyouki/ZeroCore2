/*
 *
 * IMultiblockController.java
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

import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.Set;

public interface IMultiblockController<Controller extends IMultiblockController<Controller>>
        extends IMultiblockMachine, IMultiblockValidator, ISyncableEntity {

    //region Multiblock Parts management

    /**
     * @return True if this controller has no associated blocks, false otherwise
     */
    boolean isEmpty();

    /**
     * @return The number of parts connected to this controller.
     */
    int getPartsCount();

    /**
     * Check if another controller is compatible with this one
     *
     * @return true if the other controller is compatible, false otherwise
     */
    default boolean isControllerCompatible(Controller other) {
        return this.getClass().equals(other.getClass());
    }

    /**
     * Check if the given controller instance is this controller
     *
     * @return true if the other controller instance is this controller, false otherwise
     */
    default boolean isSameController(Controller other) {
        return this == other;
    }

    /**
     * Check if the provided part is compatible with this controller.
     * Called when a part is trying to attach to it's neighboring controllers AFTER the controller's type check was performed and passed.
     *
     * @param part the part to check
     * @return true if the part is compatible, false otherwise
     */
    default boolean isPartCompatible(IMultiblockPart<Controller> part) {
        return true;
    }

    /**
     * Check if a multiblock part is being tracked by this machine.
     * @param part The part to check.
     * @return True if the multiblock part is being tracked by this machine, false otherwise.
     */
    boolean containsPart(IMultiblockPart<Controller> part);

    /**
     * Attach a new part to this machine.
     * @param part The part to add.
     */
    void attachPart(IMultiblockPart<Controller> part);

    /**
     * Call to detach a block from this machine. Generally, this should be called
     * when the tile entity is being released, e.g. on block destruction.
     * @param part The part to detach from this machine.
     * @param chunkUnloading Is this entity detaching due to the chunk unloading? If true, the multiblock will be paused instead of broken.
     */
    void detachPart(IMultiblockPart<Controller> part, boolean chunkUnloading);

    /**
     * Detach all parts. Return a set of all parts which still
     * have a valid tile entity. Chunk-safe.
     * @return A set of all parts which still have a valid tile entity.
     */
    Set<IMultiblockPart<Controller>> detachAllParts();

    /**
     * Assimilate another controller into this controller.
     * Acquire all of the other controller's blocks and attach them
     * to this one.
     *
     * @param other The controller to merge into this one.
     */
    void assimilateController(Controller other);

    /**
     * Tests whether this multiblock should consume the other multiblock
     * and become the new multiblock master when the two multiblocks
     * are adjacent. Assumes both multiblocks are the same type.
     * @param other The other multiblock controller.
     * @return True if this multiblock should consume the other, false otherwise.
     */
    boolean shouldConsumeController(Controller other);

    /**
     * Called when this machine may need to check for blocks that are no
     * longer physically connected to the reference coordinate.
     */
    Set<IMultiblockPart<Controller>> checkForDisconnections();

    //endregion
    //region Multiblock state

    /**
     * Request to be notified when the multiblock data was loaded from disk or from the network.
     * Subscribe when the multiblock is Assembled.
     * Subscribers are automatically removed when the multiblock is Disassembled or Paused.
     *
     * @param handler a {@link Runnable} that's called when the multiblock data is loaded
     * @return use this value to unsubscribe
     */
    Runnable listenForDataUpdate(Runnable handler);

    /**
     * Stop listening for multiblock data updates
     *
     * @param handler the value returned by listenForDataUpdate()
     */
    void unlistenForDataUpdate(Runnable handler);

    /**
     * Sync the controller state from the save-delegate data
     * @param data the data
     */
    void syncFromSaveDelegate(CompoundNBT data, ISyncableEntity.SyncReason syncReason);

    /**
     * Check if the machine is whole or not.
     * If the machine was not whole, but now is, assemble the machine.
     * If the machine was whole, but no longer is, disassemble the machine.
     */
    void checkIfMachineIsWhole();

    /**
     * @return True if this multiblock machine is considered assembled and ready to go.
     */
    boolean isAssembled();

    /**
     * @return True if this multiblock machine is disassembled.
     */
    boolean isDisassembled();

    /**
     * @return True if this multiblock machine is paused.
     */
    boolean isPaused();

    /**
     * Driver for the update loop. If the machine is assembled, runs
     * the game logic update method.
     */
    void updateMultiblockEntity();

    //endregion
    //region Miscellanea

    /**
     * Force this multiblock to recalculate its minimum and maximum coordinates
     * from the list of connected parts.
     */
    void recalculateCoords();

    void forceStructureUpdate(World world);

    //endregion
}
