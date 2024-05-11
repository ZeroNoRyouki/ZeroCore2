/*
 *
 * AbstractPlayPacket.java
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

import com.google.common.base.Preconditions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * A generic custom packet for the play phase
 */
public abstract class AbstractPlayPacket
        implements CustomPacketPayload {

    /**
     * Construct the local packet to be sent over the network
     *
     * @param packetId The ID of this packet
     */
    protected AbstractPlayPacket(ResourceLocation packetId) {

        Preconditions.checkNotNull(packetId, "Message ID must not be null");
        this._id = packetId;
    }

    /**
     * Construct the packet from the data received from the network.
     * Read your payload from the {@link FriendlyByteBuf} and store it locally for later processing.
     *
     * @param packetId The ID of this packet
     * @param buffer The {@link FriendlyByteBuf} containing the data received from the network
     */
    protected AbstractPlayPacket(ResourceLocation packetId, FriendlyByteBuf buffer) {
        this(packetId);
    }

    public abstract void handlePacket(PlayPayloadContext context);

    //region CustomPacketPayload

    @Override
    public ResourceLocation id() {
        return this._id;
    }

    //endregion
    //region internals

    private final ResourceLocation _id;

    //endregion
}
