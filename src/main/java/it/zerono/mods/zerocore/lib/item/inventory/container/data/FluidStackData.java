/*
 *
 * FluidStackData.java
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
import it.zerono.mods.zerocore.lib.data.stack.IStackHolderAccess;
import it.zerono.mods.zerocore.lib.fluid.FluidStackHolder;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.AmountChangedEntry;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidStackData
        extends AbstractData<FluidStack>
        implements IContainerData {

    public static FluidStackData immutable(ModContainer container, FluidStack value) {
        return of(container, () -> value, CodeHelper.emptyConsumer());
    }

    public static FluidStackData empty(ModContainer container) {
        return immutable(container, FluidStack.EMPTY);
    }

    public static FluidStackData sampled(int frequency, ModContainer container, Supplier<@NotNull FluidStack> getter,
                                         Consumer<@NotNull FluidStack> clientSideSetter) {
        return of(container, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static FluidStackData sampled(int frequency, ModContainer container, Supplier<@NotNull FluidStack> getter) {
        return of(container, new Sampler<>(frequency, getter), CodeHelper.emptyConsumer());
    }

    public static FluidStackData of(ModContainer container, Supplier<@NotNull FluidStack> getter,
                                    Consumer<@NotNull FluidStack> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new FluidStackData(getter, clientSideSetter) : new FluidStackData(getter);

        container.addBindableData(data);
        return data;
    }

    public static FluidStackData of(ModContainer container, NonNullList<FluidStack> list, int index) {

        Preconditions.checkNotNull(list, "List must not be null.");
        Preconditions.checkArgument(index >= 0 && index < list.size(), "Index must be a valid index for the list.");

        return of(container, () -> list.get(index), v -> list.set(index, v));
    }

    public static FluidStackData of(ModContainer container, IStackHolderAccess<FluidStackHolder, FluidStack> holder,
                                    int index) {

        Preconditions.checkNotNull(holder, "Holder must not be null.");

        return of(container, () -> holder.getStackAt(index), v -> holder.setStackAt(index, v));

    }

    public static FluidStackData of(ModContainer container, Supplier<@NotNull FluidStack> getter) {
        return of(container, getter, CodeHelper.emptyConsumer());
    }

    public IBindableData<Integer> amount() {

        if (null == this._amountData) {
            this._amountData = AbstractData.as(0, intConsumer ->
                    this.bind(stack -> intConsumer.accept(stack.getAmount())));
        }

        return this._amountData;
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final FluidStack current = this.getValue();

        if (this._lastValue.isEmpty() && current.isEmpty()) {
            return null;
        }

        final boolean equalFluid = FluidStack.isSameFluidSameComponents(current, this._lastValue);

        if (!equalFluid || current.getAmount() != this._lastValue.getAmount()) {

            this._lastValue = current.copy();

            if (equalFluid) {
                return new AmountChangedEntry(this._lastValue.getAmount());
            } else {
                return new FluidStackEntry(this._lastValue);
            }
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return AmountChangedEntry.from(buffer, FluidStackEntry::from);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof FluidStackEntry record) {

            // full stack

            this.setClientSideValue(record.value);
            this.notify(record.value);

        } else if (entry instanceof AmountChangedEntry record) {

            // amount only

            this.setClientSideValue(this.getValue().copyWithAmount(record.amount()));
        }
    }

    //endregion
    //region IBindableData<ByteConsumer>

    public FluidStack defaultValue() {
        return FluidStack.EMPTY;
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record FluidStackEntry(FluidStack value)
            implements ISyncedSetEntry {

        private static FluidStackEntry from(RegistryFriendlyByteBuf buffer) {
            return new FluidStackEntry(FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer));
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {

            // mark this as a full stack update. See {@link AmountChangedEntry#from}
            buffer.writeByte(1);
            FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, this.value);
        }
    }

    //endregion

    private FluidStackData(Supplier<FluidStack> getter, Consumer<FluidStack> clientSideSetter) {
        super(getter, clientSideSetter);
    }

    private FluidStackData(Supplier<FluidStack> getter) {

        super(getter);
        this._lastValue = FluidStack.EMPTY;
    }

    private FluidStack _lastValue;
    private IBindableData<Integer> _amountData;

    //endregion
}
