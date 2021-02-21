/*
 *
 * Ticker.java
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

public final class Ticker {

    public final IEvent<Runnable> Expired;

    public static Ticker singleListener(final int expireAfterTicks, final Runnable listener) {

        final Ticker ticker = new Ticker(expireAfterTicks, new SlimEvent<>());

        ticker.Expired.subscribe(listener);
        return ticker;
    }

    public static Ticker multiListener(final int expireAfterTicks) {
        return new Ticker(expireAfterTicks, new Event<>());
    }

    public void tick() {

        ++this._ticks;

        if (this._ticks >= this._expireAfter) {

            this._ticks = 0;
            this.Expired.raise(Runnable::run);
        }
    }

    @Override
    public String toString() {
        return String.format("Ticker: %d / %d", this._ticks, this._expireAfter);
    }

    //region internals

    private Ticker(final int expireAfterTicks, final IEvent<Runnable> event) {

        this._expireAfter = expireAfterTicks;
        this._ticks = 0;
        this.Expired = event;
    }

    private final int _expireAfter;
    private int _ticks;

    //endregion
}
