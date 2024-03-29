/*
 *
 * WideAmountData.java
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

import it.zerono.mods.zerocore.lib.data.WideAmount;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;

public class WideAmountData
        implements IContainerData {

    public WideAmountData(final NonNullSupplier<WideAmount> getter, final NonNullConsumer<WideAmount> setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastValue = WideAmount.ZERO;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final WideAmount current = this._getter.get();

        if (this._lastValue.equals(current)) {

            return null;

        } else {

            this._lastValue = current.copy();
            return buffer -> this._lastValue.serializeTo(buffer);
        }
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {
        this._setter.accept(WideAmount.from(dataSource));
    }

    //endregion
    //region internals

    private final NonNullSupplier<WideAmount> _getter;
    private final NonNullConsumer<WideAmount> _setter;
    private WideAmount _lastValue;

    //endregion
}

