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
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BlockPosData
        extends AbstractData<BlockPos>
        implements IContainerData {

    public static BlockPosData immutable(ModContainer container, boolean isClientSide, BlockPos value) {
        return of(container, isClientSide, () -> () -> value);
    }

    public static BlockPosData sampled(int frequency, ModContainer container, boolean isClientSide,
                                       NonNullSupplier<Supplier<BlockPos>> serverSideGetter) {
        return of(container, isClientSide, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static BlockPosData of(ModContainer container, boolean isClientSide,
                                  NonNullSupplier<Supplier<BlockPos>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final BlockPosData data = isClientSide ? new BlockPosData() : new BlockPosData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<FriendlyByteBuf> getContainerDataWriter() {

        final BlockPos current = this._getter.get();
        final int currentHash = current.hashCode();

        if (this._lastHash != currentHash) {

            this._lastHash = currentHash;
            return buffer -> buffer.writeBlockPos(current);
        }

        return null;
    }

    @Override
    public void readContainerData(final FriendlyByteBuf dataSource) {
        this.notify(dataSource.readBlockPos());
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

    private BlockPosData() {
    }

    private BlockPosData(NonNullSupplier<Supplier<BlockPos>> serverSideGetter) {

        super(serverSideGetter);
        this._lastHash = 0;
    }

    private int _lastHash;

    //endregion
}
