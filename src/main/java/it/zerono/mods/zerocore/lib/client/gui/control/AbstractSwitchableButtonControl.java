/*
 *
 * AbstractSwitchableButtonControl.java
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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.IWindow;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractSwitchableButtonControl
        extends AbstractButtonControl {

    public AbstractSwitchableButtonControl(final ModContainerScreen<? extends ModContainer> gui, final String name,
                                           final String text, final boolean active) {
        this(gui, name, text, active, null);
    }

    public AbstractSwitchableButtonControl(final ModContainerScreen<? extends ModContainer> gui, final String name,
                                           final String text, final boolean active, @Nullable final String groupName) {

        super(gui, name, text);
        this._active = active; // don't call setActive() here as we don't want to raise the events on construction

        if (Strings.isNullOrEmpty(groupName)) {

            this._grouped = false;
            this._buttonGroupNotify = (b) -> {};

        } else {

            this._grouped = true;
            this._buttonGroupNotify = ButtonGroup.linkToGroup(groupName, this);
        }
    }

    public boolean getActive() {
        return this._active;
    }

    public void setActive(final boolean active) {

        if (this._active == active) {
            return;
        }

        this._active = active;

        if (active) {

            this._buttonGroupNotify.accept(this);
            this.onActivated();

        } else {

            this.onDeactivated();
        }
    }

    public void bindActive(IBindableData<Boolean> bindableValue) {
        bindableValue.bind(this::setActive);
    }

    protected abstract void onActivated();

    protected abstract void onDeactivated();

    //region AbstractButtonControl

    /**
     * Event handler - the user has clicked on this control or on one of it's children
     *
     * @param mouseX the mouse X
     * @param mouseY the mouse Y
     * @param clickedButton the mouse button clicked
     * @return true if the event was handled, false otherwise
     */
    @Override
    public boolean onMouseClicked(final IWindow wnd, int mouseX, int mouseY, int clickedButton) {

        super.onMouseClicked(wnd, mouseX, mouseY, clickedButton);

        if (this._grouped) {

            // if this button belong to a button group don't allow it to de-activate itself

            if (!this.getActive()) {
                this.setActive(true);
            }

        } else {

            this.setActive(!this.getActive());
        }

        return true;
    }

    @Override
    protected ButtonState getButtonState() {

        if (!this.getEnabled()) {
            return this.getActive() ? ButtonState.ActiveDisabled : ButtonState.DefaultDisabled;
        } else if (this.getActive()) {
            return this.getMouseOver() ? ButtonState.ActiveHighlighted : ButtonState.Active;
        } else {
            return this.getMouseOver() ? ButtonState.DefaultHighlighted : ButtonState.Default;
        }
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" active:")
                .append(this._active)
                .append(" grouped:")
                .append(this._grouped);
    }

    //endregion
    //region internals
    //region buttons group

    static class ButtonGroup {

        public static Consumer<AbstractSwitchableButtonControl> linkToGroup(final String groupName,
                                                                            final AbstractSwitchableButtonControl button) {

            final ButtonGroup group = s_buttonGroups.computeIfAbsent(button.getGui(), gui -> {

                gui.Close.subscribe(() -> {

                    final Map<String, ButtonGroup> removed = s_buttonGroups.remove(gui);

                    removed.values().forEach(ButtonGroup::clear);
                    removed.clear();
                });

                return Maps.newHashMap();

            }).computeIfAbsent(groupName, k -> new ButtonGroup());

            group.addButton(button);

            return group::onActivated;
        }

        public ButtonGroup() {
            this._buttons = Lists.newArrayList();
        }

        public void onActivated(final AbstractSwitchableButtonControl selectedButton) {

            this._buttons.stream()
                    .filter(button -> selectedButton != button)
                    .forEach(button -> button.setActive(false));
        }

        void addButton(final AbstractSwitchableButtonControl button) {
            this._buttons.add(button);
        }

        void clear() {
            this._buttons.clear();
        }

        private final List<AbstractSwitchableButtonControl> _buttons;

        private static final Map<ModContainerScreen<? extends ModContainer>,
                Map<String, ButtonGroup>> s_buttonGroups = Maps.newHashMap();
    }

    //endregion

    private boolean _active;
    private final boolean _grouped;
    private final Consumer<AbstractSwitchableButtonControl> _buttonGroupNotify;

    //endregion
}
