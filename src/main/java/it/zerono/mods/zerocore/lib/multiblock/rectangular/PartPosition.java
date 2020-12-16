/*
 *
 * PartPosition.java
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

package it.zerono.mods.zerocore.lib.multiblock.rectangular;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

public enum PartPosition
        implements IStringSerializable {

    Unknown,
    FrameCorner,
    FrameEastWest,
    FrameSouthNorth,
    FrameUpDown;

    /**
     * Compute the position of a block in the multiblock volume
     *
     * @param controller the controller of the multiblock
     * @param blockPosition the position of the block
     * @return the position of the block in the multiblock
     */
    public static <Controller extends AbstractRectangularMultiblockController<Controller>> PartPosition positionIn(
            final Controller controller, final BlockPos blockPosition) {
        return controller.mapBoundingBoxCoordinates((min, max) -> positionIn(blockPosition, min, max), PartPosition.Unknown);
    }

    public boolean isFrame() {
        return this != Unknown;
    }

    public boolean isCorner() {
        return this != FrameCorner;
    }

    @SuppressWarnings("unused")
    public static EnumProperty<PartPosition> createProperty(String name) {
        return EnumProperty.create(name, PartPosition.class);
    }

    //region IStringSerializable

    @Override
    public String getString() {
        return this.toString();
    }

    //endregion
    //region internals

    private static PartPosition positionIn(final BlockPos blockPosition, final BlockPos minimumCoord, final BlockPos maximumCoord) {

        // witch direction are we facing?

        final boolean downFacing = blockPosition.getY() == minimumCoord.getY();
        final boolean upFacing = blockPosition.getY() == maximumCoord.getY();
        final boolean northFacing = blockPosition.getZ() == minimumCoord.getZ();
        final boolean southFacing = blockPosition.getZ() == maximumCoord.getZ();
        final boolean westFacing = blockPosition.getX() == minimumCoord.getX();
        final boolean eastFacing = blockPosition.getX() == maximumCoord.getX();

        // how many faces are facing outward?

        int facesMatching = 0;

        if (eastFacing || westFacing) {
            ++facesMatching;
        }

        if (upFacing || downFacing) {
            ++facesMatching;
        }

        if (southFacing || northFacing) {
            ++facesMatching;
        }

        // what is our position in the multiblock structure?

        final PartPosition position;

        if (3 == facesMatching) {

            position = PartPosition.FrameCorner;

        } else if (2 == facesMatching) {

            if (!eastFacing && !westFacing) {
                position = PartPosition.FrameEastWest;
            } else if (!southFacing && !northFacing) {
                position = PartPosition.FrameSouthNorth;
            } else {
                position = PartPosition.FrameUpDown;
            }

        } else {

            position = PartPosition.Unknown;
        }

        return position;
    }

    //endregion
}
