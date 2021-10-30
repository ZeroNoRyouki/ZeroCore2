/*
 *
 * FloadData.java
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

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.zerono.mods.zerocore.lib.functional.FloatSupplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;

public class FloadData
        implements IContainerData {

    public FloadData(final FloatSupplier getter, final FloatConsumer setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = 0.0f;
    }

    public static FloadData wrap(final float[] array, final int index) {
        return new FloadData(() -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final float current = this._getter.getAsFloat();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeFloat(this._getter.getAsFloat());
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {
        this._setter.accept(dataSource.readFloat());
    }

    //endregion
    //region internals

    private final FloatSupplier _getter;
    private final FloatConsumer _setter;
    private float _lastValue;

    //endregion
}
