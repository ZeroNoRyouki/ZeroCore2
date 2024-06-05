/*
 *
 * ToolTipsBuilder.java
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
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.util.NonNullFunction;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

public class ToolTipsBuilder {

    public ToolTipsBuilder() {

        this._texts = ImmutableList.builder();
        this._objects = ImmutableList.builder();
    }

    public ImmutablePair<List<Component>, List<Object>> build() {
        return new ImmutablePair<>(this._texts.build(), this._objects.build());
    }

    public ToolTipsBuilder addText(Component text) {

        Preconditions.checkNotNull(text, "Text must not be null.");

        this._texts.add(text);
        return this;
    }

    public ToolTipsBuilder addText(MutableComponent text,
                                   NonNullFunction<MutableComponent, MutableComponent> textPostProcessor) {

        Preconditions.checkNotNull(text, "Text must not be null.");
        Preconditions.checkNotNull(textPostProcessor, "Text post processor must not be null.");

        this._texts.add(textPostProcessor.apply(text));
        return this;
    }

    /**
     * Append all the siblings texts to the provided text and add it to this builder.
     *
     * @param text The initial text.
     * @param siblings The other text component to append to the initial text.
     * @return this builder.
     */
    public ToolTipsBuilder addText(MutableComponent text, Component... siblings) {

        Preconditions.checkNotNull(text, "Text must not be null.");

        this._texts.add(text);
        appendSiblings(text, siblings);
        return this;
    }

    public ToolTipsBuilder addText(MutableComponent text,
                                   NonNullFunction<MutableComponent, MutableComponent> textPostProcessor,
                                   Component... siblings) {

        this.addText(text, textPostProcessor);
        appendSiblings(text, siblings);
        return this;
    }

    public ToolTipsBuilder addLiteral(String text) {

        if (Strings.isNullOrEmpty(text)) {
            return this.addEmptyLine();
        } else {
            return this.addText(TextHelper.literal(text));
        }
    }

    public ToolTipsBuilder addLiteral(String text,
                                      NonNullFunction<MutableComponent, MutableComponent> textPostProcessor) {

        if (Strings.isNullOrEmpty(text)) {
            return this.addEmptyLine();
        } else {
            return this.addText(TextHelper.literal(text), textPostProcessor);
        }
    }

    public ToolTipsBuilder addTranslatable(String langKey) {
        return this.addText(TextHelper.translatable(langKey));
    }

    public ToolTipsBuilder addTranslatable(String langKey,
                                           NonNullFunction<MutableComponent, MutableComponent> textPostProcessor) {
        return this.addText(TextHelper.translatable(langKey), textPostProcessor);
    }

    public ToolTipsBuilder addTranslatable(String langKey, Object... arguments) {
        return this.addText(TextHelper.translatable(langKey, arguments));
    }

    public ToolTipsBuilder addTranslatable(String langKey,
                                           NonNullFunction<MutableComponent, MutableComponent> textPostProcessor,
                                           Object... arguments) {
        return this.addText(TextHelper.translatable(langKey, arguments), textPostProcessor);
    }

    public ToolTipsBuilder addEmptyLine() {
        return this.addText(CodeHelper.TEXT_EMPTY_LINE);
    }

    public ToolTipsBuilder addObject(Object object) {

        this._objects.add(object);
        return this;
    }

    public ToolTipsBuilder addObject(Object object, Object... others) {

        this._objects.add(object);

        for (final Object o : others) {
            this._objects.add(o);
        }

        return this;
    }

    protected static void appendSiblings(MutableComponent text, Component... siblings) {

        for (final Component sibling : siblings) {
            text.append(sibling);
        }
    }

    //region internals

    private final ImmutableList.Builder<Component> _texts;
    private final ImmutableList.Builder<Object> _objects;

    //endregion
}
