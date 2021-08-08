/*
 *
 * SpriteTextureMap.java
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

import it.zerono.mods.zerocore.ZeroCore;
import net.minecraft.resources.ResourceLocation;

public class SpriteTextureMap implements ISpriteTextureMap {

    public static final ISpriteTextureMap EMPTY = new SpriteTextureMap(ZeroCore.newID("textures/empty.png"), 16, 16);

    public SpriteTextureMap(final ResourceLocation textureLocation, final int width, final int height) {

        this._texture = textureLocation;
        this._width = width;
        this._height = height;
    }

    //region ISpriteTextureMap

    @Override
    public ResourceLocation getTextureLocation() {
        return this._texture;
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
    public ISpriteBuilder sprite() {
        return new SpriteBuilder(this);
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
        return String.format("%s - %d x %d", this.getTextureLocation(),
                this.getWidth(), this.getHeight());
    }

    //endregion
    //region internals

    private static class SpriteBuilder implements ISpriteBuilder {

        SpriteBuilder(final ISpriteTextureMap map) {

            this._map = map;
            this._u = this._v = 0;
            this._width = this._height = 16;
            this._overlay = null;
        }

        //region ISpriteBuilder

        @Override
        public ISpriteBuilder from(final int u, final int v) {

            this._u = u;
            this._v = v;
            return this;
        }

        @Override
        public ISpriteBuilder from(ISprite sprite) {

            this._u = sprite.getU();
            this._v = sprite.getV();
            this._width = sprite.getWidth();
            this._height = sprite.getHeight();
            this._overlay = sprite.getSpriteOverlay().orElse(null);
            this._map = sprite.getTextureMap();

            return this;
        }

        @Override
        public ISpriteBuilder ofSize(final int width, final int height) {

            this._width = width;
            this._height = height;
            return this;
        }

        @Override
        public ISpriteBuilder with(final ISprite overlay) {

            this._overlay = overlay;
            return this;
        }

        @Override
        public ISprite build() {
            return new Sprite(this._width, this._height, this._map, this._u, this._v, this._overlay);
        }

        //endregion

        private ISpriteTextureMap _map;
        private int _u;
        private int _v;
        private int _width;
        private int _height;
        private ISprite _overlay;
    }

    private final ResourceLocation _texture;
    private final int _width;
    private final int _height;

    //endregion
}
