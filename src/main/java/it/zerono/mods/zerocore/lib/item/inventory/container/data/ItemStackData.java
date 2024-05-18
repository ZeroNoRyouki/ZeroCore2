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

import it.zerono.mods.zerocore.lib.data.stack.IStackHolderAccess;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.inventory.ItemStackHolder;
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
        implements IContainerData {

    public ItemStackData(final Supplier<@NotNull ItemStack> getter, final Consumer<@NotNull ItemStack> setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = ItemStack.EMPTY;
    }

    public static ItemStackData wrap(final NonNullList<ItemStack> list, final int index) {
        return new ItemStackData(() -> list.get(index), v -> list.set(index, v));
    }

    public static ItemStackData wrap(final IItemHandlerModifiable handler, final int slot) {
        return new ItemStackData(() -> handler.getStackInSlot(slot), v -> handler.setStackInSlot(slot, v));
    }

    public static ItemStackData wrap(final IStackHolderAccess<ItemStackHolder, ItemStack> holder, final int index) {
        return new ItemStackData(() -> holder.getStackAt(index), v -> holder.setStackAt(index, v));
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final ItemStack current = this._getter.get();

        if (this._lastValue.isEmpty() && current.isEmpty()) {
            return null;
        }

        final boolean equalItem = ItemHelper.stackMatch(this._lastValue, current, ItemHelper.MatchOption.MATCH_ITEM_NBT);

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
            this._setter.accept(record.value);
        } else if (entry instanceof AmountChangedEntry record) {
            // amount only
            this._setter.accept(this._getter.get().copyWithCount(record.amount()));
        }
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

    private final Supplier<@NotNull ItemStack> _getter;
    private final Consumer<@NotNull ItemStack> _setter;
    private ItemStack _lastValue;

    //endregion
}
