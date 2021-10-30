/*
 *
 * DoubleData.java
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
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class DoubleData
        implements IContainerData {

    public DoubleData(final DoubleSupplier getter, final DoubleConsumer setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = 0.0;
    }

    public static DoubleData wrap(final double[] array, final int index) {
        return new DoubleData(() -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final double current = this._getter.getAsDouble();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeDouble(this._getter.getAsDouble());
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {
        this._setter.accept(dataSource.readDouble());
    }

    //endregion
    //region internals

    private final DoubleSupplier _getter;
    private final DoubleConsumer _setter;
    private double _lastValue;

    //endregion
}
