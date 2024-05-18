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

package it.zerono.mods.zerocore.internal.network;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.data.nbt.INestedSyncableEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.network.AbstractBlockEntityPlayPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * A network message to automatically sync a {@link ISyncableEntity} TileEntity over the network
 */
public final class ModSyncableTileMessage
        extends AbstractBlockEntityPlayPacket<ModSyncableTileMessage> {

    public static final Type<ModSyncableTileMessage> TYPE = createType(ZeroCore.ROOT_LOCATION, "tile_sync");

    public static final StreamCodec<ByteBuf, ModSyncableTileMessage> STREAM_CODEC = createStreamCodec(
            ByteBufCodecs.BOOL, packet -> packet._nested,
            ByteBufCodecs.COMPOUND_TAG, packet -> packet._payload,
            ModSyncableTileMessage::new);

    /**
     * Create a sync message for the {@link ISyncableEntity} at the given coordinates
     *
     * @param entityLevel the {@link Level} of the entity to sync
     * @param entityPosition the coordinates of the entity to sync
     * @param entity the {@link ISyncableEntity} to sync
     * @return the new sync message
     */
    public static ModSyncableTileMessage create(Level entityLevel, BlockPos entityPosition, ISyncableEntity entity) {
        return create(entityLevel, entityPosition, entity, false);
    }

    /**
     * Create a sync message for the {@link ISyncableEntity} nested into the provided {@link INestedSyncableEntity}
     *
     * @param entityLevel the {@link Level} of the entity to sync
     * @param entityPosition the coordinates of the entity to sync
     * @param nestedEntity the {@link INestedSyncableEntity} containing the entity to sync
     * @return the new sync message
     * @throws UnsupportedOperationException if there is no nested syncable entity
     */
    public static ModSyncableTileMessage create(Level entityLevel, BlockPos entityPosition, INestedSyncableEntity nestedEntity) {

        Preconditions.checkNotNull(nestedEntity, "Nested entity must not be null");

        final var entity = nestedEntity.getNestedSyncableEntity()
                .orElseThrow(() -> new UnsupportedOperationException("Unable to create a sync message for an empty nested syncable entity"));

        return create(entityLevel, entityPosition, entity, true);
    }

    //region AbstractBlockEntityPlayPacket

    @Override
    protected void processBlockEntity(PacketFlow flow, BlockEntity tileEntity) {

        if (null == tileEntity.getLevel()) {

            Log.LOGGER.error(Log.NETWORK, "Refusing to sync a BlockEntity without a valid level");
            return;
        }

        ISyncableEntity entity = null;

        if (tileEntity instanceof INestedSyncableEntity && this._nested) {
            entity = ((INestedSyncableEntity) tileEntity).getNestedSyncableEntity().orElse(null);
        } else if (tileEntity instanceof ISyncableEntity) {
            entity = (ISyncableEntity) tileEntity;
        }

        if (null != entity) {
            entity.syncDataFrom(this._payload, tileEntity.getLevel().registryAccess(), ISyncableEntity.SyncReason.NetworkUpdate);
        } else {
            Log.LOGGER.error(Log.NETWORK, "No syncable {}entity found while processing a sync message",
                    this._nested ? "(nested) " : "");
        }
    }

    //endregion
    //region internals

    private static ModSyncableTileMessage create(Level entityLevel, BlockPos entityPosition, ISyncableEntity entity,
                                                 boolean nested) {

        Preconditions.checkNotNull(entityLevel, "Entity level must not be null");
        Preconditions.checkNotNull(entityPosition, "Entity position must not be null");
        Preconditions.checkNotNull(entity, "Entity must not be null");

        final var position = new GlobalPos(entityLevel.dimension(), entityPosition);
        final var payload = entity.syncDataTo(new CompoundTag(), entityLevel.registryAccess(), ISyncableEntity.SyncReason.NetworkUpdate);

        return new ModSyncableTileMessage(position, nested, payload);
    }

    private ModSyncableTileMessage(GlobalPos position, boolean nested, CompoundTag data) {

        super(TYPE, position);

        Preconditions.checkNotNull(data, "Data must not be null");

        this._nested = nested;
        this._payload = data;
    }

    private final CompoundTag _payload;
    private final boolean _nested;

    //endregion
}
