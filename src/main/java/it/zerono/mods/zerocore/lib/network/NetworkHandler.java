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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class NetworkHandler {

    /**
     * Send packets to the server
     *
     * @param packets the packets to send
     */
    public void sendToServer(CustomPacketPayload... packets) {
        PacketDistributor.SERVER.noArg().send(packets);
    }

    /**
     * Send packets to a client-player
     *
     * @param player  the packets recipient
     * @param packets the packets to send
     */
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload... packets) {

        if (!(player instanceof FakePlayer)) {
            PacketDistributor.PLAYER.with(player).send(packets);
        }
    }

    /**
     * Send packets to all players around the given target point
     *
     * @param x         the x coordinate of the center of the area
     * @param y         the y coordinate of the center of the area
     * @param z         the z coordinate of the center of the area
     * @param radius    the radius of the area
     * @param dimension the target dimension
     * @param packets   the packets to send
     */
    public void sendToAllAround(double x, double y, double z, double radius, ResourceKey<Level> dimension,
                                CustomPacketPayload... packets) {
        PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(x, y, z, radius, dimension)).send(packets);
    }

    /**
     * Send packets to all players around the given target point
     *
     * @param center    the center of the area
     * @param radius    the radius of the area
     * @param dimension the target dimension
     * @param packets   the packets to send
     */
    public void sendToAllAround(Vec3i center, double radius, ResourceKey<Level> dimension, CustomPacketPayload... packets) {
        this.sendToAllAround(center.getX(), center.getY(), center.getZ(), radius, dimension, packets);
    }

    /**
     * Send packets to all players in the specified dimension
     *
     * @param dimension the target dimension
     * @param packets   the packets to send
     */
    public void sendToDimension(ResourceKey<Level> dimension, CustomPacketPayload... packets) {
        PacketDistributor.DIMENSION.with(dimension).send(packets);
    }

    /**
     * Send packets to all players
     *w
     * @param packets the packets to send
     */
    public void sendToAllPlayers(CustomPacketPayload... packets) {
        PacketDistributor.ALL.noArg().send(packets);
    }
}
