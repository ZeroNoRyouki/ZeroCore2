/*
 *
 * AbstractGaugeBar.java
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

import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.lib.client.gui.IOrientationAware;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.Orientation;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.DoubleData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.FloatData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IntData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.LongData;

public abstract class AbstractGaugeBar
        extends AbstractControl
        implements IOrientationAware {

    public AbstractGaugeBar(final ModContainerScreen<? extends ModContainer> gui, final String name, final double maxValue) {

        super(gui, name);
        this._maxValue = maxValue;
        this._orientation = Orientation.BottomToTop;
    }

    public double getMaxValue() {
        return this._maxValue;
    }

    public void setMaxValue(final double value) {
        this._maxValue = value;
    }

    public void bindMaxValue(DoubleData bindableValue) {
        bindableValue.bind(this::setMaxValue);
    }

    public void bindMaxValue(FloatData bindableValue) {
        bindableValue.bind(this::setMaxValue);
    }

    public void bindMaxValue(LongData bindableValue) {
        bindableValue.bind(this::setMaxValue);
    }

    public void bindMaxValue(IntData bindableValue) {
        bindableValue.bind(this::setMaxValue);
    }

    public void setOverlay(final ISprite overlay) {
        this._overlay = overlay;
    }

    //region IOrientationAware

    @Override
    public Orientation getOrientation() {
        return this._orientation;
    }

    @Override
    public void setOrientation(final Orientation orientation) {
        this._orientation = orientation;
    }

    //endregion
    //region AbstractControl

    @Override
    public void onPaintOverlay(final MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {

        super.onPaintOverlay(matrix, partialTicks, mouseX, mouseY);

        if (null != this._overlay) {
            this.paintSprite(matrix, this._overlay, 0, 0);
        }
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" max:")
                .append(this._maxValue);
    }

    //endregion
    //region internals

    private double _maxValue;
    private ISprite _overlay;
    private Orientation _orientation;

    //endregion
}
