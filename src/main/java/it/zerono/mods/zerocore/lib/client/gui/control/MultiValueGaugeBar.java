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
import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.EnumIndexedArray;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

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
        this._values.setElement(index, MathHelper.clamp(value, 0, this.getMaxValueFor(index)));
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
    public void onPaint(final MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {

        super.onPaint(matrix, partialTicks, mouseX, mouseY);

        final Rectangle area = this.getPaddingRect();
        int skip = 0;

        if (Direction.Plane.HORIZONTAL == area.getLayout()) {

            for (final Index index: this._values.getValidIndices()) {
                skip += this.paintValueRect(matrix, index, area, skip);
            }

        } else {

            for (final Index index: this._values.getValidIndices()) {
                skip += this.paintValueRect(matrix, index, area, skip);
            }
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

    protected int paintValueRect(final MatrixStack matrix, final Index index, final Rectangle rect, final int skip) {
        return this.getSprite(index)
                .map(sprite -> ModRenderHelper.paintVerticalProgressSprite(matrix, sprite,
                        this.getTint(index).orElse(Colour.WHITE), this.controlToScreen(rect.Origin.X, rect.Origin.Y),
                        (int)this.getZLevel(), rect, skip, this.getFillRatio(index)))
                .orElse(0);
    }

    private final EnumIndexedArray<Index, Double> _values;
    private final EnumIndexedArray<Index, ISprite> _sprites;
    private final EnumIndexedArray<Index, Colour> _tints;

    //endregion
}
