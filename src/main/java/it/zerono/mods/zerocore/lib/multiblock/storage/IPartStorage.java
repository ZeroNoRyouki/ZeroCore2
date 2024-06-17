/*
 *
 * IPartStorage.java
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
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface IPartStorage<Controller extends IMultiblockController<Controller>>
        extends Iterable<IMultiblockPart<Controller>> {

    boolean isEmpty();

    int size();

    boolean contains(IMultiblockPart<Controller> part);

    boolean contains(BlockPos[] positions);

    boolean contains(long[] positionsHashes);

    boolean contains(NeighboringPositions positions);

    @Nullable
    IMultiblockPart<Controller> get(BlockPos position);

    @Nullable
    IMultiblockPart<Controller> get(long positionHash);

    void get(NeighboringPositions positions, List<IMultiblockPart<Controller>> foundParts);

    @Nullable
    IMultiblockPart<Controller> getFirst();

    void addOrReplace(IMultiblockPart<Controller> part);

    default void addAll(IPartStorage<Controller> parts) {
        parts.stream().forEach(this::addOrReplace);
    }

    @Nullable
    void remove(IMultiblockPart<Controller> part);

    default void removeAll(Collection<IMultiblockPart<Controller>> parts) {
        parts.forEach(this::remove);
    }

    void clear();

    Collection<IMultiblockPart<Controller>> unmodifiable();

    Stream<IMultiblockPart<Controller>> stream();

    Stream<IMultiblockPart<Controller>> parallelStream();

    default void forEach(final Consumer<IMultiblockPart<Controller>> consumer,
                         final Predicate<IMultiblockPart<Controller>> filter) {
        this.stream()
                .filter(filter)
                .forEach(consumer);
    }

    default void forEachValidPart(final Consumer<IMultiblockPart<Controller>> consumer) {
        this.stream()
                .filter(part -> !part.isPartInvalid())
                .forEach(consumer);
    }

    default void forEachNotVisitedPart(final Consumer<IMultiblockPart<Controller>> consumer) {
        this.stream()
                .filter(IMultiblockPart::isNotVisited)
                .forEach(consumer);
    }

    CuboidBoundingBox boundingBox();
}
