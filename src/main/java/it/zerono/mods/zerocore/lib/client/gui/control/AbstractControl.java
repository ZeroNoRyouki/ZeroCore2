/*
 *
 * AbstractControl.java
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

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import it.zerono.mods.zerocore.lib.client.gui.*;
import it.zerono.mods.zerocore.lib.client.gui.layout.ILayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.Flags;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class AbstractControl
        implements IControl {

    //region IControl

    @Override
    public String getName() {
        return this._name;
    }

    @Override
    public ModContainerScreen<? extends ModContainer> getGui() {
        return this._gui;
    }

    @Override
    public Optional<IControl> findControl(final int x, final int y) {
        return this.hitTest(x, y) ? Optional.of(this) : Optional.empty();
    }

    @Override
    public Optional<IControl> findControl(String name) {
        return this._name.equalsIgnoreCase(name) ? Optional.of(this) : Optional.empty();
    }

    @Override
    public boolean containsControl(IControl control) {
        return this == control;
    }

    /**
     * Get this control parent container
     *
     * @return the current control parent container, or null
     */
    @Override
    public Optional<IControl> getParent() {
        return Optional.ofNullable(this._parent);
    }

    /*@Nullable
    @Override
    public IControl getFocus() {
        return this.hasFocus() ? this : null;
    }*/

    /**
     * Set this control parent container
     *
     * @param parent the new parent container or null if this control is not contained in a control container
     */
    @Override
    public void setParent(@Nullable final IControl parent) {

        this._parent = parent;
//        this.setControlOrigin(null != parent ? new ControlOrigin(parent.getOrigin(), parent.getBounds()) : ControlOrigin.ZERO);
        this.setControlOrigin(null != parent ? parent.getOrigin().offset(parent.getBounds().Origin) : Point.ZERO);
    }

    @Override
    public Rectangle getBounds() {
        return null != this._bounds ? this._bounds : Rectangle.ZERO;
    }

    @Override
    public void setBounds(final Rectangle bounds) {

        this._bounds = bounds;
        this.onMoved();
    }

    @Override
    public Point getOrigin() {
        return this._origin;
    }

    @Override
    public int getDesiredDimension(final DesiredDimension dimension) {

        if (null == this._desiredDimension) {
            return DesiredDimension.UNDEFINED_VALUE;
        }

        return DesiredDimension.Width == dimension ? this._desiredDimension.width : this._desiredDimension.height;
    }

    @Override
    public void setDesiredDimension(final DesiredDimension dimension, final int value) {

        if (null == this._desiredDimension) {
            this._desiredDimension = new Dimension(DesiredDimension.UNDEFINED_VALUE, DesiredDimension.UNDEFINED_VALUE);
        }

        switch (dimension) {
            case Width:
                this._desiredDimension.width = value;
                break;

            case Height:
                this._desiredDimension.height = value;
                break;
        }
    }

    @Override
    public void setDesiredDimension(int width, int height) {

        if (null == this._desiredDimension) {

            this._desiredDimension = new Dimension(width, height);

        } else {

            this._desiredDimension.width = width;
            this._desiredDimension.height = height;
        }
    }

    @Override
    public Padding getPadding() {
        return this._padding;
    }

    @Override
    public void setPadding(final int left, final int right, final int top, final int bottom) {
        this._padding = Padding.get(left, right, top, bottom);
    }

    @Override
    public Optional<ILayoutEngine.ILayoutEngineHint> getLayoutEngineHint() {
        return Optional.ofNullable(this._layoutHint);
    }

    @Override
    public void setLayoutEngineHint(final ILayoutEngine.ILayoutEngineHint hint) {
        this._layoutHint = hint;
    }

    @Override
    public boolean hitTest(final int x, final int y) {
        return this.getBounds().contains(x, y);
    }

    @Override
    public void translate(final int xOffset, final int yOffset) {

        this.getBounds().offset(xOffset, yOffset);
        this.onMoved();
    }

    @Override
    public boolean getVisible() {
        return this._flags.contains(ControlFlags.Visible);
    }

    @Override
    public void setVisible(final boolean visible) {
        this._flags.set(ControlFlags.Visible, visible);
    }

    @Override
    public boolean getEnabled() {
        return this._flags.contains(ControlFlags.Enabled);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this._flags.set(ControlFlags.Enabled, enabled);
    }

    @Override
    public boolean canAcceptFocus() {
        return true;
    }

    @Override
    public int getTabOrder() {
        return this._tabOrder;
    }

    @Override
    public void setTabOrder(final int position) {
        this._tabOrder = position;
    }

    @Override
    public boolean getMouseOver() {
        return this._flags.contains(ControlFlags.MouseOver);
    }

    @Override
    public void setMouseOver(final boolean over, final int mouseX, final int mouseY) {
        this._flags.set(ControlFlags.MouseOver, over);
    }

    @Override
    public void setBackground(final ResourceLocation texture) {
        this.setCustomBackgroundHandler((control, matrix) -> {

            ModRenderHelper.bindTexture(texture);
            control.paintTexturedRect(matrix, 0, 0, control.getBounds().Width, control.getBounds().Height, 0, 0);
        });
    }

    @Override
    public void setBackground(final ISprite sprite) {
        this.setCustomBackgroundHandler((control, matrix) -> control.paintSprite(matrix, sprite, 0, 0, this.getWidth(), this.getHeight()));
    }

    @Override
    public void setBackground(final Colour solidColour) {
        this.setCustomBackgroundHandler((control, matrix) ->
                control.paintSolidRect(matrix, 0, 0, control.getBounds().Width, control.getBounds().Height, solidColour));
    }

    @Override
    public void setBackground(final Colour startColour, final Colour endColour) {
        this.setCustomBackgroundHandler(this.getDesiredDimension(DesiredDimension.Height) > this.getDesiredDimension(DesiredDimension.Width) ?
                (c, matrix) -> c.paintHorizontalGradientRect(matrix, startColour, endColour) :
                (c, matrix) -> c.paintVerticalGradientRect(matrix, startColour, endColour));
    }

    @Override
    public void clearBackground() {
        this._backgroundPainter = (control, matrix) -> {};
    }

    @Override
    public List<ITextComponent> getTooltips() {
        return this._tooltipsLines;
    }

    @Override
    public List<Object> getTooltipsObjects() {
        return this._tooltipsObjects;
    }

    @Override
    public void paintToolTips(final MatrixStack matrix, int screenX, int screenY) {

        final RichText rich = this.getTooltipsRichText();

        if (rich.isEmpty()) {
            return;
        }

        final int borderSize = 5;

        final Rectangle richBounds = rich.bounds();
        Rectangle boxBounds = richBounds.expand(borderSize * 2, borderSize * 2)
                                        .offset(screenX + 4, screenY + 8)
                                        .fit(this.getGui().getScreenRectPadded());

        // paint the box background

        final Colour bk = Colour.fromARGB(-267386864);
        final Colour highlight1 = Colour.fromARGB((1347420415 & 16711422) >> 1 | 1347420415 & -16777216);
        final Colour highlight2 = Colour.fromARGB(1347420415);
        final int z = 300;

        ModRenderHelper.paintVerticalLine(matrix, boxBounds.getX1(), boxBounds.getY1() + 1, boxBounds.Height - 2, z, bk);
        ModRenderHelper.paintSolidRect(matrix, boxBounds.getX1() + 1, boxBounds.getY1(), boxBounds.getX2(), boxBounds.getY2() + 1, z, bk);
        ModRenderHelper.paintVerticalLine(matrix, boxBounds.getX2(), boxBounds.getY1() + 1, boxBounds.Height - 2, z, bk);

        ModRenderHelper.paintVerticalGradientLine(matrix, boxBounds.getX1() + 1, boxBounds.getY1() + 1, boxBounds.Height - 2, z, highlight1, highlight2);
        ModRenderHelper.paintHorizontalGradientLine(matrix, boxBounds.getX1() + 2, boxBounds.getY1() + 1, boxBounds.Width - 4, z, highlight1, highlight2);
        ModRenderHelper.paintHorizontalGradientLine(matrix, boxBounds.getX1() + 2, boxBounds.getY2() - 1, boxBounds.Width - 4, z, highlight1, highlight2);
        ModRenderHelper.paintVerticalGradientLine(matrix, boxBounds.getX2() - 1, boxBounds.getY1() + 1, boxBounds.Height - 2, z, highlight1, highlight2);

        // text

        rich.paint(matrix, boxBounds.getX1() + borderSize, boxBounds.getY1() + borderSize, z);
    }

    @Override
    public void setTooltips(final List<ITextComponent> lines) {

        this._tooltipsLines = lines.isEmpty() ? Collections.emptyList() : lines;
        this._tooltipsObjects = Collections.emptyList();
        this._tooltipsRichText = null;
    }

    @Override
    public void setTooltips(final List<ITextComponent> lines, final List<Object> objects) {

        this._tooltipsLines = lines.isEmpty() ? Collections.emptyList() : lines;
        this._tooltipsObjects = lines.isEmpty() || objects.isEmpty() ? Collections.emptyList() : objects;
        this._tooltipsRichText = null;
    }

    @Override
    public void useTooltipsFrom(@Nullable IControl control) {
        this._tooltipsSource = control;
    }

    @Override
    public Point controlToScreen(final int x, final int y) {

        final Point boundsOrigin = this.getBounds().Origin;
        final Point origin = this.getOrigin();

        return new Point(x + boundsOrigin.X + origin.X, y + boundsOrigin.Y + origin.Y);
    }

    @Override
    public Point screenToControl(final int x, final int y) {

        final Point boundsOrigin = this.getBounds().Origin;
        final Point origin = this.getOrigin();

        return new Point(Math.max(0, x - boundsOrigin.X - origin.X), Math.max(0, y - boundsOrigin.Y - origin.Y));
    }

    @Override
    public <Tag> Optional<Tag> getTag() {
        //noinspection unchecked
        return Optional.ofNullable((Tag)this._tag);
    }

    @Override
    public <Tag> void setTag(@Nullable Tag tag) {
        this._tag = tag;
    }

    @Override
    public boolean onMouseMoved(final IWindow wnd, int mouseX, int mouseY) {
        return false;
    }

    /**
     * Event handler - the user has clicked on this control or on one of it's children
     *
     * @param mouseX
     * @param mouseY
     * @param clickedButton the mouse button clicked
     * @return the control itself if it accept the keyboard focus, null otherwise
     */
    @Override
    public boolean onMouseClicked(final IWindow wnd, int mouseX, int mouseY, int clickedButton) {

        this.acquireFocus(wnd);
        return true;
    }

    @Override
    public boolean onMouseReleased(final IWindow wnd, int mouseX, int mouseY, int releasedButton) {
        return false;
    }

    @Override
    public boolean onMouseDragged(final IWindow wnd, int mouseX, int mouseY, int clickedButton, long timeSinceLastClick) {
        return false;
    }

    @Override
    public boolean onMouseWheel(final IWindow wnd, int mouseX, int mouseY, double movement) {
        return false;
    }

    @Override
    public boolean onCharTyped(final IWindow wnd, final char typedChar, final int keyCode) {
        return false;
    }

    @Override
    public boolean onKeyPressed(final IWindow wnd, final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }

    @Override
    public boolean onKeyReleased(final IWindow wnd, final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }

    /**
     * Event handler - this control has gained the keyboard focus
     *
     * @param previousFocus the control who lost the focus. May be null if no control had the keyboard focus
     */
    @Override
    public void onSetFocus(final IWindow wnd, @Nullable final IControl previousFocus) {
        this._flags.add(ControlFlags.Focused);
    }

    /**
     * Event handler - this control has lost the keyboard focus
     *
     * @param newFocus the control who will gain the keyboard focus. May be null if no control would gain the focus
     */
    @Override
    public void onKillFocus(final IWindow wnd, @Nullable final IControl newFocus) {
        this._flags.remove(ControlFlags.Focused);
    }

    @Override
    public void onWindowClosed() {
    }

    @Override
    public void onMoved() {
    }

    @Override
    public void onPaintBackground(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
        this._backgroundPainter.accept(this, matrix);
    }

    @Override
    public void onPaint(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
    }

    @Override
    public void onPaintOverlay(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
    }

    @Override
    public void enablePaintBlending(boolean blend) {
        this._flags.set(ControlFlags.BlendWhenPainting, blend);
    }

    @Override
    public void onPaintDebugFrame(final MatrixStack matrix, final Colour colour) {
        this.paintHollowRect(matrix, 0, 0, this.getBounds().Width, this.getBounds().Height, colour);
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this.toStringBuilder().toString();
    }

    //endregion
    //region paint helpers

    protected void setCustomBackgroundHandler(final BiConsumer<AbstractControl, MatrixStack> handler) {
        this._backgroundPainter = Preconditions.checkNotNull(handler);
    }

    protected Rectangle getPaddingRect() {

        final Padding padding = this.getPadding();

        return new Rectangle(padding.getLeft(), padding.getTop(),
                Math.max(0, this.getBounds().Width - padding.getLeft() - padding.getRight()),
                Math.max(0, this.getBounds().Height - padding.getTop() - padding.getBottom()));
    }

    protected float getZLevel() {
        return this.getGuiZLevel();
    }

    protected final RichText getTooltipsRichText() {

        if (null != this._tooltipsSource) {

            if (this._tooltipsSource instanceof AbstractControl) {
                return ((AbstractControl)this._tooltipsSource).getTooltipsRichText();
            }

            final List<ITextComponent> lines = this._tooltipsSource.getTooltips();

            return lines.isEmpty() ? RichText.EMPTY : buildTooltipsRichText(lines, this._tooltipsSource.getTooltipsObjects());
        }

        if (null == this._tooltipsRichText) {

            final List<ITextComponent> lines = this.getTooltips();

            if (!lines.isEmpty()) {
                this._tooltipsRichText = buildTooltipsRichText(lines, this.getTooltipsObjects());
            }
        }

        return null != this._tooltipsRichText ? this._tooltipsRichText : RichText.EMPTY;
    }

    private static RichText buildTooltipsRichText(final List<ITextComponent> lines, final List<Object> objects) {

        return RichText.builder()
                .textLines(lines)
                .objects(objects)
                .defaultColour(Colour.WHITE)
                //TODO add formatting and other stuff
                .build();
    }

    /**
     * Set the GL viewport to the given coordinates inside this control
     */
    protected void setViewport(final int x, final int y, final int width, final int height) {

        final double scale = this.getGui().getGuiScaleFactor();
        final Point screenXY = this.controlToScreen(x, y + height);

        GlStateManager.viewport(
                (int)Math.round(screenXY.X * scale),
                this.getGui().getMinecraftWindowHeight() - (int)Math.round(screenXY.Y * scale),
                (int)Math.round(width * scale),
                (int)Math.round(height * scale));
    }

    /**
     * Set the GL viewport to the given coordinates inside this control
     */
    protected void setViewport(final Rectangle area) {
        this.setViewport(area.getX1(), area.getY1(), area.Width, area.Height);
    }

    /**
     * Set the GL viewport to this control area
     */
    protected void setViewport() {
        this.setViewport(0, 0, this.getBounds().Width, this.getBounds().Height);
    }

    protected void setDefaultViewport() {
        GlStateManager.viewport(0, 0, this.getGui().getMinecraftWindowWidth(), this.getGui().getMinecraftWindowHeight());
    }

    protected void playSound(final SoundEvent sound) {
        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(sound, 1.0F));
    }

    protected boolean shouldBlend() {
        return this._flags.contains(ControlFlags.BlendWhenPainting);
    }

