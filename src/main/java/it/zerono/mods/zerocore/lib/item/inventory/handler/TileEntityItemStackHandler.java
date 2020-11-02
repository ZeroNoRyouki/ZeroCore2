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

import it.zerono.mods.zerocore.lib.DebuggableHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityItemStackHandler extends ItemStackHandler implements IDebuggable {

    public TileEntityItemStackHandler(TileEntity linkedTileEntity) {

        super(1);
        this._linkedTE = linkedTileEntity;
    }

    public TileEntityItemStackHandler(TileEntity linkedTileEntity, int size) {

        super(size);
        this._linkedTE = linkedTileEntity;
    }

    public TileEntityItemStackHandler(TileEntity linkedTileEntity, NonNullList<ItemStack> stacks) {

        super(stacks);
        this._linkedTE = linkedTileEntity;
    }

    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {
        DebuggableHelper.getDebugMessagesFor(messages, this);
    }

    //endregion
    //region internals

    @Override
    protected void onContentsChanged(int slot) {
        this._linkedTE.markDirty();
    }

    private final TileEntity _linkedTE;

    //endregion
}
