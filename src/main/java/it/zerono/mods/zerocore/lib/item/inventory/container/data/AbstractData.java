/*
 *
 * AbstractData.java
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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractData<T>
        implements IBindableData<T> {

    protected static <T> AbstractData<T> of(T defaultValue) {
        return new AbstractData<T>() {

            @Override
            public T defaultValue() {
                return defaultValue;
            }
        };
    }

    protected static <O> IBindableData<O> as(@Nullable final O defaultValue, Consumer<Consumer<O>> binding) {

        Preconditions.checkNotNull(binding, "Binding must not be null.");

        return new IBindableData<O>() {

            @Override
            public void bind(@Nonnull Consumer<O> consumer) {

                Preconditions.checkNotNull(consumer, "Consumer must not be null.");

                binding.accept(consumer);
            }

            @Nullable
            @Override
            public O defaultValue() {
                return defaultValue;
            }
        };
    }

    protected AbstractData() {

        this._bindings = new Event<>();
        this._getter = () -> {
            throw new IllegalStateException("Getter called on the client side");
        };
    }

    protected AbstractData(NonNullSupplier<Supplier<T>> serverSideGetter) {

        this._bindings = null;
        this._getter = Objects.requireNonNull(serverSideGetter.get());
    }

    public void notify(@Nullable final T value) {

        // Must never be null when notify() is called (on the logical client side). If it is, we want the NPE.
        assert null != this._bindings;

        this._bindings.raise(c -> c.accept(value));
    }

    //region IBindableData<T>

    @Override
    public void bind(Consumer<T> consumer) {

        Preconditions.checkNotNull(consumer, "Consumer must not be null.");
        Preconditions.checkState(null != this._bindings, "bind() called on the server logical side.");

        this._bindings.subscribe(consumer);
    }

    //endregion
    //region internals

    protected final Supplier<T> _getter;
    @Nullable
    private final IEvent<Consumer<T>> _bindings;

    //endregion
}
