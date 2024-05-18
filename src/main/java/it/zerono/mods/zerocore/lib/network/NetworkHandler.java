/*
 *
 * NetworkHandler.java
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

import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class NetworkHandler {

    /**
     * Send packets to the server
     *
     * @param packet the first packet to send
     * @param otherPackets other packets to send
     */
    public void sendToServer(CustomPacketPayload packet, CustomPacketPayload... otherPackets) {
        PacketDistributor.sendToServer(packet, otherPackets);
    }

    /**
     * Send packets to a client-player
     *
     * @param player the packets recipient
     * @param packet the first packet to send
     * @param otherPackets other packets to send
     */
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload packet, CustomPacketPayload... otherPackets) {
        PacketDistributor.sendToPlayer(player, packet, otherPackets);
    }

    /**
     * Send packets to all players around the given target point
     *
     * @param x the x coordinate of the center of the area
     * @param y the y coordinate of the center of the area
     * @param z the z coordinate of the center of the area
     * @param radius the radius of the area
     * @param level the target level
     * @param excluded don't send the packets to this player (if not null)
     * @param packet the first packet to send
     * @param otherPackets other packets to send
     */
    public void sendToAllAround(double x, double y, double z, double radius, ServerLevel level,
                                @Nullable ServerPlayer excluded, CustomPacketPayload packet,
                                CustomPacketPayload... otherPackets) {
        PacketDistributor.sendToPlayersNear(level, excluded, x, y, z, radius, packet, otherPackets);
    }

    /**
     * Send packets to all players around the given target point
     *
     * @param center the center of the area
     * @param radius the radius of the area
     * @param level the target level
     * @param excluded don't send the packets to this player (if not null)
     * @param packet the first packet to send
     * @param otherPackets other packets to send
     */
    public void sendToAllAround(Vec3i center, double radius, ServerLevel level,
                                @Nullable ServerPlayer excluded, CustomPacketPayload packet,
                                CustomPacketPayload... otherPackets) {
        this.sendToAllAround(center.getX(), center.getY(), center.getZ(), radius, level, excluded, packet, otherPackets);
    }

    /**
     * Send packets to all players in the specified dimension
     *
     * @param level the target level
     * @param packet the first packet to send
     * @param otherPackets other packets to send
     */
    public void sendToDimension(ServerLevel level, CustomPacketPayload packet, CustomPacketPayload... otherPackets) {
        PacketDistributor.sendToPlayersInDimension(level, packet, otherPackets);
    }

    /**
     * Send packets to all players
     *w
     * @param packet the first packet to send
     * @param otherPackets other packets to send
     */
    public void sendToAllPlayers(CustomPacketPayload packet, CustomPacketPayload... otherPackets) {
        PacketDistributor.sendToAllPlayers(packet, otherPackets);
    }
}
