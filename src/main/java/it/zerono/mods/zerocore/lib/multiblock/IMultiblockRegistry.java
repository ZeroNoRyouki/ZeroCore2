/*
 *
 * IMultiblockRegistry.java
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

public interface IMultiblockRegistry<Controller extends IMultiblockController<Controller>> {

    /**
     * Register a new part in the system. The part has been created either through user action or via a chunk loading.
     * @param part The part being loaded.
     */
    void onPartAdded(IMultiblockPart<Controller> part);

    /**
     * Call to remove a part from world lists.
     * @param part The part being removed.
     */
    void onPartRemovedFromWorld(IMultiblockPart<Controller> part);

    /**
     * Call to mark a controller as dead. It should only be marked as dead
     * when it has no connected parts. It will be cleaned up at the end of the next world tick.
     * Note that a controller must shed all of its blocks before being marked as dead, or the system
     * will complain at you.
     *
     * @param controller The dead controller
     */
    void addDeadController(Controller controller);

    /**
     * Call to mark a controller as dirty. Dirty means that parts have
     * been added or removed this tick.
     * @param controller The dirty controller
     */
    void addDirtyController(Controller controller);
}
