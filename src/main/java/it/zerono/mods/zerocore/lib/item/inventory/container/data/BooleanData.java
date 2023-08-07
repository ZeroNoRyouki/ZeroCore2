/*
 *
 * BooleanData.java
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

public class BooleanData
        extends AbstractData<Boolean>
        implements IContainerData {

    public static BooleanData immutable(ModContainer container, boolean isClientSide, boolean value) {
        return of(container, isClientSide, () -> () -> value);
    }

    public static BooleanData sampled(int frequency, ModContainer container, boolean isClientSide,
                                      NonNullSupplier<Supplier<Boolean>> serverSideGetter) {
        return of(container, isClientSide, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static BooleanData of(ModContainer container, boolean isClientSide,
                                 NonNullSupplier<Supplier<Boolean>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final BooleanData data = isClientSide ? new BooleanData() : new BooleanData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    public static BooleanData of(ModContainer container, boolean isClientSide,
                                 boolean[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        final BooleanData data = of(container, isClientSide, () -> () -> array[index]);

        if (isClientSide) {
            data.bind(v -> array[index] = v);
        }

        return data;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final boolean current = this._getter.get();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeBoolean(current);
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {
        this.notify(dataSource.readBoolean());
    }

    //endregion
    //region IBindableData<Boolean>

    @Nullable
    @Override
    public Boolean defaultValue() {
        return Boolean.FALSE;
    }

    //endregion
    //region internals

    private BooleanData() {
    }

    private BooleanData(NonNullSupplier<Supplier<Boolean>> serverSideGetter) {

        super(serverSideGetter);
        this._lastValue = false;
    }

    private boolean _lastValue;

    //endregion
}