//    /**
//     * Paint a series of lines in a solid colour.
//     * <p>
//     * The vertices parameter is interpreted as a series of 2 vertex per line (x, y).
//     * The lines don't need to be connected to each others
//     * <p>
//     * If the wrong number of vertices are passed in (not multiple of 2) an ArrayIndexOutOfBoundsException will be raised
//     *
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param colour    the colour to be used to fill the rectangle
//     * @param thickness the thickness of the lines
//     * @param vertices  the vertices of the lines
//     *
//     */
//    protected void paintSolidLines(final Colour colour, final int thickness, int... vertices) {
//        ModRenderHelper.paintSolidLines(colour, thickness, this.getZLevel(), this.convert2DVerticesToScreenCoords(vertices));
//    }

//    /**
//     * Paint a solid color rectangle that fill the entire control area
//     *
//     * @param colour    the colour to be used to fill the rectangle
//     */
//    protected void paintSolidRect(final MatrixStack matrix, final Colour colour) {
//        this.paintSolidRect(matrix, 0, 0, this.getBounds().Width, this.getBounds().Height, colour);
//    }

//    /**
//     * Paint a solid color rectangle with the specified coordinates and colour.
//     *
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param rect      starting coordinates and size of the rectangle
//     * @param colour    the colour to be used to fill the rectangle
//     */
//    protected void paintSolidRect(final MatrixStack matrix, final Rectangle rect, final Colour colour) {
//        this.paintSolidRect(matrix, rect.Origin.X, rect.Origin.Y, rect.Origin.X + rect.Width, rect.Origin.Y + rect.Height, colour);
//    }

