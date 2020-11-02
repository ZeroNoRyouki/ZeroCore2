/*
 *
 * Sprite.java
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

package it.zerono.mods.zerocore.lib.client.gui.sprite;

import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
public class Sprite implements ISprite {

    public static final ISprite EMPTY = SpriteTextureMap.EMPTY.sprite().build();

    public Sprite(final ISpriteTextureMap textureMap) {
        this(16, 16, textureMap, 0, 0, null);
    }

    public Sprite(final int width, final int height, final ISpriteTextureMap textureMap) {
        this(width, height, textureMap, 0, 0, null);
    }

    public Sprite(final int width, final int height, final ISpriteTextureMap textureMap, final int u, final int v) {
        this(width, height, textureMap, u, v, null);
    }

    public Sprite(final int width, final int height, final ISpriteTextureMap textureMap, @Nullable final ISprite overlay) {
        this(width, height, textureMap, 0, 0, overlay);
    }

    public Sprite(final int width, final int height, final ISpriteTextureMap textureMap,
                  final int u, final int v, @Nullable final ISprite overlay) {

        this._width = width;
        this._height = height;
        this._map = textureMap;
        this._u = u;
        this._v = v;
        this._overlay = overlay;

        final float textureMapWidth = textureMap.getWidth();
        final float textureMapHeight = textureMap.getHeight();

        this._minU = (float)this._u / textureMapWidth;
        this._maxU = (float)(this._u + this._width) / textureMapWidth;
        this._minV = (float)this._v / textureMapHeight;
        this._maxV = (float)(this._v + this._height) / textureMapHeight;
    }

    //region ISprite

    @Override
    public int getU() {
        return this._u;
    }

    @Override
    public int getV() {
        return this._v;
    }

    @Override
    public int getWidth() {
        return this._width;
    }

    @Override
    public int getHeight() {
        return this._height;
    }

    @Override
    public float getMinU() {
        return this._minU;
    }

    @Override
    public float getMaxU() {
        return this._maxU;
    }

    @Override
    public float getMinV() {
        return this._minV;
    }

    @Override
    public float getMaxV() {
        return this._maxV;
    }

    @Override
    public ISpriteTextureMap getTextureMap() {
        return this._map;
    }

    @Override
    public Optional<ISprite> getSpriteOverlay() {
        return Optional.ofNullable(this._overlay);
    }

    @Override
    public void applyOverlay(final NonNullConsumer<ISprite> overlayConsumer) {

        if (null != this._overlay) {
            overlayConsumer.accept(this._overlay);
        }
    }

    @Override
    public ISprite copy() {
        return new Sprite(this);
    }

    @Override
    public ISprite copyWith(ISprite overlay) {
        return new Sprite(this._width, this._height, this._map,
                this._u, this._v, Objects.requireNonNull(overlay));
    }

    //endregion
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
        return String.format("%d,%d - %d x %d", this.getU(), this.getV(),
                this.getWidth(), this.getHeight());
    }

    //endregion
    //region internals

    protected Sprite(final ISprite other) {

        this._width = other.getWidth();
        this._height = other.getHeight();
        this._map = other.getTextureMap();
        this._u = other.getU();
        this._v = other.getV();
        this._minU = other.getMinU();
        this._maxU = other.getMaxU();
        this._minV = other.getMinV();
        this._maxV = other.getMaxV();
        this._overlay = other.getSpriteOverlay().orElse(null);
    }

    private final int _width;
    private final int _height;
    private final ISpriteTextureMap _map;
    private final int _u;
    private final int _v;
    private final float _minU, _maxU, _minV, _maxV;
    private final ISprite _overlay;

    //endregion
}
