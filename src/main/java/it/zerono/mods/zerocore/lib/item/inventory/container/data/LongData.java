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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LongData
        extends AbstractData<Long>
        implements IContainerData {

    public static LongData immutable(ModContainer container, boolean isClientSide, long value) {
        return of(container, isClientSide, () -> () -> value);
    }

    public static LongData sampled(int frequency, ModContainer container, boolean isClientSide,
                                   NonNullSupplier<Supplier<Long>> serverSideGetter) {
        return of(container, isClientSide, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static LongData of(ModContainer container, boolean isClientSide,
                              NonNullSupplier<Supplier<Long>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final LongData data = isClientSide ? new LongData() : new LongData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    public static LongData of(ModContainer container, boolean isClientSide, long[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        final LongData data = of(container, isClientSide, () -> () -> array[index]);

        if (isClientSide) {
            data.bind(v -> array[index] = v);
        }

        return data;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {

        final long current = this._getter.get();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeVarLong(current);
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {
        this.notify(dataSource.readVarLong());
    }

    //endregion
    //region IBindableData<Long>

    @Nullable
    @Override
    public Long defaultValue() {
        return 0L;
    }

    //endregion
    //region internals

    private LongData() {
    }

    private LongData(NonNullSupplier<Supplier<Long>> serverSideGetter) {

        super(serverSideGetter);
        this._lastValue = 0;
    }

    private long _lastValue;

    //endregion
}