//    /**
//     * Paint a solid color rectangle with the specified coordinates and colour.
//     *
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param x1        starting point on the X axis
//     * @param y1        starting point on the Y axis
//     * @param x2        ending point on the X axis (not included in the rectangle)
//     * @param y2        ending point on the Y axis (not included in the rectangle)
//     * @param colour    the colour to be used to fill the rectangle
//     */
////    @Deprecated
////    protected void paintSolidRect(final int x1, final int y1, final int x2, final int y2, final Colour colour) {
////
////        final Point screenXY1 = this.controlToScreen(x1, y1);
////        final Point screenXY2 = this.controlToScreen(x2, y2);
////
////        ModRenderHelper.paintSolidRect(screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, this.getZLevel(), colour);
////    }

    protected void paintSolidRect(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final Colour colour) {
        ModRenderHelper.paintSolidRect(matrix, this.controlToScreen(x1, y1), this.controlToScreen(x2, y2), (int)this.getZLevel(), colour);
    }


    protected void paintVerticalGradientRect(final MatrixStack matrix, final Colour startColour, final Colour endColour) {
        this.paintVerticalGradientRect(matrix, 0, 0, this.getBounds().Width, this.getBounds().Height, startColour, endColour);
    }

    protected void paintVerticalGradientRect(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2,
                                             final Colour startColour, final Colour endColour) {

        final Point screenXY1 = this.controlToScreen(x1, y1);
        final Point screenXY2 = this.controlToScreen(x2, y2);

        ModRenderHelper.paintVerticalGradientRect(matrix, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, this.getZLevel(),
                startColour, endColour);
    }

    protected void paintHorizontalGradientRect(final MatrixStack matrix, final Colour startColour, final Colour endColour) {
        this.paintHorizontalGradientRect(matrix, 0, 0, this.getBounds().Width, this.getBounds().Height, startColour, endColour);
    }

    protected void paintHorizontalGradientRect(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2,
                                             final Colour startColour, final Colour endColour) {

        final Point screenXY1 = this.controlToScreen(x1, y1);
        final Point screenXY2 = this.controlToScreen(x2, y2);

        ModRenderHelper.paintHorizontalGradientRect(matrix, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, this.getZLevel(),
                startColour, endColour);
    }

    /**
     * Paint the perimeter of a rectangle with the specified coordinates and colour.
     * <p>
     * Note: all coordinates are relative to the top-left corner of this control
     *
     * @param x starting point on the X axis
     * @param y starting point on the Y axis
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param colour the colour to be used to paint the perimeter
     */
    protected void paintHollowRect(final MatrixStack matrix, final int x, final int y, final int width, final int height,
                                   final Colour colour) {
        ModRenderHelper.paintHollowRect(matrix, this.controlToScreen(x, y), width, height, (int)this.getZLevel(), colour);
    }

