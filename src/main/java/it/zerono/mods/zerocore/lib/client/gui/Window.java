/*
 *
 * Window.java
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

import com.mojang.blaze3d.vertex.PoseStack;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.Flags;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public class Window<C extends ModContainer>
        implements IWindow {

    public final IEvent<BiConsumer<IWindow, IControl>> Focus;

    Window(final IWindowsManager<C> manager, final IControlContainer topLevelContainer,
                  final int x, final int y, final int width, final int height, boolean modal) {

        this._manager = manager;
        this._topLevelContainer = topLevelContainer;
        this._flags = new Flags<>(WindowFlags.class);
        this._flags.set(WindowFlags.Modal, modal);
        this._keyboardFocus = this._mouseOver = null;
        this.Focus = new Event<>();

        this._topLevelContainer.setBounds(new Rectangle(x, y, width, height));
        this._flags.set(WindowFlags.ContentVisible, this._topLevelContainer.getVisible());
    }

    public IWindowsManager<C> getWindowsManager() {
        return this._manager;
    }

    public ModContainerScreen<C> getGuiScreen() {
        return this.getWindowsManager().getGuiScreen();
    }

    public Optional<IControl> getFocus() {
        return Optional.ofNullable(this._keyboardFocus);
    }

    public Optional<IControl> findControl(final int x, final int y) {
        return this.isVisible() ? this._topLevelContainer.findControl(x, y) : Optional.empty();
    }

    public void paintToolTips(final PoseStack matrix) {

        if (null != this._mouseOver) {

            final int mouseX = Mth.fastFloor(GuiHelper.getMouse().xpos() / this.getGuiScreen().getGuiScaleFactor());
            final int mouseY = Mth.fastFloor(GuiHelper.getMouse().ypos() / this.getGuiScreen().getGuiScaleFactor());

            this._mouseOver.paintToolTips(matrix, mouseX, mouseY);
        }
    }

    /*
    void setFocus(@Nullable IControl newFocus) {

        if (this._keyboardFocus != newFocus) {

            final IControl previousFocus = this._keyboardFocus;

            if (null != previousFocus) {
                previousFocus.onKillFocus(newFocus);
            }

            this._keyboardFocus = newFocus;

            if (null != newFocus) {
                newFocus.onSetFocus(previousFocus);
            }

            ((Event<IControl>)this.Focus).raise(this, newFocus);
        }
    }*/

    //region IWindow

    @Override
    public void setFocus(@Nullable final IControl newFocus) {
        this._manager.setFocus(this, newFocus);
    }

    @Override
    public void captureMouse(final IControl target) {
        this._manager.captureMouse(this, target);
    }

    @Override
    public void releaseMouse() {
        this._manager.releaseMouse();
    }

    @Override
    public boolean isMouseCaptured() {
        return this._manager.isMouseCaptured();
    }

    @Override
    public void startDragging(@Nonnull final IDraggable draggable, final IDragSource source) {
        this._manager.startDragging(draggable, source);
    }

    @Override
    public boolean isDragging() {
        return this._manager.isDragging();
    }

    @Override
    public void hide() {
        this._flags.add(WindowFlags.Hidden);
    }

    @Override
    public void show() {
        this._flags.remove(WindowFlags.Hidden);
    }

    @Override
    public boolean isVisible() {
        return !this._flags.contains(WindowFlags.Hidden) && this._flags.contains(WindowFlags.ContentVisible);
    }

    @Override
    public float getPaintPartialTicks() {
        return this._manager.getPaintPartialTicks();
    }

    //region internals

    void onTick() {

        this._flags.set(WindowFlags.ContentVisible, this._topLevelContainer.getVisible());

        final ModContainerScreen<C> gui = this.getGuiScreen();
        final int mouseX = Mth.fastFloor(GuiHelper.getMouse().xpos() / gui.getGuiScaleFactor());
        final int mouseY = Mth.fastFloor(GuiHelper.getMouse().ypos() / gui.getGuiScaleFactor());

        this.updateMouseOverControl(mouseX, mouseY);
    }

    void onWindowClosed() {

        this._topLevelContainer.onWindowClosed();
        this.Focus.unsubscribeAll();
    }

    void onFocusChanged(@Nullable final IControl newFocus) {
        this.Focus.raise(c -> c.accept(this, newFocus));
    }

    void onPaintBackground(final PoseStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        if (this.isVisible()) {
            this._topLevelContainer.onPaintBackground(matrix, partialTicks, mouseX, mouseY);
        }
    }

    void onPaint(final PoseStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        if (this.isVisible()) {
            this._topLevelContainer.onPaint(matrix, partialTicks, mouseX, mouseY);
        }
    }

    void onPaintOverlay(final PoseStack matrix, final float partialTicks, final int mouseX, final int mouseY) {

        if (this.isVisible()) {
            this._topLevelContainer.onPaintOverlay(matrix, partialTicks, mouseX, mouseY);
        }
    }

    void onPaintDebugFrame(final PoseStack matrix, final Colour colour) {

        if (null != this._mouseOver) {
            this._mouseOver.onPaintDebugFrame(matrix, colour);
        }
    }

    void onDisplayDebugFrameControlName() {

        if (null != this._mouseOver) {
            CodeHelper.reportErrorToPlayer(Objects.requireNonNull(Minecraft.getInstance().player), null,
                    Component.literal(this._mouseOver.getName()));
        }
    }

    /**
     * Forward the onMouseClicked on the controls on this window
     *
     * @param mouseX x coords
     * @param mouseY y coords
     * @param mouseButton the button clicked
     * @return true if the clicked control belong to this window, false otherwise
     */
    boolean onMouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        /*
        if (null != this._keyboardFocus) {
            this.setFocus(null);
        }*/

        if (this.isVisible() && this._topLevelContainer.hitTest(mouseX, mouseY)) {
            return this._topLevelContainer.onMouseClicked(this, mouseX, mouseY, mouseButton);
        }

        return false;
    }

    boolean onMouseReleased(final int mouseX, final int mouseY, final int mouseButton) {
        return this.isVisible() && this._topLevelContainer.onMouseReleased(this, mouseX, mouseY, mouseButton);
    }

    void onMouseMoved(final int mouseX, final int mouseY) {

        if (this.isVisible()) {
            this._topLevelContainer.onMouseMoved(this, mouseX, mouseY);
        }
    }

    boolean onMouseWheel(final int mouseX, final int mouseY, double movement) {
        return this.isVisible() && this._topLevelContainer.onMouseWheel(this, mouseX, mouseY, movement);
    }

    boolean onCharTyped(final char typedChar, final int keyCode) {
        return null != this._keyboardFocus && this._keyboardFocus.onCharTyped(this, typedChar, keyCode);
    }

    boolean onKeyPressed(final int keyCode, final int scanCode, final int modifiers) {
        return null != this._keyboardFocus && this._keyboardFocus.onKeyPressed(this, keyCode, scanCode, modifiers);
    }

    boolean onKeyReleased(final int keyCode, final int scanCode, final int modifiers) {
        return null != this._keyboardFocus && this._keyboardFocus.onKeyReleased(this, keyCode, scanCode, modifiers);
    }

    void validate(final Consumer<Component> errorReport) {
        this._topLevelContainer.validate(errorReport);
    }

    private void updateMouseOverControl(final int clippedX, final int clippedY) {

        final IControl newOver = this._topLevelContainer.findControl(clippedX, clippedY).orElse(null);

        if (this._mouseOver != newOver) {

            if (null != this._mouseOver) {
                this._mouseOver.setMouseOver(false, -1, -1);
            }

            if (this.isDragging() && !(newOver instanceof IDropTarget)) {
                return;
            }

            this._mouseOver = newOver;

            if (null != this._mouseOver) {
                this._mouseOver.setMouseOver(true, clippedX, clippedY);
            }
        }
    }

    private static final Pair<List<Component>, List<Object>> EMPTY_TOOLTIPS = Pair.of(Collections.emptyList(), Collections.emptyList());

    private enum WindowFlags {

        ContentVisible,
        Modal,
        Hidden,
        Focused
    }

    private final IWindowsManager<C> _manager;
    private final IControlContainer _topLevelContainer;
    private final Flags<WindowFlags> _flags;
    private IControl _mouseOver;
    private IControl _keyboardFocus;
}
