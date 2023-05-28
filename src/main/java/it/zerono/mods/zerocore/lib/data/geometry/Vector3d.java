/*
 *
 * Vector3d.java
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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Objects;

@SuppressWarnings({"WeakerAccess"})
public class Vector3d
    implements Position {

    public static final Vector3d ZERO = new Vector3d();
    public static final Vector3d HALF = new Vector3d(0.5, 0.5, 0.5);

    public static final Vector3d XN = new Vector3d(-1, 0, 0);
    public static final Vector3d XP = new Vector3d(1, 0, 0);
    public static final Vector3d YN = new Vector3d(0, -1, 0);
    public static final Vector3d YP = new Vector3d(0, 1, 0);
    public static final Vector3d ZN = new Vector3d(0, 0, -1);
    public static final Vector3d ZP = new Vector3d(0, 0, 1);

    public final double X;
    public final double Y;
    public final double Z;

    public Vector3d(final double x, final double y, final double z) {

        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public Vector3d(final Vector3d other) {
        this(other.X, other.Y, other.Z);
    }

    public Vector3d(final Vec3i other) {
        this(other.getX(), other.getY(), other.getZ());
    }

    public static Vector3d from(final Vec3i data) {
        return new Vector3d(data.getX(), data.getY(), data.getZ());
    }

    public static Vector3d from(final Vector3f data) {
        return new Vector3d(data.x(), data.y(), data.z());
    }

    public static Vector3d from(final Vector4f data) {
        return new Vector3d(data.x(), data.y(), data.z());
    }

    public static Vector3d fromCenter(final Vec3i data) {
        return new Vector3d(data.getX() + 0.5, data.getY() + 0.5, data.getZ() + 0.5);
    }

    public static Vector3d from(final Entity data) {
        return new Vector3d(data.getX(), data.getY(), data.getZ());
    }

    public static Vector3d fromCenter(final Entity data) {
        return new Vector3d(data.getX() + 0.5, data.getY() + 0.5, data.getZ() + 0.5);
    }

    public static Vector3d from(final BlockEntity data) {
        return from(data.getBlockPos());
    }

    public static Vector3d fromCenter(final BlockEntity data) {
        return fromCenter(data.getBlockPos());
    }

    public static Vector3d syncDataFrom(CompoundTag data) {
        return new Vector3d(data.getDouble("vx"), data.getDouble("vy"), data.getDouble("vz"));
    }

    public CompoundTag syncDataTo(CompoundTag data) {

        data.putDouble("vx", this.X);
        data.putDouble("vy", this.Y);
        data.putDouble("vz", this.Z);
        return data;
    }

    public Vec3i toVec3i() {
        return new Vec3i(this.X, this.Y, this.Z);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.X, this.Y, this.Z);
    }

    public Vector3d add(final double offsetX, final double offsetY, final double offsetZ) {
        return new Vector3d(this.X + offsetX, this.Y + offsetY, this.Z + offsetZ);
    }

    public Vector3d add(final double offset) {
        return this.add(offset, offset, offset);
    }

    public Vector3d add(final Vector3d v) {
        return this.add(v.X, v.Y, v.Z);
    }

    public Vector3d add(final Vec3i v) {
        return this.add(v.getX(), v.getY(), v.getZ());
    }

    public Vector3d subtract(final double offsetX, final double offsetY, final double offsetZ) {
        return this.add(-offsetX, -offsetY, -offsetZ);
    }

    public Vector3d subtract(final double offset) {
        return this.subtract(offset, offset, offset);
    }

    public Vector3d subtract(final Vec3i v) {
        return this.subtract(v.getX(), v.getY(), v.getZ());
    }

    public Vector3d subtract(final Vector3d v) {
        return this.subtract(v.X, v.Y, v.Z);
    }

    public Vector3d multiply(final double factorX, final double factorY, final double factorZ) {
        return new Vector3d(this.X * factorX, this.Y * factorY, this.Z * factorZ);
    }

    public Vector3d multiply(final double factor) {
        return this.multiply(factor, factor, factor);
    }

    public Vector3d multiply(final Vec3i factor) {
        return this.multiply(factor.getX(), factor.getY(), factor.getZ());
    }

    public Vector3d multiply(final Vector3d v) {
        return this.multiply(v.x(), v.y(), v.z());
    }

    public Vector3d divide(final double factorX, final double factorY, final double factorZ) {
        return new Vector3d(this.X / factorX, this.Y / factorY, this.Z / factorZ);
    }

    public Vector3d divide(final double factor) {
        return this.divide(factor, factor, factor);
    }

    public Vector3d divide(final Vec3i factor) {
        return this.divide(factor.getX(), factor.getY(), factor.getZ());
    }

    public Vector3d ceil() {
        return new Vector3d(Mth.ceil(this.X), Mth.ceil(this.Y), Mth.ceil(this.Z));
    }

    public Vector3d floor() {
        return new Vector3d(Mth.floor(this.X), Mth.floor(this.Y), Mth.floor(this.Z));
    }

    public Vector3d crossProduct(final Vector3d vec) {
        return new Vector3d(this.Y * vec.Z - this.Z * vec.Y, this.Z * vec.X - this.X * vec.Z, this.X * vec.Y - this.Y * vec.X);
    }

    public double magnitude() {
        return Math.sqrt(this.X * this.X + this.Y * this.Y + this.Z * this.Z);
    }

    public Vector3d normalize() {

        final double magnitude = this.magnitude();

        if (0 != magnitude) {
            return this.multiply(1.0 / magnitude);
        }

        return this;
    }

    public double scalarProduct(final double x, final double y, final double z) {
        return this.X * x + this.Y * y + this.Z * z;
    }

    public double scalarProduct(final Vector3d vector) {

        double product = this.X * vector.X + this.Y * vector.Y + this.Z * vector.Z;

        if (product > 1.0 && product < 1.00001) {
            product = 1.0;
        } else if (product < -1 && product > -1.00001) {
            product = -1.0;
        }

        return product;
    }

    //region IPosition

    @Override
    public double x() {
        return this.X;
    }

    @Override
    public double y() {
        return this.Y;
    }

    @Override
    public double z() {
        return this.Z;
    }

    //endregion
    //region Object

    @Override
    public boolean equals(Object other) {

        if (other instanceof Vector3d) {

            Vector3d v = (Vector3d)other;

            return this.X == v.X && this.Y == v.Y && this.Z == v.Z;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.X, this.Y, this.Z);
    }

    @Override
    public String toString() {
        return String.format("Vector3d (%f, %f, %f)", this.X, this.Y, this.Z);
    }

    //endregion
    //region internals

    private Vector3d() {
        this.X = this.Y = this.Z = 0;
    }

    //endregion
}
