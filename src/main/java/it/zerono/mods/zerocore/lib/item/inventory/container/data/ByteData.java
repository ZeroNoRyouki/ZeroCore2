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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ByteData
        extends AbstractData<Byte>
        implements IContainerData {

    public static ByteData immutable(ModContainer container, boolean isClientSide, byte value) {
        return of(container, isClientSide, () -> () -> value);
    }

    public static ByteData sampled(int frequency, ModContainer container, boolean isClientSide,
                                   Supplier<@NotNull Supplier<Byte>> serverSideGetter) {
        return of(container, isClientSide, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static ByteData of(ModContainer container, boolean isClientSide,
                              Supplier<@NotNull Supplier<Byte>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final ByteData data = isClientSide ? new ByteData() : new ByteData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    public static ByteData of(ModContainer container, boolean isClientSide, byte[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        final ByteData data = of(container, isClientSide, () -> () -> array[index]);

        if (isClientSide) {
            data.bind(v -> array[index] = v);
        }

        return data;
    }

    //region
    // IContainerData

    @Nullable
    @Override
    public Consumer<@NotNull FriendlyByteBuf> getContainerDataWriter() {

        final byte current = this._getter.get();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeByte(current);
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {
        this.notify(dataSource.readByte());
    }

    //endregion
    //region IBindableData<Byte>

    @Nullable
    @Override
    public Byte defaultValue() {
        return 0;
    }

    //endregion
    //region internals

    private ByteData() {
    }

    private ByteData(Supplier<@NotNull Supplier<Byte>> serverSideGetter) {

        super(serverSideGetter);
        this._lastValue = 0;
    }

    private byte _lastValue;

    //endregion
}
