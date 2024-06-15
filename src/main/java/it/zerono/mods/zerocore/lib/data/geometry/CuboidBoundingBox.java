/*
 *
 * CuboidBoundingBox.java
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.zerocore.lib.data.geometry;

import com.google.common.collect.AbstractIterator;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.NonNullFunction;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class CuboidBoundingBox
        implements Iterable<BlockPos> {

    public static final CuboidBoundingBox EMPTY;

    public CuboidBoundingBox() {
        this(CodeHelper.MAX_BLOCKPOS, CodeHelper.MIN_BLOCKPOS);
    }

    public CuboidBoundingBox(final Vec3i min, final Vec3i max) {

        this._min = new BlockPos.MutableBlockPos(min.getX(), min.getY(), min.getZ());
        this._max = new BlockPos.MutableBlockPos(max.getX(), max.getY(), max.getZ());
    }

    public CuboidBoundingBox(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {

        this._min = new BlockPos.MutableBlockPos(minX, minY, minZ);
        this._max = new BlockPos.MutableBlockPos(maxX, maxY, maxZ);
    }

    public BlockPos getMin() {
        return this._min.immutable();
    }

    public BlockPos getMax() {
        return this._max.immutable();
    }

    public int getMinX() {
        return this._min.getX();
    }

    public int getMinY() {
        return this._min.getY();
    }

    public int getMinZ() {
        return this._min.getZ();
    }

    public int getMaxX() {
        return this._max.getX();
    }

    public int getMaxY() {
        return this._max.getY();
    }

    public int getMaxZ() {
        return this._max.getZ();
    }

    public AABB getAABB() {
        return new AABB(this._min, this._max);
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    /**
     * Add a position to this bounding box.
     *
     * @param position the position to add.
     * @return the modified bounding box.
     */
    public CuboidBoundingBox add(final BlockPos position) {

        adjustPosition(this._min, position, Math::min);
        adjustPosition(this._max, position, Math::max);
        return this;
    }

    /**
     * Remove a position from the bounding box.
     *
     * @param position the position to remove.
     * @return the modified bounding box.
     */
    public CuboidBoundingBox remove(final BlockPos position) {
        return EMPTY;
    }

    /**
     * Combine the provided bounding box with this one.
     *
     * @param other the bounding box to combine.
     * @return the modified bounding box.
     */
    public CuboidBoundingBox combine(final CuboidBoundingBox other) {

        adjustPosition(this._min, other._min, Math::min);
        adjustPosition(this._max, other._max, Math::max);
        return this;
    }

    public boolean contains(final Vec3i vec) {
        return this.contains(vec.getX(), vec.getY(), vec.getZ());
    }

    public boolean contains(final int x, final int y, final int z) {
        return x >= this._min.getX() && x <= this._max.getX() &&
                y >= this._min.getY() && y <= this._max.getY() &&
                z >= this._min.getZ() && z <= this._max.getZ();
    }

    public int getLengthX() {
        return this._max.getX() - this._min.getX() + 1;
    }

    public int getLengthY() {
        return this._max.getY() - this._min.getY() + 1;
    }

    public int getLengthZ() {
        return this._max.getZ() - this._min.getZ() + 1;
    }

    public int getLength(final Direction.Axis axis) {

        switch (axis) {

            default:
            case X:
                return this.getLengthX();

            case Y:
                return this.getLengthY();

            case Z:
                return this.getLengthZ();
        }
    }

    public int getVolume() {
        return this.isEmpty() ? 0 : CodeHelper.mathVolume(this._min, this._max);
    }

    public int getInternalVolume() {
        return this.isEmpty() ? 0 : CodeHelper.mathVolume(
                this._min.getX() + 1, this._min.getY() + 1, this._min.getZ() + 1,
                this._max.getX() - 1, this._max.getY() - 1, this._max.getZ() - 1);
    }

    public BlockPos getCenter() {
        return this._min.offset(this.getLengthX() / 2, this.getLengthY() / 2, this.getLengthZ() / 2);
    }

    public int commonVertices(final Vec3i position) {
        return CodeHelper.commonVertices(position, this._min) + CodeHelper.commonVertices(position, this._max);
    }

    public <R> R map(final BiFunction<BlockPos, BlockPos, R> mapper) {
        return mapper.apply(this.getMin(), this.getMax());
    }

    public <R> R map(final BiFunction<BlockPos, BlockPos, R> mapper, final NonNullFunction<BlockPos, BlockPos> minRemapper,
                     final NonNullFunction<BlockPos, BlockPos> maxRemapper) {
        return mapper.apply(minRemapper.apply(this.getMin()), maxRemapper.apply(this.getMax()));
    }

    public void accept(final BiConsumer<BlockPos, BlockPos> consumer) {
        consumer.accept(this.getMin(), this.getMax());
    }

    public void accept(final BiConsumer<BlockPos, BlockPos> consumer, final NonNullFunction<BlockPos, BlockPos> minRemapper,
                       final NonNullFunction<BlockPos, BlockPos> maxRemapper) {
        consumer.accept(minRemapper.apply(this.getMin()), maxRemapper.apply(this.getMax()));
    }

    //region Iterable<BlockPos>

    @Override
    public Iterator<BlockPos> iterator() {
        return new AbstractIterator<>() {

            @Override
            protected BlockPos computeNext() {

                ++this._currentX;
                if (this._currentX > this._maxX) {

                    this._currentX = this._minX;
                    ++this._currentZ;

                    if (this._currentZ > this._maxZ) {

                        this._currentZ = this._minZ;
                        ++this._currentY;

                        if (this._currentY > this._maxY) {
                            return this.endOfData();
                        }
                    }
                }

                return this._cursor.set(this._currentX, this._currentY, this._currentZ);
            }

            //region internals

            final BlockPos.MutableBlockPos _cursor = new BlockPos.MutableBlockPos();
            final int _minX = _min.getX();
            final int _minY = _min.getY();
            final int _minZ = _min.getZ();
            final int _maxX = _max.getX();
            final int _maxY = _max.getY();
            final int _maxZ = _max.getZ();
            int _currentX = _minX - 1;
            int _currentY = _minY;
            int _currentZ = _minZ;

            //endregion
        };
    }

    //endregion
    //region Object

    @Override
    public boolean equals(final Object other) {

        if (this == other) {

            return true;

        } else if (!(other instanceof CuboidBoundingBox)) {

            return false;

        } else {

            final CuboidBoundingBox otherBB = (CuboidBoundingBox)other;

            return 0 == this._min.compareTo(otherBB._min) && 0 == this._max.compareTo(otherBB._max);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this._min, this._max);
    }

    public String toString() {
        return this.isEmpty() ? "EMPTY" : String.format("%s -> %s", CodeHelper.toString(this._min), CodeHelper.toString(this._max));
    }

    //endregion
    //region internals

    private static boolean adjustPosition(final BlockPos.MutableBlockPos currentPosition, final BlockPos newPosition, final IntComparator minMax) {

        final int curX = currentPosition.getX();
        final int curY = currentPosition.getY();
        final int curZ = currentPosition.getZ();
        final int newX = minMax.compare(currentPosition.getX(), newPosition.getX());
        final int newY = minMax.compare(currentPosition.getY(), newPosition.getY());
        final int newZ = minMax.compare(currentPosition.getZ(), newPosition.getZ());

        if ((newX != curX) || (newY != curY) || (newZ != curZ)) {

            currentPosition.set(newX, newY, newZ);
            return true;
        }

        return false;
    }

    private final BlockPos.MutableBlockPos _min;
    private final BlockPos.MutableBlockPos _max;

    static {

        EMPTY = new CuboidBoundingBox() {

            @Override
            public AABB getAABB() {
                return CodeHelper.EMPTY_BOUNDING_BOX;
            }

            @Override
            public CuboidBoundingBox add(final BlockPos position) {
                return new CuboidBoundingBox(position, position);
            }

            @Override
            public CuboidBoundingBox remove(final BlockPos position) {
                return EMPTY;
            }

            @Override
            public CuboidBoundingBox combine(final CuboidBoundingBox other) {
                return new CuboidBoundingBox(other._min, other._max);
            }

            @Override
            public boolean equals(Object other) {
                return other == EMPTY;
            }
        };
    }

    //endregion
}
