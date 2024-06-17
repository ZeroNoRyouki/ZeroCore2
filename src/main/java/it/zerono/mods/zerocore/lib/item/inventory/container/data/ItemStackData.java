/*
 *
 * ItemStackData.java
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
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.inventory.ItemStackHolder;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.AmountChangedEntry;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemStackData
        extends AbstractData<ItemStack>
        implements IContainerData {

    public static ItemStackData immutable(ModContainer container, ItemStack value) {
        return of(container, () -> value, CodeHelper.emptyConsumer());
    }

    public static ItemStackData empty(ModContainer container) {
        return immutable(container, ItemStack.EMPTY);
    }

    public static ItemStackData sampled(int frequency, ModContainer container, Supplier<@NotNull ItemStack> getter,
                                        Consumer<@NotNull ItemStack> clientSideSetter) {
        return of(container, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static ItemStackData of(ModContainer container, Supplier<@NotNull ItemStack> getter,
                                   Consumer<@NotNull ItemStack> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new ItemStackData(getter, clientSideSetter) : new ItemStackData(getter);

        container.addBindableData(data);
        return data;
    }

    public static ItemStackData of(ModContainer container, NonNullList<ItemStack> list, int index) {

        Preconditions.checkNotNull(list, "List must not be null.");
        Preconditions.checkArgument(index >= 0 && index < list.size(), "Index must be a valid index for the list.");

        return of(container, () -> list.get(index), v -> list.set(index, v));
    }

    public static ItemStackData of(ModContainer container, IItemHandlerModifiable handler, int slot) {

        Preconditions.checkNotNull(handler, "Handler must not be null.");
        Preconditions.checkArgument(slot >= 0 && slot < handler.getSlots(), "Slot must be a valid slot index for the handler.");

        return of(container, () -> handler.getStackInSlot(slot), v -> handler.setStackInSlot(slot, v));
    }

    public static ItemStackData of(ModContainer container, boolean isClientSide,
                                   IStackHolderAccess<ItemStackHolder, ItemStack> holder, int index) {

        Preconditions.checkNotNull(holder, "Holder must not be null.");

        return of(container, () -> holder.getStackAt(index), v -> holder.setStackAt(index, v));
    }

    public IBindableData<Integer> amount() {

        if (null == this._amountData) {
            this._amountData = AbstractData.as(0, intConsumer ->
                    this.bind(stack -> intConsumer.accept(stack.getCount())));
        }

        return this._amountData;
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final ItemStack current = this.getValue();

        if (this._lastValue.isEmpty() && current.isEmpty()) {
            return null;
        }

        final boolean equalItem = ItemHelper.stackMatch(this._lastValue, current, ItemHelper.MatchOption.MATCH_EXISTING_STACK);

        if (!equalItem || current.getCount() != this._lastValue.getCount()) {

            this._lastValue = current.copy();

            if (equalItem) {
                return new AmountChangedEntry(this._lastValue.getCount());
            } else {
                return new ItemStackEntry(this._lastValue);
            }
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return AmountChangedEntry.from(buffer, ItemStackEntry::from);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof ItemStackEntry record) {

            // full stack

            this.setClientSideValue(record.value);
            this.notify(record.value);

        } else if (entry instanceof AmountChangedEntry record) {

            // amount only

            this.setClientSideValue(this.getValue().copyWithCount(record.amount()));
        }
    }

    //endregion
    //region IBindableData<ItemStack>

    @Nullable
    @Override
    public ItemStack defaultValue() {
        return ItemStack.EMPTY;
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record ItemStackEntry(ItemStack value)
            implements ISyncedSetEntry {

        private static ItemStackEntry from(RegistryFriendlyByteBuf buffer) {
            return new ItemStackEntry(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {

            // mark this as a full stack update. See {@link AmountChangedEntry#from}
            buffer.writeByte(1);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, this.value);
        }
    }

    //endregion

    private ItemStackData(Supplier<ItemStack> getter, Consumer<ItemStack> clientSideSetter) {
        super(getter, clientSideSetter);
    }

    private ItemStackData(Supplier<ItemStack> getter) {

        super(getter);
        this._lastValue = ItemStack.EMPTY;
    }

    private ItemStack _lastValue;
    private IBindableData<Integer> _amountData;

    //endregion
}
