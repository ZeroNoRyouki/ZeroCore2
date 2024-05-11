/*
 *
 * ModSyncableTileMessage.java
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

package it.zerono.mods.zerocore.lib.network;

import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.data.nbt.INestedSyncableEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * A network message to automatically sync a {@link ISyncableEntity} TileEntity over the network
 */
public final class ModSyncableTileMessage
        extends AbstractBlockEntityPlayPacket {

    public static final ResourceLocation ID = ZeroCore.ROOT_LOCATION.buildWithSuffix("tile_sync");

    /**
     * Create a sync message for the {@link ISyncableEntity} at the given coordinates
     *
     * @param tileEntityPosition the coordinates of the entity to sync
     * @param entity the {@link ISyncableEntity} to sync
     * @return the new sync message
     */
    public static ModSyncableTileMessage create(final BlockPos tileEntityPosition, final ISyncableEntity entity) {
        return new ModSyncableTileMessage(tileEntityPosition, entity, false);
    }

    /**
     * Create a sync message for the {@link ISyncableEntity} nested into the provided {@link INestedSyncableEntity}
     *
     * @param tileEntityPosition the coordinates of the {@link INestedSyncableEntity} containing the entity to sync
     * @param entity the {@link INestedSyncableEntity} containing the entity to sync
     * @return the new sync message
     * @throws UnsupportedOperationException if there is no nested syncable entity
     */
    public static ModSyncableTileMessage create(final BlockPos tileEntityPosition, final INestedSyncableEntity entity) {
        return new ModSyncableTileMessage(tileEntityPosition, entity.getNestedSyncableEntity()
                .orElseThrow(() -> new UnsupportedOperationException("Unable to create a sync message for an empty nested syncable entity")), true);
    }

    public ModSyncableTileMessage(final FriendlyByteBuf buffer) {

        super(ID, buffer);
        this._nested = buffer.readBoolean();
        this._payload = buffer.readNbt();
    }

    //region AbstractBlockEntityPlayPacket

    @Override
    protected void processBlockEntity(PacketFlow flow, BlockEntity tileEntity) {

        ISyncableEntity entity = null;

        if (tileEntity instanceof INestedSyncableEntity && this._nested) {
            entity = ((INestedSyncableEntity) tileEntity).getNestedSyncableEntity().orElse(null);
        } else if (tileEntity instanceof ISyncableEntity) {
            entity = (ISyncableEntity) tileEntity;
        }

        if (null != entity) {
            entity.syncDataFrom(this._payload, ISyncableEntity.SyncReason.NetworkUpdate);
        } else {
            Log.LOGGER.error(Log.NETWORK, "No syncable {}entity found while processing a sync message",
                    this._nested ? "(nested) " : "");
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {

        super.write(buffer);
        buffer.writeBoolean(this._nested);
        buffer.writeNbt(this._payload);
    }

    //region internals

    /**
     * Construct the message on the sender side
     *
     * @param tileEntityPosition the coordinates of the TileEntity
     * @param entity             the TileEntity to sync
     * @param nested             true if the entity is nested inside another entity
     */
    private ModSyncableTileMessage(final BlockPos tileEntityPosition, final ISyncableEntity entity, final boolean nested) {

        super(ID, tileEntityPosition);
        this._nested = nested;
        this._payload = new CompoundTag();
        entity.syncDataTo(this._payload, ISyncableEntity.SyncReason.NetworkUpdate);
    }

    private final CompoundTag _payload;
    private final boolean _nested;

    //endregion
}