//    /**
//     * Paint the perimeter of a rectangle with the specified coordinates and colour.
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param x1        starting point on the X axis
//     * @param y1        starting point on the Y axis
//     * @param x2        ending point on the X axis (not included in the rectangle)
//     * @param y2        ending point on the Y axis (not included in the rectangle)
//     * @param colour    the colour to be used to paint the perimeter
//     */
//    protected void paintHollowRect(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final Colour colour) {
//
//        final Point screenXY1 = this.controlToScreen(x1, y1);
//        final Point screenXY2 = this.controlToScreen(x2, y2);
//
//        ModRenderHelper.paintHollowRect(matrix, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, (int)this.getZLevel(), colour);
//    }

//    /**
//     * Paint the perimeter of a rectangle with the specified coordinates and colour.
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param rect      starting coordinates and size of the rectangle
//     * @param colour    the colour to be used to fill the rectangle
//     */
//    protected void paintHollowRect(final MatrixStack matrix, final Rectangle rect, final Colour colour) {
//        this.paintHollowRect(matrix, rect.Origin.X, rect.Origin.Y, rect.Origin.X + rect.Width, rect.Origin.Y + rect.Height, colour);
//    }

    /**
     * Paint a 1 pixel wide horizontal line in the provided colour.
     * <p>
     * Note: all coordinates are relative to the top-left corner of this control
     * <p>
     *
     * @param x         starting point on the X axis
     * @param y         starting point on the Y axis
     * @param length    the length of the line
     * @param colour    the colour to be used to paint the line
     */
    protected void paintHorizontalLine(final MatrixStack matrix, final int x, final int y, final int length, final Colour colour) {
        ModRenderHelper.paintHorizontalLine(matrix, this.controlToScreen(x, y), length, (int)this.getZLevel(), colour);
    }

    /**
     * Paint a 1 pixel wide vertical line in the provided colour.
     * <p>
     * Note: all coordinates are relative to the top-left corner of this control
     * <p>
     *
     * @param x         starting point on the X axis
     * @param y         starting point on the Y axis
     * @param length    the length of the line
     * @param colour    the colour to be used to paint the line
     */
    @SuppressWarnings("unused")
    protected void paintVerticalLine(final MatrixStack matrix, final int x, final int y, final int length, final Colour colour) {
        ModRenderHelper.paintVerticalLine(matrix, this.controlToScreen(x, y), length, (int)this.getZLevel(), colour);
    }

