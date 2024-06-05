/*
 *
 * AbstractMultiblockMachineEntity.java
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

package it.zerono.mods.zerocore.base.multiblock.part;

import it.zerono.mods.zerocore.base.multiblock.AbstractMultiblockMachineController;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockDimensionVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractMultiblockMachineEntity<Controller extends AbstractMultiblockMachineController<Controller, V>,
                                                      V extends IMultiblockDimensionVariant>
    extends AbstractMultiblockEntity<Controller>
    implements IActivableMachine, IMultiblockVariantProvider<V> {

    protected AbstractMultiblockMachineEntity(BlockEntityType<?> type, BlockPos position, BlockState blockState) {
        super(type, position, blockState);
    }

    //region IActivableMachine

    @Override
    public boolean isMachineActive() {
        return this.evalOnController(IActivableMachine::isMachineActive, false);
    }

    @Override
    public void setMachineActive(boolean active) {
        this.executeOnController(c -> c.setMachineActive(active));
    }

    //endregion
}
