/*
 *
 * Button.java
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
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.IWindow;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.util.text.ITextComponent;

import java.util.function.BiConsumer;

@SuppressWarnings("WeakerAccess")
public class Button
        extends AbstractButtonControl {

    public final IEvent<BiConsumer<Button, Integer>> Clicked; // 2nd arg: mouse button clicked

    public Button(ModContainerScreen<? extends ModContainer> gui, String name, final ITextComponent text) {
        this(gui, name, text.getString());
    }

    public Button(ModContainerScreen<? extends ModContainer> gui, String name, final String text) {

        super(gui, name, text);
        this.setPressed(false);
        this.setPadding(3, 3, 2, 2);
        this.setHorizontalAlignment(HorizontalAlignment.Center);
        this.setVerticalAlignment(VerticalAlignment.Center);
        this.Clicked = new Event<>();
    }

    //region AbstractButtonControl

    @Override
    public void onWindowClosed() {

        super.onWindowClosed();
        this.Clicked.unsubscribeAll();
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

        super.onMouseClicked(wnd, mouseX, mouseY, clickedButton);
        this.setPressed(true);
        wnd.captureMouse(this);
        return true;
    }

    @Override
    public boolean onMouseReleased(final IWindow wnd, int mouseX, int mouseY, int releasedButton) {

        if (this.getPressed()) {

            this.setPressed(false);
            wnd.releaseMouse();
            this.Clicked.raise(c -> c.accept(this, releasedButton));
        }

        return true;
    }

    @Override
    public void onPaintBackground(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
        this.paintButton3D(matrix, this.getButtonState(), 0, 0, this.getBounds().Width, this.getBounds().Height);
    }

    @Override
    protected ButtonState getButtonState() {

        if (!this.getEnabled()) {
            return ButtonState.DefaultDisabled;
        } else if (this.getPressed()) {
            return ButtonState.ActiveHighlighted;
        } else if (this.getMouseOver()) {
            return ButtonState.DefaultHighlighted;
        } else {
            return ButtonState.Default;
        }
    }

    @Override
    protected int getIconWidth() {

        final int width = super.getIconWidth();

        return 0 != width ? width + 2 : 0;
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
