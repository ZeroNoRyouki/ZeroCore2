/*
 *
 * RichText.java
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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.*;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class RichText
    implements IRichText {

    public static final RichText EMPTY = new RichText();
    public static final int NO_MAX_WIDTH = -1;

    public static Builder builder() {
        return new Builder(Integer.MAX_VALUE);
    }

    public static Builder builder(final int maxWidth) {
        return maxWidth < 1 ? new Builder(Integer.MAX_VALUE) : new Builder(maxWidth);
    }

    //region IRichText

    @Override
    public void paint(final MatrixStack matrix, int x, int y, final int zLevel) {

        matrix.pushPose();
        matrix.translate(0, 0, zLevel);

        for (final TextLine line : this._lines) {

            line.paint(this, matrix, x, y);
            y += line.getHeight() + this._interline;
        }

        matrix.popPose();
    }

    @Override
    public Rectangle bounds() {

        if (null == this._bounds || this._dynamic) {
            this._bounds = new Rectangle(0, 0,
                    this._lines.stream()
                            .mapToInt(TextLine::getWidth)
                            .max()
                            .orElse(0),
                    ((this._lines.size() - 1) * this._interline) +
                            this._lines.stream()
                                    .mapToInt(TextLine::getHeight)
                                    .sum());
        }

        return this._bounds;
    }

    @Override
    public boolean isEmpty() {
        return this._lines.isEmpty();
    }

    @Override
    public boolean isNotEmpty() {
        return !this._lines.isEmpty();
    }

    //endregion
    //region internals

    private RichText() {
        this(ModRenderHelper.DEFAULT_FONT_RENDERER, Collections.emptyList());
    }

    private RichText(final NonNullSupplier<FontRenderer> font, final List<TextLine> lines) {

        this._lines = lines;
        this._fontSupplier = font;
        this._textColour = Colour.BLACK;
        this._interline = 0;
        this._bounds = null;
        this._dynamic = this._lines.stream()
                .anyMatch(TextLine::isDynamic);
    }

    private static void paintString(final RichText richText, final String chunk,
                                    final MatrixStack matrix, final int x, final int y) {
        richText._fontSupplier.get().drawShadow(matrix, chunk, x, y, richText._textColour.toARGB());
    }

    private static void paintString(final RichText richText, final ITextProperties chunk,
                                    final MatrixStack matrix, final int x, final int y) {
        richText._fontSupplier.get().drawShadow(matrix, LanguageMap.getInstance().getVisualOrder(chunk), x, y,
                richText._textColour.toARGB());
    }

    private static void paintItemStack(final RichText richText, final ItemStack chunk,
                                       final MatrixStack matrix, final int x, final int y) {
        ModRenderHelper.paintItemStackWithCount(matrix, chunk, x, y, false);
    }

    private static void paintItemStackNoCount(final RichText richText, final ItemStack chunk,
                                              final MatrixStack matrix, final int x, final int y) {
        ModRenderHelper.paintItemStack(matrix, chunk, x, y, "", false);
    }

    private static void paintSprite(final RichText richText, final ISprite chunk,
                                    final MatrixStack matrix, final int x, final int y) {
        ModRenderHelper.paintSprite(matrix, chunk, new Point(x, y), 0, 16, 16); //TODO new Point arg!!
    }

    //region ITextChunk

    private interface ITextChunk {

        int getWidth();

        int getHeight();

        void paint(RichText richText, MatrixStack matrix, int x, int y);
    }

    //endregion
    //region TextLine

    private static class TextLine
            implements ITextChunk {

        public static TextLine from(final ITextChunk chunk) {
            return new TextLine(chunk);
        }

        public static TextLine from(final List<ITextChunk> chunks) {
            return 1 == chunks.size() ? new TextLine(chunks.get(0)) : new TextLine(chunks);
        }

        private TextLine(final ITextChunk chunk) {

            Preconditions.checkNotNull(chunk);
            this._chunks = ObjectLists.unmodifiable(ObjectLists.singleton(chunk));
            this._dynamic = false;
            this._maxWidth = chunk.getWidth();
            this._maxHeight = chunk.getHeight();
        }

        private TextLine(final List<ITextChunk> chunks) {

            Preconditions.checkArgument(!chunks.isEmpty());
            this._chunks = ObjectLists.unmodifiable(new ObjectArrayList<>(chunks));

            this._dynamic = this._chunks.stream()
                    .anyMatch(c -> c instanceof DynamicTextChunk);

            this._maxWidth = this._dynamic ? -1 : this.computeMaxWidth();

            this._maxHeight = this._chunks.stream()
                    .mapToInt(ITextChunk::getHeight)
                    .max()
                    .orElse(0);
        }

        public boolean isDynamic() {
            return this._dynamic;
        }

        //region ITextChunk

        @Override
        public int getWidth() {
            return this._dynamic ? this.computeMaxWidth() : this._maxWidth;
        }

        @Override
        public int getHeight() {
            return this._maxHeight;
        }

        @Override
        public void paint(final RichText richText, final MatrixStack matrix, int x, int y) {

            for (final ITextChunk chunk : this._chunks) {

                chunk.paint(richText, matrix, x, y + ((this._maxHeight - chunk.getHeight()) / 2));
                x += chunk.getWidth();
            }
        }

        //endregion
        //region internals

        private int computeMaxWidth() {
            return this._chunks.stream()
                    .mapToInt(ITextChunk::getWidth)
                    .sum();
        }

        final ObjectList<ITextChunk> _chunks;
        final int _maxWidth;
        final int _maxHeight;
        final boolean _dynamic;

        //endregion
    }

    //endregion
    //region TextChunk

    private static class TextChunk<T>
            implements ITextChunk, NonNullSupplier<T> {

        @FunctionalInterface
        public interface IChunkPainter<T> {

            void paint(RichText richText, T chunk, MatrixStack matrix, int x, int y);
        }

        public TextChunk(final T thing, final int width, final int height, final IChunkPainter<T> painter) {

            this._thing = Objects.requireNonNull(thing);
            this._width = width;
            this._height = height;
            this._painter = Objects.requireNonNull(painter);
        }

        //region ITextChunk

        @Override
        public int getWidth() {
            return this._width;
        }

        @Override
        public int getHeight() {
            return this._height;
        }

        @Override
        public void paint(final RichText richText, final MatrixStack matrix, final int x, final int y) {
            this._painter.paint(richText, this._thing, matrix, x, y);
        }

        //endregion
        //region NonNullSupplier

        @Override
        public T get() {
            return this._thing;
        }

        //endregion
        //region internals

        private final T _thing;
        private final int _width;
        private final int _height;
        private final IChunkPainter<T> _painter;

        //endregion
    }

    //endregion
    //region DynamicTextChunk

    private static class DynamicTextChunk
            extends TextChunk<Supplier<String>> {

        public DynamicTextChunk(final Supplier<String> thing, final NonNullSupplier<FontRenderer> fontSupplier) {

            super(thing, 0, fontSupplier.get().lineHeight, DynamicTextChunk::paintString);
            this._fontSupplier = fontSupplier;
        }

        //region TextChunk

        @Override
        public int getWidth() {
            return this._fontSupplier.get().width(this.get().get());
        }

        //endregion
        //region internals

        private static void paintString(final RichText richText, final Supplier<String> chunk,
                                        final MatrixStack matrix, final int x, final int y) {
            RichText.paintString(richText, chunk.get(), matrix, x, y);
        }

        private final NonNullSupplier<FontRenderer> _fontSupplier;

        //endregion
    }

    //endregion
    //region DynamicTextChunk

    private static class TranslationTextChunk
            extends TextChunk<NonNullSupplier<ITextComponent>> {

        public TranslationTextChunk(final NonNullSupplier<ITextComponent> thing, final NonNullSupplier<FontRenderer> fontSupplier) {

            super(thing, 0, fontSupplier.get().lineHeight, TranslationTextChunk::paintString);
            this._fontSupplier = fontSupplier;
        }

        //region TextChunk

        @Override
        public int getWidth() {
            return this._fontSupplier.get().width(this.get().get());
        }

        //endregion
        //region internals

        private static void paintString(final RichText richText, final NonNullSupplier<ITextComponent> chunk,
                                        final MatrixStack matrix, final int x, final int y) {
            RichText.paintString(richText, chunk.get(), matrix, x, y);
        }

        private final NonNullSupplier<FontRenderer> _fontSupplier;

        //endregion
    }

    //endregion
    //region Builder

    public static class Builder {

        public RichText build() {

            final RichText rich = new RichText(this._fontSupplier, this.buildLines());

            rich._textColour = this._textColour;
            rich._interline = this._interline;

            return rich;
        }

        public Builder textLines(final List<ITextComponent> lines) {

            Preconditions.checkArgument(!lines.isEmpty());
            this._lines = lines;
            return this;
        }

        public Builder textLines(final ITextComponent line) {
            return this.textLines(ImmutableList.of(line));
        }

        public Builder textLines(final ITextComponent... lines) {
            return this.textLines(ImmutableList.copyOf(lines));
        }

        public Builder objects(final List<Object> objects) {

            this._objects = objects;
            return this;
        }

        /**
         * Replace the default MC font renderer with the provided one
         *
         * @param font the new font renderer
         * @return this builder
         */
        public Builder font(final NonNullSupplier<FontRenderer> font) {

            this._fontSupplier = Objects.requireNonNull(font);
            return this;
        }

        public Builder defaultColour(final Colour colour) {

            this._textColour = Objects.requireNonNull(colour);
            return this;
        }

        public Builder interline(final int interline) {

            this._interline = interline;
            return this;
        }

        //region internals

        protected Builder(final int maxWidth) {

            this._maxWidth = maxWidth;
            this._lines = Collections.emptyList();
            this._objects = Collections.emptyList();
            this._fontSupplier = ModRenderHelper.DEFAULT_FONT_RENDERER;
            this._textColour = Colour.BLACK;
            this._interline = 0;
        }

        protected List<TextLine> buildLines() {

            // splits the lines in chunks of the requested max width, also splitting lines at every \n

            //noinspection UnstableApiUsage
            return this._lines.stream()
                    .map(this._objects.isEmpty() ? this::splitPlainText : this::splitFormattedText)
                    .flatMap(Collection::stream)
                    .collect(ImmutableList.toImmutableList());
        }

        protected List<TextLine> splitPlainText(final ITextComponent component) {

            final List<ITextProperties> textProperties = this.split(component);

            if (textProperties.isEmpty()) {
                return EMPTY_LINE;
            } else {
                //noinspection UnstableApiUsage
                return textProperties.stream()
                        .map(this::line)
                        .collect(ImmutableList.toImmutableList());
            }
        }

        protected List<TextLine> splitFormattedText(final ITextComponent component) {

            final List<ITextProperties> textProperties = this.split(component);

            if (textProperties.isEmpty()) {
                return EMPTY_LINE;
            }

            final int objectsCount = this._objects.size();
            final List<TextLine> textLines = Lists.newLinkedList();
            StringBuilder sb = new StringBuilder();

            for (final ITextProperties tp : textProperties) {

                final String text = tp.getString();

                if (!text.contains("@")) {

                    textLines.add(this.line(tp));
                    continue;
                }

                final List<ITextChunk> chunks = Lists.newArrayList();
                int index = 0;

                while (index < text.length()) {

                    final String currentChar = text.substring(index, index + 1);

                    if ("@".equals(currentChar)) {

                        final int objectIndex = text.charAt(++index) - '0';

                        if ('@' - '0' == objectIndex) {

                            // convert @@ to a single @
                            sb.append('@');

                        } else if (objectIndex < 0 || objectIndex > 9 || objectIndex >= objectsCount) {

                            throw new IllegalArgumentException(text);

                        } else {

                            // replace the placeholder with the corresponding object

                            if (sb.length() > 0) {

                                // save the line so far
                                chunks.add(this.chunk(sb, component.getStyle()));
                                sb = new StringBuilder();
                            }

                            chunks.add(this.genericChunk(this._objects.get(objectIndex)));
                        }
                    } else {

                        sb.append(currentChar);
                    }

                    ++index;
                }

                if (sb.length() > 0) {
                    chunks.add(this.chunk(sb, component.getStyle()));
                }

                textLines.add(TextLine.from(chunks));
            }

            return textLines;
        }

        protected List<ITextProperties> split(final ITextComponent component) {
            return ModRenderHelper.splitLines(this._fontSupplier.get(), component, this._maxWidth, component.getStyle());
        }

        protected TextLine line(final ITextProperties tp) {
            return TextLine.from(this.chunk(tp));
        }

        protected ITextChunk chunk(final ITextProperties text) {
            return new TextChunk<>(text, this._fontSupplier.get().width(text),
                    this._fontSupplier.get().lineHeight, RichText::paintString);
        }

        protected ITextChunk chunk(final StringBuilder builder, final Style style) {
            return this.chunk(new StringTextComponent(builder.toString()).setStyle(style));
        }

        protected ITextChunk chunk(final String text) {
            return new TextChunk<>(text, this._fontSupplier.get().width(text),
                    this._fontSupplier.get().lineHeight, RichText::paintString);
        }

        private ITextChunk chunk(final Supplier<String> text) {
            return new DynamicTextChunk(text, this._fontSupplier);
        }

        private ITextChunk chunk(final NonNullSupplier<ITextComponent> text) {
            return new TranslationTextChunk(text, this._fontSupplier);
        }

        private ITextChunk chunk(final ItemStack stack) {
            return new TextChunk<>(stack, 16, 16, RichText::paintItemStack); //TODO fix width (include stack amount)
        }

        private ITextChunk chunk(final IItemProvider item) {
            return new TextChunk<>(new ItemStack(item), 16, 16, RichText::paintItemStackNoCount);
        }

        private ITextChunk chunk(final ISprite sprite) {
            return new TextChunk<>(sprite, 16, 16, RichText::paintSprite);
        }

        private ITextChunk genericChunk(final Object thing) {

            if (thing instanceof String) {
                return this.chunk((String) thing);
            } else if (thing instanceof Supplier) {
                //noinspection unchecked
                return this.chunk((Supplier<String>)thing);
            } else if (thing instanceof NonNullSupplier) {
                //noinspection unchecked
                return this.chunk((NonNullSupplier<ITextComponent>)thing);
            } else if (thing instanceof ITextComponent) {
                return this.chunk((ITextComponent)thing);
            } else if (thing instanceof ItemStack) {
                return this.chunk((ItemStack)thing);
            } else if (thing instanceof Item) {
                return this.chunk((Item)thing);
            } else if (thing instanceof Block) {
                return this.chunk((Block)thing);
            } else if (thing instanceof ISprite) {
                return this.chunk((ISprite)thing);
            }

            return EMPTY;
        }

        private static final ITextChunk EMPTY = new TextChunk<>("", 5, 5, (richText, chunk, matrix, x, y) -> {});
        private static final List<TextLine> EMPTY_LINE = ObjectLists.unmodifiable(ObjectLists.singleton(TextLine.from(EMPTY)));

        private final int _maxWidth;
        protected List<ITextComponent> _lines;
        protected List<Object> _objects;
        protected NonNullSupplier<FontRenderer> _fontSupplier;
        protected Colour _textColour;
        protected int _interline;

        //endregion
    }

    //endregion

    final List<TextLine> _lines;
    final NonNullSupplier<FontRenderer> _fontSupplier;
    private Colour _textColour;
    private int _interline;
    private Rectangle _bounds;
    private final boolean _dynamic;

    //endregion
}
