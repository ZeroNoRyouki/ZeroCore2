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

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.zerono.mods.zerocore.lib.functional.FloatSupplier;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FloadData
        implements IContainerData {

    public FloadData(final FloatSupplier getter, final FloatConsumer setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = 0.0f;
    }

    public static FloadData wrap(final float[] array, final int index) {
        return new FloadData(() -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final float current = this._getter.getAsFloat();

        if (this._lastValue != current) {

            this._lastValue = current;
            return new FloatEntry(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return FloatEntry.from(buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof FloatEntry record) {
            this._setter.accept(record.value);
        }
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record FloatEntry(float value)
            implements ISyncedSetEntry {

        private static FloatEntry from(RegistryFriendlyByteBuf buffer) {
            return new FloatEntry(buffer.readFloat());
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeFloat(this.value);
        }
    }

    //endregion

    private final FloatSupplier _getter;
    private final FloatConsumer _setter;
    private float _lastValue;

    //endregion
}
