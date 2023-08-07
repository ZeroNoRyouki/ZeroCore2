/*
 *
 * EnumData.java
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

public class EnumData<T extends Enum<T>>
        extends AbstractData<T>
        implements IContainerData {

    public static <T extends Enum<T>> EnumData<T> immutable(ModContainer container, boolean isClientSide,
                                                            Class<T> enumClass, T value) {
        return of(container, isClientSide, enumClass, () -> () -> value);
    }

    public static <T extends Enum<T>> EnumData<T> sampled(int frequency, ModContainer container, boolean isClientSide,
                                                          Class<T> enumClass, NonNullSupplier<Supplier<T>> serverSideGetter) {
        return of(container, isClientSide, enumClass, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static <T extends Enum<T>> EnumData<T> of(ModContainer container, boolean isClientSide,
                                                     Class<T> enumClass, NonNullSupplier<Supplier<T>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final EnumData<T> data = isClientSide ? new EnumData<>(enumClass) : new EnumData<>(enumClass, serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final T current = this._getter.get();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeVarInt(null != current ? current.ordinal() : -1);
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {

        final int ordinal = dataSource.readVarInt();
        final T data = -1 == ordinal ? null : this._enumClass.getEnumConstants()[ordinal];

        this.notify(data);
    }

    //endregion
    //region IBindableData<T>

    @Nullable
    @Override
    public T defaultValue() {
        return this._enumClass.getEnumConstants()[0];
    }

    //endregion
    //region internals

    private EnumData(Class<T> enumClass) {
        this._enumClass = enumClass;
    }

    private EnumData(Class<T> enumClass, NonNullSupplier<Supplier<T>> serverSideGetter) {

        super(serverSideGetter);
        this._enumClass = enumClass;
        this._lastValue = null;
    }

    private final Class<T> _enumClass;
    private T _lastValue;

    //endregion
}
