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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class WideAmountData
        extends AbstractData<WideAmount>
        implements IContainerData {

    public static WideAmountData immutable(ModContainer container, WideAmount value) {
        return of(container, () -> value, CodeHelper.emptyConsumer());
    }

    public static WideAmountData empty(ModContainer container) {
        return immutable(container, WideAmount.ZERO);
    }

    public static WideAmountData sampled(int frequency, ModContainer container, Supplier<@NotNull WideAmount> getter,
                                         Consumer<@NotNull WideAmount> clientSideSetter) {
        return of(container, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static WideAmountData sampled(int frequency, ModContainer container, Supplier<@NotNull WideAmount> getter) {
        return of(container, new Sampler<>(frequency, getter), CodeHelper.emptyConsumer());
    }

    public static WideAmountData of(ModContainer container, Supplier<@NotNull WideAmount> getter,
                                    Consumer<@NotNull WideAmount> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new WideAmountData(getter, clientSideSetter) : new WideAmountData(getter);

        container.addBindableData(data);
        return data;
    }

    public static WideAmountData of(ModContainer container, Supplier<@NotNull WideAmount> getter) {
        return of(container, getter, CodeHelper.emptyConsumer());
    }

    public IBindableData<Double> asDouble() {

        if (null == this._asDoubleBindable) {
            this._asDoubleBindable = AbstractData.as(0.0, doubleConsumer ->
                    this.bind(wideAmount -> doubleConsumer.accept(wideAmount.doubleValue())));
        }

        return this._asDoubleBindable;
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final WideAmount current = this.getValue();

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

            this.setClientSideValue(record.value);
            this.notify(record.value);
        }
    }

    //endregion
    //region IBindableData<WideAmount>

    @Nullable
    @Override
    public WideAmount defaultValue() {
        return WideAmount.ZERO;
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

    private WideAmountData(Supplier<WideAmount> getter, Consumer<WideAmount> clientSideSetter) {
        super(getter, clientSideSetter);
    }

    private WideAmountData(Supplier<WideAmount> getter) {

        super(getter);
        this._lastValue = WideAmount.ZERO;
    }

    private WideAmount _lastValue;
    @Nullable
    private IBindableData<Double> _asDoubleBindable;

    //endregion
}
