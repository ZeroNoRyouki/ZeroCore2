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

package it.zerono.mods.zerocore.lib.client.gui.databind;

import it.zerono.mods.zerocore.lib.event.IEvent;

import java.util.function.Consumer;
import java.util.function.Function;

@Deprecated // use IBinding.from()
public abstract class AbstractBinding<Source, Value> implements IBinding {

    public AbstractBinding(final Source source, final Function<Source, Value> supplier,
                           final IEvent<Consumer<Value>> consumer) {

        this._cache = null;
        this._source = source;
        this._supplier = supplier;
        this._consumerEvent = consumer;
    }

    //region IBinding

    public void update() {

        final Value current = this.getCurrent();

        if (null == this._cache || !this._cache.equals(current)) {

            this._cache = current;
            this.setCurrent(current);
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
        return "Cached value: " + this._cache.toString();
    }

    //endregion
    //region internals

    protected Value getCurrent() {
        return this._supplier.apply(this._source);
    }

    protected void setCurrent(Value current) {
        this._consumerEvent.raise(c -> c.accept(current));
    }

    private final Source _source;
    private final Function<Source, Value> _supplier;
    private final IEvent<Consumer<Value>> _consumerEvent;
    private Value _cache;

    //endregion
}
