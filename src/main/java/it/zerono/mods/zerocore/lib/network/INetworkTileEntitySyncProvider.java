/*
 *
 * INetworkTileEntitySyncProvider.java
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

import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Allow a {@link ISyncableEntity} to be synchronized to multiple remote clients (players).
 */
public interface INetworkTileEntitySyncProvider {

    /**
     * Add the player to the update queue.
     *
     * @param player the player to send updates to.
     * @param updateNow if true, send an update to the player immediately.
     */
    void enlistForUpdates(ServerPlayerEntity player, boolean updateNow);

    /**
     * Remove the player for the update queue.
     *
     * @param player the player to be removed from the update queue.
     */
    void delistFromUpdates(ServerPlayerEntity player);

    /**
     * Send an update to all enlisted players
     */
    void sendUpdates();
}
