/*
 *
 * Cuboid.java
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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import com.mojang.math.Vector3f;
import net.minecraft.core.Vec3i;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Cuboid {

    public static final Cuboid EMPTY = new Cuboid();
    public static final Cuboid FULL = new Cuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

    public final Vector3d Min;
    public final Vector3d Max;

    public Cuboid(final Vector3d min, final Vector3d max) {

        this.Min = min;
        this.Max = max;
    }

    public Cuboid(final double minX, final double minY, final double minZ,
                  final double maxX, final double maxY, final double maxZ) {
        this(new Vector3d(minX, minY, minZ), new Vector3d(maxX, maxY, maxZ));
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public Cuboid(final Cuboid other) {
        this(new Vector3d(other.Min), new Vector3d(other.Max));
    }

    public Cuboid(final Vec3i min, final Vec3i max) {
        this(Vector3d.from(min), Vector3d.from(max));
    }

    public Cuboid(final AABB boundingBox) {
        this(new Vector3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ),
             new Vector3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ));
    }

    public static Cuboid syncDataFrom(CompoundTag data) {

        if (data.contains("min") && data.contains("max")) {
            return new Cuboid(Vector3d.syncDataFrom(data.getCompound("min")),
                    Vector3d.syncDataFrom(data.getCompound("max")));
        }

        return EMPTY;
    }

    public CompoundTag syncDataTo(CompoundTag data) {

        data.put("min", this.Min.syncDataTo(new CompoundTag()));
        data.put("max", this.Max.syncDataTo(new CompoundTag()));
        return data;
    }

    public AABB toBoundingBox() {
        return new AABB(this.Min.X, this.Min.Y, this.Min.Z, this.Max.X, this.Max.Y, this.Max.Z);
    }

    public Cuboid add(final double offsetX, final double offsetY, final double offsetZ) {
        return new Cuboid(this.Min.add(offsetX, offsetY, offsetZ),
                this.Max.add(offsetX, offsetY, offsetZ));
    }

    public Cuboid add(final double offset) {
        return this.add(offset, offset, offset);
    }

    public Cuboid add(final Vector3d data) {
        return this.add(data.X, data.Y, data.Z);
    }

    public Cuboid add(final Vec3i data) {
        return this.add(data.getX(), data.getY(), data.getZ());
    }

    public Cuboid subtract(final double offsetX, final double offsetY, final double offsetZ) {
        return new Cuboid(this.Min.subtract(offsetX, offsetY, offsetZ),
                this.Max.subtract(offsetX, offsetY, offsetZ));
    }

    public Cuboid subtract(final double offset) {
        return this.subtract(offset, offset, offset);
    }

    public Cuboid subtract(final Vector3d data) {
        return this.subtract(data.X, data.Y, data.Z);
    }

    public Cuboid subtract(final Vec3i data) {
        return this.subtract(data.getX(), data.getY(), data.getZ());
    }

    public Cuboid expand(final double deltaX, final double deltaY, final double deltaZ) {
        return new Cuboid(this.Min.subtract(deltaX, deltaY, deltaZ),
                this.Max.add(deltaX, deltaY, deltaZ));
    }

    public Cuboid expand(final double delta) {
        return this.expand(delta, delta, delta);
    }

    public Cuboid expand(final Vector3d delta) {
        return this.expand(delta.X, delta.Y, delta.Z);
    }

    public Cuboid expand(final Direction side, final int amount) {

        switch (side.getAxisDirection()) {

            case NEGATIVE:
                return new Cuboid(this.Min.add(Vector3d.from(side.getNormal()).multiply(amount)), this.Max);

            case POSITIVE:
                return new Cuboid(this.Min, this.Max.add(Vector3d.from(side.getNormal()).multiply(amount)));
        }

        return this;
    }

    public boolean contains(final double x, final double y, final double z) {
        return this.Min.X - 1E-5 <= x && this.Min.Y - 1E-5 <= y && this.Min.Z - 1E-5 <= z &&
                this.Max.X + 1E-5 >= x && this.Max.Y + 1E-5 >= y && this.Max.Z + 1E-5 >= z;
    }

    public boolean contains(final Vector3d data) {
        return this.contains(data.X, data.Y, data.Z);
    }

    public boolean intersects(final Cuboid other) {
        return this.Max.X - 1E-5 > other.Min.X && this.Max.Y - 1E-5 > other.Min.Y && this.Max.Z - 1E-5 > other.Min.Z &&
                other.Max.X - 1E-5 > this.Min.X && other.Max.Y - 1E-5 > this.Min.Y && other.Max.Z - 1E-5 > this.Min.Z;
    }

    public double volume() {
        return (this.Max.X - this.Min.X + 1) * (this.Max.Y - this.Min.Y + 1) * (this.Max.Z - this.Min.Z + 1);
    }

    public Vector3d center() {
        return new Vector3d(this.Min).add(this.Max).multiply(0.5);
    }

    public Face getFace(final Direction facing) {
        return new Face(this, facing);
    }

    public double getWidth() {
        return this.Max.X - this.Min.X;
    }

    public double getHeight() {
        return this.Max.Y - this.Min.Y;
    }

    public double getDepth() {
        return this.Max.Z - this.Min.Z;
    }

    //region Object

    @Override
    public boolean equals(Object other) {

        if (other instanceof Cuboid) {

            Cuboid c = (Cuboid)other;

            return this.Min.equals(c.Min) && this.Max.equals(c.Max);
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("Cuboid (%f, %f, %f), (%f, %f, %f)", this.Min.X, this.Min.Y, this.Min.Z,
                this.Max.X, this.Max.Y, this.Max.Z);
    }

    //endregion

    public static class Face {

        public final Vector3d A;
        public final Vector3d B;
        public final Vector3d C;
        public final Vector3d D;
        public final Direction FACING;

        private Face(final Cuboid cuboid, final Direction facing) {

            this.FACING = facing;

            switch (facing) {

                default:
                case UP:
                    this.A = new Vector3d(cuboid.Min.X, cuboid.Max.Y, cuboid.Min.Z);
                    this.B = new Vector3d(cuboid.Min.X, cuboid.Max.Y, cuboid.Max.Z);
                    this.C = new Vector3d(cuboid.Max.X, cuboid.Max.Y, cuboid.Max.Z);
                    this.D = new Vector3d(cuboid.Max.X, cuboid.Max.Y, cuboid.Min.Z);
                    break;

                case DOWN:
                    this.A = new Vector3d(cuboid.Min.X, cuboid.Min.Y, cuboid.Min.Z);
                    this.B = new Vector3d(cuboid.Max.X, cuboid.Min.Y, cuboid.Min.Z);
                    this.C = new Vector3d(cuboid.Max.X, cuboid.Min.Y, cuboid.Max.Z);
                    this.D = new Vector3d(cuboid.Min.X, cuboid.Min.Y, cuboid.Max.Z);
                    break;

                case WEST:
                    this.A = new Vector3d(cuboid.Min.X, cuboid.Max.Y, cuboid.Min.Z);
                    this.B = new Vector3d(cuboid.Min.X, cuboid.Min.Y, cuboid.Min.Z);
                    this.C = new Vector3d(cuboid.Min.X, cuboid.Min.Y, cuboid.Max.Z);
                    this.D = new Vector3d(cuboid.Min.X, cuboid.Max.Y, cuboid.Max.Z);
                    break;

                case EAST:
                    this.A = new Vector3d(cuboid.Max.X, cuboid.Max.Y, cuboid.Max.Z);
                    this.B = new Vector3d(cuboid.Max.X, cuboid.Min.Y, cuboid.Max.Z);
                    this.C = new Vector3d(cuboid.Max.X, cuboid.Min.Y, cuboid.Min.Z);
                    this.D = new Vector3d(cuboid.Max.X, cuboid.Max.Y, cuboid.Min.Z);
                    break;

                case NORTH:
                    this.A = new Vector3d(cuboid.Max.X, cuboid.Max.Y, cuboid.Min.Z);
                    this.B = new Vector3d(cuboid.Max.X, cuboid.Min.Y, cuboid.Min.Z);
                    this.C = new Vector3d(cuboid.Min.X, cuboid.Min.Y, cuboid.Min.Z);
                    this.D = new Vector3d(cuboid.Min.X, cuboid.Max.Y, cuboid.Min.Z);
                    break;

                case SOUTH:
                    this.A = new Vector3d(cuboid.Min.X, cuboid.Max.Y, cuboid.Max.Z);
                    this.B = new Vector3d(cuboid.Min.X, cuboid.Min.Y, cuboid.Max.Z);
                    this.C = new Vector3d(cuboid.Max.X, cuboid.Min.Y, cuboid.Max.Z);
                    this.D = new Vector3d(cuboid.Max.X, cuboid.Max.Y, cuboid.Max.Z);
                    break;
            }
        }

        public Vector3d getVertexByIndex(final int index) {

            switch (index) {

                case 0:
                    return this.A;

                case 1:
                    return this.B;

                case 2:
                    return this.C;

                case 3:
                    return this.D;
                /*
                case 0:
                    return this.C;

                case 1:
                    return this.D;

                case 2:
                    return this.A;

                case 3:
                    return this.B;
                    */
            }

            throw new IllegalArgumentException("Invalid vertex index");
        }

        public Vector3f getNormal() {

            final Vec3i n = this.FACING.getNormal();

            return new Vector3f(n.getX(), n.getY(), n.getZ());
        }

        @Override
        public boolean equals(final Object other) {

            if (other instanceof Face) {

                Face f = (Face)other;

                return this.FACING == f.FACING && this.A.equals(f.A) && this.B.equals(f.B) && this.C.equals(f.C) && this.D.equals(f.D);
            }

            return false;
        }

        @Override
        public String toString() {
            return String.format("Cuboid.Face [%s]: %s, %s, %s, %s", this.FACING.getName(), this.A.toString(),
                    this.B.toString(), this.C.toString(), this.D.toString());
        }
    }

    private Cuboid() {
        this.Min = this.Max = Vector3d.ZERO;
    }
}
