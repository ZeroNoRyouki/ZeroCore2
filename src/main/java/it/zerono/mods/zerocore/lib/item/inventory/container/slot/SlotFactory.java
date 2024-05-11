/*
 *
 * SlotFactory.java
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

package it.zerono.mods.zerocore.lib.item.inventory.container.slot;

import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.items.IItemHandler;

public class SlotFactory {

    public SlotFactory(String inventoryName, SlotTemplate template, int index, int x, int y) {

        this._inventoryName = inventoryName;
        this._template = template;
        this._index = index;
        this._guiX = x;
        this._guiY = y;
    }

    public Slot createSlot(final IItemHandler inventory) {
        return this.getTemplate().createSlot(this, inventory);
    }

    public String getInventoryName() {
        return this._inventoryName;
    }

    public SlotTemplate getTemplate() {
        return this._template;
    }

    public SlotType getSlotType() {
        return this._template.getType();
    }

    public int getIndex() {
        return this._index;
    }

    public int getX() {
        return this._guiX;
    }

    public int getY() {
        return this._guiY;
    }

    private final String _inventoryName;
    private final SlotTemplate _template;
    private final int _index;
    private final int _guiX;
    private final int _guiY;
}
