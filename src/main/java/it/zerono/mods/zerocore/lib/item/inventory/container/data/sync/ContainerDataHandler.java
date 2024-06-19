package it.zerono.mods.zerocore.lib.item.inventory.container.data.sync;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMaps;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.internal.Lib;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IContainerData;
import it.zerono.mods.zerocore.lib.network.AbstractPlayPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ContainerDataHandler {

    public static void registerPackets(PayloadRegistrar registrar) {

        registrar.playToClient(ContainerDataHandler.ContainerSyncPacket.TYPE,
                ContainerDataHandler.ContainerSyncPacket.STREAM_CODEC,
                ContainerDataHandler.ContainerSyncPacket::handlePacket);

        registrar.playToServer(ContainerDataHandler.ContainerSyncAckPacket.TYPE,
                ContainerDataHandler.ContainerSyncAckPacket.STREAM_CODEC,
                ContainerDataHandler.ContainerSyncAckPacket::handlePacket);
    }

    public ContainerDataHandler(ModContainer container) {

        Preconditions.checkNotNull(container, "Container must not be null");

        this._container = container;
        this._data = new ObjectArrayList<>(8);
        this._canSendUpdates = false;
    }

    public void add(IContainerData datum) {

        if (MAX_DATA == this._data.size()) {
            throw new IllegalStateException("Can't add more sync data entries");
        }

        this._data.add(datum);
    }

    public void broadcastChanges(ServerPlayer player) {

        if (this._canSendUpdates && !this._data.isEmpty()) {

            final var syncSet = this.getUpdates();

            if (syncSet.isEmpty()) {
                return;
            }

            ContainerSyncPacket.send(player, syncSet);
        }
    }

    public void onScreenOpened() {
        ContainerSyncAckPacket.send(this._container.containerId);
    }

    //region Sync packet

    public static class ContainerSyncPacket
            extends AbstractPlayPacket<ContainerSyncPacket> {

        public static final Type<ContainerSyncPacket> TYPE = createType(ZeroCore.ROOT_LOCATION, "container_sync");

        public static final StreamCodec<RegistryFriendlyByteBuf, ContainerSyncPacket> STREAM_CODEC = StreamCodec.composite(
                SyncSet.STREAM_CODEC, packet -> packet._syncSet, ContainerSyncPacket::new);

        static void send(ServerPlayer player, SyncSet syncSet) {
            Lib.NETWORK_HANDLER.sendToPlayer(player, new ContainerSyncPacket(syncSet));
        }

        @Override
        public void handlePacket(IPayloadContext context) {

            final ContainerDataHandler handler = getClientSideContainerDataHandler();

            if (null != handler) {
                handler.syncFrom(this._syncSet);
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
    //region Ack packet

    public static class ContainerSyncAckPacket
            extends AbstractPlayPacket<ContainerSyncAckPacket> {

        public static final Type<ContainerSyncAckPacket> TYPE = createType(ZeroCore.ROOT_LOCATION, "container_sync_ack");

        public static final StreamCodec<ByteBuf, ContainerSyncAckPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, packet -> packet._containerId, ContainerSyncAckPacket::new);

        static void send(int containerId) {
            Lib.NETWORK_HANDLER.sendToServer(new ContainerSyncAckPacket(/*playerId,*/ containerId));
        }

        @Override
        public void handlePacket(IPayloadContext context) {

            if (context.player().containerMenu instanceof ModContainer container) {
                container.getContainerDataHandler().syncAckFrom(this._containerId);
            }
        }

        //region internals

        private ContainerSyncAckPacket(int containerId) {

            super(TYPE);
            this._containerId = containerId;
        }

        private final int _containerId;

        //endregion
    }

    //endregion
    //region internals
    //region SyncSet

    private record SyncSet(int containerId, Short2ObjectMap<ISyncedSetEntry> entries) {

        public static final SyncSet EMPTY = new SyncSet(-1, Short2ObjectMaps.emptyMap());

        public static final StreamCodec<RegistryFriendlyByteBuf, SyncSet> STREAM_CODEC = StreamCodec.ofMember(
                ContainerDataHandler::serializeSyncSet, ContainerDataHandler::deserializeSyncSet);

        public boolean isEmpty() {
            return this == EMPTY || this.entries.isEmpty();
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

            // discard the packet

            buffer.clear();
            return SyncSet.EMPTY;
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

        if (entries.isEmpty()) {
            return SyncSet.EMPTY;
        }

        return new SyncSet(this._container.containerId, entries);
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

            // discard the packet

            buffer.clear();
            return SyncSet.EMPTY;
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

    /**
     * Process an ack received from the logical client side
     *
     * @param containerId the container that sent the ack
     */
    private void syncAckFrom(final int containerId) {

        // called on the logical server side

        if (containerId == this._container.containerId) {
            this._canSendUpdates = true;
        } else {
            Log.LOGGER.warn(Log.NETWORK, "Got a sync ack from another container! Ignoring. ({} / {})",
                    containerId, this._container.containerId);
        }
    }

    /**
     * Process the updates received from the logical server side
     * @param changes the change set
     */
    private void syncFrom(SyncSet changes) {

        if (this._container.containerId != changes.containerId) {
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

    private final ModContainer _container;
    private final List<IContainerData> _data;
    public boolean _canSendUpdates;

    //endregion
}
