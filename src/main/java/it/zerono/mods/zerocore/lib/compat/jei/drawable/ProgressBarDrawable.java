/*
 *
 * ProgressBarDrawable.java
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

import it.zerono.mods.zerocore.lib.client.gui.Orientation;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.common.util.NonNullSupplier;

public class ProgressBarDrawable
        extends SpriteDrawable {

    public static ProgressBarDrawable empty() {
        return new ProgressBarDrawable(() -> Sprite.EMPTY, 0, Padding.ZERO, 0, 0, Orientation.BottomToTop) {

            @Override
            public void draw(final GuiGraphics gfx, final int xOffset, final int yOffset) {
            }

            @Override
            public void draw(final GuiGraphics gfx, final int xOffset, final int yOffset, final int maskTop,
                             final int maskBottom, final int maskLeft, final int maskRight) {
            }
        };
    }

    public void setProgress(final double progress) {
        this._progress = progress;
    }

    public void setProgress(final double maxValue, final double currentValue) {
        this._progress = currentValue / maxValue;
    }

    public double getProgress() {
        return this._progress;
    }

    public void setTint(final Colour tint) {
        this._tint = tint;
    }

    public ProgressBarDrawable(final NonNullSupplier<ISprite> sprite, final int zLevel, final Padding padding,
                               final int areaWidth, final int areaHeight, final Orientation orientation) {

        super(sprite, zLevel, padding);
        this._tint = Colour.WHITE;
        this._areaWidth = areaWidth;
        this._areaHeight = areaHeight;
        this._orientation = orientation;
        this._progress = 0.0d;
    }

    //region SpriteDrawable

    @Override
    public void draw(final GuiGraphics gfx, final int xOffset, final int yOffset) {
        ModRenderHelper.paintOrientedProgressBarSprite(gfx, this._orientation, this.getSprite(),
                xOffset + this.getPadding().getLeft(), yOffset + this.getPadding().getTop(), this.getZLevel(),
                this._areaWidth, this._areaHeight, this.getProgress(), this._tint);
    }

    @Override
    public void draw(final GuiGraphics gfx, final int xOffset, final int yOffset, final int maskTop,
                     final int maskBottom, final int maskLeft, final int maskRight) {
        ModRenderHelper.paintOrientedProgressBarSprite(gfx, this._orientation, this.getSprite(),
                xOffset + this.getPadding().getLeft() + maskLeft, yOffset + this.getPadding().getTop() + maskTop,
                this.getZLevel(), this._areaWidth - maskRight - maskLeft, this._areaHeight - maskBottom - maskTop,
                this.getProgress(), this._tint);
    }

    //endregion
    //region internals

    protected final int _areaWidth;
    protected final int _areaHeight;
    protected final Orientation _orientation;
    protected Colour _tint;
    protected double _progress;

    //endregion
}
