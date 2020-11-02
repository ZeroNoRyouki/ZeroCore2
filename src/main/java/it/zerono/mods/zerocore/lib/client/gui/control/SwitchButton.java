/*
 *
 * SwitchButton.java
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

import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.lib.client.gui.IWindow;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public class SwitchButton
        extends AbstractSwitchableButtonControl {

    public final IEvent<Consumer<SwitchButton>> Activated;
    public final IEvent<Consumer<SwitchButton>> Deactivated;

    public SwitchButton(final ModContainerScreen<? extends ModContainer> gui, final String name, final String text,
                        final boolean active) {
        this(gui, name, text, active, null);
    }

    public SwitchButton(final ModContainerScreen<? extends ModContainer> gui, final String name, final String text,
                        final boolean active, @Nullable final String groupName) {

        super(gui, name, text, active, groupName);
        this.setPressed(false);
        this.setPadding(3, 3, 2, 2);
        this.setHorizontalAlignment(HorizontalAlignment.Center);
        this.setVerticalAlignment(VerticalAlignment.Center);

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
    public Padding getPadding() {
        return this.getPressed() ? this._pressedPadding : super.getPadding();
    }

    @Override
    public void setPadding(int left, int right, int top, int bottom) {

        super.setPadding(left, right, top, bottom);
        this._pressedPadding = Padding.get(left + 1, right - 1, top + 1, bottom - 1);
    }

    /**
     * Event handler - the user has clicked on this control or on one of it's children
     *
     * @param mouseX
     * @param mouseY
     * @param clickedButton the mouse button clicked
     * @return true if the event was handled, false otherwise
     */
    @Override
    public boolean onMouseClicked(final IWindow wnd, int mouseX, int mouseY, int clickedButton) {

//        this.setPressed(!this.getPressed());
        super.onMouseClicked(wnd, mouseX, mouseY, clickedButton);
        return true;
    }

    @Override
    public void onPaintBackground(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
//        this.paint3DButton(matrix, this.getButtonState(), 0, 0, this.getBounds().Width, this.getBounds().Height);
        this.paintButton3D(matrix, this.getButtonState(), 0, 0, this.getBounds().Width, this.getBounds().Height);
    }

    @Override
    protected void onActivated() {
        this.Activated.raise(c -> c.accept(this));
    }

    @Override
    protected void onDeactivated() {
        this.Deactivated.raise(c -> c.accept(this));
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" pressed:")
                .append(this._pressed);
    }

    //endregion
    //region internals

    protected boolean getPressed() {
        return this._pressed;
    }

    protected void setPressed(final boolean pressed) {
        this._pressed = pressed;
    }

    private boolean _pressed;
    private Padding _pressedPadding;

    //endregion
}
