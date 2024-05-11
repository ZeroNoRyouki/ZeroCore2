/*
 *
 * ItemHandlerModifiableForwarder.java
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

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

@SuppressWarnings({"WeakerAccess"})
public class ItemHandlerModifiableForwarder extends ItemHandlerForwarder implements IItemHandlerModifiable {

    public ItemHandlerModifiableForwarder(final IItemHandlerModifiable handler) {
        super(handler);
    }

    public IItemHandlerModifiable getHandler() {
        return (IItemHandlerModifiable)super.getHandler();
    }

    public void setHandler(final IItemHandlerModifiable handler) {
        super.setHandler(handler);
    }

    //region IItemHandlerModifiable

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.getHandler().setStackInSlot(slot, stack);
    }
}
