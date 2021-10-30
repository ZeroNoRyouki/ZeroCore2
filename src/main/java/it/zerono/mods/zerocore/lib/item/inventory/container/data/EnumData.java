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

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumData<T extends Enum<T>>
        implements IContainerData {

    public EnumData(final Class<T> enumClass, final Supplier<T> getter, final Consumer<T> setter) {

        this._enumClass = enumClass;
        this._getter = getter;
        this._setter = setter;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final T current = this._getter.get();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> {

                final T value = this._getter.get();

                buffer.writeVarInt(null != value ? value.ordinal() : -1);
            };
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {

        final int ordinal = dataSource.readVarInt();

        this._setter.accept(-1 == ordinal ? null : this._enumClass.getEnumConstants()[ordinal]);
    }

    //endregion
    //region internals

    private final Class<T> _enumClass;
    private final Supplier<T> _getter;
    private final Consumer<T> _setter;
    private T _lastValue;

    //endregion
}
