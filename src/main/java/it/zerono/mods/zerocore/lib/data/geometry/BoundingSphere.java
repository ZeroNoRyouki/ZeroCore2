/*
 *
 * BoundingSphere.java
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

import net.minecraft.util.math.vector.Vector3i;

public class BoundingSphere {

    public static BoundingSphere of(final double minX, final double minY, final double minZ,
                                    final double maxX, final double maxY, final double maxZ) {

        final Vector3d center = new Vector3d(
                (minX + maxX) / 2.0,
                (minY + maxY) / 2.0,
                (minZ + maxZ) / 2.0);

        final double radius = Math.sqrt(
                (maxX - center.X) * (maxX - center.X) +
                (maxY - center.Y) * (maxY - center.Y) +
                (maxZ - center.Z) * (maxZ - center.Z)
        );

        return new BoundingSphere(center, radius);
    }

    public static BoundingSphere of(final Vector3d min, final Vector3d max) {
        return of(min.X, min.Y, min.Z, max.X, max.Y, max.Z);
    }

    public static BoundingSphere of(final Vector3i min, final Vector3i max) {
        return of(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public Vector3d getCenter() {
        return this._center;
    }

    public double getRadius() {
        return this._radius;
    }

    //region Object

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return String.format("%s  r%f", this.getCenter(), this.getRadius());
    }

    //endregion
    //region internals

    protected BoundingSphere(final Vector3d center, final double radius) {

        this._center = center;
        this._radius = radius;
    }

    private final Vector3d _center;
    private final double _radius;

    //endregion
}