//    /**
//     * Paint a rectangle filled with a 3D gradient from a light colour to a dark colour.
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param x1            starting point on the X axis
//     * @param y1            starting point on the Y axis
//     * @param x2            ending point on the X axis (not included in the rectangle)
//     * @param y2            ending point on the Y axis (not included in the rectangle)
//     * @param lightColour   the light colour to be used for the gradient
//     * @param darkColour    the dark colour to be used for the gradient
//     */
//    protected void paint3DGradientRect(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final double zLevel,
//                                           final Colour lightColour, final Colour darkColour) {
//
//        final Point screenXY1 = this.controlToScreen(x1, y1);
//        final Point screenXY2 = this.controlToScreen(x2, y2);
//
//        ModRenderHelper.paint3DGradientRect(matrix, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, this.getZLevel(),
//                lightColour, darkColour);
//    }

    /**
     * Paint a textured rectangle with the specified coordinates and the texture currently bound to the TextureManager.
     * <p>
     * Note: all coordinates are relative to the top-left corner of this control
     *
     * @param x         starting point on the X axis
     * @param y         starting point on the Y axis
     * @param width     the width of the rectangle
     * @param height    the height of the rectangle
     * @param minU      the starting U coordinates of the texture
     * @param minV      the starting V coordinates of the texture
     */
    protected void paintTexturedRect(final MatrixStack matrix, final int x, final int y, final int width, final int height,
                                     final int minU, final int minV) {

        final Point screenXY = this.controlToScreen(x, y);

        ModRenderHelper.paintTexturedRect(matrix, screenXY.X, screenXY.Y, this.getZLevel(), width, height, minU, minV);
    }

