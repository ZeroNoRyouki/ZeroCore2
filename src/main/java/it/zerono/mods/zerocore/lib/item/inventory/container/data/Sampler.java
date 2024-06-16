/*
 *
 * Sampler.java
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

import java.util.function.Supplier;

public class Sampler<T>
        implements Supplier<T> {

    public Sampler(int frequency, Supplier<Supplier<T>> serverSideGetter) {

        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");
        Preconditions.checkArgument(frequency > 0, "Frequency must be greater than zero.");

        this._getter = serverSideGetter.get();
        this._frequency = this._time = frequency;
    }

    @Override
    public T get() {

        if (null == this._sample) {
            return this._sample = this._getter.get();
        }

        if (0 == --this._time) {

            this._time = this._frequency;
            this._sample = this._getter.get();
        }

        return this._sample;
    }

    //region internals

    private final Supplier<T> _getter;
    private final int _frequency;
    private int _time;
    private T _sample;

    //endregion
}
