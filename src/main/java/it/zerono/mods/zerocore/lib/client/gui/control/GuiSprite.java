/*
 *
 * GuiSprite.java
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

package it.zerono.mods.zerocore.lib.client.gui.control;

import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;
import java.util.Optional;

public enum GuiSprite
        implements ISprite {

    RADIOBUTTON_NORMAL(0, 0),
    RADIOBUTTON_ACTIVE(16, 0),
    RADIOBUTTON_NORMAL_HIGHLIGHTED(32, 0),
    RADIOBUTTON_ACTIVE_HIGHLIGHTED(48, 0),
    RADIOBUTTON_NORMAL_DISABLED(64, 0),
    RADIOBUTTON_ACTIVE_DISABLED(80, 0),

    CHECKBOX_NORMAL(0, 16),
    CHECKBOX_ACTIVE(16, 16),
    CHECKBOX_NORMAL_HIGHLIGHTED(32, 16),
    CHECKBOX_ACTIVE_HIGHLIGHTED(48, 16),
    CHECKBOX_NORMAL_DISABLED(64, 16),
    CHECKBOX_ACTIVE_DISABLED(80, 16),
    ;

    GuiSprite(final int u, final int v) {
        this(16, 16, u, v, null);
    }

    @SuppressWarnings("unused")
    GuiSprite(final int width, final int height, final int u, final int v) {
        this(width, height, u, v, null);
    }

    GuiSprite(final int width, final int height, final int u, final int v,
              @Nullable final ISprite overlay) {

        this._width = width;
        this._height = height;
        this._u = u;
        this._v = v;
        this._overlay = overlay;

        final float textureMapWidth = this.getTextureMap().getWidth();
        final float textureMapHeight = this.getTextureMap().getHeight();

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
    public int getWidth() {
        return this._width;
    }

    @Override
    public int getHeight() {
        return this._height;
    }

    @Override
    public ISpriteTextureMap getTextureMap() {
        return TEXTURE_MAP;
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
        return this;
    }

    @Override
    public ISprite copyWith(ISprite overlay) {
        return this;
    }

    //endregion
    //region internals

    private final int _width;
    private final int _height;
    private final int _u;
    private final int _v;
    private final float _minU, _maxU, _minV, _maxV;
    private final ISprite _overlay;

    private static final ISpriteTextureMap TEXTURE_MAP = new SpriteTextureMap(ZeroCore.newID("textures/gui/gui_sprites.png"), 128, 128);

    //endregion
}
