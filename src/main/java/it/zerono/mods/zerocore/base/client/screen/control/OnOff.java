package it.zerono.mods.zerocore.base.client.screen.control;
/*
 * OnOff
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

import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.AbstractCompositeControl;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchButton;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class OnOff
        extends AbstractCompositeControl {

    public OnOff(final ModContainerScreen<? extends ModContainer> gui,
                 final Supplier<Boolean> activeStateSupplier, final Consumer<SwitchButton> activeStateChangedCallback,
                 final ITextComponent onTooltip, final ITextComponent offTooltip) {

        super(gui, "onoff");
        this.setDesiredDimension(50, 16);

        this._on = new SwitchButton(gui, "on", "ON", false, "onoff");
        this._on.Activated.subscribe(activeStateChangedCallback);
        this._on.Deactivated.subscribe(activeStateChangedCallback);
        this._on.setTooltips(onTooltip);
        gui.addDataBinding(activeStateSupplier, this._on::setActive);

        this._off = new SwitchButton(gui, "off", "OFF", true, "onoff");
        this._off.setTooltips(offTooltip);
        gui.addDataBinding(activeStateSupplier, active -> this._off.setActive(!active));

        this.addChildControl(this._on, this._off);
    }

    //region AbstractCompositeControl

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);
        this._on.setBounds(new Rectangle(0, 0, 25, 16));
        this._off.setBounds(new Rectangle(25, 0, 25, 16));
    }

    //endregion
    //region internals

    protected final SwitchButton _on;
    protected final SwitchButton _off;

    //endregion
}
