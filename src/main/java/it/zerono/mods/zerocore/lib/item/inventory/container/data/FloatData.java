/*
 *
 * FloadData.java
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

public class FloatData
        extends AbstractData<Float>
        implements IContainerData {

    public static FloatData immutable(ModContainer container, float value) {
        return of(container, () -> value, CodeHelper.emptyConsumer());
    }

    public static FloatData sampled(int frequency, ModContainer container, Supplier<@NotNull Float> getter,
                                    Consumer<@NotNull Float> clientSideSetter) {
        return of(container, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static FloatData sampled(int frequency, ModContainer container, Supplier<@NotNull Float> getter) {
        return of(container, new Sampler<>(frequency, getter), CodeHelper.emptyConsumer());
    }

    public static FloatData of(ModContainer container, Supplier<@NotNull Float> getter,
                               Consumer<@NotNull Float> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new FloatData(getter, clientSideSetter) : new FloatData(getter);

        container.addBindableData(data);
        return data;
    }

    public static FloatData of(ModContainer container, float[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        return of(container, () -> array[index], v -> array[index] = v);
    }

    public static FloatData of(ModContainer container, Supplier<@NotNull Float> getter) {
        return of(container, getter, CodeHelper.emptyConsumer());
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final float current = this.getValue();

        if (this._lastValue != current) {

            this._lastValue = current;
            return new FloatData.FloatEntry(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return FloatData.FloatEntry.from(buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof FloatData.FloatEntry record) {

            this.setClientSideValue(record.value);
            this.notify(record.value);
        }
    }

    //endregion
    //region IBindableData<Float>

    @Nullable
    @Override
    public Float defaultValue() {
        return 0.0f;
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record FloatEntry(float value)
            implements ISyncedSetEntry {

        private static FloatData.FloatEntry from(RegistryFriendlyByteBuf buffer) {
            return new FloatData.FloatEntry(buffer.readFloat());
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeFloat(this.value);
        }
    }

    //endregion

    private FloatData(Supplier<Float> getter, Consumer<Float> clientSideSetter) {
        super(getter, clientSideSetter);
    }

    private FloatData(Supplier<Float> getter) {

        super(getter);
        this._lastValue = 0;
    }

    private float _lastValue;

    //endregion
}
