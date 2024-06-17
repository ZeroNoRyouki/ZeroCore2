/*
 *
 * AbstractMultiblockMachineController.java
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

package it.zerono.mods.zerocore.base.multiblock;

import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockDimensionVariant;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public abstract class AbstractMultiblockMachineController<Controller extends AbstractMultiblockMachineController<Controller, V>,
                                                          V extends IMultiblockDimensionVariant>
        extends AbstractMultiblockController<Controller, V>
        implements IActivableMachine {

    protected AbstractMultiblockMachineController(Level world) {
        super(world);
    }

    //region IActivableMachine

    @Override
    public boolean isMachineActive() {
        return this._active;
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given NBT compound
     *
     * @param data       the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataFrom(data, registries, syncReason);

        if (data.contains("active")) {
            this._active = data.getBoolean("active");
        }
    }

    /**
     * Sync the entity data to the given NBT compound
     *
     * @param data       the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataTo(data, registries, syncReason);

        data.putBoolean("active", this.isMachineActive());
        return data;
    }

    //endregion
    //region internals

    protected boolean _active;

    //endregion
}
