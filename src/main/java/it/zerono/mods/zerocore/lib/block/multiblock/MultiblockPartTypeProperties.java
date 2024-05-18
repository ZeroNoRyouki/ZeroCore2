/*
 *
 * MultiblockPartTypeProperties.java
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

package it.zerono.mods.zerocore.lib.block.multiblock;

import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class MultiblockPartTypeProperties<Controller extends IMultiblockController<Controller>,
                                            PartType extends IMultiblockPartType> {

    public MultiblockPartTypeProperties(final Supplier<@NotNull Supplier<@NotNull BlockEntityType<?>>> tileTypeSupplier,
                                        final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<PartType>,
                                                @NotNull MultiblockPartBlock<Controller, PartType>> blockFactory,
                                        final String translationKey,
                                        final Function<Block.@NotNull Properties, Block.@NotNull Properties> blockPropertiesFixer) {
        this(tileTypeSupplier, blockFactory, translationKey, blockPropertiesFixer, ep -> ep);
    }

    public MultiblockPartTypeProperties(final Supplier<@NotNull Supplier<@NotNull BlockEntityType<?>>> tileTypeSupplier,
                                        final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<PartType>,
                                                @NotNull MultiblockPartBlock<Controller, PartType>> blockFactory,
                                        final String translationKey,
                                        final Function<Block.@NotNull Properties, Block.@NotNull Properties> blockPropertiesFixer,
                                        final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<PartType>,
                                                MultiblockPartBlock.@NotNull MultiblockPartProperties<PartType>> partPropertiesFixer) {

        this._tileTypeSupplier = tileTypeSupplier;
        this._blockFactory = blockFactory;
        this._translationKey = translationKey;
        this._blockPropertiesFixer = blockPropertiesFixer;
        this._extendedPropertiesFixer = partPropertiesFixer;
    }

    public MultiblockPartBlock<Controller, PartType> createBlock(final PartType type) {
        return this._blockFactory.apply(
                this._extendedPropertiesFixer.apply(
                        MultiblockPartBlock.MultiblockPartProperties.create(type, this._blockPropertiesFixer.apply(
                                        (IMultiblockPart.getDefaultBlockProperties())))));
    }

    public MultiblockPartBlock<Controller, PartType> createBlock(final PartType type, final IMultiblockVariant variant) {
        return this._blockFactory.apply(
                this._extendedPropertiesFixer.apply(
                        MultiblockPartBlock.MultiblockPartProperties.create(type, this._blockPropertiesFixer.apply(
                                variant.getDefaultBlockProperties()))
                .variant(variant)));
    }

    @Nullable
    public BlockEntity createTileEntity(final BlockState state, final BlockPos position) {
        return this._tileTypeSupplier.get().get().create(position, state);
    }

    public String getTranslationKey() {
        return this._translationKey;
    }

    //region internals

    private final Supplier<@NotNull Supplier<@NotNull BlockEntityType<?>>> _tileTypeSupplier;
    private final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<PartType>,
            @NotNull MultiblockPartBlock<Controller, PartType>> _blockFactory;
    private final Function<Block.@NotNull Properties, Block.@NotNull Properties> _blockPropertiesFixer;
    private final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<PartType>,
            MultiblockPartBlock.@NotNull MultiblockPartProperties<PartType>> _extendedPropertiesFixer;
    private final String _translationKey;

    //endregion
}
