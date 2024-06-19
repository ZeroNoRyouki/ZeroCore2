/*
 *
 * ByteData.java
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

public class ByteData
        extends AbstractData<Byte>
        implements IContainerData {

    public static ByteData immutable(ModContainer container, byte value) {
        return of(container, () -> value, CodeHelper.emptyConsumer());
    }

    public static ByteData sampled(int frequency, ModContainer container, Supplier<@NotNull Byte> getter,
                                      Consumer<@NotNull Byte> clientSideSetter) {
        return of(container, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static ByteData sampled(int frequency, ModContainer container, Supplier<@NotNull Byte> getter) {
        return of(container, new Sampler<>(frequency, getter), CodeHelper.emptyConsumer());
    }

    public static ByteData of(ModContainer container, Supplier<@NotNull Byte> getter,
                              Consumer<@NotNull Byte> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new ByteData(getter, clientSideSetter) : new ByteData(getter);

        container.addBindableData(data);
        return data;
    }

    public static ByteData of(ModContainer container, byte[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        return of(container, () -> array[index], v -> array[index] = v);
    }

    public static ByteData of(ModContainer container, Supplier<@NotNull Byte> getter) {
        return of(container, getter, CodeHelper.emptyConsumer());
    }

    //region
    // IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final byte current = this.getValue();

        if (this._lastValue != current) {

            this._lastValue = current;
            return new ByteEntry(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return ByteEntry.from(buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof ByteEntry record) {

            this.setClientSideValue(record.value);
            this.notify(record.value);
        }
    }

    //endregion
    //region IBindableData<Byte>

    @Nullable
    @Override
    public Byte defaultValue() {
        return 0;
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record ByteEntry(byte value)
            implements ISyncedSetEntry {

        private static ByteEntry from(RegistryFriendlyByteBuf buffer) {
            return new ByteEntry(buffer.readByte());
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeByte(this.value);
        }
    }

    //endregion

    private ByteData(Supplier<Byte> getter, Consumer<Byte> clientSideSetter) {
        super(getter, clientSideSetter);
    }

    private ByteData(Supplier<Byte> getter) {

        super(getter);
        this._lastValue = 0;
    }

    private byte _lastValue;

    //endregion
}
