/*
 *
 * AbstractFluidPortHandler.java
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

package it.zerono.mods.zerocore.base.multiblock.part.io.fluid;

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.AbstractIOPortHandler;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;

public abstract class AbstractFluidPortHandler<Controller extends AbstractCuboidMultiblockController<Controller>,
            T extends AbstractMultiblockEntity<Controller> & IFluidPort>
        extends AbstractIOPortHandler<Controller, T>
        implements IFluidPortHandler {

    protected AbstractFluidPortHandler(final T part, final IoMode mode) {
        super(part, mode);
    }

    public T getIoEntity() {
        return this.getPart();
    }

    public boolean isInput() {
        return this.getIoEntity().getIoDirection().isInput();
    }

    public boolean isOutput() {
        return this.getIoEntity().getIoDirection().isOutput();
    }
}
