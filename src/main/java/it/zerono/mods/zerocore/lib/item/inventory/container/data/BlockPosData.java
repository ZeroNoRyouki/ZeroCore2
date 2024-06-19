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

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockPosData
        extends AbstractData<BlockPos>
        implements IContainerData {

    public static BlockPosData immutable(ModContainer container, BlockPos value) {
        return of(container, () -> value, CodeHelper.emptyConsumer());
    }

    public static BlockPosData sampled(int frequency, ModContainer container, Supplier<@NotNull BlockPos> getter,
                                       Consumer<@NotNull BlockPos> clientSideSetter) {
        return of(container, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static BlockPosData sampled(int frequency, ModContainer container, Supplier<@NotNull BlockPos> getter) {
        return of(container, new Sampler<>(frequency, getter), CodeHelper.emptyConsumer());
    }

    public static BlockPosData of(ModContainer container, Supplier<@NotNull BlockPos> getter,
                                  Consumer<@NotNull BlockPos> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final var data = container.isClientSide() ? new BlockPosData(getter, clientSideSetter) : new BlockPosData(getter);

        container.addBindableData(data);
        return data;
    }

    public static BlockPosData of(ModContainer container, Supplier<@NotNull BlockPos> getter) {
        return of(container, getter, CodeHelper.emptyConsumer());
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final BlockPos current = this.getValue();
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

            this.setClientSideValue(record.value);
            this.notify(record.value);
        }
    }

    //endregion
    //region IBindableData<BlockPos>

    @Nullable
    @Override
    public BlockPos defaultValue() {
        return BlockPos.ZERO;
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

    private BlockPosData(Supplier<BlockPos> getter, Consumer<BlockPos> clientSideSetter) {
        super(getter, clientSideSetter);
    }

    private BlockPosData(Supplier<BlockPos> getter) {

        super(getter);
        this._lastHash = 0;
    }

    private int _lastHash;

    //endregion
}
