/*
 *
 * AbstractTextualControl.java
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
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractTextualControl
        extends AbstractControl {

    public AbstractTextualControl(ModContainerScreen<? extends ModContainer> gui, String name, final String text) {

        super(gui, name);
        this._fontRender = Minecraft.getInstance().font;
        this.setHorizontalAlignment(HorizontalAlignment.Left);
        this.setVerticalAlignment(VerticalAlignment.Center);
        this.setText(text);
        this.setColor(Theme.TEXT_ENABLED_COLOR);
        this.setDisabledColor(Theme.TEXT_DISABLED_COLOR);
    }

    public String getText() {
        return this._text;
    }

    public void setText(final String text) {
        this._text = text;
    }

    public void setText(final String formatString, final Object... parameters) {
        this.setText(String.format(formatString, parameters));
    }

    public void setText(final ITextComponent text) {
        this.setText(text./*getFormattedText*/getString());
    }

    public Colour getColor() {
        return this._enabledColor;
    }

    public void setColor(final Colour color) {
        this._enabledColor = color;
    }

    public Colour getDisabledColor() {
        return this._disabledColor;
    }

    public void setDisabledColor(final Colour color) {
        this._disabledColor = color;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return this._horizontalAlignment;
    }

    public void setHorizontalAlignment(final HorizontalAlignment alignment) {
        this._horizontalAlignment = alignment;
    }

    public VerticalAlignment getVerticalAlignment() {
        return this._verticalAlignment;
    }

    public void setVerticalAlignment(final VerticalAlignment alignment) {
        this._verticalAlignment = alignment;
    }

    public int getTextWidth() {
        return getLineWidth(this.getText());
    }

    public int getTextHeight() {
        return this.getFontRender().lineHeight - 1;
    }

    protected void paintTextLine(final MatrixStack matrix, String line, int x, int y, int lineAreaWidth,
                                 int lineAreaHeight, final Colour color) {

        final FontRenderer font = this.getFontRender();

        line = font./*trimStringToWidth*/plainSubstrByWidth(line, lineAreaWidth);

        if (line.isEmpty()) {
            return;
        }

        this.paintText(matrix, font, color, line,
                this.getHorizontalAlignment().align(x, this.getLineWidth(line), lineAreaWidth) + this.getPadding().getLeft() + this.getTextOffsetX(),
                this.getVerticalAlignment().align(y, this.getLineHeight(line), lineAreaHeight) + this.getPadding().getTop() + this.getTextOffsetY());
    }

    protected void paintText(final MatrixStack matrix, final FontRenderer render, final Colour colour, final String text, final int x, final int y) {

        if (!Strings.isNullOrEmpty(text)) {

            final Point screenXY = this.controlToScreen(x, y);

            render.draw(matrix, text, screenXY.X, screenXY.Y, colour.toARGB());
        }
    }

    protected FontRenderer getFontRender() {
        return this._fontRender;
    }

    protected int getTextOffsetX() {
        return 0;
    }

    protected int getTextOffsetY() {
        return 0;
    }

    protected int getTextAreaWidth() {
        return Math.max(0, this.getBounds().Width - (this.getPadding().getHorizontal() + this.getTextOffsetX()));
    }

    protected int getTextAreaHeight() {
        return Math.max(0, this.getBounds().Height - (this.getPadding().getVertical() + this.getTextOffsetY()));
    }

    protected int getLineWidth(final String line) {
        return this.getFontRender().width(line);
    }

    protected int getLineHeight(@SuppressWarnings("unused") final String line) {
        return this.getTextHeight();
    }

    //region AbstractControl

    @Override
    public void onPaint(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
        this.paintTextLine(matrix, this.getText(), 0, 0, this.getTextAreaWidth(), this.getTextAreaHeight(),
                this.getEnabled() ? this.getColor() : this.getDisabledColor());
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append("; text:[")
                .append(this._text)
                .append("]");
    }

    //endregion
    //region internals

    private final FontRenderer _fontRender;
    private String _text;
    private HorizontalAlignment _horizontalAlignment;
    private VerticalAlignment _verticalAlignment;
    private Colour _enabledColor;
    private Colour _disabledColor;

    //endregion
}
