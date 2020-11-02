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

import it.zerono.mods.zerocore.lib.CodeHelper;
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
        return CodeHelper.optionalMap(this.getMinimumCoord(), this.getMaximumCoord(),
                    (minPos, maxPos) -> minPos.compareTo(position) < 0 && maxPos.compareTo(position) > 0)
                .orElse(false);
    }

	//region AbstractMultiblockController

	/**
	 * @return True if the machine is "whole" and should be assembled. False otherwise.
	 */
	@Override
	protected boolean isMachineWhole(IMultiblockValidator validatorCallback) {

        final Optional<BlockPos> maximumCoord = this.getMaximumCoord();
        final Optional<BlockPos> minimumCoord = this.getMinimumCoord();

		if (this.getPartsCount() < getMinimumNumberOfPartsForAssembledMachine() ||
                !maximumCoord.isPresent() || !minimumCoord.isPresent()) {

			validatorCallback.setLastError(ValidationError.VALIDATION_ERROR_TOO_FEW_PARTS);
			return false;
		}

        final int minX = minimumCoord.get().getX();
        final int minY = minimumCoord.get().getY();
        final int minZ = minimumCoord.get().getZ();
        final int maxX = maximumCoord.get().getX();
        final int maxY = maximumCoord.get().getY();
        final int maxZ = maximumCoord.get().getZ();

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

                            validatorCallback.setLastError("zerocore:api.multiblock.validation.invalid_part", x, y, z);
                            return false;
                        }

                        if (!this.containsPart(cuboidPart)) {

                            validatorCallback.setLastError("zerocore:api.multiblock.validation.invalid_foreign_part", x, y, z);
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
                                validatorCallback.setLastError("zerocore:api.multiblock.validation.invalid_part_for_frame", x, y, z);
                            }

							return false;
						}

					} else if (1 == extremes) {

						if (y == maxY) {

							isPartValid = isCuboidMultiblockPart ? cuboidPart.isGoodForPosition(position, validatorCallback) :
                                    this.isBlockGoodForTop(this.getWorld(), x, y, z, validatorCallback);

							if (!isPartValid) {

                                if (!validatorCallback.getLastError().isPresent()) {
                                    validatorCallback.setLastError("zerocore:api.multiblock.validation.invalid_part_for_top", x, y, z);
                                }

								return false;
							}

						} else if (y == minY) {

							isPartValid = isCuboidMultiblockPart ? cuboidPart.isGoodForPosition(position, validatorCallback) :
                                    this.isBlockGoodForBottom(this.getWorld(), x, y, z, validatorCallback);

							if (!isPartValid) {

                                if (!validatorCallback.getLastError().isPresent()) {
                                    validatorCallback.setLastError("zerocore:api.multiblock.validation.invalid_part_for_bottom", x, y, z);
                                }

								return false;
							}

						} else {

							// Side
							isPartValid = isCuboidMultiblockPart ? cuboidPart.isGoodForPosition(position, validatorCallback) :
                                    this.isBlockGoodForSides(this.getWorld(), x, y, z, validatorCallback);

							if (!isPartValid) {

                                if (!validatorCallback.getLastError().isPresent()) {
                                    validatorCallback.setLastError("zerocore:api.multiblock.validation.invalid_part_for_sides", x, y, z);
                                }

								return false;
							}
						}

					} else {

						isPartValid = isCuboidMultiblockPart ? cuboidPart.isGoodForPosition(position, validatorCallback) :
                                this.isBlockGoodForInterior(this.getWorld(), x, y, z, validatorCallback);

						if (!isPartValid) {

                            if (!validatorCallback.getLastError().isPresent()) {
                                validatorCallback.setLastError("zerocore:api.multiblock.validation.reactor.invalid_part_for_interior", x, y, z);
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

		final Optional<BlockPos> minCoord = this.getMinimumCoord();
		final Optional<BlockPos> maxCoord = this.getMaximumCoord();

		if (!minCoord.isPresent() || !maxCoord.isPresent()) {
		    return;
        }

		final int minX = minCoord.get().getX();
		final int minY = minCoord.get().getY();
		final int minZ = minCoord.get().getZ();
		final int maxX = maxCoord.get().getX();
		final int maxY = maxCoord.get().getY();
		final int maxZ = maxCoord.get().getZ();

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

    //endregion
}
