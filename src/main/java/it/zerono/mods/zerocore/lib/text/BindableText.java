/*
 *
 * BindableText.java
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

package it.zerono.mods.zerocore.lib.text;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class BindableText
        implements NonNullSupplier<Component> {

    public static <T> NonNullSupplier<Component> of(IBindableData<T> bindableData,
                                                         Function<T, MutableComponent> textFactory) {
        return of(bindableData, bindableData.defaultValue(), textFactory, TextHelper.IDENTITY_TEXT_POST_PROCESSOR);
    }

    public static <T> NonNullSupplier<Component> of(IBindableData<T> bindableData, @Nullable T initialValue,
                                                         Function<T, MutableComponent> textFactory) {
        return of(bindableData, initialValue, textFactory, TextHelper.IDENTITY_TEXT_POST_PROCESSOR);
    }

    public static <T> NonNullSupplier<Component> of(IBindableData<T> bindableData, @Nullable T initialValue,
                                                         Function<T, MutableComponent> textFactory,
                                                         NonNullFunction<MutableComponent, MutableComponent> postProcessor) {

        final SingleBindableText<T> text = new SingleBindableText<>(initialValue, textFactory, postProcessor);

        bindableData.bind(text::changeValue);
        return text;
    }

    public static <T, U> NonNullSupplier<Component> of(IBindableData<T> firstBindableData, IBindableData<U> secondBindableData,
                                                            BiFunction<T, U, MutableComponent> textFactory) {
        return of(firstBindableData, firstBindableData.defaultValue(), secondBindableData, secondBindableData.defaultValue(),
                textFactory, TextHelper.IDENTITY_TEXT_POST_PROCESSOR);
    }

    public static <T, U> NonNullSupplier<Component> of(IBindableData<T> firstBindableData, @Nullable T firstInitialValue,
                                                            IBindableData<U> secondBindableData, @Nullable U secondInitialValue,
                                                            BiFunction<T, U, MutableComponent> textFactory) {
        return of(firstBindableData, firstInitialValue, secondBindableData, secondInitialValue, textFactory, TextHelper.IDENTITY_TEXT_POST_PROCESSOR);
    }

    public static <T, U> NonNullSupplier<Component> of(IBindableData<T> firstBindableData, @Nullable T firstInitialValue,
                                                            IBindableData<U> secondBindableData, @Nullable U secondInitialValue,
                                                            BiFunction<T, U, MutableComponent> textFactory,
                                                            NonNullFunction<MutableComponent, MutableComponent> postProcessor) {

        final BiBindableText<T, U> text = new BiBindableText<>(firstInitialValue, secondInitialValue, textFactory, postProcessor);

        firstBindableData.bind(text::changeFirstValue);
        secondBindableData.bind(text::changeSecondValue);
        return text;
    }

    @Nullable
    protected abstract MutableComponent build();

    protected void reset() {
        this._cachedText = null;
    }

    //region NonNullSupplier<Component>

    @Nonnull
    @Override
    public Component get() {

        if (null == this._cachedText) {

            final MutableComponent text = this.build();

            if (null != text) {
                this._cachedText = this._postProcessor.apply(text);
            } else {
                this._cachedText = CodeHelper.TEXT_EMPTY_LINE;
            }
        }

        return this._cachedText;
    }

    //endregion
    //region internals
    //region SingleBindableText

    private static class SingleBindableText<T>
            extends BindableText {
        protected SingleBindableText(@Nullable T initialValue, Function<T, MutableComponent> textFactory,
                                     NonNullFunction<MutableComponent, MutableComponent> postProcessor) {

            super(postProcessor);
            this._textFactory = Preconditions.checkNotNull(textFactory, "Text factory must not be null.");
            this._cachedValue = initialValue;
        }

        @Nullable
        @Override
        protected MutableComponent build() {
            return null != this._cachedValue ? this._textFactory.apply(this._cachedValue) : null;
        }

        public void changeValue(T value) {

            this._cachedValue = value;
            this.reset();
        }

        //region internals

        private final Function<T, MutableComponent> _textFactory;
        @Nullable
        private T _cachedValue;

        //endregion
    }

    //endregion
    //region BiBindableText

    private static class BiBindableText<T, U>
            extends BindableText {
        protected BiBindableText(@Nullable T firstInitialValue, @Nullable U secondInitialValue,
                                 BiFunction<T, U, MutableComponent> textFactory,
                                 NonNullFunction<MutableComponent, MutableComponent> postProcessor) {

            super(postProcessor);
            this._textFactory = Preconditions.checkNotNull(textFactory, "Text factory must not be null.");
            this._cachedFirstValue = firstInitialValue;
            this._cachedSecondValue = secondInitialValue;
        }

        @Nullable
        @Override
        protected MutableComponent build() {

            if (null == this._cachedFirstValue || null == this._cachedSecondValue) {
                return null;
            }

            return this._textFactory.apply(this._cachedFirstValue, this._cachedSecondValue);
        }

        public void changeFirstValue(T value) {

            this._cachedFirstValue = value;
            this.reset();
        }

        public void changeSecondValue(U value) {

            this._cachedSecondValue = value;
            this.reset();
        }

        //region internals

        private final BiFunction<T, U, MutableComponent> _textFactory;
        @Nullable
        private T _cachedFirstValue;
        @Nullable
        private U _cachedSecondValue;

        //endregion
    }

    //endregion

    protected BindableText(NonNullFunction<MutableComponent, MutableComponent> postProcessor) {
        this._postProcessor = Preconditions.checkNotNull(postProcessor, "Post processor must not be null.");
    }

    protected final NonNullFunction<MutableComponent, MutableComponent> _postProcessor;
    protected Component _cachedText;

    //endregion
}
