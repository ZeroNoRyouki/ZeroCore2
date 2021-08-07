/*
 *
 * AbstractRectangularMultiblockController.java
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

import it.zerono.mods.zerocore.lib.multiblock.AbstractMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.multiblock.validation.ValidationError;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public abstract class AbstractRectangularMultiblockController<Controller extends AbstractRectangularMultiblockController<Controller>>
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

        final int partsCount = this.getPartsCount();

        if (partsCount < this.getMinimumNumberOfPartsForAssembledMachine() || this.getBoundingBox().isEmpty()) {

            validatorCallback.setLastError(ValidationError.VALIDATION_ERROR_TOO_FEW_PARTS);
            return false;
        }

        return this.mapBoundingBoxCoordinates((min, max) -> this.isMachineWhole(validatorCallback, partsCount, min, max), false);
    }
    
    private boolean isMachineWhole(final IMultiblockValidator validatorCallback, final int partsCount,
                                   final BlockPos minimumCoord, final BlockPos maximumCoord) {

        final Vector3i translation = maximumCoord.subtract(minimumCoord);
        final Direction.Axis sizeOneAxis = getZeroAxis(translation);

        // is the size of one (and only one) dimension equal to 1?

        if (null == sizeOneAxis) {

            validatorCallback.setLastError("ONLY ONE AXIS CAN BE 1!!!");
            return false;
        }

        // check min/max sizes

        final int xLength = translation.getX() + 1;
        final int yLength = translation.getY() + 1;
        final int zLength = translation.getZ() + 1;

        if (isSizeWrong(validatorCallback, Direction.Axis.X, this.getMinimumXSize(), this.getMaximumXSize(), xLength) ||
                isSizeWrong(validatorCallback, Direction.Axis.Y, this.getMinimumYSize(), this.getMaximumYSize(), yLength) ||
                isSizeWrong(validatorCallback, Direction.Axis.Z, this.getMinimumZSize(), this.getMaximumZSize(), zLength)) {
            return false;
        }

        // check blocks

        final int perimeter;
        final BiFunction<Integer, Integer, BlockPos> positionFactory;
        final int uMin, uMax, vMin, vMax;

        switch (sizeOneAxis) {

            case X:
                perimeter = 2 * (yLength + zLength - 2);
                uMin = minimumCoord.getZ();
                uMax = maximumCoord.getZ();
                vMin = minimumCoord.getY();
                vMax = maximumCoord.getY();
                positionFactory = (u, v) -> new BlockPos(minimumCoord.getX(), v, u);
                break;

            case Y:
                perimeter = 2 * (xLength + zLength - 2);
                uMin = minimumCoord.getX();
                uMax = maximumCoord.getX();
                vMin = minimumCoord.getZ();
                vMax = maximumCoord.getZ();
                positionFactory = (u, v) -> new BlockPos(u, minimumCoord.getY(), v);
                break;

            case Z:
                perimeter = 2 * (xLength + yLength - 2);
                uMin = minimumCoord.getX();
                uMax = maximumCoord.getX();
                vMin = minimumCoord.getY();
                vMax = maximumCoord.getY();
                positionFactory = (u, v) -> new BlockPos(u, v, minimumCoord.getZ());
                break;

            default:
                throw new IllegalStateException("Illegal fourth axis");
        }

        if (perimeter != partsCount) {

            validatorCallback.setLastError("the number of blocks does not match the number of required parts!!!");
            return false;
        }

        for (int u = uMin; u <= uMax; ++u) {

            if (!this.validateBlock(positionFactory.apply(u, vMin), validatorCallback) ||
                !this.validateBlock(positionFactory.apply(u, vMax), validatorCallback)) {
                return false;
            }
        }

        for (int v = vMin + 1; v <= vMax - 1; ++v) {

            if (!this.validateBlock(positionFactory.apply(uMin, v), validatorCallback) ||
                !this.validateBlock(positionFactory.apply(uMax, v), validatorCallback)) {
                return false;
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

    protected AbstractRectangularMultiblockController(World world) {
        super(world);
    }

    @Nullable
    private static Direction.Axis getZeroAxis(final Vector3i vector) {

        final boolean x = 0 == vector.getX();
        final boolean y = 0 == vector.getY();
        final boolean z = 0 == vector.getZ();
        final int count = (x ? 1 : 0) + (y ? 1 : 0) + (z ? 1 : 0);

        if (1 != count) {
            return null;
        }

        return x ? Direction.Axis.X : y ? Direction.Axis.Y : Direction.Axis.Z;
    }

    private boolean validateBlock(final BlockPos blockPosition, final IMultiblockValidator validatorCallback) {

        final TileEntity te = WorldHelper.getLoadedTile(this.getWorld(), blockPosition);
        //noinspection unchecked
        final AbstractRectangularMultiblockPart<Controller> part = te instanceof AbstractRectangularMultiblockPart ?
                (AbstractRectangularMultiblockPart<Controller>)te : null;

        return null != part ? this.validatePart(part, blockPosition, validatorCallback) : this.validateGenericBlock(blockPosition, validatorCallback);
    }

    private boolean validatePart(final AbstractRectangularMultiblockPart<Controller> part, final BlockPos blockPosition,
                                 final IMultiblockValidator validatorCallback) {

        // Ensure this part should actually be allowed within a rectangle of this controller's type

        if (!part.getMultiblockController().map(this::isControllerCompatible).orElse(false)) {

            validatorCallback.setLastError(blockPosition, "zerocore:api.multiblock.validation.invalid_part");
            return false;
        }

        if (!this.containsPart(part)) {

            validatorCallback.setLastError(blockPosition, "zerocore:api.multiblock.validation.invalid_foreign_part");
            return false;
        }

        final PartPosition position = PartPosition.positionIn(this.castSelf(), blockPosition);

        if (!part.isGoodForPosition(position, validatorCallback)) {

            if (!validatorCallback.hasLastError()) {
                validatorCallback.setLastError(blockPosition, "zerocore:api.multiblock.validation.invalid_part_for_frame");
            }

            return false;
        }

        return true;
    }

    private boolean validateGenericBlock(final BlockPos blockPosition, final IMultiblockValidator validatorCallback) {

        if (!this.isBlockGoodForFrame(this.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), validatorCallback)) {

            if (!validatorCallback.hasLastError()) {
                validatorCallback.setLastError(blockPosition, "zerocore:api.multiblock.validation.invalid_part_for_frame");
            }

            return false;
        }

        return true;
    }

    private static boolean isValueNot(final int value, final int min, final int max) {
        return value != min && value != max;
    }

    private static boolean isOnCorner(final int a, final int b, final int aTarget, final int bTarget) {
        return a == aTarget && b == bTarget;
    }

    private static boolean isOnCorner(final int a, final int b, final int minA, final int minB, final int maxA, final int maxB) {
        return isOnCorner(a, b, minA, minB) || isOnCorner(a, b, minA, maxB) || isOnCorner(a, b, maxA, maxB) || isOnCorner(a, b, maxA, minB);
    }

    private static boolean isOnCornerX(final BlockPos pos, final BlockPos minPos, final BlockPos maxPos) {
        return isOnCorner(pos.getY(), pos.getZ(), minPos.getY(), minPos.getZ(), maxPos.getY(), maxPos.getZ());
    }

    private static boolean isOnCornerY(final BlockPos pos, final BlockPos minPos, final BlockPos maxPos) {
        return isOnCorner(pos.getX(), pos.getZ(), minPos.getX(), minPos.getZ(), maxPos.getX(), maxPos.getZ());
    }

    private static boolean isOnCornerZ(final BlockPos pos, final BlockPos minPos, final BlockPos maxPos) {
        return isOnCorner(pos.getX(), pos.getY(), minPos.getX(), minPos.getY(), maxPos.getX(), maxPos.getY());
    }

    private static boolean isSizeWrong(final IMultiblockValidator validatorCallback, final Direction.Axis axis,
                                       final int minSize, final int maxSize, final int size) {

        if (maxSize > 0 && size > maxSize) {

            validatorCallback.setLastError("zerocore:api.multiblock.validation.machine_too_large", maxSize, axis.getSerializedName());
            return true;
        }

        if (size < minSize) {

            validatorCallback.setLastError("zerocore:api.multiblock.validation.machine_too_small", minSize, axis.getSerializedName());
            return true;
        }

        return false;
    }

    @Nullable
    private AbstractRectangularMultiblockPart<Controller> getPartFromWorld(BlockPos position) {

        final TileEntity te = WorldHelper.getLoadedTile(this.getWorld(), position);

        //noinspection unchecked
        return te instanceof AbstractRectangularMultiblockPart ? (AbstractRectangularMultiblockPart<Controller>)te : null;
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

                    world.sendBlockUpdated(pos, state, state, Constants.BlockFlags.DEFAULT);
                }
            }
        }
    }

    //endregion
}
