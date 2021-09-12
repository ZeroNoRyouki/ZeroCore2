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

import javax.annotation.Nullable;

public class Ticker {

    public Ticker(final int expireAfterTicks) {

        this._expireAfter = expireAfterTicks;
        this._expireCallback = null;
        this._ticks = 0;
    }

    public void tick() {

        ++this._ticks;

        if (this._ticks >= this._expireAfter) {

            this._ticks = 0;

            if (null != this._expireCallback) {
                this._expireCallback.run();
            }
        }
    }

    public int getTicks() {
        return this._ticks;
    }

    public void reset() {
        this._ticks = 0;
    }

    protected Ticker setExpiredCallback(@Nullable final Runnable expire) {

        this._expireCallback = expire;
        return this;
    }

    //region Object

    @Override
    public String toString() {
        return String.format("Ticker: %d / %d", this._ticks, this._expireAfter);
    }

    //endregion
    //region internals

    private final int _expireAfter;
    private Runnable _expireCallback;
    private int _ticks;

    //endregion
}
