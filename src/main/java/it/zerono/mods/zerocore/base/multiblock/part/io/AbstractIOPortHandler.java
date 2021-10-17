/*
 *
 * AbstractIOPortHandler.java
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

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullPredicate;

import javax.annotation.Nullable;

public abstract class AbstractIOPortHandler<Controller extends AbstractCuboidMultiblockController<Controller>,
        T extends AbstractMultiblockEntity<Controller> & IIoEntity> {

    public T getPart() {
        return this._part;
    }

    public boolean isInput() {
        return this.getPart().getIoDirection().isInput();
    }

    public boolean isOutput() {
        return this.getPart().getIoDirection().isOutput();
    }

    public boolean isActive() {
        return this._mode.isActive();
    }

    public boolean isPassive() {
        return this._mode.isPassive();
    }

    protected AbstractIOPortHandler(final T part, final IoMode mode) {

        this._part = part;
        this._mode = mode;
    }

    @Nullable
    protected <C> C lookupConsumer(@Nullable final World world, final BlockPos position,
                                   @Nullable final Capability<C> requestedCapability,
                                   final NonNullPredicate<TileEntity> isSameHandler,
                                   @Nullable C currentConsumer) {

        if (null == world) {
            return null;
        }

        boolean wasConnected = null != currentConsumer;
        C foundConsumer = null;

        final Direction approachDirection = this.getPart().getOutwardDirection().orElse(null);

        if (null == approachDirection) {

            wasConnected = false;

        } else {

            if (null != requestedCapability) {

                final TileEntity te = WorldHelper.getLoadedTile(world, position.relative(approachDirection));

                if (null != te && !isSameHandler.test(te)) {

                    final LazyOptional<C> capability = te.getCapability(requestedCapability, approachDirection.getOpposite());

                    if (capability.isPresent()) {
                        foundConsumer = capability.orElseThrow(RuntimeException::new);
                    }
                }
            }
        }

        final boolean isConnectedNow = null != foundConsumer;

        if (wasConnected != isConnectedNow && CodeHelper.calledByLogicalClient(world)) {
            WorldHelper.notifyBlockUpdate(world, position, null, null);
        }

        return foundConsumer;
    }

    //region internals

    private final T _part;
    private final IoMode _mode;

    //endregion
}
