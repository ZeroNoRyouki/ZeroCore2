/*
 *
 * TickerListener.java
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

package it.zerono.mods.zerocore.lib;

import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.event.SlimEvent;

public class TickerListener
        extends Ticker {

    public final IEvent<Runnable> Expired;

    public static TickerListener singleListener(final int expireAfterTicks, final Runnable listener) {

        final TickerListener ticker = new TickerListener(expireAfterTicks, new SlimEvent<>());

        ticker.Expired.subscribe(listener);
        return ticker;
    }

    public static TickerListener multiListener(final int expireAfterTicks) {
        return new TickerListener(expireAfterTicks, new Event<>());
    }

    //region internals

    private TickerListener(final int expireAfterTicks, final IEvent<Runnable> event) {

        super(expireAfterTicks);
        this.Expired = event;
        this.setExpiredCallback(() -> this.Expired.raise(Runnable::run));
    }

    //endregion
}
