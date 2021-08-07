/*
 *
 * Vector3i.java
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

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;

public class Vector3i {

    public static final Vector3i ZERO = new Vector3i();

    public final int X;
    public final int Y;
    public final int Z;

    public Vector3i(final int x, final int y, final int z) {

        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public Vector3i(final Vector3i other) {
        this(other.X, other.Y, other.Z);
    }

    public static Vector3i from(final net.minecraft.util.math.vector.Vector3i data) {
        return new Vector3i(data.getX(), data.getY(), data.getZ());
    }

    public static Vector3i from(final Vector4f data) {
        return new Vector3i((int)data.x(), (int)data.y(), (int)data.z());
    }

    public static Vector3i from(final Entity data) {
        return new Vector3i((int)data.getX(), (int)data.getY(), (int)data.getZ());
    }

    public static Vector3i from(final TileEntity data) {
        return from(data.getBlockPos());
    }

    public static Vector3i syncDataFrom(CompoundNBT data) {
        return new Vector3i(data.getInt("vx"), data.getInt("vy"), data.getInt("vz"));
    }

    public CompoundNBT syncDataTo(CompoundNBT data) {

        data.putInt("vx", this.X);
        data.putInt("vy", this.Y);
        data.putInt("vz", this.Z);
        return data;
    }

    public net.minecraft.util.math.vector.Vector3i toVec3i() {
        return new net.minecraft.util.math.vector.Vector3i(this.X, this.Y, this.Z);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.X, this.Y, this.Z);
    }

    public Vector3i add(final int offsetX, final int offsetY, final int offsetZ) {
        return new Vector3i(this.X + offsetX, this.Y + offsetY, this.Z + offsetZ);
    }

    public Vector3i add(final int offset) {
        return this.add(offset, offset, offset);
    }

    public Vector3i add(final Vector3d offset) {
        return this.add((int)offset.X, (int)offset.Y, (int)offset.X);
    }

    public Vector3i add(final net.minecraft.util.math.vector.Vector3i offset) {
        return this.add(offset.getX(), offset.getY(), offset.getZ());
    }

    public Vector3i subtract(final int offsetX, final int offsetY, final int offsetZ) {
        return new Vector3i(this.X - offsetX, this.Y - offsetY, this.Z - offsetZ);
    }

    public Vector3i subtract(final int offset) {
        return this.subtract(offset, offset, offset);
    }

    public Vector3i subtract(final Vector3i offset) {
        return this.subtract(offset.X, offset.Y, offset.Z);
    }

    public Vector3i multiply(final int factorX, final int factorY, final int factorZ) {
        return new Vector3i(this.X * factorX, this.Y * factorY, this.Z * factorZ);
    }

    public Vector3i multiply(final double factorX, final double factorY, final double factorZ) {
        return new Vector3i((int)(this.X * factorX), (int)(this.Y * factorY), (int)(this.Z * factorZ));
    }

    public Vector3i multiply(final int factor) {
        return this.multiply(factor, factor, factor);
    }

    public Vector3i multiply(final double factor) {
        return this.multiply(factor, factor, factor);
    }

    public Vector3i multiply(final Vector3i factor) {
        return this.multiply(factor.X, factor.Y, factor.Z);
    }

    public Vector3i divide(final int factorX, final int factorY, final int factorZ) {
        return new Vector3i(this.X / factorX, this.Y / factorY, this.Z / factorZ);
    }

    public Vector3i divide(final double factorX, final double factorY, final double factorZ) {
        return new Vector3i((int)(this.X / factorX), (int)(this.Y / factorY), (int)(this.Z / factorZ));
    }

    public Vector3i divide(final int factor) {
        return this.divide(factor, factor, factor);
    }

    public Vector3i divide(final double factor) {
        return this.divide(factor, factor, factor);
    }

    public Vector3i divide(final net.minecraft.util.math.vector.Vector3i factor) {
        return this.divide(factor.getX(), factor.getY(), factor.getZ());
    }

    public Vector3i ceil() {
        return new Vector3i(MathHelper.ceil(this.X), MathHelper.ceil(this.Y), MathHelper.ceil(this.Z));
    }

    public Vector3i floor() {
        return new Vector3i(MathHelper.floor(this.X), MathHelper.floor(this.Y), MathHelper.floor(this.Z));
    }

    public Vector3i crossProduct(final Vector3i vec) {
        return new Vector3i(this.Y * vec.Z - this.Z * vec.Y, this.Z * vec.X - this.X * vec.Z, this.X * vec.Y - this.Y * vec.X);
    }

    public double magnitude() {
        return Math.sqrt(this.X * this.X + this.Y * this.Y + this.Z * this.Z);
    }

    public Vector3i normalize() {

        final double magnitude = this.magnitude();

        if (0 != magnitude) {
            return this.multiply(1.0 / magnitude);
        }

        return this;
    }

    public int scalarProduct(final int x, final int y, final int z) {
        return this.X * x + this.Y * y + this.Z * z;
    }

    //region Object

    @Override
    public boolean equals(final Object other) {

        if (other instanceof Vector3i) {

            Vector3i v = (Vector3i)other;

            return this.X == v.X && this.Y == v.Y && this.Z == v.Z;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("Vector3i (%d, %d, %d)", this.X, this.Y, this.Z);
    }

    //endregion
    //region internals

    private Vector3i() {
        this.X = this.Y = this.Z = 0;
    }

    //endregion
}
