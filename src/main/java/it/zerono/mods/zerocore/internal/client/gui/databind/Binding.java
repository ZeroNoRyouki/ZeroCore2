/*
 *
 * AbstractBinding.java
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

package it.zerono.mods.zerocore.internal.client.gui.databind;

import it.zerono.mods.zerocore.lib.client.gui.databind.IBinding;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.event.SlimEvent;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Binding<Value>
        implements IBinding {

    public static <Value> IBinding from(final Supplier<Value> supplier, final Consumer<Value> consumer) {

        final IEvent<Consumer<Value>> e = new SlimEvent<>();

        e.subscribe(Objects.requireNonNull(consumer));

        return new Binding<>(supplier, e);
    }

    @SafeVarargs
    public static <Value> IBinding from(final Supplier<Value> supplier, final Consumer<Value>... consumers) {

        final IEvent<Consumer<Value>> e = new Event<>();

        for (final Consumer<Value> consumer : consumers) {
            e.subscribe(Objects.requireNonNull(consumer));
        }

        return new Binding<>(supplier, e);
    }

    //region IBinding

    @Override
    public void update() {

        final Value current = this._supplier.get();

        if (null == this._cache || !this._cache.equals(current)) {

            this._cache = current;
            this._consumerEvent.raise(c -> c.accept(current));
        }
    }

    @Override
    public void close() {
        this._consumerEvent.unsubscribeAll();
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return "Cached value: " + null != this._cache ? this._cache.toString() : "null";
    }

    //endregion
    //region internals

    private Binding(final Supplier<Value> supplier,
                    final IEvent<Consumer<Value>> consumer) {

        this._cache = null;
        this._supplier = supplier;
        this._consumerEvent = consumer;
    }

    private final Supplier<Value> _supplier;
    private final IEvent<Consumer<Value>> _consumerEvent;
    private Value _cache;

    //endregion
}
