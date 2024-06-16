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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ShortData
        extends AbstractData<Short>
        implements IContainerData {

    public static ShortData immutable(ModContainer container, boolean isClientSide, short value) {
        return of(container, isClientSide, () -> () -> value);
    }

    public static ShortData sampled(int frequency, ModContainer container, boolean isClientSide,
                                    NonNullSupplier<Supplier<Short>> serverSideGetter) {
        return of(container, isClientSide, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static ShortData of(ModContainer container, boolean isClientSide,
                               NonNullSupplier<Supplier<Short>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final ShortData data = isClientSide ? new ShortData() : new ShortData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    public static ShortData of(ModContainer container, boolean isClientSide, short[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        final ShortData data = of(container, isClientSide, () -> () -> array[index]);

        if (isClientSide) {
            data.bind(v -> array[index] = v);
        }

        return data;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {

        final short current = this._getter.get();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeVarInt(current);
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {
        this.notify(dataSource.readShort());
    }

    //endregion
    //region IBindableData<Short>

    @Nullable
    @Override
    public Short defaultValue() {
        return 0;
    }

    //endregion
    //region internals

    private ShortData() {
    }

    private ShortData(NonNullSupplier<Supplier<Short>> serverSideGetter) {

        super(serverSideGetter);
        this._lastValue = 0;
    }

    private short _lastValue;

    //endregion
}