//    /**
//     * Paint a textured rectangle with the specified coordinates and the texture currently bound to the TextureManager and using the
//     * provided sprite for the texture coordinates
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param x             starting point on the X axis
//     * @param y             starting point on the Y axis
//     * @param width         the width of the rectangle
//     * @param height        the height of the rectangle
//     * @param textureSprite the sprite associated with the texture
//     */
//    protected void paintTexturedRect(final MatrixStack matrix, final int x, final int y, final int width, final int height,
//                                     final TextureAtlasSprite textureSprite) {
//
//        final Point screenXY = this.controlToScreen(x, y);
//
//        ModRenderHelper.paintTexturedRect(matrix, screenXY.X, screenXY.Y, this.getZLevel(), width, height, textureSprite);
//    }

    //region paintSprite

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given coordinates
     * <p>
     * Note: all coordinates are relative to the top-left corner of this control
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x starting point on the X axis
     * @param y starting point on the Y axis
     */
    protected void paintSprite(final MatrixStack matrix, final ISprite sprite, final int x, final int y) {
        ModRenderHelper.paintSprite(matrix, sprite, this.controlToScreen(x, y), (int)this.getZLevel(), sprite.getWidth(), sprite.getHeight());
    }

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given coordinates
     * <p>
     * Note: all coordinates are relative to the top-left corner of this control
     *
     * @param matrix the MatrixStack for the current paint operation
     * @param sprite the sprite to paint
     * @param x starting point on the X axis
     * @param y starting point on the Y axis
     * @param width the width of the area to paint
     * @param height the height of the area to paint
     */
    protected void paintSprite(final MatrixStack matrix, final ISprite sprite, final int x, final int y,
                               final int width, final int height) {
        ModRenderHelper.paintSprite(matrix, sprite, this.controlToScreen(x, y), (int)this.getZLevel(), width, height);
    }

//    /**
//     * Paint an ISprite from the associated ISpriteTextureMap at the given coordinates
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param matrix the MatrixStack for the current paint operation
//     * @param sprite        the sprite to paint
//     * @param colour        the sprite tint
//     * @param x             starting point on the X axis
//     * @param y             starting point on the Y axis
//     * @param width         the width of the sprite
//     * @param height        the height of the sprite
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     */
//    protected void paintSprite(final ISprite sprite, final Colour colour, final int x, final int y,
//                               final int width, final int height, final boolean bufferOnly) {
//
//        final Point screenXY = this.controlToScreen(x, y);
//
//        ModRenderHelper.paintSprite(sprite, colour, screenXY.X, screenXY.Y, this.getZLevel(), width, height,
//                this.shouldBlend(), bufferOnly);
//    }

    //endregion

