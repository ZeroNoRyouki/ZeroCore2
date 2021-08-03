/*
 *
 * Event.java
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

package it.zerono.mods.zerocore.lib.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.List;
import java.util.function.Consumer;

public class Event<Handler> implements IEvent<Handler> {

    public Event() {
        this._handlers = ObjectLists.emptyList();
    }

    //region IEvent

    @Override
    public Handler subscribe(final Handler handler) {

        if (ObjectLists.emptyList() == this._handlers) {
            this._handlers = new ObjectArrayList<>(4);
        }

        this._handlers.add(handler);
        return handler;
    }

    @Override
    public void unsubscribe(final Handler handler) {

        if (ObjectLists.emptyList() != this._handlers) {
            this._handlers.remove(handler);
        }
    }

    @Override
    public void unsubscribeAll() {

        if (ObjectLists.emptyList() != this._handlers) {
            this._handlers.clear();
        }
    }

    @Override
    public void raise(final Consumer<Handler> c) {
        this._handlers.forEach(c);
    }

    //endregion
    //region internals

    protected List<Handler> _handlers;

    //endregion
}
