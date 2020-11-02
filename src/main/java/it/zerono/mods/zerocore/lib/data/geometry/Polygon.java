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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.BiFunction;
import java.util.function.IntFunction;

public class Polygon {

    public static final Polygon EMPTY = new Polygon();

    public Polygon() {
        this(MIN_COORDINATES);
    }

    public Polygon(int pointCount) {

        if (pointCount < MIN_COORDINATES) {
            pointCount = MIN_COORDINATES;
        }

        this._xs = new IntArrayList(pointCount);
        this._ys = new IntArrayList(pointCount);
        this._pointsCount = 0;
    }

    public Polygon(final int... points) {

        if (0 != points.length % 2 || points.length < MIN_COORDINATES * 2) {
            throw new IllegalArgumentException("Not enough coordinates");
        }

        this._xs = new IntArrayList(points.length / 2);
        this._ys = new IntArrayList(points.length / 2);

        for (int idx = 0; idx < points.length; idx += 2) {

            this._xs.add(points[idx]);
            this._ys.add(points[idx + 1]);
        }

        this._pointsCount = this._xs.size();
    }

    public Polygon(final Polygon other) {
        this(other._xs.elements(), other._ys.elements());
    }

    public static Polygon syncDataFrom(CompoundNBT data) {

        if (data.contains("xs") && data.contains("ys")) {

            final int[] xs = data.getIntArray("xs");
            final int[] ys = data.getIntArray("ys");

            if (xs.length > 0 && xs.length == ys.length) {
                return new Polygon(xs, ys);
            }
        }

        return EMPTY;
    }

    public CompoundNBT syncDataTo(CompoundNBT data) {

        data.putIntArray("xs", this._xs.elements());
        data.putIntArray("ys", this._ys.elements());
        return data;
    }

    public Polygon addPoint(final int x, final int y) {

        final int[] xs = new int[this._pointsCount + 1];
        final int[] ys = new int[this._pointsCount + 1];

        System.arraycopy(this._xs.elements(), 0, xs, 0, this._pointsCount);
        System.arraycopy(this._ys.elements(), 0, ys, 0, this._pointsCount);

        xs[this._pointsCount] = x;
        ys[this._pointsCount] = y;

        return new Polygon(xs, ys);
    }

    public int getX(final int vertexIndex) {
        return this._xs.getInt(vertexIndex);
    }

    public int getY(final int vertexIndex) {
        return this._ys.getInt(vertexIndex);
    }

    /**
     * @return the number of points that define this polygon
     */
    public int count() {
        return this._pointsCount;
    }

    public boolean isEmpty() {
        return 0 == this._pointsCount;
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

            polygon.x(idx, polygon.getX(idx) + offsetX);
            polygon.y(idx, polygon.getY(idx) + offsetY);
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

    public boolean contains(final int x, final int y) {

        if (!this.getBounds().contains(x, y)) {
            return false;
        }

        boolean result = false;
        int i;
        int j;

        for (i = 0, j = this.count() - 1; i < this.count(); j = i++) {

            final int yi = this.getY(i);
            final int yj = this.getY(j);
            final int xi = this.getX(i);

            if ((yi > y) != (yj > y) && (x < (this.getX(j) - xi) * (y - yi) / (yj - yi) + xi)) {
                result = !result;
            }
        }

        return result;
    }

    //region Object

    @Override
    public boolean equals(Object other) {

        if (other instanceof Polygon) {

            final Polygon polygon = (Polygon)other;

            return this._pointsCount == polygon._pointsCount && this._xs.equals(polygon._xs) && this._ys.equals(polygon._ys);
        }

        return false;
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

        this._xs = new IntArrayList(xs);
        this._ys = new IntArrayList(ys);
        this._pointsCount = this._xs.size();
    }

    private void x(final int index, final int value) {
        this._xs.set(index, value);
    }

    private void y(final int index, final int value) {
        this._ys.set(index, value);
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

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private static final int MIN_COORDINATES = 3;

    private final IntArrayList _xs;
    private final IntArrayList _ys;
    private final int _pointsCount;

    private Rectangle _bounds;

    //endregion
}
