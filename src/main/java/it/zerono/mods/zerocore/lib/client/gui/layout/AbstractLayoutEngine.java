/*
 *
 * AbstractLayoutEngine.java
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

package it.zerono.mods.zerocore.lib.client.gui.layout;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.IControlContainer;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AbstractLayoutEngine<E extends AbstractLayoutEngine<?>>
        implements ILayoutEngine {

    public int getControlsSpacing() {
        return this._controlsSpacing;
    }

    public E setControlsSpacing(final int spacing) {

        Preconditions.checkArgument(spacing >= 0, "The controls spacing must be equal or greater than zero");
        this._controlsSpacing = spacing;
        return this.self();
    }

    public int getHorizontalMargin() {
        return this._horizontalMargin;
    }

    public E setHorizontalMargin(final int horizontalMargin) {

        Preconditions.checkArgument(horizontalMargin >= 0, "The horizontal margin must be equal or greater than zero");
        this._horizontalMargin = horizontalMargin;
        return this.self();
    }

    public int getVerticalMargin() {
        return this._verticalMargin;
    }

    public E setVerticalMargin(final int verticalMargin) {

        Preconditions.checkArgument(verticalMargin >= 0, "The vertical margin must be equal or greater than zero");
        this._verticalMargin = verticalMargin;
        return this.self();
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return this._horizontalAlignment;
    }

    public E setHorizontalAlignment(final HorizontalAlignment horizontalAlignment) {

        this._horizontalAlignment = horizontalAlignment;
        return this.self();
    }

    public VerticalAlignment getVerticalAlignment() {
        return this._verticalAlignment;
    }

    public E setVerticalAlignment(final VerticalAlignment verticalAlignment) {

        this._verticalAlignment = verticalAlignment;
        return this.self();
    }

    public E setZeroMargins() {

        this.setHorizontalMargin(0);
        this.setVerticalMargin(0);
        this.setControlsSpacing(0);
        return this.self();
    }

    //region internals

    protected int getControlDesiredDimension(final IControl control, final DesiredDimension dimension,
                                             final int defaultValue) {

        final int value = control.getDesiredDimension(dimension);

        return DesiredDimension.UNDEFINED_VALUE == value ? defaultValue : value;
    }

    protected ControlLayoutPosition computeLayoutPosition(IControl control, int x, int y, int maxWidth, int maxHeight) {

        final int desiredWidth = Math.min(maxWidth, this.getControlDesiredDimension(control, DesiredDimension.Width, maxWidth));
        final int desiredHeight = Math.min(maxHeight, this.getControlDesiredDimension(control, DesiredDimension.Height, maxHeight));

        return new ControlLayoutPosition(control,
                this._horizontalAlignment.align(x, desiredWidth, maxWidth),
                this._verticalAlignment.align(y, desiredHeight, maxHeight),
                desiredWidth, desiredHeight);
    }

    protected int computeDefaultValueForUndefinedDimension(final IControlContainer controlsContainer,
                                                           final DesiredDimension dimension, final int availableSize) {

        int controlsCount = 0;
        int fixedCount = 0;
        int fixedSize = 0;

        for (final IControl control : controlsContainer) {

            int controlSize = control.getDesiredDimension(dimension);

            if (DesiredDimension.UNDEFINED_VALUE != controlSize) {

                fixedSize += controlSize;
                ++fixedCount;
            }

            ++controlsCount;
        }

        fixedSize += this.getControlsSpacing() * (controlsCount - 1);
        fixedSize += 2 * (DesiredDimension.Width == dimension ? this.getHorizontalMargin() : this.getVerticalMargin());

        int undefinedSize = 0;

        if (fixedCount < controlsCount) {

            undefinedSize = (availableSize - fixedSize) / (controlsCount - fixedCount);

            if (undefinedSize <= 0) {
                undefinedSize = 1;
            }
        }

        return undefinedSize;
    }

    private E self() {
        //noinspection unchecked
        return (E)this;
    }

    private int _controlsSpacing = 5;
    private int _horizontalMargin = 5;
    private HorizontalAlignment _horizontalAlignment = HorizontalAlignment.Center;
    private int _verticalMargin = 2;
    private VerticalAlignment _verticalAlignment = VerticalAlignment.Center;

    //endregion
}
