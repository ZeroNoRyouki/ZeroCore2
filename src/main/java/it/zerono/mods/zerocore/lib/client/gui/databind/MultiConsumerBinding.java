/*
 *
 * MultiConsumerBinding.java
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

import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;

import java.util.function.Consumer;
import java.util.function.Function;

@Deprecated // use IBinding.from()
public class MultiConsumerBinding<Source, Value>
        extends AbstractBinding<Source, Value> {

    @SafeVarargs
    public MultiConsumerBinding(Source source, Function<Source, Value> supplier, final Consumer<Value>... consumers) {
        super(source, supplier, createEvent(consumers));
    }

    //region internals

    private static <Value> IEvent<Consumer<Value>> createEvent(final Consumer<Value>[] consumers) {

        final IEvent<Consumer<Value>> e = new Event<>();

        for (final Consumer<Value> c : consumers) {
            if (null != c) {
                e.subscribe(c);
            }
        }

        return e;
    }

    //endregion
}
