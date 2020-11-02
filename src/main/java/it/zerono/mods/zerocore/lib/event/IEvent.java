/*
 *
 * IEvent.java
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

public interface IEvent<Handler> {

    /**
     * Subscribe an handler to this event.
     * Use the returned value to unsubscribe the handler from the event.
     *
     * @param handler the handler to subscribe to this event
     * @return use this value to unsubscribe the handler from the event
     */
    Handler subscribe(Handler handler);

    /**
     * Unsubscribe a previously subscribed handler from this event.
     *
     * @param handler the value returned by the subscribe() method
     */
    void unsubscribe(Handler handler);

    /**
     * Unsubscribe all subscribed handlers from this event.
     */
    void unsubscribeAll();

    /**
     * Raise this event, calling all the subscribed handlers
     *
     * @param consumer
     */
    void raise(Consumer<Handler> consumer);
}
