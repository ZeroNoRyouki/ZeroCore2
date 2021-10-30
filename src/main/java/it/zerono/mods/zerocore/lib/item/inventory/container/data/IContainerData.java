/*
 *
 * IContainerData.java
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

package it.zerono.mods.zerocore.lib.item.inventory.container.data;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;

public interface IContainerData {

    /**
     * Return a {@link PacketBuffer} consumer that will be used to write this {@code IContainerData}'s data to a packet.
     * The consumer could either serialize the whole data to a packet or only the changes occurred since the last call to this method.
     *
     * Return {@code null} if no data need to be serialized to the packet (maybe because no changes occurred since the last invocation of this method).
     *
     * @return the consumer, or {@code null}
     */
    @Nullable
    NonNullConsumer<PacketBuffer> getContainerDataWriter();

    /**
     * Read back the data that was serialized to a packet by a consumer provided by {@code getContainerDataWriter}
     * @param dataSource the buffer containing the data
     */
    void readContainerData(PacketBuffer dataSource);
}
