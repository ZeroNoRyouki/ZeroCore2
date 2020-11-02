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

import net.minecraft.nbt.CompoundNBT;

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
}
