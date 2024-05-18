/*
 *
 * WideAmountData.java
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

import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class WideAmountData
        implements IContainerData {

    public WideAmountData(final Supplier<@NotNull WideAmount> getter, final Consumer<@NotNull WideAmount> setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = WideAmount.ZERO;
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final WideAmount current = this._getter.get();

        if (!this._lastValue.equals(current)) {

            this._lastValue = current.copy();
            return new WideAmountEntry(this._lastValue);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return WideAmountEntry.from(buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof WideAmountEntry record) {
            this._setter.accept(record.value);
        }
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record WideAmountEntry(WideAmount value)
            implements ISyncedSetEntry {

        private static WideAmountEntry from(RegistryFriendlyByteBuf buffer) {
            return new WideAmountEntry(WideAmount.CODECS.streamCodec().decode(buffer));
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            WideAmount.CODECS.streamCodec().encode(buffer, this.value);
        }
    }

    //endregion

    private final Supplier<@NotNull WideAmount> _getter;
    private final Consumer<@NotNull WideAmount> _setter;
    private WideAmount _lastValue;

    //endregion
}
