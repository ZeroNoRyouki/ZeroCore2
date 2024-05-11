/*
 *
 * UnmodifiableChildrenIterator.java
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

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import net.neoforged.neoforge.common.util.NonNullFunction;

import java.util.Iterator;

public class UnmodifiableChildrenIterator<T, P>
        extends AbstractIterator<T> {

    public UnmodifiableChildrenIterator(Iterator<P> parent, NonNullFunction<P, Iterator<T>> childProvider) {

        Preconditions.checkNotNull(parent, "Parent must not be null");
        Preconditions.checkNotNull(childProvider, "Child provider must not be null");

        this._parent = parent;
        this._mapper = childProvider;
    }

    //region AbstractIterator<T>

    @Override
    protected T computeNext() {

        if (null == this._cursor || !this._cursor.hasNext()) {

            this._cursor = null;

            while (this._parent.hasNext()) {

                final Iterator<T> newCursor = this._mapper.apply(this._parent.next());

                if (newCursor.hasNext()) {

                    this._cursor = newCursor;
                    break;
                }
            }

            if (null == this._cursor) {
                return this.endOfData();
            }
        }

        return this._cursor.next();
    }

    //endregion
    //region internals

    private final Iterator<P> _parent;
    private final NonNullFunction<P, Iterator<T>> _mapper;
    private Iterator<T> _cursor;

    //endregion
}
