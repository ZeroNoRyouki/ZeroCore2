/*
 *
 * TextControl.java
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

import com.google.common.collect.Lists;
import it.zerono.mods.zerocore.lib.client.gui.IWindow;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.Theme;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings({"WeakerAccess", "unused"})
public class TextInput
        extends AbstractTextualControl {

    public final IEvent<BiConsumer<TextInput, Boolean>> Changed; // 2nd arg: true if the text was changed by the user typing, false if by setText()

    public TextInput(final ModContainerScreen<? extends ModContainer> gui, final String name) {
        this(gui, name, "");
    }

    public TextInput(final ModContainerScreen<? extends ModContainer> gui, final String name, final Component text) {
        this(gui, name, text./*getFormattedText*/getString());
    }

    public TextInput(final ModContainerScreen<? extends ModContainer> gui, final String name, final String text) {

        super(gui, name, text);

        this._editable = true;
        this._maxLength = 0; // no limit
        this._paintingSuffix = "";
        this.Changed = new Event<>();

        this.setPadding(2, 2, 2, 2);
        this.setHorizontalAlignment(HorizontalAlignment.Left);
        this.setVerticalAlignment(VerticalAlignment.Center);
        this.setEditable(true);
        this.caretMoved();
    }

    public boolean isEditable() {
        return this._editable;
    }

    public void setEditable(boolean editable) {
        this._editable = editable;
    }

    public void setDisplaySuffix(@Nullable final String suffix) {

        this._paintingSuffix = suffix;
        this._paintingCache = null;
    }

    public void setFilter(final Predicate<Character> filter) {
        this._charFilter = filter;
    }

    public void addConstraint(final Function<String, Optional<String>> constraint) {

        if (null == this._constraints) {
            this._constraints = Lists.newArrayList();
        }

        this._constraints.add(constraint);
    }

    public void removeFilter() {
        this._charFilter = null;
    }

    public int getMaxLength() {
        return this._maxLength;
    }

    public void setMaxLength(final int length) {

        this._maxLength = Math.max(0, length);

        if (0 != this._maxLength && this._textBuffer.length() > this._maxLength) {
            this._textBuffer = new StringBuilder(this._textBuffer.substring(0, this._maxLength));
        }
    }

    public int intValue() throws NumberFormatException {
        return Integer.parseInt(this.getText());
    }

    public long longValue() throws NumberFormatException {
        return Long.parseLong(this.getText());
    }

    public float floatValue() throws NumberFormatException {
        return Float.parseFloat(this.getText());
    }

    public double doubleValue() throws NumberFormatException {
        return Double.parseDouble(this.getText());
    }

    //region AbstractTextualControl

    @Override
    public void onWindowClosed() {

        super.onWindowClosed();
        this.Changed.unsubscribeAll();
    }

    @Override
    public String getText() {

        if (null == this._textCache) {
            this._textCache = this._textBuffer.toString();
        }

        return this._textCache;
    }

    @Override
    public void setText(final String text) {

        this._textBuffer = text.chars()
                .mapToObj(i -> (char)i)
                .filter(this.getCharFilter())
                .limit(this._maxLength > 0 ? this._maxLength : text.length())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

        this._caretCharIndex = this._textBuffer.length();

        if (this._displayCharIndex >= this._caretCharIndex) {

            this._displayCharIndex = this._caretCharIndex -1;

            if (this._displayCharIndex < 0) {
                this._displayCharIndex = 0;
            }
        }

        this.textChanged(false);
    }

