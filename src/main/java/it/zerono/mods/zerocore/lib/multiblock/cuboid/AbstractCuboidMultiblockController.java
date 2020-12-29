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

import it.zerono.mods.zerocore.lib.multiblock.AbstractMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.multiblock.validation.ValidationError;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Optional;

public abstract class AbstractCuboidMultiblockController<Controller extends AbstractCuboidMultiblockController<Controller>>
        extends AbstractMultiblockController<Controller> {

    /**
     * Return true if the given position is in the multiblock internal volume
     * @param position the position to check
     * @return true if position lay in the internal volume, false otherwise
     */
    protected boolean containPosition(final BlockPos position) {
        return this.mapBoundingBoxCoordinates((minPos, maxPos) -> minPos.compareTo(position) < 0 && maxPos.compareTo(position) > 0, false);
    }

	//region AbstractMultiblockController

	/**
	 * @return True if the machine is "whole" and should be assembled. False otherwise.
	 */
	@Override
	protected boolean isMachineWhole(final IMultiblockValidator validatorCallback) {

        if (this.getPartsCount() < this.getMinimumNumberOfPartsForAssembledMachine() ||
                !this.hasValidBoundingBoxCoordinates()) {

            validatorCallback.setLastError(ValidationError.VALIDATION_ERROR_TOO_FEW_PARTS);
            return false;
        }

        return this.mapBoundingBoxCoordinates((min, max) -> this.isMachineWhole(validatorCallback, min, max), false);
    }

    private boolean isMachineWhole(final IMultiblockValidator validatorCallback,
                                   final BlockPos minimumCoord, final BlockPos maximumCoord) {

        final int minX = minimumCoord.getX();
        final int minY = minimumCoord.getY();
        final int minZ = minimumCoord.getZ();
        final int maxX = maximumCoord.getX();
        final int maxY = maximumCoord.getY();
        final int maxZ = maximumCoord.getZ();

        if (isSizeWrong(validatorCallback, Direction.Axis.X, this.getMinimumXSize(), this.getMaximumXSize(), maxX - minX + 1) ||
            isSizeWrong(validatorCallback, Direction.Axis.Y, this.getMinimumYSize(), this.getMaximumYSize(), maxY - minY + 1) ||
            isSizeWrong(validatorCallback, Direction.Axis.Z, this.getMinimumZSize(), this.getMaximumZSize(), maxZ - minZ + 1)) {
            return false;
        }

		// Now we run a simple check on each block within that volume.
		// Any block deviating = NO DEAL SIR

		boolean isPartValid;

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {

					// Okay, figure out what sort of block this should be.

                    final BlockPos partLocation = new BlockPos(x, y, z);
                    final Optional<AbstractCuboidMultiblockPart<Controller>> part = this.getCuboidPartFromWorld(partLocation);
                    final AbstractCuboidMultiblockPart<Controller> cuboidPart;
                    final boolean isCuboidMultiblockPart;

                    if (part.isPresent()) {

                        isCuboidMultiblockPart = true;
                        cuboidPart = part.get();

                        // Ensure this part should actually be allowed within a cube of this controller's type
                        if (!cuboidPart.getMultiblockController().map(this::isControllerCompatible).orElse(false)) {

                            validatorCallback.setLastError(partLocation, "zerocore:api.multiblock.validation.invalid_part");
                            return false;
                        }

                        if (!this.containsPart(cuboidPart)) {

                            validatorCallback.setLastError(partLocation, "zerocore:api.multiblock.validation.invalid_foreign_part");
                            return false;
                        }

                    } else {

                        // This is permitted so that we can incorporate certain non-multiblock parts inside interiors
                        isCuboidMultiblockPart = false;
                        cuboidPart = null;
                    }

					// Validate block type against both part-level and material-level validators.

                    final PartPosition position = PartPosition.positionIn(this.castSelf(), partLocation);
					int extremes = 0;

					if (x == minX) {
					    ++extremes;
					}

					if (y == minY) {
					    ++extremes;
					}

					if (z == minZ) {
					    ++extremes;
					}
					
					if (x == maxX) {
					    ++extremes;
					}

					if (y == maxY) {
					    ++extremes;
					}

					if (z == maxZ) {
					    ++extremes;
					}

					if (extremes >= 2) {

						isPartValid = isCuboidMultiblockPart ? cuboidPart.isGoodForPosition(position, validatorCallback) :
                                this.isBlockGoodForFrame(this.getWorld(), x, y, z, validatorCallback);

						if (!isPartValid) {

							if (!validatorCallback.getLastError().isPresent()) {
                                validatorCallback.setLastError(partLocation, "zerocore:api.multiblock.validation.invalid_part_for_frame");
                            }

							return false;
						}

					} else if (1 == extremes) {

						if (y == maxY) {

							isPartValid = isCuboidMultiblockPart ? cuboidPart.isGoodForPosition(position, validatorCallback) :
                                    this.isBlockGoodForTop(this.getWorld(), x, y, z, validatorCallback);

							if (!isPartValid) {

                                if (!validatorCallback.getLastError().isPresent()) {
                                    validatorCallback.setLastError(partLocation, "zerocore:api.multiblock.validation.invalid_part_for_top");
                                }

								return false;
							}

						} else if (y == minY) {

							isPartValid = isCuboidMultiblockPart ? cuboidPart.isGoodForPosition(position, validatorCallback) :
                                    this.isBlockGoodForBottom(this.getWorld(), x, y, z, validatorCallback);

							if (!isPartValid) {

                                if (!validatorCallback.getLastError().isPresent()) {
                                    validatorCallback.setLastError(partLocation, "zerocore:api.multiblock.validation.invalid_part_for_bottom");
                                }

								return false;
							}

						} else {

							// Side
							isPartValid = isCuboidMultiblockPart ? cuboidPart.isGoodForPosition(position, validatorCallback) :
                                    this.isBlockGoodForSides(this.getWorld(), x, y, z, validatorCallback);

							if (!isPartValid) {

                                if (!validatorCallback.getLastError().isPresent()) {
                                    validatorCallback.setLastError(partLocation, "zerocore:api.multiblock.validation.invalid_part_for_sides");
                                }

								return false;
							}
						}

					} else {

						isPartValid = isCuboidMultiblockPart ? cuboidPart.isGoodForPosition(position, validatorCallback) :
                                this.isBlockGoodForInterior(this.getWorld(), x, y, z, validatorCallback);

						if (!isPartValid) {

                            if (!validatorCallback.getLastError().isPresent()) {
                                validatorCallback.setLastError(partLocation, "zerocore:api.multiblock.validation.invalid_part_for_interior");
                            }

							return false;
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public void forceStructureUpdate(final World world) {
	    this.forBoundingBoxCoordinates((min, max) -> forceStructureUpdate(world, min, max));
	}

	//endregion
	//region internals

    protected AbstractCuboidMultiblockController(World world) {
        super(world);
    }

    private static boolean isSizeWrong(final IMultiblockValidator validatorCallback, final Direction.Axis axis,
                                       final int minSize, final int maxSize, final int size) {

        if (maxSize > 0 && size > maxSize) {

            validatorCallback.setLastError("zerocore:api.multiblock.validation.machine_too_large", maxSize, axis.getString());
            return true;
        }

        if (size < minSize) {

            validatorCallback.setLastError("zerocore:api.multiblock.validation.machine_too_small", minSize, axis.getString());
            return true;
        }

        return false;
    }

    private Optional<AbstractCuboidMultiblockPart<Controller>> getCuboidPartFromWorld(BlockPos position) {
        //noinspection unchecked
        return WorldHelper.getTile(this.getWorld(), position)
                .filter(te -> te instanceof AbstractCuboidMultiblockPart)
                .map(te -> (AbstractCuboidMultiblockPart<Controller>)te);
    }

    private static void forceStructureUpdate(final World world, final BlockPos minCoord, final BlockPos maxCoord) {

        final int minX = minCoord.getX();
        final int minY = minCoord.getY();
        final int minZ = minCoord.getZ();
        final int maxX = maxCoord.getX();
        final int maxY = maxCoord.getY();
        final int maxZ = maxCoord.getZ();

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {

                    final BlockPos pos = new BlockPos(x, y, z);
                    final BlockState state = world.getBlockState(pos);

                    world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.DEFAULT);
                }
            }
        }
    }

    //endregion
}
