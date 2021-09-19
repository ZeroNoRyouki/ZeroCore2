/*
 *
 * SlotType.java
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

package it.zerono.mods.zerocore.lib.item.inventory.container.slot.type;

import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotFactory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;

import java.util.function.BiFunction;

public enum SlotType {

    Unknown,
    Input,              // a slot that can accept items in sided inventories
    Output,             // a slot that can output items in sided inventories
    GhostInput(SlotGhostInput::new),         // ghost slot that accept an ItemStack from the player
    GhostOutput(SlotGhostOutput::new),        // ghost slot that don't interact with the player
    CraftingResult,     // show a crafting result to the player
    PlayerInventory,    // a slot from the player inventory
    PlayerHotbar,       // a slot from the player hotbar
    Static(SlotStatic::new),             // a slot that cannot accept nor output items in sided inventories
    Special             // a slot for special items (things to charge, item cards, etc)
    ;

    public Slot slot(final IItemHandler inventory, final SlotFactory slotFactory) {
        return this._factory.apply(inventory, slotFactory);
    }

    //region internals

    SlotType() {
        this(SlotGeneric::new);
    }

    SlotType(final BiFunction<IItemHandler, SlotFactory, Slot> factory) {
        this._factory = factory;
    }

    private final BiFunction<IItemHandler, SlotFactory, Slot> _factory;

    //endregion
}
