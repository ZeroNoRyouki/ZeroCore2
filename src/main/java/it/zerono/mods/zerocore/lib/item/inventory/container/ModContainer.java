/*
 *
 * ModContainer.java
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

package it.zerono.mods.zerocore.lib.item.inventory.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotIndexSet;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotGeneric;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"WeakerAccess"})
public class ModContainer extends Container {

    public static final String INVENTORYNAME_PLAYER_INVENTORY = "playerinventory";
    public static final String INVENTORY_CONTAINER = "container";

    public static final String SLOTGROUPNAME_PLAYER_INVENTORY = "playerinventory_main";
    public static final String SLOTGROUPNAME_PLAYER_HOTBAR = "playerinventory_hotbar";

    public ModContainer(final ContainerFactory factory, final ContainerType<?> type, final int windowId) {

        super(type, windowId);
        this._factory = factory;
        this._registeredInventories = Maps.newHashMap();
        this._inventorySlotsGroups = Maps.newHashMap();
    }

    public static ModContainer empty(final ContainerType<?> type, final int windowId) {
        return new ModContainer(ContainerFactory.EMPTY, type, windowId) {
            @Override
            public void putStackInSlot(int slotID, ItemStack stack) {
            }
        };
    }

    public Optional<IItemHandler> getInventory(final String name) {
        return Optional.ofNullable(this._registeredInventories.get(name));
    }

    public void addInventory(final String name, final IItemHandler inventory) {
        this._registeredInventories.put(name, inventory);
    }

    public void addInventory(final String name, final IInventory inventory) {
        this.addInventory(name, new InvWrapper(inventory));
    }

    public void addInventory(final String name, final PlayerInventory inventory) {
        this.addInventory(name, new PlayerInvWrapper(inventory));
    }

    public void createSlots() {

        for (final SlotFactory slotFactory : this._factory.getSlots()) {

            final String inventoryName = slotFactory.getInventoryName();

            this.getInventory(inventoryName).ifPresent(inventory -> this.addSlotFor(slotFactory, inventory));
        }
    }

    public List<Slot> getInventorySlotsGroup(final String inventoryGroupName) {

        if (this._inventorySlotsGroups.containsKey(inventoryGroupName)) {
            return Collections.unmodifiableList(this._inventorySlotsGroups.get(inventoryGroupName));
        } else {
            return Collections.emptyList();
        }
    }

    public PlayerInventoryUsage getPlayPlayerInventoryUsage() {
        return this._factory.getPlayPlayerInventoryUsage();
    }

    //region Container

    /**
     * Determines whether supplied player can use this container
     *
     * @param player the player
     */
    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }

    /**
     * Merges provided ItemStack with the first available one in the container/player inventor between startIndex
     * (included) and endIndex (excluded) respecting what the target slots accept and any stack size limit they have
     *
     * @param sourceStack       the stack to merge in
     * @param startIndex        the first index (included) to consider for the merge
     * @param endIndex          the last index (excluded) to consider for the merge
     * @param reverseDirection  if true, the search goes from endIndex to startIndex
     * @return  true if the stack was merged, false otherwise
     */
    @Override
    protected boolean mergeItemStack(ItemStack sourceStack, int startIndex, int endIndex, boolean reverseDirection) {

        if (sourceStack.isEmpty() || !this._factory.isIndexValid(startIndex) || !this._factory.isIndexValid(endIndex - 1)) {
            return false;
        }

        final int targetSlotIndexModifier = reverseDirection ? -1 : +1;
        int targetSlotIndex = reverseDirection ? endIndex - 1 : startIndex;

        // firstly, try to merge the source stack with another, compatible, stack

        if (sourceStack.isStackable()) {

            while (sourceStack.getCount() > 0 &&
                    (!reverseDirection && targetSlotIndex < endIndex || reverseDirection && targetSlotIndex >= startIndex)) {

                final Slot targetSlot = this.getSlot(targetSlotIndex);
                final ItemStack targetItemStack = targetSlot.getStack();

                if (!targetItemStack.isEmpty() &&
                        ItemHelper.stackMatch(targetItemStack, sourceStack, ItemHelper.MatchOption.MATCH_EXISTING_STACK) &&
                        targetSlot.isItemValid(sourceStack)) {

                    int mergedSize = targetItemStack.getCount() + sourceStack.getCount();
                    int maxStackSize = Math.min(targetSlot.getSlotStackLimit(), sourceStack.getMaxStackSize());

                    if (mergedSize <= maxStackSize) {

                        ItemHelper.stackSetSize(sourceStack, 0);
                        ItemHelper.stackSetSize(targetItemStack, mergedSize);
                        targetSlot.onSlotChanged();
                        return true;

                    } else if (targetItemStack.getCount() < maxStackSize) {

                        sourceStack.grow(-(maxStackSize - targetItemStack.getCount()));
                        ItemHelper.stackSetSize(targetItemStack, maxStackSize);
                        targetSlot.onSlotChanged();
                        return true;
                    }
                }

                targetSlotIndex += targetSlotIndexModifier;
            }
        }

        // secondly, try to fill an empty slot if the first attempt failed

        if (sourceStack.getCount() > 0) {

            targetSlotIndex = reverseDirection ? endIndex - 1 : startIndex;

            while (!reverseDirection && targetSlotIndex < endIndex || reverseDirection && targetSlotIndex >= startIndex) {

                final Slot targetSlot = this.getSlot(targetSlotIndex);

                if (!targetSlot.getHasStack() && targetSlot.isItemValid(sourceStack)) {

                    targetSlot.putStack(ItemHelper.stackFrom(sourceStack));
                    targetSlot.onSlotChanged();
                    ItemHelper.stackSetSize(sourceStack, 0);
                    return true;
                }

                targetSlotIndex += targetSlotIndexModifier;
            }
        }

        return false;
    }

    @Override
    public ItemStack slotClick(int clickedSlotIndex, int dragType, ClickType clickTypeIn, PlayerEntity player) {

        if (this._factory.isSlotOfType(clickedSlotIndex, SlotType.GhostInput)) {

            final Slot slot = this.getSlot(clickedSlotIndex);

            if (slot.getHasStack()) {
                slot.putStack(ItemHelper.stackEmpty());
            }
        }

        final SlotType slotType = this._factory.getSlotType(clickedSlotIndex);

        switch (slotType) {

            case GhostOutput:
            case Static:
            case Unknown:
                return ItemStack.EMPTY;

            default:
                return super.slotClick(clickedSlotIndex, dragType, clickTypeIn, player);
        }
    }

    /**
     * Called when a player shift-clicks a slot in the GUI
     *
     * When the slot shift-clicked is in the player hotbar or in the player inventory, move the ItemStack contained in
     * the clicked slot to the first available position in the other inventories handled by the container
     *
     * When the slot shift-clicked is in the other inventories handled by the container, move the ItemStack contained in
     * the clicked slot to the first available position in the player hotbar or in the player inventory
     *
     * @param player            the player
     * @param clickedSlotIndex  the index of the slot the player shift-clicked
     * @return  null/ItemStack.EMPTY if the clicked slot is empty or if it's items could not be moved.
     *          if the clicked slot is not empty and it's items could be moved, returns a copy of the ItemStack
     *          contained in the slot
     */
    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int clickedSlotIndex) {

        final Optional<SlotTemplate> clickedTemplate = this._factory.getSlotTemplate(clickedSlotIndex);

        if (!clickedTemplate.isPresent()) {

            Log.LOGGER.warn("Unknown slot clicked in a ModContainer at index " + clickedSlotIndex);
            return ItemHelper.stackEmpty();
        }

        final SlotGeneric clickedSlot = (SlotGeneric)this.getSlot(clickedSlotIndex);

        if (!clickedSlot.getHasStack()) {
            return ItemHelper.stackEmpty();
        }

        final ItemStack clickedStack = clickedSlot.getStack();
        final ItemStack resultStack = ItemHelper.stackFrom(clickedStack);

        // Try to add the clicked-stack to the other slots in this container.
        // The targeted slot are chosen depending on the type of the clicked slot while favoring Special slots over others

        switch (clickedTemplate.map(SlotTemplate::getType).orElse(SlotType.Unknown)) {

            case Input:
            case Output:
            case Static: {

                if (!this.addStackToTargetSlots(clickedStack, SlotType.Special, false) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.PlayerInventory, true) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.PlayerHotbar, false)) {

                    return ItemHelper.stackEmpty();
                }

                clickedSlot.onSlotChange(clickedStack, resultStack);
                break;
            }

            case GhostInput:
            case GhostOutput: {
                // nothing can be taken out of a ghost slot
                return ItemHelper.stackEmpty();
            }

            case PlayerInventory: {

                if (!this.addStackToTargetSlots(clickedStack, SlotType.Special, false) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.Input, false) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.PlayerHotbar, false)) {

                    return ItemHelper.stackEmpty();
                }
                break;
            }

            case PlayerHotbar: {

                if (!this.addStackToTargetSlots(clickedStack, SlotType.Special, false) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.Input, false) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.PlayerInventory, false)) {

                    return ItemHelper.stackEmpty();
                }
                break;
            }

            case Special: {

                if (!this.addStackToTargetSlots(clickedStack, SlotType.PlayerInventory, true) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.PlayerHotbar, false)) {

                    return ItemHelper.stackEmpty();
                }

                clickedSlot.onSlotChange(clickedStack, resultStack);
                break;
            }
        }

        if (clickedStack.getCount() == 0) {
            clickedSlot.putStack(ItemHelper.stackEmpty());
        } else {
            clickedSlot.onSlotChanged();
        }

        if (clickedStack.getCount() == resultStack.getCount()) {
            return ItemHelper.stackEmpty();
        }

        clickedSlot.onTake(player, clickedStack);

        return resultStack;
    }

    /**
     * Adds an item slot to this container
     *
     * DO NOT CALL THIS DIRECTLY
     *
     * @param slotIn    the slot to be added
     */
    @Override
    protected Slot addSlot(Slot slotIn) {

        if (!(slotIn instanceof SlotGeneric)) {
            throw new RuntimeException("Do not call Container.addSlot() directly! Use the ContainerFactory to add your slots to a ModContainer");
        }

        return super.addSlot(slotIn);
    }

    //endregion
    //region internals

    /**
     * Adds an item slot to this container and to the corresponding inventory slots group
     */
    private void addSlotFor(final SlotFactory slotFactory, final IItemHandler inventory) {

        final Slot slot = slotFactory.createSlot(inventory);

        super.addSlot(slot);
        this.addSlotToGroup(slotFactory.getInventoryName(), slot);

        switch (slotFactory.getTemplate().getType()) {

            case PlayerInventory:
                this.addSlotToGroup(SLOTGROUPNAME_PLAYER_INVENTORY, slot);
                break;

            case PlayerHotbar:
                this.addSlotToGroup(SLOTGROUPNAME_PLAYER_HOTBAR, slot);
                break;
        }
    }

    private void addSlotToGroup(final String inventoryName, final Slot slot) {
        this._inventorySlotsGroups.computeIfAbsent(inventoryName, name -> Lists.newArrayList()).add(slot);
    }

    private boolean addStackToTargetSlots(final ItemStack stack, final SlotType targetSlotsType,
                                          final boolean reverseDirection) {

        final List<SlotIndexSet> targetIndices = this._factory.getIndicesForType(targetSlotsType);

        if (targetIndices.isEmpty()) {
            return false;
        }

        for (final SlotIndexSet indexSet : targetIndices) {
            for (final Range<Integer> range: indexSet.asRanges()) {

                if (this.mergeItemStack(stack, range.lowerEndpoint(), range.upperEndpoint(), reverseDirection)) {
                    return true;
                }
            }
        }

        return false;
    }

    private final ContainerFactory _factory;
    private final Map<String, IItemHandler> _registeredInventories;
    private final Map<String, List<Slot>> _inventorySlotsGroups;

    //endregion
}
