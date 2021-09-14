/*
 *
 * ISyncableEntity.java
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

package it.zerono.mods.zerocore.lib.data.nbt;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.zerono.mods.zerocore.lib.item.inventory.ItemStackHolder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Sync a generic entity from/to NBT
 */
@SuppressWarnings("unused")
public interface ISyncableEntity {

    enum SyncReason {

        /**
         * A full sync, usually from/to the on disk data
         */
        FullSync,

        /**
         * A sync from/to the other side of the network
         */
        NetworkUpdate;

        public boolean isFullSync() {
            return this == FullSync;
        }

        public boolean isNetworkUpdate() {
            return this == NetworkUpdate;
        }
    }

    /**
     * Sync the entity data from the given {@link CompoundNBT}
     *
     * @param data       the {@link CompoundNBT} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    default void syncDataFrom(CompoundNBT data, SyncReason syncReason) {
    }

    /**
     * Sync the entity data to the given {@link CompoundNBT}
     *
     * @param data       the {@link CompoundNBT} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundNBT} the data was written to (usually {@code data})
     */
    default CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {
        return data;
    }

    /**
     * Utility method to sync a child {@link ISyncableEntity} from the parent entity data.
     * Call this in the parent entity {@code syncDataFrom} method
     *
     * @param childEntity the child entity to sync
     * @param dataKey     the name to load the child entity data from
     * @param parentData  the parent {@link CompoundNBT} to read from the provided dataKey
     * @param syncReason  the reason why the synchronization is necessary
     * @return true if the child data were found and the synchronization started
     */
    default boolean syncChildDataEntityFrom(ISyncableEntity childEntity, String dataKey, CompoundNBT parentData, SyncReason syncReason) {

        if (parentData.contains(dataKey)) {

            childEntity.syncDataFrom(parentData.getCompound(dataKey), syncReason);
            return true;
        }

        return false;
    }

    /**
     * Utility method to sync a child {@link ISyncableEntity} into the parent entity data.
     * Call this in the parent entity {@code syncDataTo} method
     *
     * @param childEntity the child entity to sync
     * @param dataKey     the name to store the child entity data under
     * @param parentData  the parent {@link CompoundNBT} to write to under the provided dataKey
     * @param syncReason  the reason why the synchronization is necessary
     */
    default void syncChildDataEntityTo(ISyncableEntity childEntity, String dataKey, CompoundNBT parentData, SyncReason syncReason) {
        parentData.put(dataKey, childEntity.syncDataTo(new CompoundNBT(), syncReason));
    }

    /**
     * Call the provided {@link NonNullConsumer} if the specified {@link CompoundNBT} element is present in the parent data.
     *
     * @param dataKey       the name of the element
     * @param parentData    the {@link CompoundNBT} to read the element from
     * @param consumer      a {@link NonNullConsumer} that will be called if the element is present
     */
    default void syncDataElementFrom(String dataKey, CompoundNBT parentData, NonNullConsumer<CompoundNBT> consumer) {

        if (parentData.contains(dataKey)) {
            consumer.accept(parentData.getCompound(dataKey));
        }
    }

    /**
     * Store the provided {@link CompoundNBT} element in the parent data.
     *
     * @param dataKey       the name for the element
     * @param parentData    the {@link CompoundNBT} to store the element into
     * @param value         the value to store
     */
    default void syncDataElementTo(String dataKey, CompoundNBT parentData, CompoundNBT value) {
        parentData.put(dataKey, value);
    }

    /**
     * Fill the provided {@link ItemStackHandler} if the specified element is present in the parent data.
     *
     * @param dataKey       the name of the element
     * @param parentData    the {@link CompoundNBT} to read the element from
     * @param value         the {@link ItemStackHandler} to load the data into
     */
    default void syncDataElementFrom(String dataKey, CompoundNBT parentData, ItemStackHandler value) {

        if (parentData.contains(dataKey)) {
            value.deserializeNBT(parentData.getCompound(dataKey));
        }
    }

    /**
     * Store the provided {@link ItemStackHandler} element in the parent data.
     *
     * @param dataKey       the name for the element
     * @param parentData    the {@link CompoundNBT} to store the element into
     * @param value         the value to store
     */
    default void syncDataElementTo(String dataKey, CompoundNBT parentData, ItemStackHandler value) {
        parentData.put(dataKey, value.serializeNBT());
    }

    /**
     * Fill the provided {@link ItemStackHolder} if the specified element is present in the parent data.
     *
     * @param dataKey       the name of the element
     * @param parentData    the {@link CompoundNBT} to read the element from
     * @param value         the {@link ItemStackHandler} to load the data into
     */
    default void syncDataElementFrom(String dataKey, CompoundNBT parentData, ItemStackHolder value) {

        if (parentData.contains(dataKey)) {
            value.deserializeNBT(parentData.getCompound(dataKey));
        }
    }

    /**
     * Store the provided {@link ItemStackHolder} element in the parent data.
     *
     * @param dataKey       the name for the element
     * @param parentData    the {@link CompoundNBT} to store the element into
     * @param value         the value to store
     */
    default void syncDataElementTo(String dataKey, CompoundNBT parentData, ItemStackHolder value) {
        parentData.put(dataKey, value.serializeNBT());
    }

    /**
     * Call the provided {@link BooleanConsumer} if the specified boolean element is present in the parent data.
     *
     * @param dataKey       the name of the element
     * @param parentData    the {@link CompoundNBT} to read the element from
     * @param consumer      a {@link BooleanConsumer} that will be called if the element is present
     */
    default void syncBooleanElementFrom(String dataKey, CompoundNBT parentData, BooleanConsumer consumer) {

        if (parentData.contains(dataKey)) {
            consumer.accept(parentData.getBoolean(dataKey));
        }
    }

    /**
     * Store the provided boolean element in the parent data.
     *
     * @param dataKey       the name for the element
     * @param parentData    the {@link CompoundNBT} to store the element into
     * @param value         the value to store
     */
    default void syncBooleanElementTo(String dataKey, CompoundNBT parentData, boolean value) {
        parentData.putBoolean(dataKey, value);
    }
}
