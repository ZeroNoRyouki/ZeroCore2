/*
 *
 * IOPortBlock.java
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

package it.zerono.mods.zerocore.base.multiblock.part.io;

import it.zerono.mods.zerocore.base.multiblock.part.GenericDeviceBlock;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class IOPortBlock<Controller extends IMultiblockController<Controller>,
                            PartType extends IMultiblockPartType>
        extends GenericDeviceBlock<Controller, PartType>
        implements INeighborChangeListener.Notifier {

    public IOPortBlock(final MultiblockPartProperties<PartType> properties) {
        super(properties);
    }

    //region Block

    @Override
    public void onRemove(BlockState state, Level world, BlockPos position, BlockState newState, boolean isMoving) {

        if (state.getBlock() != newState.getBlock()) {
            this.getIIoEntity(world, position).ifPresent(ioe -> ioe.onBlockReplaced(state, world, position, newState, isMoving));
        }

        super.onRemove(state, world, position, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos position, Player player,
                                 InteractionHand hand, BlockHitResult hit) {

        if (InteractionHand.MAIN_HAND == hand && player.getMainHandItem().is(TagsHelper.TAG_WRENCH)) {

            this.callOnLogicalServer(world, w -> this.getIIoEntity(w, position).ifPresent(IIoEntity::toggleIoDirection));
            return InteractionResult.SUCCESS;
        }

        return super.use(state, world, position, player, hand, hit);
    }

    //endregion
    //region internals

    private Optional<IIoEntity> getIIoEntity(final Level world, final BlockPos position) {
        return WorldHelper.getTile(world, position)
                .filter(te -> te instanceof IIoEntity)
                .map(te -> (IIoEntity)te);
    }

    //endregion
}
