/*
 *
 * WideEnergyReceiverForwarder2.java
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
import it.zerono.mods.zerocore.lib.energy.IWideEnergyReceiver2;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public class WideEnergyReceiverForwarder2
        extends AbstractWideEnergyHandlerForwarder2<IWideEnergyReceiver2>
        implements IWideEnergyReceiver2 {

    public WideEnergyReceiverForwarder2(final IWideEnergyReceiver2 handler) {
        super(handler);
    }

    //region IWideEnergyReceiver2

    /**
     * Add energy, expressed in the specified {@link EnergySystem}, to an IWideEnergyReceiver.
     * Internal distribution is left entirely to the IWideEnergyReceiver
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from the direction the request is coming from, or null for any directions
     * @param maxAmount maximum amount of energy to receive
     * @param mode how the operation is carried out
     * @return amount of energy that was (or would have been, if simulated) received
     */
    @Override
    public WideAmount receiveEnergy(EnergySystem system, @Nullable Direction from, WideAmount maxAmount, OperationMode mode) {
        return this.getHandler().receiveEnergy(system, from, maxAmount, mode);
    }

    //endregion
}
