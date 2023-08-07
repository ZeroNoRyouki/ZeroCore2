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
import it.zerono.mods.zerocore.lib.data.stack.IStackHolderAccess;
import it.zerono.mods.zerocore.lib.fluid.FluidStackHolder;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FluidStackData
        extends AbstractData<FluidStack>
        implements IContainerData {

    public static FluidStackData immutable(ModContainer container, boolean isClientSide, FluidStack value) {
        return of(container, isClientSide, () -> () -> value);
    }

    public static FluidStackData empty(boolean isClientSide) {
        return isClientSide ? new FluidStackData() : new FluidStackData(() -> () -> FluidStack.EMPTY);
    }

    public static FluidStackData sampled(int frequency, ModContainer container, boolean isClientSide,
                                         NonNullSupplier<Supplier<FluidStack>> serverSideGetter) {
        return of(container, isClientSide, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static FluidStackData of(ModContainer container, boolean isClientSide,
                                    NonNullSupplier<Supplier<FluidStack>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final FluidStackData data = isClientSide ? new FluidStackData() : new FluidStackData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    public static FluidStackData of(ModContainer container, boolean isClientSide, NonNullList<FluidStack> list, int index) {

        Preconditions.checkNotNull(list, "List must not be null.");
        Preconditions.checkArgument(index >= 0 && index < list.size(), "Index must be a valid index for the list.");

        final FluidStackData data = of(container, isClientSide,
                () -> () -> list.get(index));

        if (isClientSide) {
            data.bind(v -> list.set(index, v));
        }

        return data;
    }

    public static FluidStackData of(ModContainer container, boolean isClientSide,
                                    IStackHolderAccess<FluidStackHolder, FluidStack> holder, int index) {

        Preconditions.checkNotNull(holder, "Holder must not be null.");

        final FluidStackData data = of(container, isClientSide,
                () -> () -> holder.getStackAt(index));

        if (isClientSide) {
            data.bind(v -> holder.setStackAt(index, v));
        }

        return data;
    }

    public IBindableData<Integer> amount() {

        if (null == this._amountData) {
            this._amountData = AbstractData.of(0);
        }

        return this._amountData;
    }

    //region IContainerData

    @Nullable
    @Override
    public FluidStack defaultValue() {
        return FluidStack.EMPTY;
    }

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final FluidStack current = this._getter.get();

        if (this._lastValue.isEmpty() && current.isEmpty()) {
            return null;
        }

        final boolean equalFluid = current.isFluidEqual(this._lastValue);

        if (!equalFluid || current.getAmount() != this._lastValue.getAmount()) {

            this._lastValue = current.copy();

            if (equalFluid) {
                return buffer -> {

                    buffer.writeByte(1);
                    buffer.writeVarInt(current.getAmount());
                };
            } else {
                return buffer -> {

                    buffer.writeByte(0);
                    buffer.writeFluidStack(current);
                };
            }
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {

        switch (dataSource.readByte()) {

            case 0: {
                // full stack

                final FluidStack data = dataSource.readFluidStack();

                this.notify(data);

                if (null != this._amountData) {
                    this._amountData.notify(data.getAmount());
                }

                break;
            }

            case 1: {
                // amount only

                if (null != this._amountData) {
                    this._amountData.notify(dataSource.readVarInt());
                }

                break;
            }
        }
    }

    //endregion
    //region internals

    private FluidStackData() {
    }

    private FluidStackData(NonNullSupplier<Supplier<FluidStack>> serverSideGetter) {

        super(serverSideGetter);
        this._lastValue = FluidStack.EMPTY;
    }

    private FluidStack _lastValue;
    private AbstractData<Integer> _amountData;

    //endregion
}
