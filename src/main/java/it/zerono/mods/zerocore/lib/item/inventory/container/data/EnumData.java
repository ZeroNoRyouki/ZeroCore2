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
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumData<T extends Enum<T>>
        extends AbstractData<T>
        implements IContainerData {

    public static <T extends Enum<T>> EnumData<T> immutable(ModContainer container, Class<T> enumClass, T value) {
        return of(container, enumClass, () -> value, CodeHelper.emptyConsumer());
    }

    public static <T extends Enum<T>> EnumData<T> sampled(int frequency, ModContainer container, Class<T> enumClass,
                                                          Supplier<@NotNull T> getter,
                                                          Consumer<@NotNull T> clientSideSetter) {
        return of(container, enumClass, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static <T extends Enum<T>> EnumData<T> sampled(int frequency, ModContainer container, Class<T> enumClass,
                                                          Supplier<@NotNull T> getter) {
        return of(container, enumClass, new Sampler<>(frequency, getter), CodeHelper.emptyConsumer());
    }

    public static <T extends Enum<T>> EnumData<T> of(ModContainer container, Class<T> enumClass,
                                                     Supplier<@NotNull T> getter,
                                                     Consumer<@NotNull T> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new EnumData<>(enumClass, getter, clientSideSetter) : new EnumData<>(enumClass, getter);

        container.addBindableData(data);
        return data;
    }

    public static <T extends Enum<T>> EnumData<T> of(ModContainer container, Class<T> enumClass, Supplier<@NotNull T> getter) {
        return of(container, enumClass, getter, CodeHelper.emptyConsumer());
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final T current = this.getValue();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeEnum(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return new EnumEntry<>(buffer.readEnum(this._enumClass));
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof EnumEntry) {

            //noinspection unchecked
            final var enumEntry = (EnumEntry<T>) entry;

            this.setClientSideValue(enumEntry.value);
            this.notify(enumEntry.value);
        }
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
    //region ISyncedSetEntry

    private record EnumEntry<T extends Enum<T>>(T value)
            implements ISyncedSetEntry {

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeEnum(this.value);
        }
    }

    //endregion

    private EnumData(Class<T> enumClass, Supplier<T> getter, Consumer<T> clientSideSetter) {

        super(getter, clientSideSetter);
        this._enumClass = enumClass;
    }

    private EnumData(Class<T> enumClass, Supplier<T> getter) {

        super(getter);
        this._enumClass = enumClass;
        this._lastValue = null;
    }

    private final Class<T> _enumClass;
    private T _lastValue;

    //endregion
}
