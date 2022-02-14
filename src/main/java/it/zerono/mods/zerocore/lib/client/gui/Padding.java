/*
 *
 * Padding.java
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

package it.zerono.mods.zerocore.lib.client.gui;

import com.google.common.collect.Maps;

import java.lang.ref.SoftReference;
import java.util.Map;

public final class Padding {

    public static final Padding ZERO;

    public static Padding get(int left, int right, int top, int bottom) {

        left = clamp(left);
        right = clamp(right);
        top = clamp(top);
        bottom = clamp(bottom);

        final int hash = pack(left, right, top, bottom);
        SoftReference<Padding> ref = Padding.s_cache.get(hash);
        Padding padding;

        if (null == ref || null == (padding = ref.get())) {

            padding = new Padding(left, right, top, bottom);
            ref = new SoftReference<>(padding);
            Padding.s_cache.put(hash, ref);
        }

        return padding;
    }

    public int getLeft() {
        return this._left;
    }

    public int getRight() {
        return this._right;
    }

    public int getTop() {
        return this._top;
    }

    public int getBottom() {
        return this._bottom;
    }

    public int getHorizontal() {
        return this._left + this._right;
    }

    public int getVertical() {
        return this._top + this._bottom;
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
        return String.format("[%d - %d - %d - %d]",  this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
    }

    //endregion
    //region internals

    private Padding(final int left, final int right, final int top, final int bottom) {

        this._left = clamp(left);
        this._right = clamp(right);
        this._top = clamp(top);
        this._bottom = clamp(bottom);
    }

    private static int clamp(final int value) {
        return Math.max(0, Math.min(100, value));
    }

    private static int pack(final int byte1, final int byte2, final int byte3, final int byte4) {
        return ((byte1 << 24) | (byte2 << 16) | (byte3 << 8) | (byte4));
    }

    private final int _left;
    private final int _right;
    private final int _top;
    private final int _bottom;

    private static final Map<Integer, SoftReference<Padding>> s_cache;

    static {

        s_cache = Maps.newHashMap();
        ZERO = Padding.get(0, 0, 0, 0);
    }

    //endregion
}
