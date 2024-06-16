/*
 *
 * SpriteDrawable.java
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

package it.zerono.mods.zerocore.lib.compat.jei.drawable;

import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SpriteDrawable
        implements IDrawableStatic {

    public SpriteDrawable(final Supplier<@NotNull ISprite> sprite, final int zLevel) {
        this(sprite, zLevel, Padding.ZERO);
    }

    public SpriteDrawable(final Supplier<@NotNull ISprite> sprite, final int zLevel, final Padding padding) {

        this._sprite = sprite;
        this._padding = padding;
        this._zLevel = zLevel;
    }

    public ISprite getSprite() {
        return this._sprite.get();
    }

    public Padding getPadding() {
        return this._padding;
    }

    public int getZLevel() {
        return this._zLevel;
    }

    //region IDrawableStatic

    @Override
    public int getWidth() {
        return this.getSprite().getWidth();
    }

    @Override
    public int getHeight() {
        return this.getSprite().getHeight();
    }

    @Override
    public void draw(final GuiGraphics gfx, final int xOffset, final int yOffset) {
        ModRenderHelper.paintSprite(gfx, this.getSprite(), xOffset + this.getPadding().getLeft(),
                yOffset + this.getPadding().getTop(), this._zLevel, this.getWidth(), this.getHeight());
    }

    @Override
    public void draw(final GuiGraphics gfx, final int xOffset, final int yOffset, final int maskTop,
                     final int maskBottom, final int maskLeft, final int maskRight) {
        ModRenderHelper.paintSprite(gfx, this.getSprite(), xOffset, yOffset, this._zLevel, this.getPadding(),
                this.getWidth(), this.getHeight(), maskTop, maskBottom, maskLeft, maskRight);
    }

    //endregion
    //region internals

    private final Supplier<@NotNull ISprite> _sprite;
    private final Padding _padding;
    private final int _zLevel;

    //endregion
}
