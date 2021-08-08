/*
 *
 * EmptyVanillaInventory.java
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

package it.zerono.mods.zerocore.lib.item.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class EmptyVanillaInventory
    implements Container {

    public static final EmptyVanillaInventory INSTANCE = new EmptyVanillaInventory();

    //region IInventory

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    @Override
    public ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStack.EMPTY;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStack.EMPTY;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setItem(int index, ItemStack stack) {
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {
    }

    //endregion
    //region internals

    private EmptyVanillaInventory() {
    }

    //endregion
}
