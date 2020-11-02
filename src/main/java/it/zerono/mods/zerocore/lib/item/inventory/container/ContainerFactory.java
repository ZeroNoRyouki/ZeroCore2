/*
 *
 * ContainerFactory.java
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
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotIndexSet;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess"})
public class ContainerFactory {

    public static final ContainerFactory EMPTY = new ContainerFactory();

    protected ContainerFactory() {

        this._slotFactories = Lists.newArrayList();
        this._slotIndexToTemplateMap = Maps.newHashMap();
        this._templateToIndicesMap = Maps.newHashMap();
        this._slotTypeToIndicesCache = null;
        this._playPlayerInventoryUsage = PlayerInventoryUsage.None;

        this.onAddSlots();
    }

    /**
     * Override in your derived class to add your slots to the factory
     *
     * Keep in mind that this is called during object construction
     */
    protected void onAddSlots() {
    }

    /**
     * Check if the given index is valid
     *
     * @param index the slot index
     * @return true if the index is valid, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isIndexValid(final int index) {
        return this._slotIndexToTemplateMap.containsKey(index);
    }

    public Iterable<SlotFactory> getSlots() {
        return this._slotFactories;
    }

    /**
     * Return the SlotTemplate of the slot at the given index
     * @param index the slot index
     * @return  the SlotTemplate of the slot, or null if there is no slot at the given index
     */
    Optional<SlotTemplate> getSlotTemplate(final int index) {
        return Optional.ofNullable(this._slotIndexToTemplateMap.get(index));
    }

    /**
     * Return the type of the slot at the given index
     *
     * @param index the slot index
     * @return the SlotType of the slot, or SlotType.Unknown if there is no slot at the given index
     */
    public SlotType getSlotType(final int index) {
        return this.getSlotTemplate(index).map(SlotTemplate::getType).orElse(SlotType.Unknown);
    }

    /**
     * Check if the slot at the given index is of the given type
     *
     * @param index the slot index
     * @param type  they type to check
     * @return  true if the slot types matches, false otherwise
     */
    public boolean isSlotOfType(final int index, final SlotType type) {
        return type == this.getSlotType(index);
    }

    /**
     * Get a List containing all the SlotIndexSet of all the slots having the given type
     *
     * @param type  the slot type
     * @return a List of all the SlotIndexSet for the given slot type
     */
    public List<SlotIndexSet> getIndicesForType(final SlotType type) {

        if (null == this._slotTypeToIndicesCache || !this._slotTypeToIndicesCache.containsKey(type)) {

            final List<SlotIndexSet> list = this._templateToIndicesMap.entrySet().stream()
                    .filter(entry -> null != entry.getKey() && entry.getKey().getType() == type)
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

            if (null == this._slotTypeToIndicesCache) {
                this._slotTypeToIndicesCache = Maps.newHashMap();
            }

            this._slotTypeToIndicesCache.put(type, list);
            return list;

        } else {

            return this._slotTypeToIndicesCache.get(type);
        }
    }

    public PlayerInventoryUsage getPlayPlayerInventoryUsage() {
        return this._playPlayerInventoryUsage;
    }

    protected void addSlot(final int inventorySlotIndex, final String inventoryName, final SlotTemplate template, int x, int y) {

        final SlotFactory slotFactory = new SlotFactory(inventoryName, template, inventorySlotIndex, x, y);
        final int slotIndex = this._slotFactories.size();

        this._slotFactories.add(slotFactory);
        this._slotIndexToTemplateMap.put(slotIndex, template);

        SlotIndexSet indexSet = this._templateToIndicesMap.get(template);

        if (null == indexSet) {
            this._templateToIndicesMap.put(template, indexSet = new SlotIndexSet(template));
        }

        indexSet.addIndex(slotIndex);

        // clear the type->indices cache (if it exist) every time a now slot is added
        if (null != this._slotTypeToIndicesCache) {

            this._slotTypeToIndicesCache.clear();
            this._slotTypeToIndicesCache = null;
        }
    }

    protected void addSlotsLine(final int startingInventorySlotIndex, final String inventoryName, final SlotTemplate template,
                                int x, int y, int slotAmount, int horizontalOffset, int verticalOffset) {

        for (int slotIndex = startingInventorySlotIndex ; slotIndex < startingInventorySlotIndex + slotAmount ; ++slotIndex) {

            this.addSlot(slotIndex, inventoryName, template, x, y);
            x += horizontalOffset;
            y += verticalOffset;
        }
    }

    protected void addSlotsRow(final int startingInventorySlotIndex, final String inventoryName, final SlotTemplate template,
                               int x, int y, int slotAmount, int offset) {
        this.addSlotsLine(startingInventorySlotIndex, inventoryName, template, x, y, slotAmount, offset, 0);
    }

    protected void addSlotsColumn(final int startingInventorySlotIndex, final String inventoryName, final SlotTemplate template,
                                  int x, int y, int slotAmount, int offset) {
        this.addSlotsLine(startingInventorySlotIndex, inventoryName, template, x, y, slotAmount, 0, offset);
    }

    @SuppressWarnings("SameParameterValue")
    protected void addSlotBox(final int startingInventorySlotIndex, final String inventoryName, final SlotTemplate template,
                              int x, int y, int horizontalAmount, int horizontalOffset, int verticalAmount, int verticalOffset) {

        for (int i = 0 ; i < verticalAmount ; ++i) {

            this.addSlotsRow(startingInventorySlotIndex + (i * horizontalAmount), inventoryName, template, x, y,
                    horizontalAmount, horizontalOffset);
            y += verticalOffset;
        }
    }

    protected void addPlayerMainInventorySlots(final int x, final int y) {

        this.addSlotBox(9, ModContainer.INVENTORYNAME_PLAYER_INVENTORY, SlotTemplate.TEMPLATE_PLAYERINVENTORY, x, y, 9, 18, 3, 18);
        this._playPlayerInventoryUsage = PlayerInventoryUsage.MainInventory;
    }

    protected void addPlayerHotBarSlots(final int x, final int y) {

        this.addSlotsRow(0, ModContainer.INVENTORYNAME_PLAYER_INVENTORY, SlotTemplate.TEMPLATE_PLAYERHOTBAR, x, y, 9, 18);
        this._playPlayerInventoryUsage = PlayerInventoryUsage.HotBar;
    }

    protected void addStandardPlayerInventorySlots(final int x, final int y) {

        this.addPlayerHotBarSlots(x, y + 58*0);
        this.addPlayerMainInventorySlots(x, y);
        this._playPlayerInventoryUsage = PlayerInventoryUsage.Both;
    }

    //region internals

    private final List<SlotFactory> _slotFactories;
    private final Map<Integer, SlotTemplate> _slotIndexToTemplateMap;
    private final Map<SlotTemplate, SlotIndexSet> _templateToIndicesMap;
    private Map<SlotType, List<SlotIndexSet>> _slotTypeToIndicesCache;
    private PlayerInventoryUsage _playPlayerInventoryUsage;

    //endregion
}
