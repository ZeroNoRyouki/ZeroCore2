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

import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockPosData
        implements IContainerData {

    public BlockPosData(Supplier<@NotNull BlockPos> getter, Consumer<@NotNull BlockPos> setter) {

        this._getter = getter;
        this._setter = setter;
        this._lastHash = 0;
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final BlockPos current = this._getter.get();
        final int currentHash = current.hashCode();

        if (this._lastHash != currentHash) {

            this._lastHash = currentHash;
            return new BlockPosEntry(current);
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return BlockPosEntry.from(buffer);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof BlockPosEntry record) {
            this._setter.accept(record.value);
        }
    }

    //endregion
    //region internals
    //region ISyncedSetEntry

    private record BlockPosEntry(BlockPos value)
            implements ISyncedSetEntry {

        private static BlockPosEntry from(RegistryFriendlyByteBuf buffer) {
            return new BlockPosEntry(buffer.readBlockPos());
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {
            buffer.writeBlockPos(this.value);
        }
    }

    //endregion

    private final Supplier<@NotNull BlockPos> _getter;
    private final Consumer<@NotNull BlockPos> _setter;
    private int _lastHash;

    //endregion
}
