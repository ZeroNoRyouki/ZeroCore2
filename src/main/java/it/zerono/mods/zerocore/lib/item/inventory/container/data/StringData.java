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
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StringData
        extends AbstractData<String>
        implements IContainerData {

    public static StringData immutable(ModContainer container, int maxLength, String value) {

        Preconditions.checkNotNull(value, "Value must not be null.");
        Preconditions.checkArgument(value.length() <= maxLength, "Value is too big.");

        return of(container, maxLength, () -> value, CodeHelper.emptyConsumer());
    }

    public static StringData sampled(int frequency, ModContainer container, int maxLength,
                                     Supplier<@NotNull String> getter, Consumer<@NotNull String> clientSideSetter) {
        return of(container, maxLength, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static StringData of(ModContainer container, int maxLength, Supplier<@NotNull String> getter,
                                Consumer<@NotNull String> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new StringData(maxLength, getter, clientSideSetter) :
                new StringData(maxLength, getter);

        container.addBindableData(data);
        return data;
    }

    public static StringData of(ModContainer container, int maxLength, String[] array, int index) {

        Preconditions.checkNotNull(array, "Array must not be null.");
        Preconditions.checkArgument(index >= 0 && index < array.length, "Index must be a valid index for the array.");

        return of(container, maxLength, () -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final String current = this.getValue();

        if (!this._lastValue.equals(current)) {

            this._lastValue = current;
            return new StringEntry(this._maxLengthCodec, this._lastValue);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return StringEntry.from(this._maxLengthCodec, buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof StringEntry record) {

            this.setClientSideValue(record.value);
            this.notify(record.value);
        }
    }

    //endregion
    //region IBindableData<Boolean>

    @Nullable
    @Override
    public String defaultValue() {
        return "";
    }

    //endregion
    //region ISyncedSetEntry

    private record StringEntry(StreamCodec<ByteBuf, String> codec, String value)
            implements ISyncedSetEntry {

        private static StringEntry from(StreamCodec<ByteBuf, String> codec, RegistryFriendlyByteBuf buffer) {
            return new StringEntry(codec, codec.decode(buffer));
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            this.codec.encode(buffer, this.value);
        }
    }

    //endregion
    //region internals

    private StringData(int maxLength, Supplier<@NotNull String> getter, Consumer<@NotNull String> clientSideSetter) {

        super(getter, clientSideSetter);
        this._maxLengthCodec = ByteBufCodecs.stringUtf8(maxLength);
    }

    private StringData(int maxLength, Supplier<@NotNull String> getter) {

        super(getter);
        this._maxLengthCodec = ByteBufCodecs.stringUtf8(maxLength);
        this._lastValue = "";
    }

    private final StreamCodec<ByteBuf, String> _maxLengthCodec;
    private String _lastValue;

    //endregion
}
