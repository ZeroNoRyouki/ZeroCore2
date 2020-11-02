/*
 *
 * IteratorTracker.java
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

package it.zerono.mods.zerocore.lib.data;

import net.minecraftforge.common.util.NonNullSupplier;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Optional;

public class IteratorTracker<T> {

    public IteratorTracker(final NonNullSupplier<Iterator<T>> factory) {

        this._factory = factory;
        this.reset();
    }

    public Optional<T> next() {

        if (!this._iterator.hasNext()) {
            this.reset();
        }

//        return Optional.ofNullable(this._iterator.next());

        T next;

        try {

            next = this._iterator.next();

        } catch (ConcurrentModificationException e) {

            this.reset();
            next = this._iterator.next();
        }

        return Optional.ofNullable(next);
    }

    public void reset() {
        this._iterator = this._factory.get();
    }

    //region internals

    private final NonNullSupplier<Iterator<T>> _factory;
    private Iterator<T> _iterator;

    //endregion
}
