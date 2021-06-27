/*
 *
 * IMultiblockMachine.java
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

import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IMultiblockMachine {

    /**
     * @return the World associated to this multiblock machine.
     */
    World getWorld();

    /**
     * @return the reference coordinate for this multiblock machine, the block with the lowest x, y, z coordinates,
     * evaluated in that order.
     */
    Optional<BlockPos> getReferenceCoord();

    /**
     * @return The minimum bounding-box coordinate containing this machine's blocks.
     */
    @Deprecated // use getBoundingBox()
    Optional<BlockPos> getMinimumCoord();

    /**
     * @return The maximum bounding-box coordinate containing this machine's blocks.
     */
    @Deprecated // use getBoundingBox()
    Optional<BlockPos> getMaximumCoord();

    /**
     * @return The bounding-box encompassing this machine's blocks.
     */
    default CuboidBoundingBox getBoundingBox() {
        return CuboidBoundingBox.EMPTY;
    }

    @Deprecated // use getBoundingBox()
    default boolean hasValidBoundingBoxCoordinates() {
        return true;
    }

    @Deprecated // use getBoundingBox()
    <T> T mapBoundingBoxCoordinates(BiFunction<BlockPos, BlockPos, T> minMaxCoordMapper, T defaultValue);

    @Deprecated // use getBoundingBox()
    <T> T mapBoundingBoxCoordinates(BiFunction<BlockPos, BlockPos, T> minMaxCoordMapper, T defaultValue,
                                    Function<BlockPos, BlockPos> minRemapper, Function<BlockPos, BlockPos> maxRemapper);

    default <T> T mapReferenceCoordinates(Function<BlockPos, T> mapper, T defaultValue) {
        return this.getReferenceCoord().map(mapper).orElse(defaultValue);
    }

    @Deprecated // use getBoundingBox()
    void forBoundingBoxCoordinates(BiConsumer<BlockPos, BlockPos> minMaxCoordConsumer);

    @Deprecated // use getBoundingBox()
    void forBoundingBoxCoordinates(BiConsumer<BlockPos, BlockPos> minMaxCoordConsumer,
                                   Function<BlockPos, BlockPos> minRemapper, Function<BlockPos, BlockPos> maxRemapper);

    default void forReferenceCoordinates(Consumer<BlockPos> consumer) {
        this.getReferenceCoord().ifPresent(consumer);
    }
}
