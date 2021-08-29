/*
 *
 * IControl.java
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

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.zerono.mods.zerocore.lib.client.gui.layout.ILayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * A single control in a GUI
 * <p>
 * The coordinates system used by a control is always relative to it's parent control container
 */
@SuppressWarnings("unused")
public interface IControl {

    String getName();

    ModContainerScreen<? extends ModContainer> getGui();

    /**
     * Find the control that contains the point (x,y)
     */
    Optional<IControl> findControl(int x, int y);

    Optional<IControl> findControl(String name);

    boolean containsControl(IControl control);

    /**
     * Get this control parent container
     *
     * @return the current control parent container, or null
     */
    Optional<IControl> getParent();

    /*
    @Nullable
    IControl getFocus();*/

    /**
     * Set this control parent container
     *
     * @param parent the new parent container or null if this control is not contained in a control container
     */
    void setParent(@Nullable IControl parent);

    Rectangle getBounds();

//    Rectangle getScreenBounds();

    void setBounds(Rectangle bounds);

    default int getWidth() {
        return this.getBounds().Width;
    }

    default int getHeight() {
        return this.getBounds().Height;
    }

    Point getOrigin();

    int getDesiredDimension(DesiredDimension dimension);

    void setDesiredDimension(DesiredDimension dimension, int value);

    void setDesiredDimension(int width, int height);

    Padding getPadding();

    void setPadding(int left, int right, int top, int bottom);

    default void setPadding(int value) {
        this.setPadding(value, value, value, value);
    }

    Optional<ILayoutEngine.ILayoutEngineHint> getLayoutEngineHint();

    void setLayoutEngineHint(ILayoutEngine.ILayoutEngineHint hint);

    /**
     * Check if the point (x,y) is inside this control bounds
     *
     * @param x
     * @param y
     * @return true if the point is inside this control bounds, false otherwise
     */
    boolean hitTest(int x, int y);

    void translate(int xOffset, int yOffset);

    boolean getVisible();

    void setVisible(boolean visible);

    boolean getEnabled();

    void setEnabled(boolean enabled);

    boolean canAcceptFocus();

    int getTabOrder();

    void setTabOrder(int position);

    boolean getMouseOver();

    void setMouseOver(boolean over, int mouseX, int mouseY);

    void setBackground(ResourceLocation texture);

    void setBackground(ISprite sprite);

    void setBackground(Colour solidColour);

    void setBackground(Colour startColour, Colour endColour);

    void clearBackground();

    /**
     * Retrieve a list of text lines to show in this control tooltips ballon
     *
     * @return a list of text lines or an empty list if no tooltips are available
     */
    List<ITextComponent> getTooltips();

    /**
     * Retrieve a list of objects to fill the placeholders in the strings returned by {@code getTooltips}
     *
     * @return a list of objects or an empty list if no objects are available
     */
    List<Object> getTooltipsObjects();

    /**
     * Return the maximum width for the tooltips popup of this control
     *
     * @return the maximum width in pixels or -1 to not wrap the tooltips text
    */
    default int getTooltipsPopupMaxWidth() {
        return this.getGui().getTooltipsPopupMaxWidth();
    }

    void paintToolTips(MatrixStack matrix, int screenX, int screenY);

    default void setTooltips(ITextComponent... lines) {
        this.setTooltips(ImmutableList.copyOf(lines));
    }

    void setTooltips(List<ITextComponent> lines);

    void setTooltips(List<ITextComponent> lines, List<Object> objects);

    void useTooltipsFrom(@Nullable IControl control);

    Point controlToScreen(int x, int y);

    Point screenToControl(int x, int y);

    <Tag> Optional<Tag> getTag();

    <Tag> void setTag(@Nullable Tag tag);

    boolean onMouseMoved(IWindow wnd, int mouseX, int mouseY);

    /**
     * Event handler - the user has clicked on this control or on one of it's children
     *
     * @param mouseX
     * @param mouseY
     * @param clickedButton the mouse button clicked
     * @return true if the event was handled, false otherwise
     */
    boolean onMouseClicked(IWindow wnd, int mouseX, int mouseY, int clickedButton);

    boolean onMouseReleased(IWindow wnd, int mouseX, int mouseY, int releasedButton);

    boolean onMouseDragged(IWindow wnd, int mouseX, int mouseY, int clickedButton, long timeSinceLastClick);

    boolean onMouseWheel(IWindow wnd, int mouseX, int mouseY, double movement);

    /**
     * Event handler - the user has pressed a key on the keyboard on this control or on one of it's children
     *
     * @param typedChar the character typed
     * @param keyCode   they scan code of the pressed key
     * @return true if the event was handled, false otherwise
     */
    boolean onCharTyped(IWindow wnd, char typedChar, int keyCode);

    boolean onKeyPressed(IWindow wnd, int keyCode, int scanCode, int modifiers);

    boolean onKeyReleased(IWindow wnd, int keyCode, int scanCode, int modifiers);

    /**
     * Event handler - this control has gained the keyboard focus
     *
     * @param previousFocus the control who lost the focus. May be null if no control had the keyboard focus
     */
    void onSetFocus(IWindow wnd, @Nullable IControl previousFocus);

    /**
     * Event handler - this control has lost the keyboard focus
     *
     * @param newFocus the control who will gain the keyboard focus. May be null if no control would gain the focus
     */
    void onKillFocus(IWindow wnd, @Nullable IControl newFocus);

    void onWindowClosed();

    /**
     * Event handler - this control was moved to another position, it's bounds changed or it's control-origin changed
     */
    void onMoved();

    void onPaintBackground(MatrixStack matrix, float partialTicks, int mouseX, int mouseY);

    void onPaint(MatrixStack matrix, float partialTicks, int mouseX, int mouseY);

    void onPaintOverlay(MatrixStack matrix, float partialTicks, int mouseX, int mouseY);

    void enablePaintBlending(boolean blend);

    default void onPaintDebugFrame(MatrixStack matrix, Colour colour) {
    }
}
