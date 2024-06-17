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

package it.zerono.mods.zerocore.base.client.screen.control;

import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.AbstractCompositeControl;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchButton;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class OnOff
        extends AbstractCompositeControl {

    public OnOff(final ModContainerScreen<? extends ModContainer> gui,
                 final IBindableData<Boolean> bindableState, final Consumer<SwitchButton> activeStateChangedCallback,
                 final Component onTooltip, final Component offTooltip) {
        this(gui, 25, 16, bindableState, activeStateChangedCallback, onTooltip, offTooltip);
    }

    public OnOff(final ModContainerScreen<? extends ModContainer> gui, int buttonWidth, int buttonHeight,
                 final IBindableData<Boolean> bindableState, final Consumer<SwitchButton> activeStateChangedCallback,
                 final Component onTooltip, final Component offTooltip) {

        super(gui, "onoff");
        this.setDesiredDimension(2 * buttonWidth, buttonHeight);

        this._on = new SwitchButton(gui, "on", "ON", false, "onoff");
        this._on.setDesiredDimension(buttonWidth, buttonHeight);
        this._on.Activated.subscribe(activeStateChangedCallback);
        this._on.Deactivated.subscribe(activeStateChangedCallback);
        this._on.setTooltips(onTooltip);

        this._off = new SwitchButton(gui, "off", "OFF", true, "onoff");
        this._off.setDesiredDimension(buttonWidth, buttonHeight);
        this._off.setTooltips(offTooltip);

        bindableState.bind(active -> {

            this._on.setActive(active);
            this._off.setActive(!active);
        });

        this.addChildControl(this._on, this._off);
    }

    //region AbstractCompositeControl

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);

        this._on.setBounds(new Rectangle(0, 0,
                this._on.getDesiredDimension(DesiredDimension.Width),
                this._on.getDesiredDimension(DesiredDimension.Height)));

        this._off.setBounds(new Rectangle(this._on.getDesiredDimension(DesiredDimension.Width), 0,
                this._off.getDesiredDimension(DesiredDimension.Width),
                this._off.getDesiredDimension(DesiredDimension.Height)));
    }

    //endregion
    //region internals

    protected final SwitchButton _on;
    protected final SwitchButton _off;

    //endregion
}
