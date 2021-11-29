/*
 *
 * Point.java
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

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class Point {

    public static final Point ZERO = new Point();

    public final int X;
    public final int Y;

    public Point(final int x, final int y) {

        this.X = x;
        this.Y = y;
    }

    public Point(final Point other) {
        this(other.X, other.Y);
    }

    public static Point syncDataFrom(CompoundTag data) {

        if (data.contains("px") && data.contains("py")) {
            return new Point(data.getInt("px"), data.getInt("py"));
        }

        return ZERO;
    }

    public CompoundTag syncDataTo(CompoundTag data) {

        data.putInt("px", this.X);
        data.putInt("py", this.Y);
        return data;
    }

    public Point offset(final int offsetX, final int offsetY) {
        return new Point(this.X + offsetX, this.Y + offsetY);
    }

    public Point offset(final Point offset) {
        return new Point(this.X + offset.X, this.Y + offset.Y);
    }

    public boolean collinear(final Direction.Axis axis, final int n1, final int n2) {

        switch (axis) {

            case X:
                return n1 <= this.X && this.X <= n2;

            case Y:
                return n1 <= this.Y && this.Y <= n2;

            default:
                return false;
        }
    }

    public boolean collinear(final Point p1, final Point p2) {
        return 0 == this.X * (p1.Y - p2.Y) + p1.X * (p2.Y - this.Y) + p2.X * (this.Y - p1.Y);
    }

    //region Object

    @Override
    public boolean equals(Object other) {

        if (other instanceof Point) {

            final Point p = (Point)other;

            return this.X == p.X && this.Y == p.Y;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.X, this.Y);
    }

    @Override
    public String toString() {
        return String.format("Point (%d, %d)", this.X, this.Y);
    }

    //endregion
    //region internals

    private Point() {
        this.X = this.Y = 0;
    }

    //endregion
}
