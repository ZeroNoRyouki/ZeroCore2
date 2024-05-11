/*
 *
 * IntData.java
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

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class IntData
        implements IContainerData {

    public IntData(final IntSupplier getter, final IntConsumer setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = 0;
    }

    public static IntData wrap(final int[] array, final int index) {
        return new IntData(() -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {

        final int current = this._getter.getAsInt();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeVarInt(this._getter.getAsInt());
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {
        this._setter.accept(dataSource.readVarInt());
    }

    //endregion
    //region internals

    private final IntSupplier _getter;
    private final IntConsumer _setter;
    private int _lastValue;

    //endregion
}
