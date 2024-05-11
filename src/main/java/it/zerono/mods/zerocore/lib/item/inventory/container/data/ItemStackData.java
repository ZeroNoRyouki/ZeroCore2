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
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.NonNullConsumer;
import net.neoforged.neoforge.common.util.NonNullSupplier;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public class ItemStackData
        implements IContainerData {

    public ItemStackData(final NonNullSupplier<ItemStack> getter, final NonNullConsumer<ItemStack> setter) {

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

    @Nullable
    @Override
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {

        final ItemStack current = this._getter.get();

        if (this._lastValue.isEmpty() && current.isEmpty()) {
            return null;
        }

        final boolean equalItem = ItemHelper.stackMatch(this._lastValue, current, ItemHelper.MatchOption.MATCH_ITEM_NBT);

        if (!equalItem || current.getCount() != this._lastValue.getCount()) {

            this._lastValue = current.copy();

            if (equalItem) {
                return buffer -> {

                    buffer.writeByte(1);
                    buffer.writeVarInt(this._lastValue.getCount());
                };
            } else {
                return buffer -> {

                    buffer.writeByte(0);
                    buffer.writeItem(this._lastValue);
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
                this._setter.accept(dataSource.readItem());
                break;

            case 1:
                // count only
                this._setter.accept(ItemHelper.stackFrom(this._getter.get(), dataSource.readVarInt()));
                break;
        }
    }

    //endregion
    //region internals

    private final NonNullSupplier<ItemStack> _getter;
    private final NonNullConsumer<ItemStack> _setter;
    private ItemStack _lastValue;

    //endregion
}
