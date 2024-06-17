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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractData<T>
        implements IBindableData<T> {

    protected static <T> AbstractData<T> of(T defaultValue) {
        return new AbstractData<>(() -> defaultValue) {

            @Override
            public T defaultValue() {
                return defaultValue;
            }
        };
    }

    protected static <O> IBindableData<O> as(@Nullable final O defaultValue, Consumer<Consumer<O>> binding) {

        Preconditions.checkNotNull(binding, "Binding must not be null.");

        return new IBindableData<>() {

            @Override
            public void bind(@NotNull Consumer<O> consumer) {

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

    /**
     * Logical client-side initializer
     *
     * @param getter a {@link Supplier} that will be used to get the value
     * @param clientSideSetter a {@link Consumer} that will be used to set the value back on the client side
     */
    protected AbstractData(Supplier<T> getter, Consumer<T> clientSideSetter) {

        Preconditions.checkNotNull(getter, "Getter must not be null");
        Preconditions.checkNotNull(clientSideSetter, "Client side setter must not be null");

        this._bindings = new Event<>();
        this._getter = getter;
        this._clientSideSetter = clientSideSetter;
    }

    /**
     * Logical server-side initializer
     *
     * @param getter a {@link Supplier} that will be used to get the value
     */
    protected AbstractData(Supplier<T> getter) {

        Preconditions.checkNotNull(getter, "Getter must not be null");

        this._bindings = null;
        this._getter = getter;
        this._clientSideSetter = $ -> {
            throw new IllegalStateException("Setter called on the server side!");
        };
    }

    protected T getValue() {
        return this._getter.get();
    }

    protected void setClientSideValue(T value) {
        this._clientSideSetter.accept(value);
    }

    /**
     * Notify registered listeners that the data has changed
     *
     * @param value the new value of the data
     */
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
    protected final Consumer<T> _clientSideSetter;
    @Nullable
    private final IEvent<Consumer<T>> _bindings;

    //endregion
}
