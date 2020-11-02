/*
 *
 * Flags.java
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

import java.util.EnumSet;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess"})
public class Flags<E extends Enum<E>> {

    public Flags(final Class<E> enumClass) {
        this._flags = EnumSet.noneOf(enumClass);
    }

    @SafeVarargs
    public Flags(final E value, final E... others) {
        this._flags = EnumSet.of(value, others);
    }

    public boolean contains(final E flag) {
        return this._flags.contains(flag);
    }

    public void add(final E flag) {
        this._flags.add(flag);
    }

    public void addIf(final E flag, final boolean add) {

        if (add) {
            this.add(flag);
        }
    }

    public void remove(final E flag) {
        this._flags.remove(flag);
    }

    public void removeIf(final E flag, final boolean remove) {

        if (remove) {
            this.remove(flag);
        }
    }

    public void set(final E flag, final boolean active) {

        if (active) {
            this.add(flag);
        } else {
            this.remove(flag);
        }
    }

    public void flip(final E flag) {
        this.set(flag, !this.contains(flag));
    }

    //region Object

    @Override
    public String toString() {
        return this._flags.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    //endregion
    //region internals

    private final EnumSet<E> _flags;

    //endregion
}
