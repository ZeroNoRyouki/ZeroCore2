/*
 *
 * NetworkTileEntitySyncProvider.java
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

import com.google.common.collect.Sets;
import it.zerono.mods.zerocore.internal.network.Network;
import it.zerono.mods.zerocore.lib.data.nbt.INestedSyncableEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class NetworkTileEntitySyncProvider implements INetworkTileEntitySyncProvider {

    /**
     * Create a new NetworkTileEntitySyncProvider for the {@link ISyncableEntity} at the given coordinates
     *
     * @param coordinates the coordinates of the entity to sync
     * @param entity the {@link ISyncableEntity} to sync
     * @return the new NetworkTileEntitySyncProvider
     */
    public static NetworkTileEntitySyncProvider create(final BlockPos coordinates, final ISyncableEntity entity) {
        return new NetworkTileEntitySyncProvider(() -> ModSyncableTileMessage.create(coordinates, entity));
    }

    /**
     * Create a new NetworkTileEntitySyncProvider for the {@link ISyncableEntity} nested into the provided {@link INestedSyncableEntity}
     *
     * @param coordinatesSupplier a Supplier for the coordinates of the {@link INestedSyncableEntity} containing the entity to sync
     * @param entity the {@link INestedSyncableEntity} containing the entity to sync
     * @return the new NetworkTileEntitySyncProvider
     */
    public static NetworkTileEntitySyncProvider create(final NonNullSupplier<BlockPos> coordinatesSupplier, final INestedSyncableEntity entity) {
        return new NetworkTileEntitySyncProvider(() -> ModSyncableTileMessage.create(coordinatesSupplier.get(), entity));
    }

    //region INetworkTileEntitySyncProvider

    /**
     * Add the player to the update queue.
     *
     * @param player    the player to send updates to.
     * @param updateNow if true, send an update to the player immediately.
     */
    @Override
    public void enlistForUpdates(ServerPlayer player, boolean updateNow) {

        this._players.add(player);

        if (updateNow) {
            this.getUpdateMessage().ifPresent(update -> this.sendUpdate(update, player));
        }
    }

    /**
     * Remove the player for the update queue.
     *
     * @param player the player to be removed from the update queue.
     */
    @Override
    public void delistFromUpdates(ServerPlayer player) {
        this._players.remove(player);
    }

    /**
     * Send an update to all enlisted players
     */
    @Override
    public void sendUpdates() {
        if (!this._players.isEmpty()) {
            this.getUpdateMessage().ifPresent(update -> this._players.forEach(player -> this.sendUpdate(update, player)));
        }
    }

    //region internals

    private NetworkTileEntitySyncProvider(final Supplier<IModMessage> messageSupplier) {

        this._messageSupplier = messageSupplier;
        this._players = Sets.newHashSet();
    }

    private Optional<IModMessage> getUpdateMessage() {
        return Optional.ofNullable(this._messageSupplier.get());
    }

    private void sendUpdate(final IModMessage update, final ServerPlayer player) {
        Network.HANDLER.sendToPlayer(update, player);
    }

    private final Supplier<IModMessage> _messageSupplier;
    private final Set<ServerPlayer> _players;

    //endregion
}
