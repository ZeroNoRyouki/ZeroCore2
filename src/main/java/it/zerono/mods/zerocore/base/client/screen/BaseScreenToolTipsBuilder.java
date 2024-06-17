/*
 *
 * BaseScreenToolTipsBuilder.java
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

package it.zerono.mods.zerocore.base.client.screen;

import it.zerono.mods.zerocore.lib.client.gui.ToolTipsBuilder;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import it.zerono.mods.zerocore.lib.text.BindableText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public class BaseScreenToolTipsBuilder
        extends ToolTipsBuilder {

    public BaseScreenToolTipsBuilder addTextAsTitle(MutableComponent text, Component... siblings) {
        return this.addText(text, ClientBaseHelper::formatAsTitle, siblings);
    }

    public BaseScreenToolTipsBuilder addLiteralAsTitle(String text) {
        return this.addLiteral(text, ClientBaseHelper::formatAsTitle);
    }

    public BaseScreenToolTipsBuilder addTranslatableAsTitle(String langKey) {
        return this.addTranslatable(langKey, ClientBaseHelper::formatAsTitle);
    }

    public BaseScreenToolTipsBuilder addTranslatableAsTitle(String langKey, Object... arguments) {
        return this.addTranslatable(langKey, ClientBaseHelper::formatAsTitle, arguments);
    }

    public BaseScreenToolTipsBuilder addTextAsValue(MutableComponent text, Component... siblings) {
        return this.addText(text, ClientBaseHelper::formatAsValue, siblings);
    }

    public BaseScreenToolTipsBuilder addLiteralAsValue(String text) {
        return this.addLiteral(text, ClientBaseHelper::formatAsValue);
    }

    public BaseScreenToolTipsBuilder addTranslatableAsValue(String langKey) {
        return this.addTranslatable(langKey, ClientBaseHelper::formatAsValue);
    }

    public BaseScreenToolTipsBuilder addTranslatableAsValue(String langKey, Object... arguments) {
        return this.addTranslatable(langKey, ClientBaseHelper::formatAsValue, arguments);
    }

    public BaseScreenToolTipsBuilder addTextAsInfo(MutableComponent text, Component... siblings) {
        return this.addText(text, ClientBaseHelper::formatAsInfo, siblings);
    }

    public BaseScreenToolTipsBuilder addLiteralAsInfo(String text) {
        return this.addLiteral(text, ClientBaseHelper::formatAsInfo);
    }

    public BaseScreenToolTipsBuilder addTranslatableAsInfo(String langKey) {
        return this.addTranslatable(langKey, ClientBaseHelper::formatAsInfo);
    }

    public BaseScreenToolTipsBuilder addTranslatableAsInfo(String langKey, Object... arguments) {
        return this.addTranslatable(langKey, ClientBaseHelper::formatAsInfo, arguments);
    }

    public <T> BaseScreenToolTipsBuilder addBindableObjectAsValue(IBindableData<T> value,
                                                                  Function<T, MutableComponent> textFactory) {
        return this.addBindableObjectAsValue(value, value.defaultValue(), textFactory);
    }

    public <T> BaseScreenToolTipsBuilder addBindableObjectAsValue(IBindableData<T> value, @Nullable T initialValue,
                                                                  Function<T, MutableComponent> textFactory) {
        return this.addObject(value.asBindableText(initialValue, textFactory, ClientBaseHelper::formatAsValue));
    }

    public <T, U> BaseScreenToolTipsBuilder addBindableObjectAsValue(IBindableData<T> firstValue, IBindableData<U> secondValue,
                                                                     BiFunction<T, U, MutableComponent> textFactory) {
        return this.addObject(BindableText.of(firstValue, firstValue.defaultValue(), secondValue, secondValue.defaultValue(),
                textFactory, ClientBaseHelper::formatAsValue));
    }

    public <T, U> BaseScreenToolTipsBuilder addBindableObjectAsValue(IBindableData<T> firstValue, @Nullable T initialFirstValue,
                                                                     IBindableData<U> secondValue, @Nullable U initialSecondValue,
                                                                     BiFunction<T, U, MutableComponent> textFactory) {
        return this.addObject(BindableText.of(firstValue, initialFirstValue, secondValue, initialSecondValue,
                textFactory, ClientBaseHelper::formatAsValue));
    }

    //region ToolTipsBuilder

    @Override
    public BaseScreenToolTipsBuilder addText(Component text) {

        super.addText(text);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addText(MutableComponent text,
                                             Function<@NotNull MutableComponent, @NotNull MutableComponent> textPostProcessor) {

        super.addText(text, textPostProcessor);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addText(MutableComponent text, Component... siblings) {

        super.addText(text, siblings);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addText(MutableComponent text,
                                             Function<@NotNull MutableComponent, @NotNull MutableComponent> textPostProcessor,
                                             Component... siblings) {

        super.addText(text, textPostProcessor, siblings);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addLiteral(String text) {

        super.addLiteral(text);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addLiteral(String text,
                                                Function<@NotNull MutableComponent, @NotNull MutableComponent> textPostProcessor) {

        super.addLiteral(text, textPostProcessor);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addTranslatable(String langKey) {

        super.addTranslatable(langKey);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addTranslatable(String langKey,
                                                     Function<@NotNull MutableComponent, @NotNull MutableComponent> textPostProcessor) {

        super.addTranslatable(langKey, textPostProcessor);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addTranslatable(String langKey, Object... arguments) {

        super.addTranslatable(langKey, arguments);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addTranslatable(String langKey,
                                                     Function<@NotNull MutableComponent, @NotNull MutableComponent> textPostProcessor,
                                                     Object... arguments) {

        super.addTranslatable(langKey, textPostProcessor, arguments);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addEmptyLine() {

        super.addEmptyLine();
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addObject(Object object) {

        super.addObject(object);
        return this;
    }

    @Override
    public BaseScreenToolTipsBuilder addObject(Object object, Object... others) {

        super.addObject(object, others);
        return this;
    }

    //endregion
}
