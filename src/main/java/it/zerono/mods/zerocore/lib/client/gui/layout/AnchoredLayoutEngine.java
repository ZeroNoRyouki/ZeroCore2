package it.zerono.mods.zerocore.lib.client.gui.layout;
/*
 * AnchoredLayoutEngine
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
 * Do not remove or edit this header
 *
 */

import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.IControlContainer;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;

public class AnchoredLayoutEngine
        extends AbstractLayoutEngine<AnchoredLayoutEngine> {

    public enum Anchor
            implements ILayoutEngineHint {

        /**
         * Align the control on the left side of the parent and set its height equal to the parent height
         */
        Left,
        /**
         * Align the control on the right side of the parent and set its height equal to the parent height
         */
        Right,
        /**
         * Align the control on the top side of the parent and set its width equal to the parent width
         */
        Top,
        /**
         * Align the control on the bottom side of the parent and set its width equal to the parent width
         */
        Bottom,
        /**
         * Align the control to the top-left corner of the parent
         */
        TopLeft,
        /**
         * Align the control to the top-right corner of the parent
         */
        TopRight,
        /**
         * Align the control to the bottom-left corner of the parent
         */
        BottomLeft,
        /**
         * Align the control to the bottom-right corner of the parent
         */
        BottomRight,
        /**
         * Center the control in the parent area
         */
        Center,
    }

    @Override
    public void layout(final IControlContainer controlsContainer) {

        final Rectangle parentBounds = controlsContainer.getBounds();
        final int horizontalMargin = this.getHorizontalMargin();
        final int verticalMargin = this.getVerticalMargin();
        final int defaultWidth = this.computeDefaultValueForUndefinedDimension(controlsContainer, DesiredDimension.Width, parentBounds.Width);
        final int defaultHeight = this.computeDefaultValueForUndefinedDimension(controlsContainer, DesiredDimension.Height, parentBounds.Height);
        final int bandMaxWidth = parentBounds.Width - (horizontalMargin * 2);
        final int bandMaxHeight = parentBounds.Height - (verticalMargin * 2);

        for (final IControl control : controlsContainer) {

            final int controlWidth = Math.min(bandMaxWidth, this.getControlDesiredDimension(control, DesiredDimension.Width, defaultWidth));
            final int controlHeight = Math.min(bandMaxHeight, this.getControlDesiredDimension(control, DesiredDimension.Height, defaultHeight));
            final Rectangle newBounds;

            switch (this.getAnchorFrom(control)) {

                case Top:
                    newBounds = new Rectangle(horizontalMargin, verticalMargin, bandMaxWidth, controlHeight);
                    break;

                case Bottom:
                    newBounds = new Rectangle(horizontalMargin, parentBounds.Height - controlHeight - verticalMargin, bandMaxWidth, controlHeight);
                    break;

                case Left:
                    newBounds = new Rectangle(horizontalMargin, verticalMargin, controlWidth, bandMaxHeight);
                    break;

                case Right:
                    newBounds = new Rectangle(parentBounds.Width - controlWidth - horizontalMargin, verticalMargin, controlWidth, bandMaxHeight);
                    break;

                case TopLeft:
                    newBounds = new Rectangle(horizontalMargin, verticalMargin, controlWidth, controlHeight);
                    break;

                case TopRight:
                    newBounds = new Rectangle(parentBounds.Width - controlWidth - horizontalMargin, verticalMargin, controlWidth, controlHeight);
                    break;

                case BottomLeft:
                    newBounds = new Rectangle(horizontalMargin, parentBounds.Height - controlHeight - verticalMargin, controlWidth, controlHeight);
                    break;

                case BottomRight:
                    newBounds = new Rectangle(parentBounds.Width - controlWidth - horizontalMargin, parentBounds.Height - controlHeight - verticalMargin, controlWidth, controlHeight);
                    break;

                case Center:
                    newBounds = new Rectangle((bandMaxWidth - controlWidth) / 2, (bandMaxHeight - controlHeight) / 2, controlWidth, controlHeight);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown anchor");
            }

            control.setBounds(newBounds);
        }
    }

    //region internals

    private Anchor getAnchorFrom(final IControl control) {
        return control.getLayoutEngineHint()
                .filter(h -> h instanceof Anchor)
                .map(h -> (Anchor)h)
                .orElse(Anchor.TopLeft);
    }

    //endregion
}