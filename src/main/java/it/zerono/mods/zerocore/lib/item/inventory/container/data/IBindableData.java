/*
 *
 * IBindableData.java
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

package it.zerono.mods.zerocore.lib.item.inventory.container.data;

import it.zerono.mods.zerocore.lib.text.BindableText;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface IBindableData<T> {

    /**
     * Add a consumer for the updated data that will be called, on the client side, after a data update.
     *
     * @param consumer The consumer.
     */
    void bind(@Nonnull Consumer<T> consumer);

    @Nullable
    default T defaultValue() {
        return null;
    }

    default NonNullSupplier<ITextComponent> asBindableText(@Nonnull Function<T, IFormattableTextComponent> textFactory) {
        return asBindableText(this.defaultValue(), textFactory);
    }

    default NonNullSupplier<ITextComponent> asBindableText(@Nullable T initialValue, @Nonnull Function<T, IFormattableTextComponent> textFactory) {
        return BindableText.of(this, initialValue, textFactory);
    }

    default NonNullSupplier<ITextComponent> asBindableText(@Nonnull Function<T, IFormattableTextComponent> textFactory,
                                                           @Nonnull NonNullFunction<IFormattableTextComponent, IFormattableTextComponent> textPostProcessor) {
        return asBindableText(this.defaultValue(), textFactory, textPostProcessor);
    }

    default NonNullSupplier<ITextComponent> asBindableText(@Nullable T initialValue,
                                                           @Nonnull Function<T, IFormattableTextComponent> textFactory,
                                                           @Nonnull NonNullFunction<IFormattableTextComponent, IFormattableTextComponent> textPostProcessor) {
        return BindableText.of(this, initialValue, textFactory, textPostProcessor);
    }
}
