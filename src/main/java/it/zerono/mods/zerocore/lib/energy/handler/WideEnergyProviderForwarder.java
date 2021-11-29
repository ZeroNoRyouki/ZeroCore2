/*
 *
 * WideEnergyProviderForwarder.java
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
import it.zerono.mods.zerocore.lib.energy.IWideEnergyProvider;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

@Deprecated //use WideEnergyProviderForwarder2
public class WideEnergyProviderForwarder
        extends AbstractWideEnergyHandlerForwarder<IWideEnergyProvider>
        implements IWideEnergyProvider {

    public WideEnergyProviderForwarder(final IWideEnergyProvider handler) {
        super(handler);
    }

    //region IWideEnergyProvider

    /**
     * Remove energy, expressed in the specified {@link EnergySystem}, from an IWideEnergyProvider.
     * Internal distribution is left entirely to the IWideEnergyProvider
     *
     * @param system    the {@link EnergySystem} used by the request
     * @param from      the direction the request is coming from, or null for any directions
     * @param maxAmount maximum amount of energy to extract
     * @param simulate  if true, the extraction will only be simulated
     * @return amount of energy that was (or would have been, if simulated) extracted
     */
    @Override
    public double extractEnergy(final EnergySystem system, final @Nullable Direction from,
                                final double maxAmount, final boolean simulate) {
        return this.getHandler().extractEnergy(system, from, maxAmount, simulate);
    }

    //endregion
}
