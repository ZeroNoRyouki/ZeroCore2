/*
 *
 * InventoryIterator.java
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

import net.minecraftforge.items.IItemHandler;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class InventoryIterator implements Iterator<IInventorySlot> {

    public InventoryIterator(IItemHandler inventory) {

        this._inventory = inventory;
        this._currentSlotIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return this._currentSlotIndex < this._inventory.getSlots();
    }

    @Override
    public IInventorySlot next() {

        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        return new InventorySlot(this._inventory, this._currentSlotIndex++);
    }

    private final IItemHandler _inventory;
    private int _currentSlotIndex;
}
