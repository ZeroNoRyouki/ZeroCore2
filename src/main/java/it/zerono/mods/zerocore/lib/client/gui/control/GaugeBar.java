/*
 *
 * GaugeBar.java
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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

public class GaugeBar
        extends AbstractGaugeBar {

    public GaugeBar(ModContainerScreen<? extends ModContainer> gui, String name, double maxValue, ISprite barSprite) {

        super(gui, name, maxValue);
        this._value = 0;
        this._barSprite = Preconditions.checkNotNull(barSprite);
        this._barSpriteTint = Colour.WHITE;
    }

    public double getValue() {
        return this._value;
    }

    public void setValue(final double value) {
        this._value = Mth.clamp(value, 0, this.getMaxValue());
    }

    public void setBarSprite(final ISprite barSprite) {
        this._barSprite = Preconditions.checkNotNull(barSprite);
    }

    public void setBarSpriteTint(final Colour tint) {
        this._barSpriteTint = tint;
    }

    //region AbstractGaugeBar

    @Override
    public void onPaint(final GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {

        super.onPaint(gfx, partialTicks, mouseX, mouseY);

        final Rectangle area = this.getPaddingRect();

        ModRenderHelper.paintOrientedProgressBarSprite(gfx, this.getOrientation(), this._barSprite,
                this.controlToScreen(area.Origin.X, area.Origin.Y), (int)this.getZLevel(), area, this.getFillRatio(),
                this._barSpriteTint);
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" value:")
                .append(this._value);
    }

    //endregion
    //region internals

    protected double getFillRatio() {
        return this.getValue() / this.getMaxValue();
    }

    private double _value;
    @Nonnull
    private ISprite _barSprite;
    private Colour _barSpriteTint;

    //endregion
}
