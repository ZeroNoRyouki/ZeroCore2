/*
 *
 * SlotTemplate.java
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

import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotGeneric;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotGhostInput;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotGhostOutput;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.BiPredicate;

@SuppressWarnings({"WeakerAccess"})
public class SlotTemplate {

    public static final SlotTemplate TEMPLATE_PLAYERINVENTORY = new SlotTemplate(SlotType.PlayerInventory);
    public static final SlotTemplate TEMPLATE_PLAYERHOTBAR = new SlotTemplate(SlotType.PlayerHotbar);

    public SlotTemplate(final SlotType type) {
        this(type, getDefaultValidator(type));
    }

    public SlotTemplate(final SlotType type, final EnumSet<ItemHelper.MatchOption> filterOptions,
                        final ItemStack... filterItemStacks) {

        this(type, (Integer slotIndex, ItemStack stack) -> {

            if (filterItemStacks.length > 0) {
                for (final ItemStack filter : filterItemStacks) {
                    if (ItemHelper.stackMatch(filter, stack, filterOptions)) {
                        return true;
                    }
                }
            }

            return false;
        });
    }

    public SlotTemplate(final SlotType type, final Class<?> filterItem) {
        this(type, (Integer slotIndex, ItemStack stack) ->
                !stack.isEmpty() && filterItem.isInstance(stack.getItem()));
    }

    public SlotTemplate(final SlotType type, final ISlotFilter filter) {
        this(type, (BiPredicate<Integer, ItemStack>) filter::isStackAllowedForSlot);
    }

    public SlotType getType() {
        return this._type;
    }

    public Slot createSlot(final SlotFactory slotFactory, final IItemHandler inventory) {

        switch (slotFactory.getSlotType()) {

            case GhostInput:
                return new SlotGhostInput(inventory, slotFactory);

            case GhostOutput:
                return new SlotGhostOutput(inventory, slotFactory);

            default:
                return new SlotGeneric(inventory, slotFactory);
        }
    }

    public boolean match(final int slotIndex, final ItemStack stack) {
        return this._validator.test(slotIndex, stack);
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (null == other || this.getClass() != other.getClass()) {
            return false;
        }

        return this._type == ((SlotTemplate)other)._type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this._type);
    }

    private SlotTemplate(final SlotType type, final BiPredicate<Integer, ItemStack> validator) {

        this._type = type;
        this._validator = validator;
    }

    private static BiPredicate<Integer, ItemStack> getDefaultValidator(final SlotType type) {

        switch (type) {

            case Input:
            case GhostInput:
            case PlayerInventory:
            case PlayerHotbar:
            case Special:
                return (i, s) -> true;

            default:
                return (i, s) -> false;
        }
    }

    private final SlotType _type;
    private final BiPredicate<Integer, ItemStack> _validator;
}
