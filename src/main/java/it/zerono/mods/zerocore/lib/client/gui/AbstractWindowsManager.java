/*
 *
 * AbstractWindowsManager.java
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

package it.zerono.mods.zerocore.lib.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class AbstractWindowsManager<C extends ModContainer> implements IWindowsManager<C> {

    //region IWindowsManager

    @Override
    public ModContainerScreen<C> getGuiScreen() {
        return this._guiContainer;
    }

    @Override
    public IWindow createWindow(final IControlContainer rootContainer, final boolean modalWindow,
                                final int x, final int y, final int width, final int height) {

        final Window<C> wnd = new Window<>(this, rootContainer, x, y, width, height, modalWindow);

        this.addWindow(wnd, modalWindow);
        return wnd;
    }

    @Override
    public double getMouseWheelMovement() {
        return this._lastWheelMovement;
    }

    @Override
    public void setFocus(@Nullable final IWindow wnd, @Nullable IControl newFocus) {

        if (this._keyboardFocusTarget != newFocus) {

            final IControl previousFocus = this._keyboardFocusTarget;

            if (null != previousFocus) {
                previousFocus.onKillFocus(this._keyboardFocusWindow, newFocus);
            }

            this._keyboardFocusTarget = newFocus;
            this._keyboardFocusWindow = wnd;

            if (null != newFocus && null != wnd) {
                newFocus.onSetFocus(wnd, previousFocus);
            }

            // notify every window of the focus change
            this.forEachWindow(window -> window.onFocusChanged(newFocus));
        }
    }

    @Override
    public void captureMouse(final IWindow wnd, IControl target) {

        this._mouseCaptureTarget = target;
        this._mouseCaptureWindow = wnd;
    }

    @Override
    public void releaseMouse() {

        this._mouseCaptureTarget = null;
        this._mouseCaptureWindow = null;
    }

    @Override
    public boolean isMouseCaptured() {
        return null != this._mouseCaptureTarget && null != this._mouseCaptureWindow;
    }

    @Override
    public void startDragging(final IDraggable draggable, final IDragSource source) {
        this._dragData = new DragData(draggable, source);
    }

    @Override
    public boolean isDragging() {
        return null != this._dragData && this._dragData.isDragging();
    }

    @Override
    public void hideWindow(final IWindow wnd) {
        this.showWindow(wnd, false);
    }

    @Override
    public void showWindow(final IWindow wnd) {
        this.showWindow(wnd, true);
    }

    @Override
    public boolean isWindowVisible(final IWindow wnd) {
        return false; //TODO imp
    }

    @Override
    public float getPaintPartialTicks() {
        return this._paintPartialTicks;
    }

    @Override
    public void onThemeChanged(Theme newTheme) {
        this.forEachWindow(window -> window.onThemeChanged(newTheme));
    }

    //region AbstractWindowsManager

    public static void enableDebugFrame(final boolean enable) {

        s_debugFrame = CodeHelper.isDevEnv() && enable;

        if (null != Minecraft.getInstance().player) {
            CodeHelper.sendStatusMessage(Minecraft.getInstance().player,
                    Component.literal(String.format("GUI debug hover-frame is now %s", s_debugFrame ? "enabled" : "disabled")));
        }
    }

    protected abstract void addWindow(Window<C> wnd, boolean isModal);
    protected abstract void showWindow(IWindow window, boolean show);
    protected abstract void forEachWindow(Consumer<Window<C>> action);
    protected abstract void forEachInteractiveWindow(Consumer<Window<C>> action);
    @Nullable protected abstract <R> R forEachInteractiveWindow(Function<Window<C>, R> transformation, @Nullable R invalidResult);
    protected abstract Optional<IControl> findControl(int x, int y);

    protected AbstractWindowsManager(final ModContainerScreen<C> guiContainer) {

        this._guiContainer = guiContainer;
        this.resetState();

        this.onGuiContainerCreateHandler = this.getGuiScreen().Create.subscribe(this::onGuiContainerCreate);
        this.onGuiContainerClosedHandler = this.getGuiScreen().Close.subscribe(this::onGuiContainerClosed);
    }

    private void onGuiContainerCreate() {
        this.resetState();
    }

    private void onGuiContainerClosed() {

        this.forEachWindow(Window::onWindowClosed);
        this.getGuiScreen().Create.unsubscribe(this.onGuiContainerCreateHandler);
        this.getGuiScreen().Close.unsubscribe(this.onGuiContainerClosedHandler);
    }

    void onGuiContainerTick() {
        this.forEachWindow(Window::onTick);
    }

    void onGuiContainerPaintBackground(final GuiGraphics gfx, final float partialTicks) {

        final int mouseX = this.getGuiScreen().getClippedMouseX();
        final int mouseY = this.getGuiScreen().getClippedMouseY();

        this._paintPartialTicks = partialTicks;
        this.forEachWindow(window -> window.onPaintBackground(gfx, partialTicks, mouseX, mouseY));
    }

    void onGuiContainerPaintForeground(final GuiGraphics gfx) {

        final float partialTicks = this.getPaintPartialTicks();
        final int mouseX = this.getGuiScreen().getClippedMouseX();
        final int mouseY = this.getGuiScreen().getClippedMouseY();

        /*
        Minecraft translate the OpenGL matrix to {guiLeft, guiTop, 0} before calling GuiContainer.drawGuiContainerForegroundLayer
        so we need to traslate the matrix back to {0, 0, 0}, do our stuff, and translate it back to {guiLeft, guiTop, 0} ...
         */
        final int guiLeft = this.getGuiScreen().getGuiX();
        final int guiTop = this.getGuiScreen().getGuiY();
        final var matrix = gfx.pose();

        matrix.translate(-guiLeft, -guiTop, 0.0f);

        // paint all the controls!

        this.forEachWindow(window -> window.onPaint(gfx, partialTicks, mouseX, mouseY));
        this.forEachWindow(window -> window.onPaintOverlay(gfx, partialTicks, mouseX, mouseY));

        // ... and the tool tips (skip them if ALT is pressed) ...

        if (!Screen.hasAltDown()) {

            this.forEachInteractiveWindow(w -> w.paintToolTips(gfx));
            Lighting.setupForFlatItems();
        }

        // ... and the dragged object

        if (this.isDragging()) {
            this._dragData.paint(gfx, mouseX, mouseY, this.getGuiScreen().getZLevel());
        }

        if (s_debugFrame) {
            this.forEachInteractiveWindow(window -> window.onPaintDebugFrame(gfx, Colour.WHITE));
        }

        // translate the GL matrix back to make the MC code happy
        matrix.translate(guiLeft, guiTop, 0.0f);
    }

    boolean onGuiContainerMouseClicked(final double mouseX, final double mouseY, final int clickedButton) {

        final int mx = Mth.floor(mouseX);
        final int my = Mth.floor(mouseY);

        if (this.isDragging()) {

            if (CodeHelper.MOUSE_BUTTON_RIGHT == clickedButton) {
                this.cancelDragging();
            } else {
                this.stopDragging(mx, my);
            }

            return true;
        }

        final Boolean result = this.forEachInteractiveWindow(window -> window.onMouseClicked(mx, my, clickedButton), false);

        return (null != result) && result;
    }

    boolean onGuiContainerMouseReleased(final double mouseX, final double mouseY, final int mouseButton) {

        final int mx = Mth.floor(mouseX);
        final int my = Mth.floor(mouseY);

        if (this.isDragging()) {
            this.stopDragging(mx, my);
        }

        if (s_debugFrame && CodeHelper.MOUSE_BUTTON_RIGHT == mouseButton) {
            this.forEachInteractiveWindow(Window::onDisplayDebugFrameControlName);
        }

        if (-1 != mouseButton) {

            return this.raiseMouseReleased(mx, my, mouseButton);

        } else {

            this.raiseMouseMoved(mx, my);
            return false;
        }
    }

    void onGuiContainerMouseMoved(final double mouseX, final double mouseY) {
        this.raiseMouseMoved(Mth.floor(mouseX), Mth.floor(mouseY));
    }

    boolean onGuiContainerMouseScrolled(final double mouseX, final double mouseY, final double scrollDelta) {

        this._lastWheelMovement = scrollDelta;
        return this.raiseMouseWheel(Mth.floor(mouseX), Mth.floor(mouseY), scrollDelta);
    }

    boolean onGuiContainerCharTyped(final char typedChar, final int keyCode) {

        try {

            if (null != this._keyboardFocusTarget && null != this._keyboardFocusWindow) {
                if (this._keyboardFocusTarget.onCharTyped(this._keyboardFocusWindow, typedChar, keyCode)) {
                    return true;
                }
            }

            final Boolean result = this.forEachInteractiveWindow(window -> window.onCharTyped(typedChar, keyCode), false);

            return (null != result) && result;

        } catch (Exception e) {
            return false;
        }
    }

    boolean onGuiContainerKeyPressed(final int keyCode, final int scanCode, final int modifiers) {

        try {

            if (null != this._keyboardFocusTarget && null != this._keyboardFocusWindow) {
                if (this._keyboardFocusTarget.onKeyPressed(this._keyboardFocusWindow, keyCode, scanCode, modifiers)) {
                    return true;
                }
            }

            final Boolean result = this.forEachInteractiveWindow(window -> window.onKeyPressed(keyCode, scanCode, modifiers), false);

            return (null != result) && result;

        } catch (Exception e) {
            return false;
        }
    }

    boolean onGuiContainerKeyReleased(final int keyCode, final int scanCode, final int modifiers) {

        try {

            if (null != this._keyboardFocusTarget && null != this._keyboardFocusWindow) {
                if (this._keyboardFocusTarget.onKeyReleased(this._keyboardFocusWindow, keyCode, scanCode, modifiers)) {
                    return true;
                }
            }

            final Boolean result = this.forEachInteractiveWindow(window -> window.onKeyReleased(keyCode, scanCode, modifiers), false);

            return (null != result) && result;

        } catch (Exception e) {
            return false;
        }
    }

    protected void resetState() {

        this._lastWheelMovement = 0;
        this._paintPartialTicks = 0.0f;
        this.releaseMouse();
        this.setFocus(null, null);

        if (null != this._dragData) {

            this._dragData.clear();
            this._dragData = null;
        }
    }

    void validate(Consumer<Component> errorReport) {
        this.forEachWindow(w -> w.validate(errorReport));
    }

    //region internals

    private void raiseMouseMoved(final int mouseX, final int mouseY) {

        if (null != this._mouseCaptureTarget) {

            final Point childXY = this._mouseCaptureTarget.screenToControl(mouseX, mouseY);

            this._mouseCaptureTarget.onMouseMoved(this._mouseCaptureWindow, childXY.X, childXY.Y);

        } else {

            this.forEachInteractiveWindow(window -> window.onMouseMoved(mouseX, mouseY));
        }
    }

    private boolean raiseMouseReleased(final int mouseX, final int mouseY, final int mouseButton) {

        if (null != this._mouseCaptureTarget) {

            final Point childXY = this._mouseCaptureTarget.screenToControl(mouseX, mouseY);

            return this._mouseCaptureTarget.onMouseReleased(this._mouseCaptureWindow, childXY.X, childXY.Y, mouseButton);

        } else {

            final Boolean result = this.forEachInteractiveWindow(window -> window.onMouseReleased(mouseX, mouseY, mouseButton), false);

            return (null != result) && result;
        }
    }

    private boolean raiseMouseWheel(final int mouseX, final int mouseY, final double wheelMovement) {

        if (null != this._mouseCaptureTarget) {

            final Point childXY = this._mouseCaptureTarget.screenToControl(mouseX, mouseY);

            return this._mouseCaptureTarget.onMouseWheel(this._mouseCaptureWindow, childXY.X, childXY.Y, wheelMovement);

        } else {

            final Boolean result = this.forEachInteractiveWindow(window -> window.onMouseWheel(mouseX, mouseY, wheelMovement), false);

            return (null != result) && result;
        }
    }

    private void cancelDragging() {

        if (this.isDragging()) {

            this._dragData.pushBack();
            this._dragData.clear();
            this._dragData = null;
        }
    }

    private void stopDragging(final int mouseX, final int mouseY) {

        if (!this.isDragging()) {
            return;
        }

        final boolean dropped = this.findControl(mouseX, mouseY)
                .filter(control -> control instanceof IDropTarget && this._dragData.tryDrop((IDropTarget)control))
                .isPresent();

        if (!dropped) {
            // IDropTarget not found or drop refused -> send it back to where it came from...
            this._dragData.pushBack();
        }

        this._dragData.clear();
        this._dragData = null;
    }

    private static boolean s_debugFrame = false;

    private final ModContainerScreen<C> _guiContainer;
    private final Runnable onGuiContainerCreateHandler;
    private final Runnable onGuiContainerClosedHandler;
    private double _lastWheelMovement;
    private IControl _keyboardFocusTarget;
    private IWindow  _keyboardFocusWindow;
    private IControl _mouseCaptureTarget;
    private IWindow _mouseCaptureWindow;
    private DragData _dragData;
    private float _paintPartialTicks;

    private final static class DragData {

        DragData(final IDraggable draggable, final IDragSource source) {

            this._draggable = draggable;
            this._source = source;
            this._paintXOffset = -(draggable.getWidth() / 2);
            this._paintYOffset = -(draggable.getHeight() / 2);
        }

        boolean isDragging() {
            return null != this._source && null != this._draggable;
        }

        void clear() {

            this._draggable = null;
            this._source = null;
            this._paintXOffset = this._paintYOffset = 0;
        }

        void paint(final GuiGraphics gfx, final int mouseX, final int mouseY, final float zLevel) {
            this._draggable.onPaint(gfx, mouseX + this._paintXOffset, mouseY + this._paintYOffset, zLevel, IDraggable.PaintState.Dragging);
        }

        void pushBack() {

            this._source.setDraggable(this._draggable);
            this._draggable = null;
        }

        boolean tryDrop(final IDropTarget dropTarget) {

            if (dropTarget.canAcceptDrop(this._draggable)) {

                this._source.setDraggable(null);
                dropTarget.setDraggable(this._draggable);
                this._draggable = null;

                return true;
            }

            return false;
        }

        private IDraggable _draggable;
        private IDragSource _source;
        private int _paintXOffset;
        private int _paintYOffset;
    }
}
