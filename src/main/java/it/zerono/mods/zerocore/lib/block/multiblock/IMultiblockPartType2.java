/*
 *
 * IMultiblockPartType2.java
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
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface IMultiblockPartType2<Controller extends IMultiblockController<Controller>,
                                        PartType extends IMultiblockPartType2<Controller, PartType>>
        extends IMultiblockPartType {

    MultiblockPartTypeProperties<Controller, PartType> getPartTypeProperties();

    default MultiblockPartBlock<Controller, PartType> createBlock() {
        //noinspection unchecked
        return this.getPartTypeProperties().createBlock((PartType)this);
    }

    default MultiblockPartBlock<Controller, PartType> createBlock(final IMultiblockVariant variant) {
        //noinspection unchecked
        return this.getPartTypeProperties().createBlock((PartType)this, variant);
    }

    @Nullable
    @Override
    default BlockEntity createTileEntity(BlockState state, BlockPos position) {
        return this.getPartTypeProperties().createTileEntity(state, position);
    }

    @Override
    default String getTranslationKey() {
        return this.getPartTypeProperties().getTranslationKey();
    }
}
