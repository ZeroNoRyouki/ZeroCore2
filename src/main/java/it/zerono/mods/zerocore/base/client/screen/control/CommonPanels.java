/*
 *
 * VerticalSeparatorPanel.java
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

package it.zerono.mods.zerocore.base.client.screen.control;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.base.client.screen.BaseIcons;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.Picture;
import it.zerono.mods.zerocore.lib.client.gui.control.Static;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.Util;
import net.minecraftforge.common.util.NonNullSupplier;

public class CommonPanels {

    public final static int STANDARD_PANEL_HEIGHT = 84;

    public static Panel empty(ModContainerScreen<? extends ModContainer> gui, int width, int height) {

        Preconditions.checkArgument(width > 0, "Width must be greater than zero.");
        Preconditions.checkArgument(height > 0, "Height must be greater than zero.");

        return Util.make(new Panel(gui), p -> p.setDesiredDimension(width, height));
    }

    public static Panel empty(ModContainerScreen<? extends ModContainer> gui, int width) {
        return empty(gui, width, STANDARD_PANEL_HEIGHT);
    }

    public static Panel verticalSeparator(ModContainerScreen<? extends ModContainer> gui, int height) {

        Preconditions.checkArgument(height > 0, "Height must be greater than zero.");

        final Static s = new Static(gui, 1, height);

        s.setColor(Colour.BLACK);
        s.setLayoutEngineHint(FixedLayoutEngine.hint(5, 0, 1, height));

        final Panel p = new Panel(gui);

        p.setDesiredDimension(11, height);
        p.setLayoutEngine(new FixedLayoutEngine());
        p.addControl(s);

        return p;
    }

    public static Panel verticalSeparator(ModContainerScreen<? extends ModContainer> gui) {
        return verticalSeparator(gui, STANDARD_PANEL_HEIGHT);
    }

    public static Panel horizontalSeparator(ModContainerScreen<? extends ModContainer> gui, int width) {

        Preconditions.checkArgument(width > 0, "Width must be greater than zero.");

        final Panel p = new Panel(gui);

        p.setDesiredDimension(width, 1);
        p.setLayoutEngine(new FixedLayoutEngine());
        p.setBackground(Colour.BLACK);
        return p;
    }

    public static Panel verticalTemperatureScale(ModContainerScreen<? extends ModContainer> gui, int height) {

        Preconditions.checkArgument(height > 0, "Height must be greater than zero.");

        final Picture pic = new Picture(gui, gui.nextGenericName(), BaseIcons.TemperatureScale, 5, 59);

        pic.setLayoutEngineHint(FixedLayoutEngine.hint(3, 23, 5, 59));

        final Panel p = new Panel(gui);

        p.setDesiredDimension(11, height);
        p.setLayoutEngine(new FixedLayoutEngine());
        p.addControl(pic);

        return p;
    }

    public static Panel verticalTemperatureScale(ModContainerScreen<? extends ModContainer> gui) {
        return verticalTemperatureScale(gui, CommonPanels.STANDARD_PANEL_HEIGHT);
    }

    public static Panel verticalCommandPanel(ModContainerScreen<? extends ModContainer> gui, int width, int height) {

        Preconditions.checkArgument(height > 0, "Height must be greater than zero.");

        final Panel p = new Panel(gui);

        p.setDesiredDimension(width, height);
        p.setLayoutEngine(new FixedLayoutEngine());

        return p;
    }

    public static Panel verticalCommandPanel(ModContainerScreen<? extends ModContainer> gui, int width) {
        return verticalCommandPanel(gui, width, STANDARD_PANEL_HEIGHT);
    }

    public static Panel verticalCommandPanel(ModContainerScreen<? extends ModContainer> gui) {
        return verticalCommandPanel(gui, -1, STANDARD_PANEL_HEIGHT);
    }

    public static Picture icon(ModContainerScreen<? extends ModContainer> gui, NonNullSupplier<ISprite> icon) {
        return Util.make(new Picture(gui, "icn", icon), p -> p.setDesiredDimension(16, 16));
    }
}
