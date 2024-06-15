/*
 *
 * Zone.java
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

package it.zerono.mods.zerocore.lib.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

@Deprecated
public abstract class Zone implements Comparable<Zone> {

    public static Zone rectangular(final BlockPos minCoords, final BlockPos maxCoords) {
        return new RectangularCuboidZone(minCoords, maxCoords);
    }

    public static Zone sphere(final BlockPos center, int radius) {
        return new SphereZone(center, radius);
    }

    public abstract boolean contains(BlockPos position);

    public abstract AABB getBoundingBox();

    @Override
    public int compareTo(Zone zone) {

        final AABB myBB = this.getBoundingBox();
        final AABB hisBB = zone.getBoundingBox();
        //TODO imp!!
        return 0;
    }

    private static class RectangularCuboidZone extends Zone {

        RectangularCuboidZone(final BlockPos minCoords, final BlockPos maxCoords) {

            this._minCoords = minCoords;
            this._maxCoords = maxCoords;
        }

        @Override
        public boolean contains(final BlockPos position) {
            return (this._minCoords.getX() <= position.getX() && position.getX() <= this._maxCoords.getX()) &&
                   (this._minCoords.getY() <= position.getY() && position.getY() <= this._maxCoords.getY()) &&
                   (this._minCoords.getZ() <= position.getZ() && position.getZ() <= this._maxCoords.getZ());
        }

        @Override
        public AABB getBoundingBox() {
            return new AABB(this._minCoords, this._maxCoords);
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) {
                return true;
            }

            if (!(o instanceof RectangularCuboidZone)) {
                return false;
            }

            final RectangularCuboidZone other = (RectangularCuboidZone)o;

            return this._minCoords.equals(other._minCoords) && this._maxCoords.equals(other._maxCoords);
        }

        private final BlockPos _minCoords;
        private final BlockPos _maxCoords;
    }

    private static class SphereZone extends Zone {

        SphereZone(final BlockPos center, final int radius) {

            this._center = center;
            this._radius = radius;
        }

        @Override
        public boolean contains(final BlockPos position) {
            return (((position.getX() - this._center.getX()) * (position.getX() - this._center.getX())) +
                    ((position.getY() - this._center.getY()) * (position.getY() - this._center.getY())) +
                    ((position.getZ() - this._center.getZ()) * (position.getZ() - this._center.getZ()))) <=
                    this._radius * this._radius;
        }

        @Override
        public AABB getBoundingBox() {

            final int centerX = this._center.getX();
            final int centerY = this._center.getY();
            final int centerZ = this._center.getZ();

            return new AABB(centerX - this._radius, centerY - this._radius, centerZ - this._radius,
                                     centerX + this._radius - 1, centerY + this._radius - 1, centerZ + this._radius - 1);
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) {
                return true;
            }

            if (!(o instanceof SphereZone)) {
                return false;
            }

            final SphereZone other = (SphereZone)o;

            return this._radius == other._radius && this._center.equals(other._center);
        }

        private final BlockPos _center;
        private final int _radius;
    }
}
