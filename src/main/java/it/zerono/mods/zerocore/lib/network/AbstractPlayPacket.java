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
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * A generic custom packet for the play phase
 */
public abstract class AbstractPlayPacket<Packet extends AbstractPlayPacket<Packet>>
        implements CustomPacketPayload {

    public static <T extends AbstractPlayPacket<T>> Type<T> createType(ResourceLocationBuilder builder, String name) {
        return new Type<>(builder.buildWithSuffix(name));
    }

    /**
     * Initializes a newly created {@code AbstractPlayPacket} object
     *
     * @param type The {@link Type} of this packet
     */
    protected AbstractPlayPacket(Type<Packet> type) {

        Preconditions.checkNotNull(type, "Type must not be null");
        this._type = type;
    }

    public abstract void handlePacket(IPayloadContext context);

    //region CustomPacketPayload

    @Override
    public Type<Packet> type() {
        return _type;
    }

    //endregion
    //region internals

    private final Type<Packet> _type;

    //endregion
}
