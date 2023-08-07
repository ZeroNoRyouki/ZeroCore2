/*
 *
 * BarsPanel.java
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
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.AbstractControlContainer;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;

public class BarsPanel
        extends AbstractControlContainer {

    public BarsPanel(ModContainerScreen<? extends ModContainer> gui, String name) {
        this(gui, name, CommonPanels.STANDARD_PANEL_HEIGHT);
    }

    public BarsPanel(ModContainerScreen<? extends ModContainer> gui, String name, int height) {

        super(gui, name);

        this.setBarsHeight(height);
        this.setDesiredDimension(DesiredDimension.Width, 0);
        this.setLayoutEngine(new HorizontalLayoutEngine()
                .setZeroMargins()
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));
    }

    public BarsPanel add(IControl control) {

        this.addControl(control);
        this.addWidth(control);
        return this;
    }

    public BarsPanel addTemperatureScale() {

        final IControl control = CommonPanels.verticalTemperatureScale(this.getGui(), this.getBarsHeight());

        this.addChildControl(control);
        this.addWidth(control);
        return this;
    }

    public BarsPanel addVerticalSeparator() {

        final IControl control = CommonPanels.verticalSeparator(this.getGui(), this.getBarsHeight());

        this.addChildControl(control);
        this.addWidth(control);
        return this;
    }

    public BarsPanel addEmptyPanel(int width) {

        this.addChildControl(CommonPanels.empty(this.getGui(), width, this.getBarsHeight()));
        this.addWidth(width);
        return this;
    }

    //region internals

    private int getBarsHeight() {
        return this.getDesiredDimension(DesiredDimension.Height);
    }

    private void setBarsHeight(int height) {

        Preconditions.checkArgument(height > 0, "Height must be greater than zero.");

        this.setDesiredDimension(DesiredDimension.Height, height);
    }

    private void addWidth(int width) {
        this.setDesiredDimension(DesiredDimension.Width, this.getDesiredDimension(DesiredDimension.Width) + width);
    }

    private void addWidth(IControl control) {
        this.addWidth(control.getDesiredDimension(DesiredDimension.Width));
    }

    //endregion
}
