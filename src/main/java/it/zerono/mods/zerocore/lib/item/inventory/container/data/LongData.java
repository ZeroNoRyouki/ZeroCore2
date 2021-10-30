/*
 *
 * LongData.java
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
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

public class LongData
        implements IContainerData {

    public LongData(final LongSupplier getter, final LongConsumer setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = 0;
    }

    public static LongData wrap(final long[] array, final int index) {
        return new LongData(() -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final long current = this._getter.getAsLong();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeVarLong(this._getter.getAsLong());
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {
        this._setter.accept(dataSource.readVarLong());
    }

    //endregion
    //region internals

    private final LongSupplier _getter;
    private final LongConsumer _setter;
    private long _lastValue;

    //endregion
}