//    @Override
//    public boolean onMouseClicked(final IWindow wnd, final int mouseX, final int mouseY, final int clickedButton) {
//
//        if (this.isEditable()) {
//
//            if (CodeHelper.MOUSE_BUTTON_RIGHT == clickedButton) {
//
//                this.setText("");
//                this.applyConstraints();
//            }
//
//            this.caretMoved();
//        }
//
//        return super.onMouseClicked(wnd, mouseX, mouseY, clickedButton);
//    }

    @Override
    public boolean onCharTyped(final IWindow wnd, final char typedChar, final int keyCode) {

        if (!this.isEditable() || 0 == typedChar) {
            return false; // ignore it
        }

        if ((0 == this._maxLength || (this._maxLength > 0 && this._textBuffer.length() < this._maxLength)) && this.getCharFilter().test(typedChar)) {

            this._textBuffer.insert(this._caretCharIndex, typedChar);
            ++this._caretCharIndex;
            this.textChanged(true);
            this.caretMoved();
        }

        return true;
    }

    @Override
    public boolean onKeyPressed(final IWindow wnd, final int keyCode, final int scanCode, final int modifiers) {

        if (!this.isEditable()) {
            return false; // ignore it
        }

        if (GLFW.GLFW_KEY_BACKSPACE == keyCode) {

            // delete the character before the caret

            if (this._textBuffer.length() > 0 && this._caretCharIndex > 0) {

                this._textBuffer.deleteCharAt(--this._caretCharIndex);

                if (this._displayCharIndex > 0) {
                    --this._displayCharIndex;
                }

                this.textChanged(true);
                this.caretMoved();
            }

        } else if (GLFW.GLFW_KEY_DELETE == keyCode) {

            // delete the character at the caret

            if (this._caretCharIndex < this._textBuffer.length()) {

                this._textBuffer.deleteCharAt(this._caretCharIndex);
                this._caretCharIndex = Math.min(this._caretCharIndex, this._textBuffer.length());

                if (this._displayCharIndex > 0) {
                    --this._displayCharIndex;
                }

                this.textChanged(true);
                this.caretMoved();
            }

        } else if (GLFW.GLFW_KEY_HOME == keyCode) {

            this._caretCharIndex = 0;
            this.caretMoved();

        } else if (GLFW.GLFW_KEY_END == keyCode) {

            this._caretCharIndex = this._textBuffer.length();
            this.caretMoved();

        } else if (GLFW.GLFW_KEY_LEFT == keyCode) {

            if (this._caretCharIndex > 0) {

                --this._caretCharIndex;
                this.caretMoved();
            }

        } else if (GLFW.GLFW_KEY_RIGHT == keyCode) {

            if (this._caretCharIndex < this._textBuffer.length()) {

                ++this._caretCharIndex;
                this.caretMoved();
            }

        } else if (GLFW.GLFW_KEY_ENTER == keyCode || GLFW.GLFW_KEY_KP_ENTER == keyCode || GLFW.GLFW_KEY_ESCAPE == keyCode) {

            return false;
        }

        return super.onKeyPressed(wnd, keyCode, scanCode, modifiers);
    }

    @Override
    public void onPaintBackground(final GuiGraphics gfx, final float partialTicks, final int mouseX, final int mouseY) {

        final Theme theme = this.getTheme();

        this.paint3DSunkenBox(gfx, 0, 0, this.getBounds().Width, this.getBounds().Height,
                theme.TEXTFIELD_NORMAL_3D_GRADIENT_LIGHT, theme.TEXTFIELD_NORMAL_3D_GRADIENT_DARK,
                theme.TEXTFIELD_NORMAL_3D_BORDER_LIGHT, theme.TEXTFIELD_NORMAL_3D_BORDER_DARK
        );
    }

    @Override
    public void onPaint(final GuiGraphics gfx, float partialTicks, final int mouseX, final int mouseY) {

        this.ensureVisible();
        this.paintTextLine(gfx, this.getTextForPainting(), 0, 0, this.getTextAreaWidth(),
                this.getTextAreaHeight(), this.getEnabled() ? this.getColor() : this.getDisabledColor());

        if (this.hasFocus()) {
            this.paintCaret(gfx);
        }
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append("; buffer:[")
                .append(this._textBuffer)
                .append("]; textCache:[")
                .append(null != this._textCache ? this._textCache : "")
                .append("]; paintCache:[")
                .append(null != this._paintingCache ? this._paintingCache : "")
                .append("]; maxLen:")
                .append(this._maxLength)
                .append("; editable:")
                .append(this._editable);
    }

    //endregion
    //region internals

    private void textChanged(final boolean changedByTyping) {

        this._textCache = null;
        this.resetPaintingCache();

        if (changedByTyping) {
            this.applyConstraints();
        }

        if (null != this.Changed) {
            // the Event instance is not yet created when the initial text of the control is set
            this.Changed.raise(c -> c.accept(this, changedByTyping));
        }
    }

    private void applyConstraints() {

        if (null == this._constraints) {
            return;
        }

        final String text = this.getText();
        String current = text;

        for (final Function<String, Optional<String>> constraint : this._constraints) {
            current = constraint.apply(current).orElse(current);
        }

        if (!text.equals(current)) {
            this.setText(current);
        }
    }

    private void resetPaintingCache() {
        this._paintingCache = null;
    }

    private void caretMoved() {
        this._caretBlinkTimer = System.currentTimeMillis();
    }

    protected String getTextForPainting() {

        if (null == this._paintingCache) {
            this._paintingCache = this.suffixedText().substring(this._displayCharIndex);
        }

        return this._paintingCache;
    }

    private String suffixedText() {
        return null != this._paintingSuffix ? this.getText() + this._paintingSuffix : this.getText();
    }

    private void ensureVisible() {

        if (this._caretCharIndex < this._displayCharIndex) {

            this._displayCharIndex = this._caretCharIndex;

        } else {

            final Font font = this.getFontRender();
            final String text = this.getText();
            final int textAreaWidth = this.getTextAreaWidth();

            int visibleTextWidth = this.getLineWidth(text.substring(this._displayCharIndex, this._caretCharIndex));

            while (visibleTextWidth > textAreaWidth) {

//                visibleTextWidth -= font.getCharWidth(text.charAt(this._displayCharIndex));
                // ugly as hell...
                visibleTextWidth -= font.width(text.substring(this._displayCharIndex, this._displayCharIndex + 1));

                ++this._displayCharIndex;
            }
        }

        this.resetPaintingCache();
    }

    private void paintCaret(final GuiGraphics gfx) {

        if (((this._caretBlinkTimer - System.currentTimeMillis()) / 500) % 2 != 0) {
            return;
        }

        final int x = Math.max(1, this.getLineWidth(this.getText().substring(this._displayCharIndex, this._caretCharIndex)));

        this.paintSolidRect(gfx, x, 2, x + 1, 2 + this.getTextAreaHeight() - 1, this.getTheme().TEXTFIELD_CARET);
    }

    private Predicate<Character> getCharFilter() {
        return null != this._charFilter ? this._charFilter : DEFAULT_CHAR_FILTER;
    }

    private static final Predicate<Character> DEFAULT_CHAR_FILTER = character -> true;

    private StringBuilder _textBuffer;
    private String _textCache;
    private String _paintingSuffix;
    private String _paintingCache;
    private int _displayCharIndex;
    private int _caretCharIndex;
    private long _caretBlinkTimer;
    private boolean _editable;
    private Predicate<Character> _charFilter = DEFAULT_CHAR_FILTER;
    private List<Function<String, Optional<String>>> _constraints;
    private int _maxLength;

    //endregion
}
