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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShortData
        extends AbstractData<Short>
        implements IContainerData {

    public static ShortData immutable(ModContainer container, short value) {
        return of(container, () -> value, CodeHelper.emptyConsumer());
    }

    public static ShortData sampled(int frequency, ModContainer container, Supplier<@NotNull Short> getter,
                                    Consumer<@NotNull Short> clientSideSetter) {
        return of(container, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static ShortData of(ModContainer container, Supplier<@NotNull Short> getter,
                               Consumer<@NotNull Short> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new ShortData(getter, clientSideSetter) : new ShortData(getter);

        container.addBindableData(data);
        return data;
    }

    public static ShortData of(ModContainer container, short[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        return of(container, () -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final short current = this.getValue();

        if (this._lastValue != current) {

            this._lastValue = current;
            return new ShortData.ShortEntry(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return ShortData.ShortEntry.from(buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof ShortData.ShortEntry record) {

            this.setClientSideValue(record.value);
            this.notify(record.value);
        }
    }

    //endregion
    //region IBindableData<Short>

    @Nullable
    @Override
    public Short defaultValue() {
        return 0;
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

    private ShortData(Supplier<Short> getter, Consumer<Short> clientSideSetter) {
        super(getter, clientSideSetter);
    }

    private ShortData(Supplier<Short> getter) {

        super(getter);
        this._lastValue = 0;
    }

    private short _lastValue;

    //endregion
}
