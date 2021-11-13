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
import it.zerono.mods.zerocore.lib.client.gui.control.GaugeBar;
import it.zerono.mods.zerocore.lib.client.gui.control.Picture;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.function.Supplier;

public abstract class AbstractVerticalIconGaugeBar
        extends AbstractCompositeControl {

    protected AbstractVerticalIconGaugeBar(final ModContainerScreen<? extends ModContainer> gui, final String name,
                                           final double maxValue, final Supplier<Double> valueSupplier,
                                           final NonNullSupplier<ISprite> barSprite, final NonNullSupplier<ISprite> iconSprite) {

        super(gui, name);
        this.setDesiredDimension(18, 84);

        // gauge bar

        this._bar = new GaugeBar(gui, "bar", maxValue, barSprite.get());
        this._bar.setOrientation(Orientation.BottomToTop);
        this._bar.setDesiredDimension(18, 66);
        this._bar.setBackground(BaseIcons.BarBackground.get());
        this._bar.setPadding(1);

        gui.addDataBinding(valueSupplier, this._bar::setValue);

        // icon

        this._icon = new Picture(gui, "icn", iconSprite.get(), 16, 16);
        this._icon.useTooltipsFrom(this._bar);

        this.addChildControl(this._icon, this._bar);
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

    protected final GaugeBar _bar;
    protected final Picture _icon;

    //endregion
}
