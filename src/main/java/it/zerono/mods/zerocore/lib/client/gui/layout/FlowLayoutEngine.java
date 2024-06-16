/*
 *
 * FlowLayoutEngine.java
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

import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.IControlContainer;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;

/**
 * A {@link ILayoutEngine layout engine} that will place one {@link IControl control} after the other.
 *
 * <p>Each {@link IControl control} will be resized to their respective {@link IControl#setDesiredDimension(int, int) desired dimension}
 * but their maximum width will be constrained to the width of their container.</p>
 */
public class FlowLayoutEngine
        extends AbstractLayoutEngine<FlowLayoutEngine> {

    @Override
    public void layout(IControlContainer controlsContainer) {

        final Rectangle parentBounds = controlsContainer.getBounds();
        final int maxWidth = parentBounds.Width - 2 * this.getHorizontalMargin();
        int availableWidth, availableHeight, rowHeight = 0, left, top;

        availableHeight = parentBounds.Height - 2 * this.getVerticalMargin();
        availableWidth = maxWidth;
        left = 0;
        top = this.getVerticalMargin();

        for (final IControl control : controlsContainer) {

            int controlWidth = Math.min(maxWidth, this.getControlDesiredDimension(control, DesiredDimension.Width, maxWidth));

            if (controlWidth > availableWidth) {

                // move to the next row

                left = 0;
                top += rowHeight + this.getControlsSpacing();
                availableWidth = maxWidth;
                availableHeight -= rowHeight + this.getControlsSpacing();
                controlWidth = Math.min(availableWidth, this.getControlDesiredDimension(control, DesiredDimension.Width, availableWidth));
                rowHeight = 0;
            }

            final int controlHeight = Math.min(availableHeight, this.getControlDesiredDimension(control, DesiredDimension.Height, availableHeight));

            rowHeight = Math.max(rowHeight, controlHeight);

            control.setBounds(new Rectangle(this.getHorizontalMargin() + left, top, controlWidth, controlHeight));

            left += controlWidth + this.getControlsSpacing();
            availableWidth -= controlWidth + this.getControlsSpacing();
        }
    }
}
