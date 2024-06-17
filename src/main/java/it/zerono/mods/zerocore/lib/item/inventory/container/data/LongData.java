/*
 *
 * LongData.java
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
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LongData
        extends AbstractData<Long>
        implements IContainerData {

    public static LongData immutable(ModContainer container, long value) {
        return of(container, () -> value, CodeHelper.emptyConsumer());
    }

    public static LongData sampled(int frequency, ModContainer container, Supplier<@NotNull Long> getter,
                                   Consumer<@NotNull Long> clientSideSetter) {
        return of(container, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static LongData of(ModContainer container, Supplier<@NotNull Long> getter,
                              Consumer<@NotNull Long> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new LongData(getter, clientSideSetter) : new LongData(getter);

        container.addBindableData(data);
        return data;
    }

    public static LongData of(ModContainer container, long[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        return of(container, () -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final long current = this.getValue();

        if (this._lastValue != current) {

            this._lastValue = current;
            return new LongEntry(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return LongEntry.from(buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof LongEntry record) {

            this.setClientSideValue(record.value);
            this.notify(record.value);
        }
    }

    //endregion
    //region IBindableData<Long>

    @Nullable
    @Override
    public Long defaultValue() {
        return 0L;
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record LongEntry(long value)
            implements ISyncedSetEntry {

        private static LongEntry from(RegistryFriendlyByteBuf buffer) {
            return new LongEntry(buffer.readLong());
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeLong(this.value);
        }
    }

    //endregion

    private LongData(Supplier<Long> getter, Consumer<Long> clientSideSetter) {
        super(getter, clientSideSetter);
    }

    private LongData(Supplier<Long> getter) {

        super(getter);
        this._lastValue = 0;
    }

    private long _lastValue;

    //endregion
}
