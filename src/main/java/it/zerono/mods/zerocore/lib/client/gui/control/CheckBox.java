/*
 *
 * CheckBox.java
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

import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;

import java.util.function.Consumer;

public class CheckBox
        extends AbstractSwitchableButtonControl {

    public final IEvent<Consumer<CheckBox>> Activated;
    public final IEvent<Consumer<CheckBox>> Deactivated;

    public CheckBox(final ModContainerScreen<? extends ModContainer> gui, final String name,
                    final String text, final boolean active) {

        super(gui, name, text, active);
        this.setIconForState(GuiSprite.CHECKBOX_NORMAL, ButtonState.Default);
        this.setIconForState(GuiSprite.CHECKBOX_ACTIVE, ButtonState.Active);
        this.setIconForState(GuiSprite.CHECKBOX_NORMAL_HIGHLIGHTED, ButtonState.DefaultHighlighted);
        this.setIconForState(GuiSprite.CHECKBOX_ACTIVE_HIGHLIGHTED, ButtonState.ActiveHighlighted);
        this.setIconForState(GuiSprite.CHECKBOX_NORMAL_DISABLED, ButtonState.DefaultDisabled);
        this.setIconForState(GuiSprite.CHECKBOX_ACTIVE_DISABLED, ButtonState.ActiveDisabled);

        this.Activated = new Event<>();
        this.Deactivated = new Event<>();
    }

    //region AbstractSwitchableButtonControl

    @Override
    public void onWindowClosed() {

        super.onWindowClosed();
        this.Activated.unsubscribeAll();
        this.Deactivated.unsubscribeAll();
    }

    @Override
    protected void onActivated() {
        this.Activated.raise(c -> c.accept(this));
    }

    @Override
    protected void onDeactivated() {
        this.Deactivated.raise(c -> c.accept(this));
    }

    //endregion
}
