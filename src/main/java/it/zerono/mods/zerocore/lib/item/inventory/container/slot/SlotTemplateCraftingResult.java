/*
 *
 * SlotTemplateCraftingResult.java
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

import it.zerono.mods.zerocore.lib.item.inventory.container.ICrafter;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotCraftingResult;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;

@SuppressWarnings({"WeakerAccess"})
public class SlotTemplateCraftingResult extends SlotTemplate {

    public SlotTemplateCraftingResult(final ICrafter crafter) {

        super(SlotType.CraftingResult);
        this._crafter = crafter;
    }

    public ICrafter getCrafter() {
        return this._crafter;
    }

    @Override
    public Slot createSlot(final SlotFactory slotFactory, final IItemHandler itemHandler) {

        switch (slotFactory.getSlotType()) {

            case CraftingResult:
                return new SlotCraftingResult(itemHandler, slotFactory, this.getCrafter());

            default:
                return super.createSlot(slotFactory, itemHandler);
        }
    }

    private final ICrafter _crafter;
}
