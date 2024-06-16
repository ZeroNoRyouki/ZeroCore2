/*
 *
 * HorizontalLayoutEngine.java
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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.IControlContainer;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;

import java.util.List;

public class HorizontalLayoutEngine
        extends AbstractLayoutEngine<HorizontalLayoutEngine> {

    @Override
    public void layout(final IControlContainer controlsContainer) {

        final Rectangle parentBounds = controlsContainer.getBounds();
        final int defaultWidth = this.computeDefaultValueForUndefinedDimension(controlsContainer,
                DesiredDimension.Width, parentBounds.Width);

        final int spacing = this.getControlsSpacing();
        final int verticalMargin = this.getVerticalMargin();
        final int maxHeight = parentBounds.Height - 2 * verticalMargin;

        final List<ControlLayoutPosition> positions = new ObjectArrayList<>(controlsContainer.getControlsCount());
        int availableSpace = parentBounds.Width - (2 * this.getHorizontalMargin()) -
                ((controlsContainer.getControlsCount() - 1) * spacing);

        for (final IControl control : controlsContainer) {

            final int controlWidth = Math.min(availableSpace, this.getControlDesiredDimension(control,
                    DesiredDimension.Width, defaultWidth));

            positions.add(this.computeLayoutPosition(control, 0, verticalMargin, controlWidth, maxHeight));
            availableSpace -= controlWidth;
        }

        int left = switch (this.getHorizontalAlignment()) {
            case Left -> 0;
            case Right -> availableSpace;
            case Center -> availableSpace / 2;
        };

        for (final var position : positions) {

            position.layoutAtX(left);
            left += position.with() + spacing;
        }
    }
}
