/*
 *
 * ItemHandlerForwarder.java
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

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

@SuppressWarnings({"WeakerAccess"})
public class ItemHandlerForwarder implements IItemHandler {

    public ItemHandlerForwarder(final IItemHandler handler) {
        this.setHandler(handler);
    }

    public IItemHandler getHandler() {
        return this._handler;
    }

    public void setHandler(final IItemHandler handler) {
        this._handler = handler;
    }

    //region IItemHandler

    @Override
    public int getSlots() {
        return this.getHandler().getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.getHandler().getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return this.getHandler().insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.getHandler().extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.getHandler().getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return this.getHandler().isItemValid(slot, stack);
    }

    //region internals

    private IItemHandler _handler;
}
