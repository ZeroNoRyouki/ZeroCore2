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

import it.unimi.dsi.fastutil.ints.IntComparator;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

import java.util.Objects;

public class CuboidBoundingBox {

    public static final CuboidBoundingBox EMPTY;

    public CuboidBoundingBox() {
        this(CodeHelper.MAX_BLOCKPOS, CodeHelper.MIN_BLOCKPOS);
    }

    public CuboidBoundingBox(final Vector3i min, final Vector3i max) {

        this._min = new BlockPos.Mutable(min.getX(), min.getY(), min.getZ());
        this._max = new BlockPos.Mutable(max.getX(), max.getY(), max.getZ());
    }

    public CuboidBoundingBox(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {

        this._min = new BlockPos.Mutable(minX, minY, minZ);
        this._max = new BlockPos.Mutable(maxX, maxY, maxZ);
    }

    public BlockPos getMin() {
        return this._min;
    }

    public BlockPos getMax() {
        return this._max;
    }

    public AxisAlignedBB getAABB() {

        if (null == this._aabb) {
            this._aabb = new AxisAlignedBB(this._min, this._max);
        }

        return this._aabb;
    }

    public boolean isEmpty() {
        return this == EMPTY || 0 == this._min.compareTo(this._max);
    }

    public CuboidBoundingBox add(final BlockPos position) {

        if (adjustPosition(this._min, position, Math::min) || adjustPosition(this._max, position, Math::max)) {
            this._aabb = null;
        }

        return this;
    }

    public CuboidBoundingBox combine(final CuboidBoundingBox other) {

        if (other._min.compareTo(this._min) < 0) {
            this._min.set(other._min);
        }

        if (other._max.compareTo(this._max) > 0) {
            this._max.set(other._max);
        }

        return this;
    }

    public boolean contains(final Vector3i vec) {
        return this.contains(vec.getX(), vec.getY(), vec.getZ());
    }

    public boolean contains(final int x, final int y, final int z) {
        return x >= this._min.getX() && x < this._max.getX() &&
                y >= this._min.getY() && y < this._max.getY() &&
                z >= this._min.getZ() && z < this._max.getZ();
    }

    public int getLengthX() {
        return this._max.getX() - this._min.getX();
    }

    public int getLengthY() {
        return this._max.getY() - this._min.getY();
    }

    public int getLengthZ() {
        return this._max.getZ() - this._min.getZ();
    }

    public int getVolume() {
        return this.isEmpty() ? 0 : CodeHelper.mathVolume(this._min, this._max);
    }

    public BlockPos getCenter() {
        return this._min.offset(this.getLengthX() / 2, this.getLengthY() / 2, this.getLengthZ() / 2);
    }

    public int commonVertices(final Vector3i position) {
        return CodeHelper.commonVertices(position, this._min) + CodeHelper.commonVertices(position, this._max);
    }

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
        return String.format("Cuboid (%s) -> (%s)", this._min.toString(), this._max.toString());
    }

    //endregion
    //region internals

    private static boolean adjustPosition(final BlockPos.Mutable currentPosition, final BlockPos newPosition, final IntComparator minMax) {

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

    private final BlockPos.Mutable _min;
    private final BlockPos.Mutable _max;
    private AxisAlignedBB _aabb;

    static {

        EMPTY = new CuboidBoundingBox() {

            @Override
            public AxisAlignedBB getAABB() {
                return CodeHelper.EMPTY_BOUNDING_BOX;
            }

            @Override
            public CuboidBoundingBox add(final BlockPos position) {
                return new CuboidBoundingBox(position, position);
            }

            @Override
            public boolean equals(Object other) {
                return other == EMPTY;
            }
        };
    }

    //endregion
}
