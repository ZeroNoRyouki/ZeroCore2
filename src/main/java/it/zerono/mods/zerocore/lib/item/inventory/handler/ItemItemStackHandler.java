///*
// *
// * ItemItemStackHandler.java
// *
// * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *
// * DO NOT REMOVE OR EDIT THIS HEADER
// *
// */
//
//package it.zerono.mods.zerocore.lib.item.inventory.handler;
//
//import com.google.common.base.Preconditions;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraftforge.items.ItemStackHandler;
//
//@Deprecated //TODO not fully imp!
//public class ItemItemStackHandler extends ItemStackHandler {
//
//    public ItemItemStackHandler(ItemStack owner, int inventorySize) {
//
//        super(inventorySize);
//
//        Preconditions.checkNotNull(owner, "The inventory owner must be a valid ItemStack");
//        this._owner = owner;
//        this._autoSave = true;
//    }
//
//    public ItemStack getOwner() {
//        return this._owner;
//    }
//
//    public void save() {
//
//        final ItemStack owner = this.getOwner();
//        final CompoundNBT nbt = owner.getTag();
//
//        //TODO imp!
//    }
//
//    protected void load() {
//        //TODO imp!
//    }
//
//    @Override
//    protected void onContentsChanged(int slot) {
//
//        if (this._autoSave) {
//            this.save();
//        }
//    }
//
//    private final ItemStack _owner;
//    private final boolean _autoSave;
//
//    private static final String STORAGE_KEY ="inventory";
//}
