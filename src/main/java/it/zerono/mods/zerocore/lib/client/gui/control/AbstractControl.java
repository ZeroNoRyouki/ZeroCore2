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
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

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
    public void setBackground(final ISprite sprite) {
        this.setCustomBackgroundHandler((control, gfx) -> control.paintSprite(gfx, sprite, 0, 0, this.getWidth(), this.getHeight()));
    }

    @Override
    public void setBackground(final Colour solidColour) {
        this.setCustomBackgroundHandler((control, gfx) ->
                control.paintSolidRect(gfx, 0, 0, control.getBounds().Width, control.getBounds().Height, solidColour));
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
    public List<Component> getTooltips() {
        return null != this._tooltipsSource ? this._tooltipsSource.getTooltips() : this._tooltipsLines;
    }

    @Override
    public List<Object> getTooltipsObjects() {
        return null != this._tooltipsSource ? this._tooltipsSource.getTooltipsObjects() : this._tooltipsObjects;
    }

    @Override
    public void paintToolTips(final GuiGraphics gfx, int screenX, int screenY) {

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
        final int z = ModRenderHelper.GUI_TOPMOST_Z;

        ModRenderHelper.paintVerticalLine(gfx, boxBounds.getX1(), boxBounds.getY1() + 1, boxBounds.Height - 2, z, bk);
        ModRenderHelper.paintSolidRect(gfx, boxBounds.getX1() + 1, boxBounds.getY1(), boxBounds.getX2(), boxBounds.getY2() + 1, z, bk);
        ModRenderHelper.paintVerticalLine(gfx, boxBounds.getX2(), boxBounds.getY1() + 1, boxBounds.Height - 2, z, bk);

        ModRenderHelper.paintVerticalGradientLine(gfx, boxBounds.getX1() + 1, boxBounds.getY1() + 1, boxBounds.Height - 2, z, highlight1, highlight2);
        ModRenderHelper.paintHorizontalGradientLine(gfx, boxBounds.getX1() + 2, boxBounds.getY1() + 1, boxBounds.Width - 4, z, highlight1, highlight2);
        ModRenderHelper.paintHorizontalGradientLine(gfx, boxBounds.getX1() + 2, boxBounds.getY2() - 1, boxBounds.Width - 4, z, highlight1, highlight2);
        ModRenderHelper.paintVerticalGradientLine(gfx, boxBounds.getX2() - 1, boxBounds.getY1() + 1, boxBounds.Height - 2, z, highlight1, highlight2);

        // text

        rich.paint(gfx, boxBounds.getX1() + borderSize, boxBounds.getY1() + borderSize, z);
    }

    @Override
    public void setTooltips(final List<Component> lines) {

        this._tooltipsLines = lines.isEmpty() ? Collections.emptyList() : lines;
        this._tooltipsObjects = Collections.emptyList();
        this._tooltipsRichText = null;
        this._tooltipsSource = null;
    }

    @Override
    public void setTooltips(final List<Component> lines, final List<Object> objects) {

        this._tooltipsLines = lines.isEmpty() ? Collections.emptyList() : lines;
        this._tooltipsObjects = lines.isEmpty() || objects.isEmpty() ? Collections.emptyList() : objects;
        this._tooltipsRichText = null;
        this._tooltipsSource = null;
    }

    @Override
    public void useTooltipsFrom(@Nullable IControl control) {
        this._tooltipsSource = control;
    }

    @Override
    public void clearTooltips() {

        this._tooltipsSource = null;
        this._tooltipsLines = Collections.emptyList();
        this._tooltipsObjects = Collections.emptyList();
        this._tooltipsRichText = null;
    }

    @Override
    public Point controlToScreen(final int x, final int y) {

        final Point boundsOrigin = this.getBounds().Origin;
        final Point origin = this.getOrigin();

        return new Point(x + boundsOrigin.X + origin.X, y + boundsOrigin.Y + origin.Y);
    }

    @Override
    public void controlToScreen(final double[] xs, final double[] ys) {

        if (xs.length != ys.length) {
            throw new IllegalArgumentException("The coordinates arrays must be of the same size.");
        }

        final double deltaX = this.getBounds().Origin.X + this.getOrigin().X;
        final double deltaY = this.getBounds().Origin.Y + this.getOrigin().Y;

        for (int i = 0; i < xs.length; ++i) {

            xs[i] += deltaX;
            ys[i] += deltaY;
        }
    }

    @Override
    public Point screenToControl(final int x, final int y) {

        final Point boundsOrigin = this.getBounds().Origin;
        final Point origin = this.getOrigin();

        return new Point(Math.max(0, x - boundsOrigin.X - origin.X), Math.max(0, y - boundsOrigin.Y - origin.Y));
    }

    @Override
    public void screenToControl(final double[] xs, final double[] ys) {

        if (xs.length != ys.length) {
            throw new IllegalArgumentException("The coordinates arrays must be of the same size.");
        }

        final double deltaX = this.getBounds().Origin.X + this.getOrigin().X;
        final double deltaY = this.getBounds().Origin.Y + this.getOrigin().Y;

        for (int i = 0; i < xs.length; ++i) {

            xs[i] = Math.max(0, xs[i] - deltaX);
            ys[i] = Math.max(0, ys[i] - deltaY);
        }
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
    public Theme getTheme() {
        return this._gui.getTheme();
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
     * @return the control itself if it accepts the keyboard focus, null otherwise
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
    public void onPaintBackground(final GuiGraphics gfx, final float partialTicks, final int mouseX, final int mouseY) {
        this._backgroundPainter.accept(this, gfx);
    }

    @Override
    public void onPaint(final GuiGraphics gfx, final float partialTicks, final int mouseX, final int mouseY) {
    }

    @Override
    public void onPaintOverlay(final GuiGraphics gfx, final float partialTicks, final int mouseX, final int mouseY) {
    }

    @Override
    public void enablePaintBlending(boolean blend) {
        this._flags.set(ControlFlags.BlendWhenPainting, blend);
    }

    @Override
    public void onPaintDebugFrame(final GuiGraphics gfx, final Colour colour) {

        this.paintHollowRect(gfx, 0, 0, this.getBounds().Width, this.getBounds().Height, colour);

        if (Screen.hasShiftDown()) {
            gfx.renderTooltip(this.getGui().getFont(), TextHelper.literal(this.getName()), 0, 20);
        }
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this.toStringBuilder().toString();
    }

    //endregion
    //region paint helpers

    protected void setCustomBackgroundHandler(final BiConsumer<AbstractControl, GuiGraphics> handler) {
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

        if (this._tooltipsSource instanceof AbstractControl) {
            return ((AbstractControl)this._tooltipsSource).getTooltipsRichText();
        }

        if (null == this._tooltipsRichText) {

            final List<Component> lines = this.getTooltips();

            if (!lines.isEmpty()) {
                this._tooltipsRichText = buildTooltipsRichText(lines, this.getTooltipsObjects(), this.getTooltipsPopupMaxWidth());
            }
        }

        return null != this._tooltipsRichText ? this._tooltipsRichText : RichText.EMPTY;
    }

    private static RichText buildTooltipsRichText(final List<Component> lines, final List<Object> objects,
                                                  final int popupMaxWidth) {

        return RichText.builder(popupMaxWidth)
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

        GlStateManager._viewport(
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
        GlStateManager._viewport(0, 0, this.getGui().getMinecraftWindowWidth(), this.getGui().getMinecraftWindowHeight());
    }

    protected void playSound(final SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    protected void playSound(final Holder<SoundEvent> sound) {
        this.playSound(sound.value());
    }

    protected boolean shouldBlend() {
        return this._flags.contains(ControlFlags.BlendWhenPainting);
    }

    protected void paintSolidRect(final GuiGraphics gfx, final int x1, final int y1, final int x2, final int y2, final Colour colour) {
        ModRenderHelper.paintSolidRect(gfx, this.controlToScreen(x1, y1), this.controlToScreen(x2, y2), (int)this.getZLevel(), colour);
    }

    protected void paintVerticalGradientRect(final GuiGraphics gfx, final Colour startColour, final Colour endColour) {
        this.paintVerticalGradientRect(gfx, 0, 0, this.getBounds().Width, this.getBounds().Height, startColour, endColour);
    }

    protected void paintVerticalGradientRect(final GuiGraphics gfx, final int x1, final int y1, final int x2, final int y2,
                                             final Colour startColour, final Colour endColour) {

        final Point screenXY1 = this.controlToScreen(x1, y1);
        final Point screenXY2 = this.controlToScreen(x2, y2);

        ModRenderHelper.paintVerticalGradientRect(gfx, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, this.getZLevel(),
                startColour, endColour);
    }

    protected void paintHorizontalGradientRect(final GuiGraphics gfx, final Colour startColour, final Colour endColour) {
        this.paintHorizontalGradientRect(gfx, 0, 0, this.getBounds().Width, this.getBounds().Height, startColour, endColour);
    }

    protected void paintHorizontalGradientRect(final GuiGraphics gfx, final int x1, final int y1, final int x2, final int y2,
                                             final Colour startColour, final Colour endColour) {

        final Point screenXY1 = this.controlToScreen(x1, y1);
        final Point screenXY2 = this.controlToScreen(x2, y2);

        ModRenderHelper.paintHorizontalGradientRect(gfx, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, this.getZLevel(),
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
    protected void paintHollowRect(final GuiGraphics gfx, final int x, final int y, final int width, final int height,
                                   final Colour colour) {
        ModRenderHelper.paintHollowRect(gfx, this.controlToScreen(x, y), width, height, (int)this.getZLevel(), colour);
    }

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
    protected void paintHorizontalLine(final GuiGraphics gfx, final int x, final int y, final int length, final Colour colour) {
        ModRenderHelper.paintHorizontalLine(gfx, this.controlToScreen(x, y), length, (int)this.getZLevel(), colour);
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
    protected void paintVerticalLine(final GuiGraphics gfx, final int x, final int y, final int length, final Colour colour) {
        ModRenderHelper.paintVerticalLine(gfx, this.controlToScreen(x, y), length, (int)this.getZLevel(), colour);
    }

    //region paintSprite

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given coordinates
     * <p>
     * Note: all coordinates are relative to the top-left corner of this control
     *
     * @param gfx the GuiGraphics for the current paint operation
     * @param sprite the sprite to paint
     * @param x starting point on the X axis
     * @param y starting point on the Y axis
     */
    protected void paintSprite(final GuiGraphics gfx, final ISprite sprite, final int x, final int y) {
        ModRenderHelper.paintSprite(gfx, sprite, this.controlToScreen(x, y), (int)this.getZLevel(), sprite.getWidth(), sprite.getHeight());
    }

    /**
     * Paint an ISprite from the associated ISpriteTextureMap at the given coordinates
     * <p>
     * Note: all coordinates are relative to the top-left corner of this control
     *
     * @param gfx the GuiGraphics for the current paint operation
     * @param sprite the sprite to paint
     * @param x starting point on the X axis
     * @param y starting point on the Y axis
     * @param width the width of the area to paint
     * @param height the height of the area to paint
     */
    protected void paintSprite(final GuiGraphics gfx, final ISprite sprite, final int x, final int y,
                               final int width, final int height) {
        ModRenderHelper.paintSprite(gfx, sprite, this.controlToScreen(x, y), (int)this.getZLevel(), width, height);
    }

    //endregion

    protected void paintButton3D(final GuiGraphics gfx, final int x, final int y, final int width, final int height,
                                 final Colour darkOutlineColour, final Colour gradientLightColour, final Colour gradientDarkColour,
                                 final Colour borderLightColour, final Colour borderDarkColour) {

        if (gradientLightColour.equals(gradientDarkColour)) {
            ModRenderHelper.paintButton3D(gfx, this.controlToScreen(x, y), width, height, (int) this.getZLevel(),
                    darkOutlineColour, gradientLightColour, borderLightColour, borderDarkColour);
        } else {
            ModRenderHelper.paintButton3D(gfx, this.controlToScreen(x, y), width, height, (int) this.getZLevel(),
                    darkOutlineColour, gradientLightColour, gradientDarkColour, borderLightColour, borderDarkColour);
        }
    }

    protected void paint3DSunkenBox(final GuiGraphics gfx, final int x1, final int y1, final int x2, final int y2, final Colour gradientLightColour,
                                    final Colour gradientDarkColour, final Colour borderLightColour, final Colour borderDarkColour) {

        final Point screenXY1 = this.controlToScreen(x1, y1);
        final Point screenXY2 = this.controlToScreen(x2, y2);

        if (gradientLightColour.equals(gradientDarkColour)) {
            ModRenderHelper.paint3DSunkenBox(gfx, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, this.getZLevel(),
                    gradientLightColour, borderLightColour, borderDarkColour);
        } else {
            ModRenderHelper.paint3DSunkenBox(gfx, screenXY1.X, screenXY1.Y, screenXY2.X, screenXY2.Y, this.getZLevel(),
                    gradientLightColour, gradientDarkColour, borderLightColour, borderDarkColour);
        }
    }

    protected void paintItemStack(final GuiGraphics gfx, final ItemStack stack, final boolean highlight) {
        this.paintItemStack(gfx, stack, 0, 0, highlight);
    }

    protected void paintItemStack(final GuiGraphics gfx, final ItemStack stack, final int x, final int y, final boolean highlight) {

        final Point screenXY = this.controlToScreen(x, y);

        ModRenderHelper.paintItemStack(gfx, stack, screenXY.X, screenXY.Y, "", highlight);
    }

    protected void paintItemStackWithCount(final GuiGraphics gfx, final ItemStack stack, final boolean highlight) {
        this.paintItemStackWithCount(gfx, stack, 0, 0, highlight);
    }

    protected void paintItemStackWithCount(final GuiGraphics gfx, final ItemStack stack, final int x, final int y, final boolean highlight) {

        final Point screenXY = this.controlToScreen(x, y);

        ModRenderHelper.paintItemStackWithCount(gfx, stack, screenXY.X, screenXY.Y, highlight);
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
    private BiConsumer<AbstractControl, GuiGraphics> _backgroundPainter;
    private int _tabOrder;
    private List<Component> _tooltipsLines;
    private List<Object> _tooltipsObjects;
    private RichText _tooltipsRichText;
    private IControl _tooltipsSource;
    private Object _tag;

    //endregion
}