//    /**
//     * Paint a rectangle filled with an ISprite up to the indicated progress percentage.
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param x1            starting point on the X axis
//     * @param y1            starting point on the Y axis
//     * @param x2            ending point on the X axis (not included in the rectangle)
//     * @param y2            ending point on the Y axis (not included in the rectangle)
//     * @param progress      a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
//     * @param sprite        the sprite to fill the rect with
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     * @return the width or height of the rect painted
//     */
//    public int paintStretchedProgressSprite(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final double progress,
//                                            final ISprite sprite, final boolean bufferOnly) {
//
//        final Point screenXY1 = this.controlToScreen(x1, y1);
//        final Point screenXY2 = this.controlToScreen(x2, y2);
//
//        return ModRenderHelper.paintStretchedProgressSprite(matrix, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y,
//                this.getZLevel(), progress, sprite, this.shouldBlend(), bufferOnly);
//    }

//    /**
//     * Paint a rectangle filled with an ISprite up to the indicated progress percentage.
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param x1            starting point on the X axis
//     * @param y1            starting point on the Y axis
//     * @param x2            ending point on the X axis (not included in the rectangle)
//     * @param y2            ending point on the Y axis (not included in the rectangle)
//     * @param progress      a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
//     * @param sprite        the sprite to fill the rect with
//     * @param tint          the sprite tint
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     * @return the width or height of the rect painted
//     */
//    public int paintStretchedProgressSprite(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final double progress,
//                                            final ISprite sprite, final Colour tint, final boolean bufferOnly) {
//
//        final Point screenXY1 = this.controlToScreen(x1, y1);
//        final Point screenXY2 = this.controlToScreen(x2, y2);
//
//        return ModRenderHelper.paintStretchedProgressSprite(matrix, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y,
//                this.getZLevel(), progress, sprite, tint, this.shouldBlend(), bufferOnly);
//    }

//    /**
//     * Paint a rectangle filled with an ISprite up to the indicated progress percentage.
//     * <p>
//     * Note: all coordinates are relative to the top-left corner of this control
//     *
//     * @param rect          starting coordinates and size of the rectangle
//     * @param progress      a percentage indicating how much to fill the rect (must be between 0.0 and 1.0)
//     * @param sprite        the sprite to fill the rect with
//     * @param bufferOnly    if false, only the vertex buffer will be updated and nothing will be actually painted
//     * @return the width or height of the rect painted
//     */
//    public int paintStretchedProgressSprite(final MatrixStack matrix, final Rectangle rect, final double progress,
//                                            final ISprite sprite, final boolean bufferOnly) {
//
//        final Point screenXY1 = this.controlToScreen(rect.getX1(), rect.getY1());
//        final Point screenXY2 = this.controlToScreen(rect.getX1() + rect.Width, rect.getY1() + rect.Height);
//
//        return ModRenderHelper.paintStretchedProgressSprite(matrix, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y,
//                this.getZLevel(), progress, sprite, this.shouldBlend(), bufferOnly);
//    }

    protected void paintButton3D(final MatrixStack matrix, final int x, final int y, final int width, final int height,
                                 final Colour darkOutlineColour, final Colour gradientLightColour, final Colour gradientDarkColour,
                                 final Colour borderLightColour, final Colour borderDarkColour) {
        ModRenderHelper.paintButton3D(matrix, this.controlToScreen(x, y), width, height, (int)this.getZLevel(),
                darkOutlineColour, gradientLightColour, gradientDarkColour, borderLightColour, borderDarkColour);
    }

