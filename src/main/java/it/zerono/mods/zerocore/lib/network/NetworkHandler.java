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

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"WeakerAccess"})
public class NetworkHandler {

    public NetworkHandler(final ResourceLocation channelName, String protocolVersion) {

        this._nextIndex = 0;
        this._protocolVersion = protocolVersion;
        this._channel = NetworkRegistry.ChannelBuilder.named(channelName)
                .clientAcceptedVersions(this._protocolVersion::equals)
                .serverAcceptedVersions(this._protocolVersion::equals)
                .networkProtocolVersion(() -> this._protocolVersion)
                .simpleChannel();
    }

    /**
     * Register a new mod message on this channel
     * <p>
     * Usage: registerMessage(YourModMessage.class, YourModMessage::new);
     *
     * @param messageType    the message class
     * @param messageFactory a factory to create a message from a PacketBuffer containing the message data
     */
    public <T extends IModMessage> void registerMessage(final Class<T> messageType,
                                                        final Function<PacketBuffer, T> messageFactory) {

        this._channel.registerMessage(this._nextIndex++, messageType, T::encodeTo,
                messageFactory, NetworkHandler::handleMessage);
    }

    /**
     * Send a message to the server
     *
     * @param message the message to send
     */
    public <T extends IModMessage> void sendToServer(final T message) {
        this._channel.sendToServer(message);
    }

    /**
     * Send a message to a player (his client)
     *
     * @param message the message to send
     * @param player  the message recipient
     */
    public <T extends IModMessage> void sendToPlayer(final T message, final ServerPlayerEntity player) {

        if (!(player instanceof FakePlayer)) {
            this._channel.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    /**
     * Send a message in reply to another message
     *
     * @param message         the message to send
     * @param originalContext the message context of the original message
     */
    public <T extends IModMessage> void sendReply(final T message, final NetworkEvent.Context originalContext) {
        this._channel.reply(message, originalContext);
    }

    /**
     * Send a message to the specified target
     *
     * @param message the message to send
     * @param target  the message target
     */
    public <T extends IModMessage> void sendTo(final T message, final PacketDistributor.PacketTarget target) {
        this._channel.send(target, message);
    }

    /**
     * Send a message to all players around the given target point
     *
     * @param message        the message to send
     * @param targetSupplier the target point
     */
    public <T extends IModMessage> void sendToAllAround(final T message,
                                                        final Supplier<PacketDistributor.TargetPoint> targetSupplier) {
        this.sendTo(message, PacketDistributor.NEAR.with(targetSupplier));
    }

    /**
     * Send a message to all players around the given target point
     *
     * @param message   the message to send
     * @param x         the x coordinate of the center of the area
     * @param y         the y coordinate of the center of the area
     * @param z         the z coordinate of the center of the area
     * @param radius    the radius of the area
     * @param dimension the target dimension
     */
    public <T extends IModMessage> void sendToAllAround(final T message,
                                                        final double x, final double y, final double z,
                                                        final double radius, final RegistryKey<World> dimension) {
        this.sendToAllAround(message, PacketDistributor.TargetPoint.p(x, y, z, radius, dimension));
    }

    /**
     * Send a message to all players around the given target point
     *
     * @param message   the message to send
     * @param center    the center of the area
     * @param radius    the radius of the area
     * @param dimension the target dimension
     */
    public <T extends IModMessage> void sendToAllAround(final T message, final BlockPos center,
                                                        final double radius, final RegistryKey<World> dimension) {
        this.sendToAllAround(message, PacketDistributor.TargetPoint.p(center.getX(), center.getY(), center.getZ(),
                radius, dimension));
    }

    /**
     * Send a message to all players in the specified dimension
     *
     * @param message   the message to send
     * @param dimension the target dimension
     */
    public <T extends IModMessage> void sendToDimension(final T message, final RegistryKey<World> dimension) {
        this.sendTo(message, PacketDistributor.DIMENSION.with(() -> dimension));
    }

    /**
     * Send a message to all players in the specified dimension
     *
     * @param message the message to send
     */
    public <T extends IModMessage> void sendToAllPlayers(final T message) {
        this.sendTo(message, PacketDistributor.ALL.noArg());
    }

    //region internals

    private static <T extends IModMessage> void handleMessage(final T message,
                                                              final Supplier<NetworkEvent.Context> contextSupplier) {

        final NetworkEvent.Context messageContext = contextSupplier.get();

        messageContext.enqueueWork(() -> message.processMessage(messageContext));
        messageContext.setPacketHandled(true);
    }

    private final String _protocolVersion;
    private final SimpleChannel _channel;
    private int _nextIndex;
}
