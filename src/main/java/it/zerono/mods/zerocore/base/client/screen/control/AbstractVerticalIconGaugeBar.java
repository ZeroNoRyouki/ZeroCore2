/*
 *
 * AbstractVerticalIconGaugeBar.java
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

import it.zerono.mods.zerocore.base.client.screen.BaseIcons;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.Orientation;
import it.zerono.mods.zerocore.lib.client.gui.control.AbstractCompositeControl;
import it.zerono.mods.zerocore.lib.client.gui.control.AbstractGaugeBar;
import it.zerono.mods.zerocore.lib.client.gui.control.Picture;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.DoubleData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.FloatData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IntData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.LongData;
import net.minecraftforge.common.util.NonNullSupplier;

public abstract class AbstractVerticalIconGaugeBar<Bar extends AbstractGaugeBar>
        extends AbstractCompositeControl {

    protected AbstractVerticalIconGaugeBar(final ModContainerScreen<? extends ModContainer> gui, final String name,
                                           final Bar bar, final NonNullSupplier<ISprite> iconSprite) {

        super(gui, name);
        this.setDesiredDimension(18, 84);

        this._bar = bar;
        this._bar.setOrientation(Orientation.BottomToTop);
        this._bar.setDesiredDimension(18, 66);
        this._bar.setBackground(BaseIcons.BarBackground.get());
        this._bar.setPadding(1);
        this._icon = CommonPanels.icon(gui, iconSprite);
        this.addChildControl(this._icon, this._bar);
    }

    public void bindMaxValue(DoubleData bindableValue) {
        this._bar.bindMaxValue(bindableValue);
    }

    public void bindMaxValue(FloatData bindableValue) {
        this._bar.bindMaxValue(bindableValue);
    }

    public void bindMaxValue(LongData bindableValue) {
        this._bar.bindMaxValue(bindableValue);
    }

    public void bindMaxValue(IntData bindableValue) {
        this._bar.bindMaxValue(bindableValue);
    }

    //region AbstractCompositeControl

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);
        this._icon.setBounds(new Rectangle(1, 0, 16, 16));
        this._bar.setBounds(new Rectangle(0, 18, 18, 66));
    }

    //endregion
    //region internals

    protected final Bar _bar;
    protected final Picture _icon;

    //endregion
}
