/*
 *
 * UpDown.java
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
import it.zerono.mods.zerocore.lib.client.gui.Theme;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.geometry.Polygon;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;

public class UpDown
        extends AbstractControl {

    public final IEvent<BiConsumer<Direction.AxisDirection, Integer>> Clicked;

    public UpDown(ModContainerScreen<? extends ModContainer> gui, String name) {

        super(gui, name);
        this.Clicked = new Event<>();
    }

    //region AbstractControl

    @Override
    public void onMoved() {

        super.onMoved();

        final Rectangle bound = this.getBounds();
        final int w = bound.Width - 1;
        final int h = bound.Height - 1;

        // expand the edge by one (so outside the control) to always detect the edge
        this._upArrow = new Polygon(-1, -1, w, -1, -1, h);
        this._downArrow = new Polygon(w + 1, 0, w + 1, h + 1, 0, h + 1);

        this._upArrowPaint = new Polygon(0, 0, w - 1, 0, 0, h - 1).transform(this::controlToScreen);
        this._downArrowPaint = new Polygon(w, 1, w, h, 1, h).transform(this::controlToScreen);
    }

    /**
     * Event handler - the user has clicked on this control or on one of it's children
     *
     * @param clickedButton the mouse button clicked
     * @return true if the event was handled, false otherwise
     */
    @Override
    public boolean onMouseClicked(final IWindow wnd, int mouseX, int mouseY, int clickedButton) {

        super.onMouseClicked(wnd, mouseX, mouseY, clickedButton);

        final int childX = mouseX - this.getBounds().Origin.X;
        final int childY = mouseY - this.getBounds().Origin.Y;

        if (this._upArrow.contains(childX, childY)) {
            this.setPressed(Direction.AxisDirection.POSITIVE);
        } else if (this._downArrow.contains(childX, childY)) {
            this.setPressed(Direction.AxisDirection.NEGATIVE);
        } else {
            this.setPressed(null);
        }

        wnd.captureMouse(this);
        this.playClickSound();
        return true;
    }

    @Override
    public boolean onMouseReleased(final IWindow wnd, int mouseX, int mouseY, int releasedButton) {

        this.getPressed().ifPresent(button -> {

            this.setPressed(null);
            wnd.releaseMouse();
            this.Clicked.raise(c -> c.accept(button, releasedButton));
        });

        return true;
    }

    @Override
    public void setMouseOver(boolean over, int mouseX, int mouseY) {

        super.setMouseOver(over, mouseX, mouseY);

        if (over) {

            final Point mouse = this.screenToControl(mouseX, mouseY);

            if (this._upArrow.contains(mouse.X, mouse.Y)) {
                this.setOver(Direction.AxisDirection.POSITIVE);
            } else if (this._downArrow.contains(mouse.X, mouse.Y)) {
                this.setOver(Direction.AxisDirection.NEGATIVE);
            } else {
                this.setOver(null);
            }
        } else {

            this.setOver(null);
        }
    }

    @Override
    public void onPaintBackground(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        this.paintUpButton(matrix);
        this.paintDownButton(matrix);
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(" pressed:")
                .append(this._pressed)
                .append(" over:")
                .append(this._over);
    }

    //endregion
    //region internals

    protected Optional<Direction.AxisDirection> getPressed() {
        return Optional.ofNullable(this._pressed);
    }

    protected void setPressed(@Nullable final Direction.AxisDirection button) {
        this._pressed = button;
    }

    protected Optional<Direction.AxisDirection> getOver() {
        return Optional.ofNullable(this._over);
    }

    protected void setOver(@Nullable final Direction.AxisDirection button) {
        this._over = button;
    }

    protected ButtonState getButtonState(final Direction.AxisDirection button) {

        if (!this.getEnabled()) {
            return ButtonState.DefaultDisabled;
        } else if (this.getPressed().filter(b -> b == button).isPresent()) {
            return ButtonState.ActiveHighlighted;
        } else if (this.getOver().filter(b -> b == button).isPresent()) {
            return ButtonState.DefaultHighlighted;
        } else {
            return ButtonState.Default;
        }
    }

    protected void paintUpButton(final MatrixStack matrix) {

        final ButtonState state = this.getButtonState(Direction.AxisDirection.POSITIVE);
        final Theme theme = this.getTheme();

        switch (state) {

            case DefaultDisabled:
                this.paintUpButton(matrix, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_DISABLED_3D_GRADIENT_LIGHT,
                        theme.BUTTON_DISABLED_3D_GRADIENT_DARK,
                        theme.BUTTON_DISABLED_3D_BORDER_LIGHT,
                        theme.BUTTON_DISABLED_3D_BORDER_DARK);
                break;

            case Active:
            case ActiveHighlighted:
                this.paintUpButton(matrix, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_ACTIVE_3D_GRADIENT_LIGHT,
                        theme.BUTTON_ACTIVE_3D_GRADIENT_DARK,
                        theme.BUTTON_ACTIVE_3D_BORDER_LIGHT,
                        theme.BUTTON_ACTIVE_3D_BORDER_DARK);

                break;

            case DefaultHighlighted:
                this.paintUpButton(matrix, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT,
                        theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK,
                        theme.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT,
                        theme.BUTTON_HIGHLIGHTED_3D_BORDER_DARK);

                break;

            case Default:
                this.paintUpButton(matrix, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_NORMAL_3D_GRADIENT_LIGHT,
                        theme.BUTTON_NORMAL_3D_GRADIENT_DARK,
                        theme.BUTTON_NORMAL_3D_BORDER_LIGHT,
                        theme.BUTTON_NORMAL_3D_BORDER_DARK);
                break;
        }
    }

    protected void paintDownButton(final MatrixStack matrix) {

        final ButtonState state = this.getButtonState(Direction.AxisDirection.NEGATIVE);
        final Theme theme = this.getTheme();

        switch (state) {

            case DefaultDisabled:
                this.paintDownButton(matrix, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_DISABLED_3D_GRADIENT_LIGHT,
                        theme.BUTTON_DISABLED_3D_GRADIENT_DARK,
                        theme.BUTTON_DISABLED_3D_BORDER_LIGHT,
                        theme.BUTTON_DISABLED_3D_BORDER_DARK);
                break;

            case Active:
            case ActiveHighlighted:
                this.paintDownButton(matrix, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_ACTIVE_3D_GRADIENT_LIGHT,
                        theme.BUTTON_ACTIVE_3D_GRADIENT_DARK,
                        theme.BUTTON_ACTIVE_3D_BORDER_LIGHT,
                        theme.BUTTON_ACTIVE_3D_BORDER_DARK);

                break;

            case DefaultHighlighted:
                this.paintDownButton(matrix, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT,
                        theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK,
                        theme.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT,
                        theme.BUTTON_HIGHLIGHTED_3D_BORDER_DARK);

                break;

            case Default:
                this.paintDownButton(matrix, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_NORMAL_3D_GRADIENT_LIGHT,
                        theme.BUTTON_NORMAL_3D_GRADIENT_DARK,
                        theme.BUTTON_NORMAL_3D_BORDER_LIGHT,
                        theme.BUTTON_NORMAL_3D_BORDER_DARK);
                break;
        }
    }

    protected void paintUpButton(final MatrixStack matrix, final Colour darkOutlineColour, final Colour gradientLightColour,
                                 final Colour gradientDarkColour, final Colour borderLightColour,
                                 final Colour borderDarkColour) {

        final float z = this.getZLevel();
        final int x1 = this._upArrowPaint.getX(0);
        final int y1 = this._upArrowPaint.getY(0);
        final int x2 = this._upArrowPaint.getX(1);
        final int y2 = this._upArrowPaint.getY(1);
        final int x3 = this._upArrowPaint.getX(2);
        final int y3 = this._upArrowPaint.getY(2);

        // gradient
        ModRenderHelper.paint3DGradientTriangle(matrix,
                x1 + 1.0, y1 + 1.0,
                x2 - 0.5, y2 + 1.0,
                x3 + 1.0, y3,
                z, gradientLightColour, gradientDarkColour);

        // light borders
        ModRenderHelper.paintSolidLines(matrix, borderLightColour, this.getGui().getGuiScaleFactor(), z,
                x1 + 1.0, y1 + 1.5, x2 - 1.5, y2 + 1.5);
        ModRenderHelper.paintSolidLines(matrix, borderLightColour, this.getGui().getGuiScaleFactor(), z,
                x1 + 1.5, y1 + 1.5, x3 + 1.5, y3 - 1.5);

        // dark border
        ModRenderHelper.paintSolidLines(matrix, borderDarkColour, this.getGui().getGuiScaleFactor(), z,
                x2 - 1.0, y2 + 1.0, x3 + 1.0, y3 - 0.5);

        // dark outline
        ModRenderHelper.paintSolidLines(matrix, darkOutlineColour, this.getGui().getGuiScaleFactor(), z,
                x1, y1 + 0.5, x2 + 1.0, y2 + 0.5,
                x2 + 0.5, y2 + 0.5, x3 + 0.5, y3 + 1.0,
                x3 + 0.5, y3 + 1.0, x1 + 0.5, y1);
    }

    protected void paintDownButton(final MatrixStack matrix, final Colour darkOutlineColour, final Colour gradientLightColour,
                                 final Colour gradientDarkColour, final Colour borderLightColour,
                                 final Colour borderDarkColour) {

        final float z = this.getZLevel();
        final int x1 = this._downArrowPaint.getX(0);
        final int y1 = this._downArrowPaint.getY(0);
        final int x2 = this._downArrowPaint.getX(1);
        final int y2 = this._downArrowPaint.getY(1);
        final int x3 = this._downArrowPaint.getX(2);
        final int y3 = this._downArrowPaint.getY(2);

        // gradient
        ModRenderHelper.paint3DGradientTriangle(matrix,
                x1, y1 + 1.5,
                x2, y2,
                x3 + 1.5, y3,
                z, gradientLightColour, gradientDarkColour);

        // light border
        ModRenderHelper.paintSolidLines(matrix, borderLightColour, this.getGui().getGuiScaleFactor(), z,
                x1, y1 + 2.0, x3 + 2.0, y3 - 0.5);

        // dark borders
        ModRenderHelper.paintSolidLines(matrix, borderDarkColour, this.getGui().getGuiScaleFactor(), z,
                x1 - 0.5, y1 + 1.5, x2 - 0.5, y2 - 1.0);
        ModRenderHelper.paintSolidLines(matrix, borderDarkColour, this.getGui().getGuiScaleFactor(), z,
                x2, y2 - 0.5, x3 + 1.5, y3 - 0.5);

        // dark outline
        ModRenderHelper.paintSolidLines(matrix, darkOutlineColour, this.getGui().getGuiScaleFactor(), z,
                x1 + 0.5, y1, x2 + 0.5, y2 + 1.0,
                x2 + 0.5, y2 + 0.5, x3, y3 + 0.5,
                x3 + 0.5, y3 + 0.5, x1 + 0.5, y1
        );
    }

    private void playClickSound() {
        this.playSound(SoundEvents.UI_BUTTON_CLICK);
    }

    private Polygon _upArrow;
    private Polygon _downArrow;
    private Polygon _upArrowPaint;
    private Polygon _downArrowPaint;

    private Direction.AxisDirection _pressed;
    private Direction.AxisDirection _over;

    //endregion
}
