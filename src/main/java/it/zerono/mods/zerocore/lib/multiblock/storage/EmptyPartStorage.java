/*
 *
 * EmptyPartStorage.java
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

package it.zerono.mods.zerocore.lib.multiblock.storage;

import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.world.NeighboringPositions;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public final class EmptyPartStorage<Controller extends IMultiblockController<Controller>>
        implements IPartStorage<Controller> {

    public static <Controller extends IMultiblockController<Controller>> IPartStorage<Controller> getInstance() {
        //noinspection unchecked
        return (IPartStorage<Controller>)s_instance;
    }

    //region IPartStorage

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean contains(IMultiblockPart<Controller> part) {
        return false;
    }

    @Override
    public boolean contains(BlockPos[] positions) {
        return false;
    }

    @Override
    public boolean contains(long[] positionsHashes) {
        return false;
    }

    @Override
    public boolean contains(NeighboringPositions positions) {
        return false;
    }

    @Nullable
    @Override
    public IMultiblockPart<Controller> get(BlockPos position) {
        return null;
    }

    @Nullable
    @Override
    public IMultiblockPart<Controller> get(long positionHash) {
        return null;
    }

    @Override
    public void get(NeighboringPositions positions, List<IMultiblockPart<Controller>> foundParts) {
    }

    @Nullable
    @Override
    public IMultiblockPart<Controller> getFirst() {
        return null;
    }

    @Override
    public void addOrReplace(IMultiblockPart<Controller> part) {
    }

    @Override
    public void remove(IMultiblockPart<Controller> part) {
    }

    @Override
    public void clear() {
    }

    @Override
    public Collection<IMultiblockPart<Controller>> unmodifiable() {
        return Collections.emptyList();
    }

    @Override
    public Stream<IMultiblockPart<Controller>> stream() {
        return this.unmodifiable().stream();
    }

    @Override
    public Stream<IMultiblockPart<Controller>> parallelStream() {
        return this.unmodifiable().parallelStream();
    }

    @Override
    public CuboidBoundingBox boundingBox() {
        return CuboidBoundingBox.EMPTY;
    }

    //endregion
    //region Iterable<IMultiblockPart<Controller>>

    @Override
    public Iterator<IMultiblockPart<Controller>> iterator() {
        return this.unmodifiable().iterator();
    }

    //endregion
    //region internals

    private static final EmptyPartStorage<?> s_instance = new EmptyPartStorage<>();

    //endregion
}
