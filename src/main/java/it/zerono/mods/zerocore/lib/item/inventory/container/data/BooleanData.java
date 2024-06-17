/*
 *
 * BooleanData.java
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

public class BooleanData
        extends AbstractData<Boolean>
        implements IContainerData {

    public static BooleanData immutable(ModContainer container, boolean value) {
        return of(container, () -> value, CodeHelper.emptyConsumer());
    }

    public static BooleanData sampled(int frequency, ModContainer container, Supplier<@NotNull Boolean> getter,
                                      Consumer<@NotNull Boolean> clientSideSetter) {
        return of(container, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static BooleanData of(ModContainer container, Supplier<@NotNull Boolean> getter,
                                 Consumer<@NotNull Boolean> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new BooleanData(getter, clientSideSetter) : new BooleanData(getter);

        container.addBindableData(data);
        return data;
    }

    public static BooleanData of(ModContainer container, boolean[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        return of(container, () -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final boolean current = this.getValue();

        if (this._lastValue != current) {

            this._lastValue = current;
            return new BooleanEntry(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return BooleanEntry.from(buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof BooleanEntry record) {

            this.setClientSideValue(record.value);
            this.notify(record.value);
        }
    }

    //endregion
    //region IBindableData<Boolean>

    @Nullable
    @Override
    public Boolean defaultValue() {
        return Boolean.FALSE;
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record BooleanEntry(boolean value)
            implements ISyncedSetEntry {

        private static BooleanEntry from(RegistryFriendlyByteBuf buffer) {
            return new BooleanEntry(buffer.readBoolean());
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeBoolean(this.value);
        }
    }

    //endregion

    private BooleanData(Supplier<Boolean> getter, Consumer<Boolean> clientSideSetter) {
        super(getter, clientSideSetter);
    }

    private BooleanData(Supplier<Boolean> getter) {

        super(getter);
        this._lastValue = false;
    }

    private boolean _lastValue;

    //endregion
}
