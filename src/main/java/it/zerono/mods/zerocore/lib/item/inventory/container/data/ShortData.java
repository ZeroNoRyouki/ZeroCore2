/*
 *
 * ShortData.java
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

import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.zerono.mods.zerocore.lib.functional.ShortSupplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;

public class ShortData
        implements IContainerData {

    public ShortData(final ShortSupplier getter, final ShortConsumer setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = 0;
    }

    public static ShortData wrap(final short[] array, final int index) {
        return new ShortData(() -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final short current = this._getter.getAsShort();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeVarInt(this._getter.getAsShort());
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {
        this._setter.accept(dataSource.readShort());
    }

    //endregion
    //region internals

    private final ShortSupplier _getter;
    private final ShortConsumer _setter;
    private short _lastValue;

    //endregion
}
