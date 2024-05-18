/*
 *
 * IntData.java
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

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class IntData
        implements IContainerData {

    public IntData(final IntSupplier getter, final IntConsumer setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = 0;
    }

    public static IntData wrap(final int[] array, final int index) {
        return new IntData(() -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final int current = this._getter.getAsInt();

        if (this._lastValue != current) {

            this._lastValue = current;
            return new IntEntry(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return IntEntry.from(buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof IntEntry record) {
            this._setter.accept(record.value);
        }
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record IntEntry(int value)
            implements ISyncedSetEntry {

        private static IntEntry from(RegistryFriendlyByteBuf buffer) {
            return new IntEntry(buffer.readInt());
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeInt(this.value);
        }
    }

    //endregion

    private final IntSupplier _getter;
    private final IntConsumer _setter;
    private int _lastValue;

    //endregion
}
