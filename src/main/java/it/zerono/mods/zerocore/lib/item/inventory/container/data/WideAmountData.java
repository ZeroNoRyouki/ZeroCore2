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
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WideAmountData
        extends AbstractData<WideAmount>
        implements IContainerData {

    public static WideAmountData immutable(ModContainer container, boolean isClientSide, WideAmount value) {
        return of(container, isClientSide, () -> () -> value);
    }

    public static WideAmountData empty(boolean isClientSide) {
        return isClientSide ? new WideAmountData() : new WideAmountData(() -> () -> WideAmount.ZERO);
    }

    public static WideAmountData sampled(int frequency, ModContainer container, boolean isClientSide,
                                         Supplier<@NotNull Supplier<WideAmount>> serverSideGetter) {
        return of(container, isClientSide, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static WideAmountData of(ModContainer container, boolean isClientSide,
                                    Supplier<@NotNull Supplier<WideAmount>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final WideAmountData data = isClientSide ? new WideAmountData() : new WideAmountData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    public IBindableData<Double> asDouble() {

        if (null == this._asDoubleBindable) {
            this._asDoubleBindable = AbstractData.as(0.0, doubleConsumer ->
                    this.bind(wideAmount -> doubleConsumer.accept(wideAmount.doubleValue())));
        }

        return this._asDoubleBindable;
    }

    //region IContainerData

    @Nullable
    @Override
    public Consumer<@NotNull FriendlyByteBuf> getContainerDataWriter() {

        final WideAmount current = this._getter.get().copy();

        if (this._lastValue.equals(current)) {

            return null;

        } else {

            this._lastValue = current;
            return current::serializeTo;
        }
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {
        this.notify(WideAmount.from(dataSource));
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

    private WideAmountData() {
    }

    private WideAmountData(Supplier<@NotNull Supplier<WideAmount>> serverSideGetter) {

        super(serverSideGetter);
        this._lastValue = WideAmount.ZERO;
    }

    private WideAmount _lastValue;
    @Nullable
    private IBindableData<Double> _asDoubleBindable;

    //endregion
}

