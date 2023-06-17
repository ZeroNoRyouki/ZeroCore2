/*
 *
 * MultiValueGaugeBar.java
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
import it.zerono.mods.zerocore.lib.data.EnumIndexedArray;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.Optional;
import java.util.function.Supplier;

public class MultiValueGaugeBar<Index extends Enum<Index>>
        extends AbstractGaugeBar {

    @SafeVarargs
    public MultiValueGaugeBar(ModContainerScreen<? extends ModContainer> gui, String name, double maxValue,
                              Index firstValidIndex, Index secondValidIndex, Index... otherValidIndices) {

        super(gui, name, maxValue);
        this._values = new EnumIndexedArray<>(Double[]::new, firstValidIndex, secondValidIndex, otherValidIndices);
        this._sprites = new EnumIndexedArray<>(ISprite[]::new, firstValidIndex, secondValidIndex, otherValidIndices);
        this._tints = new EnumIndexedArray<>(Colour[]::new, firstValidIndex, secondValidIndex, otherValidIndices);
        this._values.setAll(0.0);
    }

    public double getValue(final Index index) {
        return this._values.getElement(index).orElse(0.0);
    }

    public void setValue(final Index index, final double value) {
        this._values.setElement(index, Mth.clamp(value, 0, this.getMaxValueFor(index)));
    }

    public void setBarSprite(final Index index, final ISprite sprite) {
        this._sprites.setElement(index, Preconditions.checkNotNull(sprite));
    }

    public void setBarSprite(final Index index, final Supplier<ISprite> sprite) {
        this.setBarSprite(index, sprite.get());
    }

    public void setBarSpriteTint(final Index index, final Colour tint) {
        this._tints.setElement(index, tint);
    }

    //region AbstractGaugeBar

    @Override
    public void onPaint(final GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {

        super.onPaint(gfx, partialTicks, mouseX, mouseY);

        final Rectangle area = this.getPaddingRect();
        int skip = 0;

        for (final Index index: this._values.getValidIndices()) {
            skip += this.paintValueRect(gfx, index, area, skip);
        }
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" indices:")
                .append(this._values.getValidIndices());
    }

    //endregion
    //region internals

    protected double getFillRatio() {
        return this._values.stream()
                .mapToDouble(Double::doubleValue)
                .sum() / this.getMaxValue();
    }

    protected double getFillRatio(final Index index) {
        return this.getValue(index) / this.getMaxValue();
    }

    protected double getMaxValueFor(final Index index) {
        return this.getMaxValue();
    }

    protected Optional<ISprite> getSprite(final Index index) {
        return this._sprites.getElement(index);
    }

    protected Optional<Colour> getTint(final Index index) {
        return this._tints.getElement(index);
    }

    protected Colour getTintOrDefault(final Index index) {
        return this._tints.getElement(index, Colour.WHITE);
    }

    protected int paintValueRect(final GuiGraphics gfx, final Index index, final Rectangle rect, final int skip) {

        final double progress = this.getFillRatio(index);

        if (progress < 0.01) {
            return 0;
        }

        return this.getSprite(index)
                .map(sprite -> this.paintValueRect(gfx, sprite, this.getTintOrDefault(index), rect, skip, progress))
                .orElse(0);
    }

    private int paintValueRect(GuiGraphics gfx, ISprite sprite, Colour tint, Rectangle rect, int skip, double progress) {

        final var orientation = this.getOrientation();
        final var origin = this.controlToScreen(rect.Origin.X, rect.Origin.Y);
        final int x, y, areaWidth, areaHeight, filled;

        switch (orientation) {

            default:
            case BottomToTop:
                x = origin.X;
                y = origin.Y - skip;
                areaWidth = rect.Width;
                areaHeight = rect.Height;
                filled = (int)(areaHeight * progress);
                break;

            case TopToBottom:
                x = origin.X;
                y = origin.Y + skip;
                areaWidth = rect.Width;
                areaHeight = rect.Height;
                filled = (int)(areaHeight * progress);
                break;

            case LeftToRight:
                x = origin.X + skip;
                y = origin.Y;
                areaWidth = rect.Width;
                areaHeight = rect.Height;
                filled = (int)(areaWidth * progress);
                break;

            case RightToLeft:
                x = origin.X - skip;
                y = origin.Y;
                areaWidth = rect.Width;
                areaHeight = rect.Height;
                filled = (int)(areaWidth * progress);
                break;
        }

        ModRenderHelper.paintOrientedProgressBarSprite(gfx, orientation, sprite, x, y, (int)this.getZLevel(),
                areaWidth, areaHeight, progress, tint);

        return filled;
    }

    private final EnumIndexedArray<Index, Double> _values;
    private final EnumIndexedArray<Index, ISprite> _sprites;
    private final EnumIndexedArray<Index, Colour> _tints;

    //endregion
}
