/*
 *
 * SlimEvent.java
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

import java.util.function.Consumer;

public class SlimEvent<Handler> implements IEvent<Handler> {

    public SlimEvent() {
        this._handler = null;
    }

    //region IEvent

    @Override
    public Handler subscribe(Handler handler) {

        if (null == this._handler) {

            this._handler = handler;
            return handler;

        } else {

            throw new IllegalStateException("An handler is already defined for this event");
        }
    }

    @Override
    public void unsubscribe(Handler handler) {

        if (this._handler == handler) {
            this._handler = null;
        }
    }

    @Override
    public void unsubscribeAll() {
        this._handler = null;
    }

    @Override
    public void raise(final Consumer<Handler> c) {

        if (null != this._handler) {
            c.accept(this._handler);
        }
    }

    //endregion
    //region internals

    private Handler _handler;

    //endregion
}

