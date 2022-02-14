/*
 *
 * Polygon.java
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

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

public class Polygon {

    public static final Polygon EMPTY = new Polygon();

    public Polygon() {
        this(new int[MIN_COORDINATES], new int[MIN_COORDINATES]);
    }

    public Polygon(int pointCount) {
        this(new int[pointCount], new int[pointCount]);
    }

    public Polygon(final int... points) {

        if (0 != points.length % 2 || points.length < MIN_COORDINATES * 2) {
            throw new IllegalArgumentException("Not enough coordinates");
        }

        final int length = points.length / 2;
        final int[] xs = new int[length];
        final int[] ys = new int[length];

        for (int pointsIdx = 0, targetIdx = 0; pointsIdx < points.length; pointsIdx += 2, ++targetIdx) {

            xs[targetIdx] = points[pointsIdx];
            ys[targetIdx] = points[pointsIdx + 1];
        }

        this._xs = xs;
        this._ys = ys;
        this._pointsCount = length;

        this._constants = new int[length];
        this._multipliers = new int[length];
        this.computeConstants();
    }

    public Polygon(final Polygon other) {
        this(other._xs, other._ys);
    }

    public static Polygon syncDataFrom(CompoundTag data) {

        if (data.contains("xs") && data.contains("ys")) {

            final int[] xs = data.getIntArray("xs");
            final int[] ys = data.getIntArray("ys");

            if (xs.length > 0 && xs.length == ys.length) {
                return new Polygon(xs, ys);
            }
        }

        return EMPTY;
    }

    public CompoundTag syncDataTo(CompoundTag data) {

        data.putIntArray("xs", this._xs);
        data.putIntArray("ys", this._ys);
        return data;
    }

    public Polygon addPoint(final int x, final int y) {

        final int[] xs = new int[this._pointsCount + 1];
        final int[] ys = new int[this._pointsCount + 1];

        System.arraycopy(this._xs, 0, xs, 0, this._pointsCount);
        System.arraycopy(this._ys, 0, ys, 0, this._pointsCount);

        xs[this._pointsCount] = x;
        ys[this._pointsCount] = y;

        return new Polygon(xs, ys);
    }

    public int getX(final int vertexIndex) {
        return this._xs[vertexIndex];
    }

    public int getY(final int vertexIndex) {
        return this._ys[vertexIndex];
    }

    /**
     * @return the number of points that define this polygon
     */
    public int count() {
        return this._pointsCount;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public Rectangle getBounds() {

        if (this.isEmpty()) {
            return Rectangle.ZERO;
        }

        if (null == this._bounds) {
            this._bounds = this.computeBounds();
        }

        return this._bounds;
    }

    public Polygon offset(final int offsetX, final int offsetY) {

        if (this.isEmpty()) {
            return EMPTY;
        }

        final Polygon polygon = new Polygon(this);

        for (int idx = 0; idx < polygon.count(); ++idx) {

            polygon._xs[idx] = polygon._xs[idx] + offsetX;
            polygon._ys[idx] = polygon._ys[idx] + offsetY;
        }

        return polygon;
    }

    public Polygon offset(final int... offsets) {

        if (this.isEmpty()) {
            return EMPTY;
        }

        if (offsets.length < this.count() * 2) {
            throw new IllegalArgumentException("Not enough offsets");
        }

        final int[] xs = new int[this.count()];
        final int[] ys = new int[this.count()];
        int offsetIdx = 0;

        for (int idx = 0; idx < this.count(); ++idx, offsetIdx += 2) {

            xs[idx] = this.getX(idx) + offsets[offsetIdx];
            ys[idx] = this.getY(idx) + offsets[offsetIdx + 1];
        }

        return new Polygon(xs, ys);
    }

    public Polygon transform(final IntFunction<Integer> xMapper, final IntFunction<Integer> yMapper) {

        if (this.isEmpty()) {
            return EMPTY;
        }

        final int[] xs = new int[this.count()];
        final int[] ys = new int[this.count()];

        for (int idx = 0; idx < this.count(); ++idx) {

            xs[idx] = xMapper.apply(this.getX(idx));
            ys[idx] = yMapper.apply(this.getY(idx));
        }

        return new Polygon(xs, ys);
    }

    public Polygon transform(final BiFunction<Integer, Integer, Point> mapper) {

        if (this.isEmpty()) {
            return EMPTY;
        }

        final int[] xs = new int[this.count()];
        final int[] ys = new int[this.count()];

        for (int idx = 0; idx < this.count(); ++idx) {

            final Point p = mapper.apply(this.getX(idx), this.getY(idx));

            xs[idx] = p.X;
            ys[idx] = p.Y;
        }

        return new Polygon(xs, ys);
    }

    /**
     * Return true if the provided point is inside the polygon
     *
     * @implNote the result is undefined if the point lay on a edge
     *
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @return true if the point is inside the polygon, false otherwise
     */
    public boolean contains(final int x, final int y) {

        boolean oddNodes = false;
        boolean current = this._ys[this._pointsCount - 1] > y;
        boolean previous;

        for (int i = 0; i < this._pointsCount; ++i) {

            previous = current;
            current = this._ys[i] > y;

            if (current != previous) {
                oddNodes ^= y * this._multipliers[i] + this._constants[i] < x;
            }
        }

        return oddNodes;
    }

    //region Object

    @Override
    public boolean equals(Object other) {

        if (other instanceof Polygon) {

            final Polygon polygon = (Polygon)other;

            return this._pointsCount == polygon._pointsCount &&
                    Arrays.equals(this._xs, polygon._xs) &&
                    Arrays.equals(this._ys, polygon._ys);
        }

        return false;
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(this._pointsCount);

        result = 31 * result + Arrays.hashCode(this._xs);
        result = 31 * result + Arrays.hashCode(this._ys);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Polygon [%d]", this.count());
    }

    //endregion
    //region internals

    private Polygon(final int[] xs, final int[] ys) {

        if (xs.length <= 0 || xs.length != ys.length) {
            throw new IllegalArgumentException("Invalid coordinates");
        }

        final int length = xs.length;

        this._xs = Arrays.copyOf(xs, length);
        this._ys = Arrays.copyOf(ys, length);
        this._pointsCount = length;

        this._constants = new int[length];
        this._multipliers = new int[length];
        this.computeConstants();
    }

    private void computeConstants() {

        final int[] xs = this._xs;
        final int[] ys = this._ys;
        int i, j = this._pointsCount - 1;

        for (i = 0; i < this._pointsCount; i++) {

            if (ys[j] == ys[i]) {

                this._constants[i] = xs[i];
                this._multipliers[i] = 0;

            } else {

                this._constants[i] = xs[i] - (ys[i] * xs[j]) / (ys[j] - ys[i]) + (ys[i] * xs[i]) / (ys[j] - ys[i]);
                this._multipliers[i] = (xs[j] - xs[i]) / (ys[j] - ys[i]);
            }

            j = i;
        }
    }

    private Rectangle computeBounds() {

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int idx = 0; idx < this.count(); ++idx) {

            int x = this.getX(idx);
            int y = this.getY(idx);

            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private static final int MIN_COORDINATES = 3;

    private final int[] _xs;
    private final int[] _ys;
    private final int[] _constants;
    private final int[] _multipliers;
    private final int _pointsCount;

    private Rectangle _bounds;

    //endregion
}
