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

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class BooleanData
        implements IContainerData {

    public BooleanData(final BooleanSupplier getter, final BooleanConsumer setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = false;
    }

    public static BooleanData wrap(final boolean[] array, final int index) {
        return new BooleanData(() -> array[index], v -> array[index] = v);
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {

        final boolean current = this._getter.getAsBoolean();

        if (this._lastValue != current) {

            this._lastValue = current;
            return buffer -> buffer.writeBoolean(this._getter.getAsBoolean());
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {
        this._setter.accept(dataSource.readBoolean());
    }

    //endregion
    //region internals

    private final BooleanSupplier _getter;
    private final BooleanConsumer _setter;
    private boolean _lastValue;

    //endregion
}
