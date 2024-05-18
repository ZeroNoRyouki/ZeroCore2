/*
 *
 * EnumData.java
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

import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumData<T extends Enum<T>>
        implements IContainerData {

    public EnumData(final Class<T> enumClass, final Supplier<T> getter, final Consumer<T> setter) {

        this._enumClass = enumClass;
        this._getter = getter;
        this._setter = setter;
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final T current = this._getter.get();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> {

                final T value = this._getter.get();

                buffer.writeEnum(value);
            };
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return new EnumEntry<>(buffer.readEnum(this._enumClass));
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof EnumEntry) {
            //noinspection unchecked
            this._setter.accept(((EnumEntry<T>)entry).value);
        }
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record EnumEntry<T extends Enum<T>>(T value)
            implements ISyncedSetEntry {

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeEnum(this.value);
        }
    }

    //endregion

    private final Class<T> _enumClass;
    private final Supplier<T> _getter;
    private final Consumer<T> _setter;
    private T _lastValue;

    //endregion
}
