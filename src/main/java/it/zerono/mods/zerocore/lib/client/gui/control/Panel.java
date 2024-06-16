/*
 *
 * Panel.java
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

import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.layout.*;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Objects;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class Panel
        extends AbstractControlContainer {

    public Panel(final ModContainerScreen<? extends ModContainer> gui) {
        this(gui, gui.nextGenericName());
    }

    public Panel(final ModContainerScreen<? extends ModContainer> gui, final String name) {
        super(gui, name);
    }

    public Panel(final ModContainerScreen<? extends ModContainer> gui, final String name, ILayoutEngine layoutEngine) {

        super(gui, name);
        this.setLayoutEngine(layoutEngine);
    }

    public void setCustomBackgroundPainter(final BiConsumer<AbstractControl, GuiGraphics> painter) {
        this.setCustomBackgroundHandler(Objects.requireNonNull(painter));
    }

    //region standard Panels

    public static Panel verticalStrip(final ModContainerScreen<? extends ModContainer> gui, final int width,
                                      final int controlSpacing) {

        final Panel p = new Panel(gui);

        p.setDesiredDimension(DesiredDimension.Width, width);
        p.setLayoutEngine(new VerticalLayoutEngine()
                .setHorizontalAlignment(HorizontalAlignment.Center)
                .setZeroMargins()
                .setControlsSpacing(controlSpacing));

        return p;
    }

    public static Panel horizontalStrip(final ModContainerScreen<? extends ModContainer> gui, final int height,
                                        final int controlSpacing) {

        final Panel p = new Panel(gui);

        p.setDesiredDimension(DesiredDimension.Height, height);
        p.setLayoutEngine(new HorizontalLayoutEngine()
                .setVerticalAlignment(VerticalAlignment.Center)
                .setZeroMargins()
                .setControlsSpacing(controlSpacing));

        return p;
    }

    //endregion
}
