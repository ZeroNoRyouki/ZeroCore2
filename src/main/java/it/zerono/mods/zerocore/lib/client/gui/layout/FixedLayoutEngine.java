/*
 *
 * FixedLayoutEngine.java
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


@SuppressWarnings("unused")
public class FixedLayoutEngine
        extends AbstractLayoutEngine<FixedLayoutEngine> {

    public static ILayoutEngineHint hint(final int x, final int y, final int width, final int height) {
        return new FixedLayoutHint(x, y, width, height);
    }

    public static ILayoutEngineHint hint(final int x, final int y) {
        return new FixedLayoutHint(x, y, DesiredDimension.UNDEFINED_VALUE, DesiredDimension.UNDEFINED_VALUE);
    }

    @Override
    public void layout(final IControlContainer container) {
        container.forEach(control -> control.getLayoutEngineHint()
                .filter(hint -> hint instanceof FixedLayoutHint)
                .map(hint -> (FixedLayoutHint) hint)
                .ifPresent(fixedHint -> control.setBounds(new Rectangle(fixedHint.X, fixedHint.Y,
                        fixedHint.getDimension(DesiredDimension.Width, control),
                        fixedHint.getDimension(DesiredDimension.Height, control)))
                ));
    }

    private static class FixedLayoutHint implements ILayoutEngineHint {
        
        FixedLayoutHint(final int x, final int y, final int width, final int height) {

            this.X = x;
            this.Y = y;
            this.Width = width;
            this.Height = height;
        }

        int getDimension(final DesiredDimension dimension, final IControl control) {

            final int value = DesiredDimension.Width == dimension ? this.Width : this.Height;

            return DesiredDimension.UNDEFINED_VALUE != value ? value : control.getDesiredDimension(dimension);
        }

        private final int X;
        private final int Y;
        private final int Width;
        private final int Height;
    }
}