//    @Deprecated
//    protected void paint3DButton(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final Colour darkOutlineColour,
//                                 final Colour gradientLightColour, final Colour gradientDarkColour,
//                                 final Colour borderLightColour, final Colour borderDarkColour) {
//
//        final Point screenXY1 = this.controlToScreen(x1, y1);
//        final Point screenXY2 = this.controlToScreen(x2, y2);
//
//        ModRenderHelper.paint3DButton(matrix, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, this.getZLevel(),
//                darkOutlineColour, gradientLightColour, gradientDarkColour, borderLightColour, borderDarkColour);
//    }

    protected void paint3DSunkenBox(final MatrixStack matrix, final int x1, final int y1, final int x2, final int y2, final Colour gradientLightColour,
                                    final Colour gradientDarkColour, final Colour borderLightColour, final Colour borderDarkColour) {

        final Point screenXY1 = this.controlToScreen(x1, y1);
        final Point screenXY2 = this.controlToScreen(x2, y2);

        ModRenderHelper.paint3DSunkenBox(matrix, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, this.getZLevel(),
                gradientLightColour, gradientDarkColour, borderLightColour, borderDarkColour);
    }

    protected void paintItemStack(final MatrixStack matrix, final ItemStack stack, final boolean highlight) {
        this.paintItemStack(matrix, stack, 0, 0, highlight);
    }

    protected void paintItemStack(final MatrixStack matrix, final ItemStack stack, final int x, final int y, final boolean highlight) {

        final Point screenXY = this.controlToScreen(x, y);

        ModRenderHelper.paintItemStack(matrix, stack, screenXY.X, screenXY.Y, "", highlight);
    }

    protected void paintItemStackWithCount(final MatrixStack matrix, final ItemStack stack, final boolean highlight) {
        this.paintItemStackWithCount(matrix, stack, 0, 0, highlight);
    }

    protected void paintItemStackWithCount(final MatrixStack matrix, final ItemStack stack, final int x, final int y, final boolean highlight) {

        final Point screenXY = this.controlToScreen(x, y);

        ModRenderHelper.paintItemStackWithCount(matrix, stack, screenXY.X, screenXY.Y, highlight);
    }

    //endregion
    //region misc helpers

    protected StringBuilder toStringBuilder() {
        return new StringBuilder()
                .append("id:")
                .append(this.getName())
                .append(" flags:")
                .append(this._flags);
    }

    protected int nextGenericId() {
        return this.getGui().nextGenericId();
    }

    protected String nextGenericName() {
        return this.getGui().nextGenericName();
    }

    protected void enqueueTask(final Runnable runnable) {
        this.getGui().enqueueTask(runnable);
    }

    //endregion
    //region internals

    protected AbstractControl(ModContainerScreen<? extends ModContainer> gui) {
        this(gui, gui.nextGenericName());
    }

    protected AbstractControl(ModContainerScreen<? extends ModContainer> gui, final String name) {

        this._name = name;
        this._gui = gui;
        this._flags = new Flags<>(ControlFlags.class);
        this._parent = null;
        this._bounds = null;
        this._origin = Point.ZERO;
        this._desiredDimension = null;
        this._padding = Padding.ZERO;
        this._layoutHint = null;
        this._tabOrder = -1;
        this._tooltipsLines = Collections.emptyList();
        this._tooltipsObjects = Collections.emptyList();
        this._tooltipsRichText = null;
        this._tag = null;

        this.clearBackground();
        this.setVisible(true);
        this._flags.add(ControlFlags.Enabled);
    }

    protected void requestTickUpdates(final Runnable handler) {
        this.getGui().requestTickUpdates(handler);
    }

    protected float getGuiZLevel() {
        return this._gui.getZLevel();
    }

    protected final boolean hasFocus() {
        return this._flags.contains(ControlFlags.Focused);
    }

    protected void acquireFocus(final IWindow wnd) {

        if (this.canAcceptFocus()) {
            wnd.setFocus(this);
        }
    }

    protected void setControlOrigin(final Point origin) {

        this._origin = origin;
        this.onMoved();
    }

//    private int[] convert2DVerticesToScreenCoords(int[] vertices) {
//
//        final int[] screenVertices = new int[vertices.length];
//
//        for (int i = 0; i < vertices.length; i += 2) {
//
//            final Point screenXY = this.controlToScreen(vertices[i], vertices[i + 1]);
//
//            screenVertices[i] = screenXY.X;
//            screenVertices[i + 1] = screenXY.Y;
//        }
//
//        return screenVertices;
//    }

    private enum ControlFlags {

        Visible,
        Enabled,
        MouseOver,
        Focused,
        BlendWhenPainting,
    }

    private final String _name;
    private final ModContainerScreen<? extends ModContainer> _gui;
    private final Flags<ControlFlags> _flags;
    private IControl _parent;
    private Rectangle _bounds;
    private Point _origin;
    private Dimension _desiredDimension;
    private Padding _padding;
    private ILayoutEngine.ILayoutEngineHint _layoutHint;
    private BiConsumer<AbstractControl, MatrixStack> _backgroundPainter;
    private int _tabOrder;
    private List<ITextComponent> _tooltipsLines;
    private List<Object> _tooltipsObjects;
    private RichText _tooltipsRichText;
    private IControl _tooltipsSource;
    private Object _tag;

    //endregion
}
