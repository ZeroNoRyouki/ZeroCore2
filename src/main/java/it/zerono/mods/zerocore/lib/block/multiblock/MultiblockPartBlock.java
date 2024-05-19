/*
 *
 * MultiblockPartBlock.java
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

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.multiblock.validation.ValidationError;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public class MultiblockPartBlock<Controller extends IMultiblockController<Controller>,
                                 PartType extends IMultiblockPartType>
        extends ModBlock
        implements EntityBlock {

    public MultiblockPartBlock(final MultiblockPartProperties<PartType> properties) {

        super(properties);
        this._partType = properties._partType;
        this._multiblockVariant = properties._multiblockVariant;
    }

    public PartType getPartType() {
        return this._partType;
    }

    public Optional<IMultiblockVariant> getMultiblockVariant() {
        return Optional.ofNullable(this._multiblockVariant);
    }

    protected boolean openGui(final ServerPlayer player, final AbstractModBlockEntity mbe) {
        return mbe.openGui(player);
    }

    /**
     * Called when the block is right-clicked by a player.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos position, Player player,
                                               BlockHitResult hit) {

        if (CodeHelper.calledByLogicalServer(world)) {

            final Optional<IMultiblockPart<Controller>> part = WorldHelper.getMultiblockPartFrom(world, position);

            // report any multiblock errors

            if (player.isCrouching()) {

                final Optional<Controller> controller = part.flatMap(IMultiblockPart::getMultiblockController);

                final ValidationError error = controller.isEmpty() ? ValidationError.VALIDATION_ERROR_NOT_CONNECTED :
                        controller.filter(IMultiblockValidator::hasLastError)
                                .flatMap(IMultiblockValidator::getLastError)
                                .orElse(null);

                if (null != error) {

                    CodeHelper.reportErrorToPlayer(player, error);
                    return InteractionResult.SUCCESS;
                }
            }

            // open block GUI

            if (part.filter(p -> p instanceof MenuProvider && p instanceof AbstractModBlockEntity)
                    .map(p -> (AbstractModBlockEntity)p)
                    .filter(mbe -> mbe.canOpenGui(world, position, state))
                    .map(mbe -> this.openGui((ServerPlayer) player, mbe))
                    .orElse(false)) {
                return InteractionResult.CONSUME;
            }
        } else {

//            return WorldHelper.getMultiblockPartFrom(world, position)
//                    .filter(p -> p instanceof MenuProvider && p instanceof AbstractModBlockEntity)
//                    .map(p -> (AbstractModBlockEntity)p)
//                    .filter(mbe -> mbe.canOpenGui(world, position, state))
//                    .map(mbe -> InteractionResult.CONSUME)
//                    .orElse(InteractionResult.PASS);

            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(state, world, position, player, hit);
    }

    public static class MultiblockPartProperties<PartType extends IMultiblockPartType>
            extends ExtendedProperties<MultiblockPartProperties<PartType>> {

        public static <PartType extends IMultiblockPartType> MultiblockPartProperties<PartType> create(
                final PartType partType, final Block.Properties blockProperties) {
            return new MultiblockPartProperties<>(partType, blockProperties);
        }

        public MultiblockPartProperties<PartType> variant(final IMultiblockVariant variant) {

            this._multiblockVariant = variant;
            return this;
        }

        //region internals

        private MultiblockPartProperties(final PartType partType, final Block.Properties blockProperties) {

            this._partType = partType;
            this._multiblockVariant = null;
            this.setBlockProperties(blockProperties);
        }

        private final PartType _partType;
        private IMultiblockVariant _multiblockVariant;

        //endregion
    }

    //region EntityBlock

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos position, final BlockState state) {
        return this.getPartType().createTileEntity(state, position);
    }

    //endregion
    //region internals

    private final PartType _partType;
    private final IMultiblockVariant _multiblockVariant;

    //endregion
}
