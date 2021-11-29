/*
 *
 * WideEnergyProviderForwarder2.java
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

import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyProvider2;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public class WideEnergyProviderForwarder2
        extends AbstractWideEnergyHandlerForwarder2<IWideEnergyProvider2>
        implements IWideEnergyProvider2 {

    public WideEnergyProviderForwarder2(final IWideEnergyProvider2 handler) {
        super(handler);
    }

    //region IWideEnergyProvider2

    /**
     * Remove energy, expressed in the specified {@link EnergySystem}, from an IWideEnergyProvider2.
     * Internal distribution is left entirely to the IWideEnergyProvider
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from the direction the request is coming from, or null for any directions
     * @param maxAmount maximum amount of energy to extract
     * @param mode how the operation is carried out
     * @return amount of energy that was (or would have been, if simulated) extracted
     */
    @Override
    public WideAmount extractEnergy(final EnergySystem system, final @Nullable Direction from,
                                    final WideAmount maxAmount, final OperationMode mode) {
        return this.getHandler().extractEnergy(system, from, maxAmount, mode);
    }

    //endregion
}
