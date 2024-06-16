/*
 *
 * AbstractWideEnergyHandlerForwarder.java
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

package it.zerono.mods.zerocore.lib.energy.handler;

import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyHandler;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

@Deprecated // use AbstractWideEnergyHandlerForwarder2
public abstract class AbstractWideEnergyHandlerForwarder<T extends IWideEnergyHandler>
        implements IWideEnergyHandler {

    protected AbstractWideEnergyHandlerForwarder(final T handler) {
        this.setHandler(handler);
    }

    public T getHandler() {
        return this._handler;
    }

    public void setHandler(final T handler) {
        this._handler = handler;
    }

    //region IWideEnergyHandler

    /**
     * Get the {@link EnergySystem} used by this entity
     *
     * @return the {@link EnergySystem} in use
     */
    @Override
    public EnergySystem getEnergySystem() {
        return this.getHandler().getEnergySystem();
    }

    /**
     * Returns true if the entity can connect on a given side and support with the provided {@link EnergySystem}.
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from   the direction the request is coming from, or null for any directions
     */
    @Override
    public boolean canConnectEnergy(final EnergySystem system, final @Nullable Direction from) {
        return this.getHandler().canConnectEnergy(system, from);
    }

    /**
     * Returns the amount of energy currently stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from   the direction the request is coming from, or null for any directions
     */
    @Override
    public double getEnergyStored(final EnergySystem system, final @Nullable Direction from) {
        return this.getHandler().getEnergyStored(system, from);
    }

    /**
     * Returns the maximum amount of energy that can be stored expressed in the requested {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from   the direction the request is coming from, or null for any directions
     */
    @Override
    public double getCapacity(final EnergySystem system, final @Nullable Direction from) {
        return this.getHandler().getCapacity(system, from);
    }

    //endregion
    //region internals

    private T _handler;

    //endregion
}
