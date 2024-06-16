/*
 *
 * StringData.java
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

public class StringData
        extends AbstractData<String>
        implements IContainerData {

    public static StringData immutable(ModContainer container, boolean isClientSide, int maxLength, String value) {

        Preconditions.checkNotNull(value, "Value must not be null.");
        Preconditions.checkArgument(value.length() <= maxLength, "Value is too big.");

        return of(container, isClientSide, maxLength, () -> () -> value);
    }

    public static StringData sampled(int frequency, ModContainer container, boolean isClientSide, int maxLength,
                                     Supplier<@NotNull Supplier<String>> serverSideGetter) {
        return of(container, isClientSide, maxLength, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static StringData of(ModContainer container, boolean isClientSide, int maxLength,
                                Supplier<@NotNull Supplier<String>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final StringData data = isClientSide ? new StringData(maxLength) : new StringData(maxLength, serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    public static StringData of(ModContainer container, boolean isClientSide, int maxLength, String[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        final StringData data = of(container, isClientSide, maxLength, () -> () -> array[index]);

        if (isClientSide) {
            data.bind(v -> array[index] = v);
        }

        return data;
    }

    //region IContainerData

    @Nullable
    @Override
    public Consumer<@NotNull FriendlyByteBuf> getContainerDataWriter() {

        final String current = this._getter.get();

        if (!this._lastValue.equals(current)) {

            this._lastValue = current;
            return buffer -> buffer.writeUtf(current, this._maxLength);
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {
        this.notify(dataSource.readUtf(this._maxLength));
    }

    //endregion
    //region IBindableData<Boolean>

    @Nullable
    @Override
    public String defaultValue() {
        return "";
    }

    //endregion
    //region internals

    private StringData(int maxLength) {
        this._maxLength = maxLength;
    }

    private StringData(int maxLength, Supplier<@NotNull Supplier<String>> serverSideGetter) {

        super(serverSideGetter);
        this._maxLength = maxLength;
        this._lastValue = "";
    }

    private final int _maxLength;
    private String _lastValue;

    //endregion
}
