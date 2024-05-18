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
import it.zerono.mods.zerocore.lib.fluid.FluidStackHolder;
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
        implements IContainerData {

    public FluidStackData(final Supplier<@NotNull FluidStack> getter, final Consumer<@NotNull FluidStack> setter) {

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

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final FluidStack current = this._getter.get();

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
            this._setter.accept(record.value);
        } else if (entry instanceof AmountChangedEntry record) {
            // amount only
            this._setter.accept(this._getter.get().copyWithAmount(record.amount()));
        }
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

    private final Supplier<@NotNull FluidStack> _getter;
    private final Consumer<@NotNull FluidStack> _setter;
    private FluidStack _lastValue;

    //endregion
}
