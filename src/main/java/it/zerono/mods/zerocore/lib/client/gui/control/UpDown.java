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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;

public class UpDown
        extends AbstractControl {

    public final IEvent<BiConsumer<Direction.AxisDirection, Integer>> Clicked;

    public UpDown(ModContainerScreen<? extends ModContainer> gui, String name) {

        super(gui, name);
        this.Clicked = new Event<>();
        this._paintX = new double[9 * 2];
        this._paintY = new double[9 * 2];
    }

    //region AbstractControl

    @Override
    public void onMoved() {

        super.onMoved();

        final Rectangle bound = this.getBounds();
        final int w = bound.Width - 1;
        final int h = bound.Height - 1;
        double l, r, t, b;

        // expand the edge by one (so outside the control) to always detect the edge
        this._upArrow = new Polygon(-1, -1, w, -1, -1, h);
        this._downArrow = new Polygon(w + 1, 0, w + 1, h + 1, 0, h + 1);

        /*
         * Vertexes order:
         * 1-2
         * |/
         * 3
         */

        l = 0.0;
        r = bound.Width - 1.0;
        t = 0.0;
        b = bound.Height - 1.0;

        // 1
        this._paintX[0] = l + 1.0;
        this._paintY[0] = t + 1.0;
        this._paintX[1] = l + 0.75;
        this._paintY[1] = t + 0.75;
        this._paintX[2] = l + 0.25;
        this._paintY[2] = t + 0.25;
        // 2
        this._paintX[3] = r - 1.75;
        this._paintY[3] = t + 1.0;
        this._paintX[4] = r - 1.25;
        this._paintY[4] = t + 0.75;
        this._paintX[5] = r - 0.25;
        this._paintY[5] = t + 0.25;
        // 3
        this._paintX[6] = l + 1.0;
        this._paintY[6] = b - 1.75;
        this._paintX[7] = l + 0.75;
        this._paintY[7] = b - 1.25;
        this._paintX[8] = l + 0.25;
        this._paintY[8] = b - 0.25;

        /*
         * Vertexes order:
         *   3
         *  /|
         * 2-1
         */

        l = 1.0;
        r = bound.Width;
        t = 1.0;
        b = bound.Height;

        // 1
        this._paintX[9] = r - 1.0;
        this._paintY[9] = b - 1.0;
        this._paintX[10] = r - 0.75;
        this._paintY[10] = b - 0.75;
        this._paintX[11] = r - 0.25;
        this._paintY[11] = b - 0.25;
        // 2
        this._paintX[12] = l + 1.75;
        this._paintY[12] = b - 1.0;
        this._paintX[13] = l + 1.25;
        this._paintY[13] = b - 0.75;
        this._paintX[14] = l + 0.25;
        this._paintY[14] = b - 0.25;
        // 3
        this._paintX[15] = r - 1.0;
        this._paintY[15] = t + 1.75;
        this._paintX[16] = r - 0.75;
        this._paintY[16] = t + 1.25;
        this._paintX[17] = r - 0.25;
        this._paintY[17] = t + 0.25;

        this.controlToScreen(this._paintX, this._paintY);
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
    public void onPaintBackground(final GuiGraphics gfx, final float partialTicks, final int mouseX, final int mouseY) {

        this.paintUpButton(gfx);
        this.paintDownButton(gfx);
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

    protected void paintUpButton(final GuiGraphics gfx) {

        final ButtonState state = this.getButtonState(Direction.AxisDirection.POSITIVE);
        final Theme theme = this.getTheme();

        switch (state) {

            case DefaultDisabled:
                this.paintUpButton(gfx, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_DISABLED_3D_GRADIENT_LIGHT,
                        theme.BUTTON_DISABLED_3D_GRADIENT_DARK,
                        theme.BUTTON_DISABLED_3D_BORDER_LIGHT,
                        theme.BUTTON_DISABLED_3D_BORDER_DARK);
                break;

            case Active:
            case ActiveHighlighted:
                this.paintUpButton(gfx, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_ACTIVE_3D_GRADIENT_LIGHT,
                        theme.BUTTON_ACTIVE_3D_GRADIENT_DARK,
                        theme.BUTTON_ACTIVE_3D_BORDER_LIGHT,
                        theme.BUTTON_ACTIVE_3D_BORDER_DARK);

                break;

            case DefaultHighlighted:
                this.paintUpButton(gfx, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT,
                        theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK,
                        theme.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT,
                        theme.BUTTON_HIGHLIGHTED_3D_BORDER_DARK);

                break;

            case Default:
                this.paintUpButton(gfx, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_NORMAL_3D_GRADIENT_LIGHT,
                        theme.BUTTON_NORMAL_3D_GRADIENT_DARK,
                        theme.BUTTON_NORMAL_3D_BORDER_LIGHT,
                        theme.BUTTON_NORMAL_3D_BORDER_DARK);
                break;
        }
    }

    protected void paintDownButton(final GuiGraphics gfx) {

        final ButtonState state = this.getButtonState(Direction.AxisDirection.NEGATIVE);
        final Theme theme = this.getTheme();

        switch (state) {

            case DefaultDisabled:
                this.paintDownButton(gfx, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_DISABLED_3D_GRADIENT_LIGHT,
                        theme.BUTTON_DISABLED_3D_GRADIENT_DARK,
                        theme.BUTTON_DISABLED_3D_BORDER_LIGHT,
                        theme.BUTTON_DISABLED_3D_BORDER_DARK);
                break;

            case Active:
            case ActiveHighlighted:
                this.paintDownButton(gfx, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_ACTIVE_3D_GRADIENT_LIGHT,
                        theme.BUTTON_ACTIVE_3D_GRADIENT_DARK,
                        theme.BUTTON_ACTIVE_3D_BORDER_LIGHT,
                        theme.BUTTON_ACTIVE_3D_BORDER_DARK);

                break;

            case DefaultHighlighted:
                this.paintDownButton(gfx, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_LIGHT,
                        theme.BUTTON_HIGHLIGHTED_3D_GRADIENT_DARK,
                        theme.BUTTON_HIGHLIGHTED_3D_BORDER_LIGHT,
                        theme.BUTTON_HIGHLIGHTED_3D_BORDER_DARK);

                break;

            case Default:
                this.paintDownButton(gfx, theme.DARK_OUTLINE_COLOR,
                        theme.BUTTON_NORMAL_3D_GRADIENT_LIGHT,
                        theme.BUTTON_NORMAL_3D_GRADIENT_DARK,
                        theme.BUTTON_NORMAL_3D_BORDER_LIGHT,
                        theme.BUTTON_NORMAL_3D_BORDER_DARK);
                break;
        }
    }

    protected void paintUpButton(final GuiGraphics gfx, final Colour darkOutlineColour, final Colour gradientLightColour,
                                 final Colour gradientDarkColour, final Colour borderLightColour,
                                 final Colour borderDarkColour) {

        final float z = this.getZLevel();

        /*
         * Vertexes order:
         * 1-2
         * |/
         * 3
         */

        // gradient

        double x1 = this._paintX[0];
        double y1 = this._paintY[0];
        double x2 = this._paintX[3];
        double y2 = this._paintY[3];
        double x3 = this._paintX[6];
        double y3 = this._paintY[6];

        if (gradientLightColour.equals(gradientDarkColour)) {
            ModRenderHelper.paint3DSolidTriangle(gfx,
                    x1, y1,
                    x2, y2,
                    x3, y3,
                    z, gradientLightColour);
        } else {
            ModRenderHelper.paint3DGradientTriangle(gfx,
                    x1, y1,
                    x2, y2,
                    x3, y3,
                    z, gradientLightColour, gradientDarkColour, gradientDarkColour);
        }

        // 3D borders

        x1 = this._paintX[1];
        y1 = this._paintY[1];
        x2 = this._paintX[1 + 3];
        y2 = this._paintY[1 + 3];
        x3 = this._paintX[1 + 6];
        y3 = this._paintY[1 + 6];

        // - dark
        ModRenderHelper.paintSolidLines(gfx, borderDarkColour, 0.25, z,
                x2, y2, x3, y3);

        // - light
        ModRenderHelper.paintSolidLines(gfx, borderLightColour, 0.5, z,
                x1, y1, x2, y2,
                x1, y1, x3, y3);

        // dark outline

        x1 = this._paintX[2];
        y1 = this._paintY[2];
        x2 = this._paintX[2 + 3];
        y2 = this._paintY[2 + 3];
        x3 = this._paintX[2 + 6];
        y3 = this._paintY[2 + 6];

        ModRenderHelper.paintSolidLines(gfx, darkOutlineColour, 0.5, z,
                x1, y1, x2, y2,
                x3, y3, x1, y1);
        ModRenderHelper.paintSolidLines(gfx, darkOutlineColour, 0.25, z,
                x2, y2, x3, y3);
    }

    protected void paintDownButton(final GuiGraphics gfx, final Colour darkOutlineColour, final Colour gradientLightColour,
                                 final Colour gradientDarkColour, final Colour borderLightColour,
                                 final Colour borderDarkColour) {

        final float z = this.getZLevel();

        /*
         * Vertexes order:
         *   3
         *  /|
         * 2-1
         */

        // gradient

        double x1 = this._paintX[9];
        double y1 = this._paintY[9];
        double x2 = this._paintX[9 + 3];
        double y2 = this._paintY[9 + 3];
        double x3 = this._paintX[9 + 6];
        double y3 = this._paintY[9 + 6];

        if (gradientLightColour.equals(gradientDarkColour)) {
            ModRenderHelper.paint3DSolidTriangle(gfx,
                    x1, y1,
                    x2, y2,
                    x3, y3,
                    z, gradientLightColour);
        } else {
            ModRenderHelper.paint3DGradientTriangle(gfx,
                    x1, y1,
                    x2, y2,
                    x3, y3,
                    z, gradientDarkColour, gradientLightColour, gradientDarkColour);
        }

        // 3D borders

        x1 = this._paintX[10];
        y1 = this._paintY[10];
        x2 = this._paintX[10 + 3];
        y2 = this._paintY[10 + 3];
        x3 = this._paintX[10 + 6];
        y3 = this._paintY[10 + 6];

        // - light
        ModRenderHelper.paintSolidLines(gfx, borderLightColour, 0.25, z,
                x2, y2, x3, y3 );

        // - dark
        ModRenderHelper.paintSolidLines(gfx, borderDarkColour, 0.5, z,
                x1, y1, x2 - 0.25, y2,
                x1, y1, x3, y3 - 0.25);

        // dark outline

        x1 = this._paintX[11];
        y1 = this._paintY[11];
        x2 = this._paintX[11 + 3];
        y2 = this._paintY[11 + 3];
        x3 = this._paintX[11 + 6];
        y3 = this._paintY[11 + 6];

        ModRenderHelper.paintSolidLines(gfx, darkOutlineColour, 0.5, z,
                x1, y1, x2, y2,
                x3, y3, x1, y1);
        ModRenderHelper.paintSolidLines(gfx, darkOutlineColour, 0.25, z,
                x2, y2, x3, y3);
    }

    private void playClickSound() {
        this.playSound(SoundEvents.UI_BUTTON_CLICK);
    }

    private Polygon _upArrow;
    private Polygon _downArrow;

    private final double[] _paintX;
    private final double[] _paintY;

    private Direction.AxisDirection _pressed;
    private Direction.AxisDirection _over;

    //endregion
}
