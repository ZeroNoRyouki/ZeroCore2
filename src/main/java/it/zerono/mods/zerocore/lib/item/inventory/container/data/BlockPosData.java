/*
 *
 * BlockPosData.java
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

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;

public class BlockPosData
        implements IContainerData {

    public BlockPosData(final NonNullSupplier<BlockPos> getter, final NonNullConsumer<BlockPos> setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastHash = 0;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {

        final BlockPos current = this._getter.get();
        final int currentHash = current.hashCode();

        if (this._lastHash != currentHash) {

            this._lastHash = currentHash;
            return buffer -> buffer.writeBlockPos(this._getter.get());
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {
        this._setter.accept(dataSource.readBlockPos());
    }

    //endregion
    //region internals

    private final NonNullSupplier<BlockPos> _getter;
    private final NonNullConsumer<BlockPos> _setter;
    private int _lastHash;

    //endregion
}
