/*
 *
 * AbstractCuboidMultiblockPart.java
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

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox;
import it.zerono.mods.zerocore.lib.multiblock.AbstractMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractCuboidMultiblockPart<Controller extends AbstractCuboidMultiblockController<Controller>>
        extends AbstractMultiblockPart<Controller> {

	public AbstractCuboidMultiblockPart(final TileEntityType<?> type) {

		super(type);
		this._position = PartPosition.Unknown;
        this._outwardFacings = BlockFacings.NONE;
	}

    public abstract boolean isGoodForPosition(PartPosition position, IMultiblockValidator validatorCallback);

	/**
	 * Get the outward facing of the part in the formed multiblock
	 *
	 * @return the outward facing of the part. A face is "set" in the BlockFacings object if that face is facing outward
	 */
	public BlockFacings getOutwardFacings() {
		return this._outwardFacings;
	}

	/**
	 * Get the position of the part in the formed multiblock
	 *
	 * @return the position of the part
	 */
	public PartPosition getPartPosition() {
		return this._position;
	}

	/**
	 * Return the single direction this part is facing if the part is in one side of the multiblock
	 *
	 * @return an {@link Optional} direction toward with the part is facing or an empty {@link Optional} if the part
     * is not in one side of the multiblock
	 */
	public Optional<Direction> getOutwardDirection() {
        return CodeHelper.optionalOr(this.getPartPosition().getDirection(), () -> {

            BlockFacings out = this.getOutwardFacings();

            return (out.some() && 1 == out.countFacesIf(true)) ? out.firstIf(true) : Optional.empty();
        });
	}

	/**
	 * Return the single direction this part is facing based on it's position in the multiblock
	 *
	 * @return an {@link Optional} direction toward with the part is facing or an empty {@link Optional} if the part
     * is not in one side of the multiblock
	 */
	@SuppressWarnings("unused")
	public Optional<Direction> getOutwardFacingFromWorldPosition() {
        return this.evalOnController(controller -> {

            final CuboidBoundingBox bb = controller.getBoundingBox();

            return getOutwardFacingFromWorldPositionInternal(this.getWorldPosition(), bb.getMin(), bb.getMax());

        }, Optional.empty());
	}

    public Direction getOutwardFacingFromWorldPosition(final Direction defaultResult) {
	    return this.evalOnController(controller -> {

	        final CuboidBoundingBox bb = controller.getBoundingBox();

	        return getOutwardFacingFromWorldPositionInternal(this.getWorldPosition(), bb.getMin(), bb.getMax())
                    .orElse(defaultResult);

        }, defaultResult);
    }

    private static Optional<Direction> getOutwardFacingFromWorldPositionInternal(final BlockPos partPosition,
                                                                                 final BlockPos min,
                                                                                 final BlockPos max) {

        final int x = partPosition.getX(), y = partPosition.getY(), z = partPosition.getZ();

        final BlockFacings facings = BlockFacings.from(min.getY() == y, max.getY() == y,
                min.getZ() == z, max.getZ() == z,
                min.getX() == x, max.getX() == x);

        if (facings.some() && 1 == facings.countFacesIf(true)) {
            return facings.firstIf(true);
        } else {
            return Optional.empty();
        }
    }

    void setPartPosition(final PartPosition position, final BlockFacings facings) {

	    this._position = position;
	    this._outwardFacings = facings;
    }

	/**
     * Tell all blocks in our outward-facing-set that our blockstate is changed
	 */
    @SuppressWarnings("unused")
	public void notifyOutwardNeighborsOfStateChange() {

		final Block blockType = this.getBlockType();
		final BlockFacings facings = this.getOutwardFacings();
		final BlockPos position = this.getWorldPosition();
		final World world = this.getLevel();

		if (null != world) {
            for (final Direction facing : CodeHelper.DIRECTIONS) {
                if (facings.isSet(facing)) {
                    WorldHelper.notifyNeighborsOfStateChange(world, position.relative(facing), blockType);
                }
            }
        }
	}

	//region AbstractMultiblockPart

	@Override
	public void onPreMachineAssembled(Controller controller) {
	}

	@Override
	public void onPostMachineAssembled(Controller controller) {
	}

	@Override
	public void onPreMachineBroken() {
	}

	@Override
	public void onPostMachineBroken() {

		this._position = PartPosition.Unknown;
        this._outwardFacings = BlockFacings.NONE;
	}

	//region internals

    private PartPosition _position;
    private BlockFacings _outwardFacings;

    //endregion
}
