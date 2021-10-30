/*
 *
 * ByteData.java
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

import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.zerono.mods.zerocore.lib.functional.ByteSupplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;

public class ByteData
        implements IContainerData {

    public ByteData(final ByteSupplier getter, final ByteConsumer setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = 0;
    }

    public static ByteData wrap(final byte[] array, final int index) {
        return new ByteData(() -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final byte current = this._getter.getAsByte();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeByte(this._getter.getAsByte());
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {
        this._setter.accept(dataSource.readByte());
    }

    //endregion
    //region internals

    private final ByteSupplier _getter;
    private final ByteConsumer _setter;
    private byte _lastValue;

    //endregion
}
