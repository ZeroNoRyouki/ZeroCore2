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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FloatData
        extends AbstractData<Float>
        implements IContainerData {

    public static FloatData immutable(ModContainer container, boolean isClientSide, float value) {
        return of(container, isClientSide, () -> () -> value);
    }

    public static FloatData sampled(int frequency, ModContainer container, boolean isClientSide,
                                    NonNullSupplier<Supplier<Float>> serverSideGetter) {
        return of(container, isClientSide, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static FloatData of(ModContainer container, boolean isClientSide,
                               NonNullSupplier<Supplier<Float>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final FloatData data = isClientSide ? new FloatData() : new FloatData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    public static FloatData of(ModContainer container, boolean isClientSide, float[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        final FloatData data = of(container, isClientSide, () -> () -> array[index]);

        if (isClientSide) {
            data.bind(v -> array[index] = v);
        }

        return data;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final float current = this._getter.get();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeFloat(current);
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {
        this.notify(dataSource.readFloat());
    }

    //endregion
    //region IBindableData<ByteConsumer>

    @Nullable
    @Override
    public Float defaultValue() {
        return 0.0f;
    }

    //endregion
    //region internals

    private FloatData() {
    }

    private FloatData(NonNullSupplier<Supplier<Float>> serverSideGetter) {

        super(serverSideGetter);
        this._lastValue = 0;
    }

    private float _lastValue;

    //endregion
}
