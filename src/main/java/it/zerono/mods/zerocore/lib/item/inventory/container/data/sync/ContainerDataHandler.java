package it.zerono.mods.zerocore.lib.item.inventory.container.data.sync;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMaps;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IContainerData;
import it.zerono.mods.zerocore.lib.network.AbstractPlayPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ContainerDataHandler {

    public ContainerDataHandler(int containerId) {

        Preconditions.checkArgument(containerId >= 0, "Container ID must be greater or equal to zero");

        this._containerId = containerId;
        this._data = new ObjectArrayList<>(8);
    }

    public void add(IContainerData datum) {

        if (MAX_DATA == this._data.size()) {
            throw new IllegalStateException("Can't add more sync data entries");
        }

        this._data.add(datum);
    }

    public void broadcastChanges(ServerPlayer player) {

        if (!this._data.isEmpty()) {
            Lib.NETWORK_HANDLER.sendToPlayer(player, new ContainerSyncPacket(this.getUpdates()));
        }
    }

    //region Sync packet

    public static class ContainerSyncPacket
            extends AbstractPlayPacket<ContainerSyncPacket> {

        public static final Type<ContainerSyncPacket> TYPE = createType(ZeroCore.ROOT_LOCATION, "container_sync");

        public static final StreamCodec<RegistryFriendlyByteBuf, ContainerSyncPacket> STREAM_CODEC = StreamCodec.composite(
                SyncSet.STREAM_CODEC, packet -> packet._syncSet, ContainerSyncPacket::new);

        @Override
        public void handlePacket(IPayloadContext context) {

            final ContainerDataHandler syncedData = getClientSideContainerDataHandler();

            if (null != syncedData) {
                syncedData.syncFrom(this._syncSet);
            }
        }

        //region internals

        private ContainerSyncPacket(SyncSet syncSet) {

            super(TYPE);

            Preconditions.checkNotNull(syncSet, "Sync set must not be null");
            this._syncSet = syncSet;
        }

        private final SyncSet _syncSet;

        //endregion
    }

    //endregion
    //region internals
    //region SyncSet

    private record SyncSet(int containerId, Short2ObjectMap<ISyncedSetEntry> entries) {

        public static final StreamCodec<RegistryFriendlyByteBuf, SyncSet> STREAM_CODEC = StreamCodec.ofMember(
                ContainerDataHandler::serializeSyncSet, ContainerDataHandler::deserializeSyncSet);

        public static SyncSet empty() {
            return new SyncSet(-1, Short2ObjectMaps.emptyMap());
        }
    }

    //endregion

    private static void serializeSyncSet(SyncSet set, RegistryFriendlyByteBuf buffer) {

        final var entries = set.entries();

        buffer.writeInt(set.containerId);
        buffer.writeShort(entries.size());

        for (final var mapEntry : entries.short2ObjectEntrySet()) {

            // the index
            buffer.writeShort(mapEntry.getShortKey());
            // the data
            mapEntry.getValue().accept(buffer);
        }
    }

    private static SyncSet deserializeSyncSet(RegistryFriendlyByteBuf buffer) {

        /* - Called on the network thread - */

        final ContainerDataHandler handler = getClientSideContainerDataHandler();

        if (null == handler) {
            return SyncSet.empty();
        }

        return handler.getUpdates(buffer);
    }

    /**
     * Called on the physical server side to acquire the values to sync
     *
     * @return a {@link SyncSet} containing the values to be synced
     */
    private SyncSet getUpdates() {

        final Short2ObjectMap<ISyncedSetEntry> entries = new Short2ObjectArrayMap<>(this._data.size());

        for (short index = 0; index < this._data.size(); ++index) {

            final var entry = this._data.get(index).getChangedValue();

            if (null != entry) {
                entries.put(index, entry);
            }
        }

        return new SyncSet(this._containerId, entries);
    }

    /**
     * Called on the physical client side to deserialize the {@link SyncSet} containing the values to be synced
     *
     * <p>Called on the network thread</p>
     *
     * @return the deserialized {@link SyncSet} containing the values to be synced
     */
    private SyncSet getUpdates(RegistryFriendlyByteBuf buffer) {

        final int remoteContainerId = buffer.readInt();
        final short count = buffer.readShort();

        if (count > MAX_DATA) {
            return SyncSet.empty();
        }

        final Short2ObjectMap<ISyncedSetEntry> entries = new Short2ObjectArrayMap<>(count);

        for (short i = 0; i < count; ++i) {

            final short index = buffer.readShort();

            if (index >= 0 && index < this._data.size()) {
                entries.put(index, this._data.get(index).getValueFrom(buffer));
            }
        }

        return new SyncSet(remoteContainerId, entries);
    }

    private void syncFrom(SyncSet changes) {

        if (this._containerId != changes.containerId) {
            return;
        }

        for (final var mapEntry : changes.entries().short2ObjectEntrySet()) {

            final short index = mapEntry.getShortKey();

            if (index >= 0 && index < this._data.size()) {
                this._data.get(index).updateFrom(mapEntry.getValue());
            }
        }
    }

    @Nullable
    private static ContainerDataHandler getClientSideContainerDataHandler() {

        final ModContainer container = ZeroCore.getProxy().getCurrentClientSideModContainer();

        return null == container ? null : container.getContainerDataHandler();
    }

    private static final int MAX_DATA = 256;

    private final int _containerId;
    private final List<IContainerData> _data;

    //endregion
}
