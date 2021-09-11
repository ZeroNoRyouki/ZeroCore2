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

package it.zerono.mods.zerocore.lib.multiblock.cuboid;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Optional;

public enum PartPosition
        implements IStringSerializable {

	Unknown(null, Type.Unknown),
	Interior(null, Type.Unknown),
	FrameCorner(null, Type.Frame),
	FrameEastWest(null, Type.Frame),
	FrameSouthNorth(null, Type.Frame),
	FrameUpDown(null, Type.Frame),
	TopFace(Direction.UP, Type.Face),
	BottomFace(Direction.DOWN, Type.Face),
	NorthFace(Direction.NORTH, Type.Face),
	SouthFace(Direction.SOUTH, Type.Face),
	EastFace(Direction.EAST, Type.Face),
	WestFace(Direction.WEST, Type.Face);

	public enum Type {

		Unknown,
		Interior,
		Frame,
		Face
	}

    /**
     * Compute the position of a block in the multiblock volume
     *
     * @param controller the controller of the multiblock
     * @param blockPosition the position of the block
     * @return the position of the block in the multiblock
     */
    public static <Controller extends AbstractCuboidMultiblockController<Controller>> PartPosition positionIn(
            final Controller controller, final BlockPos blockPosition) {
        return controller.mapBoundingBoxCoordinates((min, max) -> positionIn(blockPosition, min, max), PartPosition.Unknown);
    }

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

        if (facesMatching <= 0) {
            position = PartPosition.Interior;
        } else if (facesMatching >= 3) {
            position = PartPosition.FrameCorner;
        } else if (facesMatching == 2) {

            if (!eastFacing && !westFacing) {
                position = PartPosition.FrameEastWest;
            } else if (!southFacing && !northFacing) {
                position = PartPosition.FrameSouthNorth;
            } else {
                position = PartPosition.FrameUpDown;
            }

        } else {

            // only 1 face matches

            if (eastFacing) {
                position = PartPosition.EastFace;
            } else if (westFacing) {
                position = PartPosition.WestFace;
            } else if (southFacing) {
                position = PartPosition.SouthFace;
            } else if (northFacing) {
                position = PartPosition.NorthFace;
            } else if (upFacing) {
                position = PartPosition.TopFace;
            } else {
                position = PartPosition.BottomFace;
            }
        }

        return position;
    }
	
	public boolean isFace() {
		return this._type == Type.Face;
	}

	public boolean isVerticalFace() {
        return EastFace == this || WestFace == this || NorthFace == this || SouthFace == this;
    }

    public boolean isHorizontalFace() {
        return TopFace == this || BottomFace == this;
    }

	public boolean isFrame() {
		return this._type == Type.Frame;
	}

	public Optional<Direction> getDirection() {
		return Optional.ofNullable(this._facing);
	}

	public Type getType() {
		return this._type;
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

	PartPosition(@Nullable Direction facing, Type type) {

		this._facing = facing;
		this._type = type;
	}

	private final Direction _facing;
	private final Type _type;

	//endregion
}
