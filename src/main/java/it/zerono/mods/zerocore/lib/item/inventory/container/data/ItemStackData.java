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
import it.zerono.mods.zerocore.lib.data.stack.IStackHolderAccess;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.inventory.ItemStackHolder;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ItemStackData
        extends AbstractData<ItemStack>
        implements IContainerData {

    public static ItemStackData immutable(ModContainer container, boolean isClientSide, ItemStack value) {
        return of(container, isClientSide, () -> () -> value);
    }

    public static ItemStackData empty(boolean isClientSide) {
        return isClientSide ? new ItemStackData() : new ItemStackData(() -> () -> ItemStack.EMPTY);
    }

    public static ItemStackData sampled(int frequency, ModContainer container, boolean isClientSide,
                                        NonNullSupplier<Supplier<ItemStack>> serverSideGetter) {
        return of(container, isClientSide, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static ItemStackData of(ModContainer container, boolean isClientSide,
                                   NonNullSupplier<Supplier<ItemStack>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final ItemStackData data = isClientSide ? new ItemStackData() : new ItemStackData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    public static ItemStackData of(ModContainer container, boolean isClientSide, NonNullList<ItemStack> list, int index) {

        Preconditions.checkNotNull(list, "List must not be null.");
        Preconditions.checkArgument(index >= 0 && index < list.size(), "Index must be a valid index for the list.");

        final ItemStackData data = of(container, isClientSide,
                () -> () -> list.get(index));

        if (isClientSide) {
            data.bind(v -> list.set(index, v));
        }

        return data;
    }

    public static ItemStackData of(ModContainer container, boolean isClientSide, IItemHandlerModifiable handler, int slot) {

        Preconditions.checkNotNull(handler, "Handler must not be null.");
        Preconditions.checkArgument(slot >= 0 && slot < handler.getSlots(), "Slot must be a valid slot index for the handler.");

        final ItemStackData data = of(container, isClientSide,
                () -> () -> handler.getStackInSlot(slot));

        if (isClientSide) {
            data.bind(v -> handler.setStackInSlot(slot, v));
        }

        return data;
    }

    public static ItemStackData of(ModContainer container, boolean isClientSide,
                                   IStackHolderAccess<ItemStackHolder, ItemStack> holder, int index) {

        Preconditions.checkNotNull(holder, "Holder must not be null.");

        final ItemStackData data = of(container, isClientSide,
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
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {

        final ItemStack current = this._getter.get();

        if (this._lastValue.isEmpty() && current.isEmpty()) {
            return null;
        }

        final boolean equalItem = ItemHelper.stackMatch(this._lastValue, current, ItemHelper.MatchOption.MATCH_ITEM_DAMAGE_NBT);

        if (!equalItem || current.getCount() != this._lastValue.getCount()) {

            this._lastValue = current.copy();

            if (equalItem) {
                return buffer -> {

                    buffer.writeByte(1);
                    buffer.writeVarInt(current.getCount());
                };
            } else {
                return buffer -> {

                    buffer.writeByte(0);
                    buffer.writeItemStack(current, true);
                };
            }
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {

        switch (dataSource.readByte()) {

            case 0: {
                // full stack

                final ItemStack data = dataSource.readItem();

                this.notify(data);

                if (null != this._amountData) {
                    this._amountData.notify(data.getCount());
                }

                break;
            }

            case 1: {
                // count only

                if (null != this._amountData) {
                    this._amountData.notify(dataSource.readVarInt());
                }

                break;
            }
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

    private ItemStackData() {
    }

    private ItemStackData(NonNullSupplier<Supplier<ItemStack>> serverSideGetter) {

        super(serverSideGetter);
        this._lastValue = ItemStack.EMPTY;
    }

    private ItemStack _lastValue;
    private AbstractData<Integer> _amountData;

    //endregion
}
