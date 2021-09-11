/*
 *
 * TileEntityItemStackHandler.java
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

package it.zerono.mods.zerocore.lib.item.inventory.handler;

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.DebuggableHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.IntConsumer;

public class TileEntityItemStackHandler extends ItemStackHandler implements IDebuggable {

    public TileEntityItemStackHandler(final TileEntity linkedTileEntity) {
        this(linkedTileEntity, 1);
    }

    public TileEntityItemStackHandler(final TileEntity linkedTileEntity, final int size) {
        this(linkedTileEntity, size, TileEntityItemStackHandler::defaultValidator);
    }

    public TileEntityItemStackHandler(final TileEntity linkedTileEntity, final int size,
                                      final BiPredicate<Integer, ItemStack> itemValidator) {

        super(size);
        this._linkedTE = linkedTileEntity;
        this._itemValidator = itemValidator;
        this._slotChanged = CodeHelper.VOID_INT_CONSUMER;
    }

    public TileEntityItemStackHandler(final TileEntity linkedTileEntity, final NonNullList<ItemStack> stacks) {
        this(linkedTileEntity, stacks, TileEntityItemStackHandler::defaultValidator);
    }

    public TileEntityItemStackHandler(final TileEntity linkedTileEntity, final NonNullList<ItemStack> stacks,
                                      final BiPredicate<Integer, ItemStack> itemValidator) {

        super(stacks);
        this._linkedTE = linkedTileEntity;
        this._itemValidator = itemValidator;
        this._slotChanged = CodeHelper.VOID_INT_CONSUMER;
    }

    public TileEntityItemStackHandler setContentsChangedListener(final IntConsumer listener) {

        this._slotChanged = Objects.requireNonNull(listener);
        return this;
    }

    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {
        DebuggableHelper.getDebugMessagesFor(messages, this);
    }

    //endregion
    //region ItemStackHandler

    @Override
    public boolean isItemValid(final int slot, final ItemStack stack) {
        return this._itemValidator.test(slot, stack);
    }

    //endregion
    //region internals

    @Override
    protected void onContentsChanged(int slot) {

        this._linkedTE.setChanged();
        this._slotChanged.accept(slot);
    }

    private static boolean defaultValidator(final Integer slot, final ItemStack stack) {
        return true;
    }

    private final TileEntity _linkedTE;
    private final BiPredicate<Integer, ItemStack> _itemValidator;
    private IntConsumer _slotChanged;

    //endregion
}
