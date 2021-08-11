/*
 *
 * AbstractModMessage.java
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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

/**
 * A generic network message
 */
@SuppressWarnings({"WeakerAccess"})
public abstract class AbstractModMessage implements IModMessage {

    /**
     * Construct the message from the data received from the network.
     * Read your payload from the {@link FriendlyByteBuf} and store it locally for later processing.
     *
     * @param buffer the {@link FriendlyByteBuf} containing the data received from the network.
     */
    public AbstractModMessage(FriendlyByteBuf buffer) {
    }

    /**
     * Encode your data into the {@link FriendlyByteBuf} so it could be sent on the network to the other side.
     *
     * @param buffer the {@link FriendlyByteBuf} to encode your data into
     */
    public abstract void encodeTo(FriendlyByteBuf buffer);

    /**
     * Process the data received from the network.
     *
     * @param messageContext context for {@link NetworkEvent}
     */
    public abstract void processMessage(NetworkEvent.Context messageContext);

    /**
     * Construct the local message to be sent over the network.
     */
    protected AbstractModMessage() {
    }
}
