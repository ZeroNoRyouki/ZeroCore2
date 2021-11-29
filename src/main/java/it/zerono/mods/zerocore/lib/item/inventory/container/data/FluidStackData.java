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

import it.zerono.mods.zerocore.lib.data.stack.IStackHolderAccess;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import it.zerono.mods.zerocore.lib.fluid.FluidStackHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidStackData
        implements IContainerData {

    public FluidStackData(final NonNullSupplier<FluidStack> getter, final NonNullConsumer<FluidStack> setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = FluidStack.EMPTY;
    }

    public static FluidStackData wrap(final NonNullList<FluidStack> list, final int index) {
        return new FluidStackData(() -> list.get(index), v -> list.set(index, v));
    }

    public static FluidStackData wrap(final IStackHolderAccess<FluidStackHolder, FluidStack> holder, final int index) {
        return new FluidStackData(() -> holder.getStackAt(index), v -> holder.setStackAt(index, v));
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {

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
                    buffer.writeVarInt(this._lastValue.getAmount());
                };
            } else {
                return buffer -> {

                    buffer.writeByte(0);
                    buffer.writeFluidStack(this._lastValue);
                };
            }
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {

        switch (dataSource.readByte()) {

            case 0:
                // full stack
                this._setter.accept(dataSource.readFluidStack());
                break;

            case 1:
                // amount only
                this._setter.accept(FluidHelper.stackFrom(this._getter.get(), dataSource.readVarInt()));
                break;
        }
    }

    //endregion
    //region internals

    private final NonNullSupplier<FluidStack> _getter;
    private final NonNullConsumer<FluidStack> _setter;
    private FluidStack _lastValue;

    //endregion
}
