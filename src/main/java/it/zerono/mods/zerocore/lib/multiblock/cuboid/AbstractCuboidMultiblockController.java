/*
 *
 * AbstractCuboidMultiblockController.java
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

import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox;
import it.zerono.mods.zerocore.lib.multiblock.AbstractMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.multiblock.validation.ValidationError;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractCuboidMultiblockController<Controller extends AbstractCuboidMultiblockController<Controller>>
        extends AbstractMultiblockController<Controller> {

	//region AbstractMultiblockController

    @Override
    protected boolean isMachineWhole(final IMultiblockValidator validatorCallback) {

        final CuboidBoundingBox bb = this.getBoundingBox();

        if (this.getPartsCount() < this.getMinimumNumberOfPartsForAssembledMachine() || bb.isEmpty()) {

            validatorCallback.setLastError(ValidationError.VALIDATION_ERROR_TOO_FEW_PARTS);
            return false;
        }

        final Level world = this.getWorld();
        final BlockPos boundingBoxMin = bb.getMin();
        final BlockPos boundingBoxMax = bb.getMax();
        final int minX = boundingBoxMin.getX();
        final int minY = boundingBoxMin.getY();
        final int minZ = boundingBoxMin.getZ();
        final int maxX = boundingBoxMax.getX();
        final int maxY = boundingBoxMax.getY();
        final int maxZ = boundingBoxMax.getZ();

        if (isSizeWrong(validatorCallback, Direction.Axis.X, this.getMinimumXSize(), this.getMaximumXSize(), maxX - minX + 1) ||
                isSizeWrong(validatorCallback, Direction.Axis.Y, this.getMinimumYSize(), this.getMaximumYSize(), maxY - minY + 1) ||
                isSizeWrong(validatorCallback, Direction.Axis.Z, this.getMinimumZSize(), this.getMaximumZSize(), maxZ - minZ + 1)) {
            return false;
        }

        // if we have some detached parts let's do a preemptive check of what's in their place now

        if (null != this._detachedParts && !this._detachedParts.isEmpty()) {

            for (final IMultiblockPart<Controller> part : this._detachedParts) {

                final BlockPos partLocation = part.getWorldPosition();
                final int x = partLocation.getX();
                final int y = partLocation.getY();
                final int z = partLocation.getZ();
                int extremes = 0, errorIndex;
                boolean isValid;

                if (x == minX) {
                    ++extremes;
                }

                if (x == maxX) {
                    ++extremes;
                }

                if (y == minY) {
                    ++extremes;
                }

                if (y == maxY) {
                    ++extremes;
                }

                if (z == minZ) {
                    ++extremes;
                }

                if (z == maxZ) {
                    ++extremes;
                }

                if (extremes >= 2) {

                    errorIndex = 0;
                    isValid = this.isBlockGoodForFrame(world, x, y, z, validatorCallback);

                } else if (1 == extremes) {

                    if (y == maxY) {

                        errorIndex = 1;
                        isValid = this.isBlockGoodForTop(world, x, y, z, validatorCallback);

                    } else if (y == minY) {

                        errorIndex = 2;
                        isValid = this.isBlockGoodForBottom(world, x, y, z, validatorCallback);

                    } else {

                        errorIndex = 3;
                        isValid = this.isBlockGoodForSides(world, x, y, z, validatorCallback);
                    }

                } else {

                    errorIndex = 4;
                    isValid = this.isBlockGoodForInterior(world, x, y, z, validatorCallback);
                }

                if (!isValid) {

                    // report error and quit

                    if (validatorCallback.isLastErrorEmpty()) {
                        validatorCallback.setLastError(partLocation, s_errors[errorIndex]);
                    }

                    return false;
                }
            }

            this._detachedParts.clear();
        }

        final BlockPos.MutableBlockPos partLocation = new BlockPos.MutableBlockPos();
        boolean isValid;
        int errorIndex;

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {

                    // Okay, figure out what sort of block this should be.

                    partLocation.set(x, y, z);

                    final IMultiblockPart<Controller> part = this._connectedParts.get(BlockPos.asLong(x, y, z));
                    int extremes = 0;
                    boolean downFacing = false;
                    boolean upFacing = false;
                    boolean northFacing = false;
                    boolean southFacing = false;
                    boolean westFacing = false;
                    boolean eastFacing = false;

                    if (x == minX) {

                        ++extremes;
                        westFacing = true;
                    }

                    if (x == maxX) {

                        ++extremes;
                        eastFacing = true;
                    }

                    if (y == minY) {

                        ++extremes;
                        downFacing = true;
                    }

                    if (y == maxY) {

                        ++extremes;
                        upFacing = true;
                    }

                    if (z == minZ) {

                        ++extremes;
                        northFacing = true;
                    }

                    if (z == maxZ) {

                        ++extremes;
                        southFacing = true;
                    }

                    if (part instanceof AbstractCuboidMultiblockPart) {

                        ///////////////////////////////////////////////////////////////////////////////////////////////
                        // found a cuboid part. is it valid?

                        if (!part.testOnController(this::isControllerCompatible)) {

                            validatorCallback.setLastError(partLocation, "zerocore:api.multiblock.validation.invalid_part");
                            return false;
                        }

                        final PartPosition partPosition;

                        if (extremes >= 2) {

                            errorIndex = 0;

                            if (!eastFacing && !westFacing) {
                                partPosition = PartPosition.FrameEastWest;
                            } else if (!southFacing && !northFacing) {
                                partPosition = PartPosition.FrameSouthNorth;
                            } else {
                                partPosition = PartPosition.FrameUpDown;
                            }

                        } else if (1 == extremes) {

                            if (y == maxY) {

                                errorIndex = 1;
                                partPosition = PartPosition.TopFace;

                            } else if (y == minY) {

                                errorIndex = 2;
                                partPosition = PartPosition.BottomFace;

                            } else {

                                errorIndex = 3;

                                if (eastFacing) {
                                    partPosition = PartPosition.EastFace;
                                } else if (westFacing) {
                                    partPosition = PartPosition.WestFace;
                                } else if (southFacing) {
                                    partPosition = PartPosition.SouthFace;
                                } else if (northFacing) {
                                    partPosition = PartPosition.NorthFace;
                                } else if (upFacing) {
                                    partPosition = PartPosition.TopFace;
                                } else {
                                    partPosition = PartPosition.BottomFace;
                                }
                            }

                        } else {

                            errorIndex = 4;
                            partPosition = PartPosition.Interior;
                        }

                        final BlockFacings facings = BlockFacings.from(downFacing, upFacing, northFacing, southFacing, westFacing, eastFacing);

                        ((AbstractCuboidMultiblockPart<Controller>)part).setPartPosition(partPosition, facings);
                        isValid = ((AbstractCuboidMultiblockPart<Controller>)part).isGoodForPosition(partPosition, validatorCallback);

                        ///////////////////////////////////////////////////////////////////////////////////////////////

                    } else {

                        ///////////////////////////////////////////////////////////////////////////////////////////////
                        // found something else. is it valid?

                        if (extremes >= 2) {

                            errorIndex = 0;
                            isValid = this.isBlockGoodForFrame(world, x, y, z, validatorCallback);

                        } else if (1 == extremes) {

                            if (y == maxY) {

                                errorIndex = 1;
                                isValid = this.isBlockGoodForTop(world, x, y, z, validatorCallback);

                            } else if (y == minY) {

                                errorIndex = 2;
                                isValid = this.isBlockGoodForBottom(world, x, y, z, validatorCallback);

                            } else {

                                errorIndex = 3;
                                isValid = this.isBlockGoodForSides(world, x, y, z, validatorCallback);
                            }

                        } else {

                            errorIndex = 4;
                            isValid = this.isBlockGoodForInterior(world, x, y, z, validatorCallback);
                        }

                        ///////////////////////////////////////////////////////////////////////////////////////////////
                    }

                    if (!isValid) {

                        // report error and quit

                        if (validatorCallback.isLastErrorEmpty()) {
                            validatorCallback.setLastError(partLocation, s_errors[errorIndex]);
                        }

                        return false;
                    }
                }
            }
        }

        return true;
    }

	@Override
	public void forceStructureUpdate(final Level world) {

	    final CuboidBoundingBox bb = this.getBoundingBox();

	    if (bb.isEmpty()) {
	        return;
        }

	    final BlockPos minCoord = bb.getMin();
	    final BlockPos maxCoord = bb.getMax();
        final int minX = minCoord.getX();
        final int minY = minCoord.getY();
        final int minZ = minCoord.getZ();
        final int maxX = maxCoord.getX();
        final int maxY = maxCoord.getY();
        final int maxZ = maxCoord.getZ();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {

                    final BlockState state = world.getBlockState(pos.set(x, y ,z));

                    world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                }
            }
        }
	}

	//endregion
	//region internals

    protected AbstractCuboidMultiblockController(Level world) {
        super(world);
    }

    private static boolean isSizeWrong(final IMultiblockValidator validatorCallback, final Direction.Axis axis,
                                       final int minSize, final int maxSize, final int size) {

        if (maxSize > 0 && size > maxSize) {

            //noinspection AutoBoxing
            validatorCallback.setLastError("zerocore:api.multiblock.validation.machine_too_large", maxSize, axis.getSerializedName());
            return true;
        }

        if (size < minSize) {

            //noinspection AutoBoxing
            validatorCallback.setLastError("zerocore:api.multiblock.validation.machine_too_small", minSize, axis.getSerializedName());
            return true;
        }

        return false;
    }

    private static final String[] s_errors;

	static {

        s_errors = new String[5];
        s_errors[0] = "zerocore:api.multiblock.validation.invalid_part_for_frame";
        s_errors[1] = "zerocore:api.multiblock.validation.invalid_part_for_top";
        s_errors[2] = "zerocore:api.multiblock.validation.invalid_part_for_bottom";
        s_errors[3] = "zerocore:api.multiblock.validation.invalid_part_for_sides";
        s_errors[4] = "zerocore:api.multiblock.validation.invalid_part_for_interior";
    }

    //endregion
}
