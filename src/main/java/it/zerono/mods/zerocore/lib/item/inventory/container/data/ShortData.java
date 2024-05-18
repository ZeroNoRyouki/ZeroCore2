/*
 *
 * ShortData.java
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

import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.zerono.mods.zerocore.lib.functional.ShortSupplier;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShortData
        implements IContainerData {

    public ShortData(final ShortSupplier getter, final ShortConsumer setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = 0;
    }

    public static ShortData wrap(final short[] array, final int index) {
        return new ShortData(() -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final short current = this._getter.getAsShort();

        if (this._lastValue != current) {

            this._lastValue = current;
            return new ShortEntry(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return ShortEntry.from(buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof ShortEntry record) {
            this._setter.accept(record.value);
        }
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record ShortEntry(short value)
            implements ISyncedSetEntry {

        private static ShortEntry from(RegistryFriendlyByteBuf buffer) {
            return new ShortEntry(buffer.readShort());
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeShort(this.value);
        }
    }

    //endregion

    private final ShortSupplier _getter;
    private final ShortConsumer _setter;
    private short _lastValue;

    //endregion
}
