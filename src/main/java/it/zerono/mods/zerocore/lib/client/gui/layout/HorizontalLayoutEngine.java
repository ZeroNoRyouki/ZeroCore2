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

import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.IControlContainer;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;

public class HorizontalLayoutEngine
        extends AbstractLayoutEngine<HorizontalLayoutEngine> {

    @Override
    public void layout(final IControlContainer controlsContainer) {

        final Rectangle parentBounds = controlsContainer.getBounds();
        final int undefined = this.computeUndefinedDimensionSize(controlsContainer, DesiredDimension.Width, parentBounds.Width);

        int left = this.getHorizontalMargin();

        for (final IControl control : controlsContainer) {

            final int controlWidth = Math.min(parentBounds.Width - left, this.getControlDesiredDimension(control, DesiredDimension.Width, undefined));
            final Rectangle newBounds = this.getControlAlignedBounds(control, left, this.getVerticalMargin(),
                    controlWidth, parentBounds.Height - this.getVerticalMargin() * 2);

            control.setBounds(newBounds);

            left += controlWidth + this.getControlsSpacing();
        }
    }
}
