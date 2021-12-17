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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.internal.network.Network;
import it.zerono.mods.zerocore.lib.data.nbt.IConditionallySyncableEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.event.Event;
import it.zerono.mods.zerocore.lib.event.IEvent;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IContainerData;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotIndexSet;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotGeneric;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"WeakerAccess"})
public class ModContainer
        extends AbstractContainerMenu
        implements IContainerData {

    public static final String INVENTORYNAME_PLAYER_INVENTORY = "playerinventory";
    public static final String INVENTORY_CONTAINER = "container";

    public static final String SLOTGROUPNAME_PLAYER_INVENTORY = "playerinventory_main";
    public static final String SLOTGROUPNAME_PLAYER_HOTBAR = "playerinventory_hotbar";

    public ModContainer(final ContainerFactory factory, final MenuType<?> type, final int windowId, final Inventory playerInventory) {

        super(type, windowId);
        this._factory = factory;
        this._registeredInventories = Maps.newHashMap();
        this._inventorySlotsGroups = Maps.newHashMap();
        this._player = playerInventory.player;
    }

    public static ModContainer empty(final MenuType<?> type, final int windowId, final Inventory playerInventory) {
        return new ModContainer(ContainerFactory.EMPTY, type, windowId, playerInventory) {
            @Override
            public void setItem(int slotID, int p_182408_, ItemStack stack) {
            }
        };
    }

    public Optional<IItemHandler> getInventory(final String name) {
        return Optional.ofNullable(this._registeredInventories.get(name));
    }

    public void addInventory(final String name, final IItemHandler inventory) {
        this._registeredInventories.put(name, inventory);
    }

    public void addInventory(final String name, final Container inventory) {
        this.addInventory(name, new InvWrapper(inventory));
    }

    public void addInventory(final String name, final Inventory inventory) {
        this.addInventory(name, new PlayerInvWrapper(inventory));
    }

    public void addContainerData(final IContainerData data) {

        if (null == this._dataToSync) {
            this._dataToSync = new ObjectArrayList<>(4);
        }

        if (Short.MAX_VALUE == this._dataToSync.size()) {
            throw new IllegalStateException("Too many container data object added!");
        }

        this._dataToSync.add(data);
    }

    public Runnable subscribeContainerDataUpdate(final Runnable handler) {

        if (null == this._dataUpdateEvent) {
            this._dataUpdateEvent = new Event<>();
        }

        return this._dataUpdateEvent.subscribe(handler);
    }

    public void unsubscribeContainerDataUpdate(final Runnable handler) {

        if (null != this._dataUpdateEvent) {
            this._dataUpdateEvent.unsubscribe(handler);
        }
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

    public void syncFrom(@Nullable final IConditionallySyncableEntity entity) {
        this._syncableEntity = entity;
    }

    public void onContainerDataSync(final CompoundTag data) {

        if (null != this._syncableEntity && this._syncableEntity.getSyncableEntityId().equals(new ResourceLocation(data.getString("id")))) {
            this._syncableEntity.syncDataFrom(data.getCompound("payload"), ISyncableEntity.SyncReason.NetworkUpdate);
        }
    }

    public Player getPlayer() {
        return this._player;
    }

    //region IContainerData

    /**
     * Return a {@link FriendlyByteBuf} consumer that will be used to write this {@code IContainerData}'s data to a packet.
     * The consumer could either serialize the whole data to a packet or only the changes occurred since the last call to this method.
     * <p>
     * Return {@code null} if no data need to be serialized to the packet (maybe because no changes occurred since the last invocation of this method).
     *
     * @return the consumer, or {@code null}
     */
    @Nullable
    @Override
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {
        return buffer -> {

            buffer.writeInt(this.containerId);

            if (null != this._dataToSync && !this._dataToSync.isEmpty()) {
                for (int idx = 0; idx < this._dataToSync.size(); ++idx) {

                    final NonNullConsumer<FriendlyByteBuf> writer = this._dataToSync.get(idx).getContainerDataWriter();

                    if (null != writer) {

                        buffer.writeShort(idx);
                        writer.accept(buffer);
                    }
                }
            }

            buffer.writeShort(-1);
        };
    }

    /**
     * Read back the data that was serialized to a packet by a consumer provided by {@code getContainerDataWriter}
     *
     * @param dataSource the buffer containing the data
     */
    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {

        if (this.containerId == dataSource.readInt() && null != this._dataToSync && !this._dataToSync.isEmpty()) {

            short idx;

            while (-1 != (idx = dataSource.readShort())) {
                this._dataToSync.get(idx).readContainerData(dataSource);
            }

            if (null != this._dataUpdateEvent) {
                this._dataUpdateEvent.raise(Runnable::run);
            }
        }
    }

    //endregion
    //region Container

    /**
     * Determines whether supplied player can use this container
     *
     * @param player the player
     */
    @Override
    public boolean stillValid(Player player) {
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
    protected boolean moveItemStackTo(ItemStack sourceStack, int startIndex, int endIndex, boolean reverseDirection) {

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
                final ItemStack targetItemStack = targetSlot.getItem();

                if (!targetItemStack.isEmpty() &&
                        ItemHelper.stackMatch(targetItemStack, sourceStack, ItemHelper.MatchOption.MATCH_EXISTING_STACK) &&
                        targetSlot.mayPlace(sourceStack)) {

                    int mergedSize = targetItemStack.getCount() + sourceStack.getCount();
                    int maxStackSize = Math.min(targetSlot.getMaxStackSize(), sourceStack.getMaxStackSize());

                    if (mergedSize <= maxStackSize) {

                        ItemHelper.stackSetSize(sourceStack, 0);
                        ItemHelper.stackSetSize(targetItemStack, mergedSize);
                        targetSlot.setChanged();
                        return true;

                    } else if (targetItemStack.getCount() < maxStackSize) {

                        sourceStack.grow(-(maxStackSize - targetItemStack.getCount()));
                        ItemHelper.stackSetSize(targetItemStack, maxStackSize);
                        targetSlot.setChanged();
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

                if (!targetSlot.hasItem() && targetSlot.mayPlace(sourceStack)) {

                    targetSlot.set(ItemHelper.stackFrom(sourceStack));
                    targetSlot.setChanged();
                    ItemHelper.stackSetSize(sourceStack, 0);
                    return true;
                }

                targetSlotIndex += targetSlotIndexModifier;
            }
        }

        return false;
    }

    @Override
    public void clicked(int clickedSlotIndex, int dragType, ClickType clickTypeIn, Player player) {

        if (this._factory.isSlotOfType(clickedSlotIndex, SlotType.GhostInput)) {

            final Slot slot = this.getSlot(clickedSlotIndex);

            if (slot.hasItem()) {
                slot.set(ItemStack.EMPTY);
            }
        }

        final SlotType slotType = this._factory.getSlotType(clickedSlotIndex);

        switch (slotType) {

            case GhostOutput:
            case Static:
            case Unknown:
                break;

            default:
                super.clicked(clickedSlotIndex, dragType, clickTypeIn, player);
                break;
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
    public ItemStack quickMoveStack(Player player, int clickedSlotIndex) {

        final Optional<SlotTemplate> clickedTemplate = this._factory.getSlotTemplate(clickedSlotIndex);

        if (!clickedTemplate.isPresent()) {

            Log.LOGGER.warn("Unknown slot clicked in a ModContainer at index " + clickedSlotIndex);
            return ItemStack.EMPTY;
        }

        final SlotGeneric clickedSlot = (SlotGeneric)this.getSlot(clickedSlotIndex);

        if (!clickedSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        final ItemStack clickedStack = clickedSlot.getItem();
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
                    return ItemStack.EMPTY;
                }

                clickedSlot.onQuickCraft(clickedStack, resultStack);
                break;
            }

            case GhostInput:
            case GhostOutput: {
                // nothing can be taken out of a ghost slot
                return ItemStack.EMPTY;
            }

            case PlayerInventory: {

                if (!this.addStackToTargetSlots(clickedStack, SlotType.Special, false) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.Input, false) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.PlayerHotbar, false)) {
                    return ItemStack.EMPTY;
                }
                break;
            }

            case PlayerHotbar: {

                if (!this.addStackToTargetSlots(clickedStack, SlotType.Special, false) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.Input, false) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.PlayerInventory, false)) {
                    return ItemStack.EMPTY;
                }
                break;
            }

            case Special: {

                if (!this.addStackToTargetSlots(clickedStack, SlotType.PlayerInventory, true) &&
                    !this.addStackToTargetSlots(clickedStack, SlotType.PlayerHotbar, false)) {
                    return ItemStack.EMPTY;
                }

                clickedSlot.onQuickCraft(clickedStack, resultStack);
                break;
            }
        }

        if (clickedStack.getCount() == 0) {
            clickedSlot.set(ItemStack.EMPTY);
        } else {
            clickedSlot.setChanged();
        }

        if (clickedStack.getCount() == resultStack.getCount()) {
            return ItemStack.EMPTY;
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

    @Override
    public void broadcastChanges() {

        super.broadcastChanges();

        if (this._player instanceof ServerPlayer) {

            if (null != this._syncableEntity && this._syncableEntity.shouldSyncEntity()) {

                final CompoundTag envelope = new CompoundTag();

                envelope.putString("id", this._syncableEntity.getSyncableEntityId().toString());
                envelope.put("payload", this._syncableEntity.syncDataTo(new CompoundTag(), ISyncableEntity.SyncReason.NetworkUpdate));
                Network.sendServerContainerDataSync((ServerPlayer)this._player, envelope);
            }

            if (null != this._dataToSync && !this._dataToSync.isEmpty()) {
                Network.sendServerContainerData((ServerPlayer)this._player, this);
            }
        }
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

                if (this.moveItemStackTo(stack, range.lowerEndpoint(), range.upperEndpoint(), reverseDirection)) {
                    return true;
                }
            }
        }

        return false;
    }

    private final ContainerFactory _factory;
    private final Map<String, IItemHandler> _registeredInventories;
    private final Map<String, List<Slot>> _inventorySlotsGroups;
    private final Player _player;
    private IEvent<Runnable> _dataUpdateEvent;
    private IConditionallySyncableEntity _syncableEntity;
    private ObjectList<IContainerData> _dataToSync;

    //endregion
}
