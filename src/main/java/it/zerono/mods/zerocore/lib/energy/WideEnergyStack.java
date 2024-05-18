package it.zerono.mods.zerocore.lib.energy;
/*
 * WideEnergyStack
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
 * Do not remove or edit this header
 *
 */

import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import net.minecraft.network.codec.StreamCodec;

public class WideEnergyStack
        implements IEnergySystemAware {

    public static final WideEnergyStack EMPTY = new WideEnergyStack();

    public static final ModCodecs<WideEnergyStack, ByteBuf> CODECS = new ModCodecs<>(
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            EnergySystem.CODECS.field("system", WideEnergyStack::getEnergySystem),
                            WideAmount.CODECS.field("amount", WideEnergyStack::getAmount)
                    ).apply(instance, WideEnergyStack::new)),
            StreamCodec.composite(
                    EnergySystem.CODECS.streamCodec(), WideEnergyStack::getEnergySystem,
                    WideAmount.CODECS.streamCodec(), WideEnergyStack::getAmount,
                    WideEnergyStack::new)
    );

    public WideEnergyStack(final EnergySystem system) {
        this(system, WideAmount.ZERO);
    }

    public WideEnergyStack(final EnergySystem system, final WideAmount amount) {

        this._system = system;
        this._amount = amount.copy();
    }

    public WideEnergyStack copy() {

        if (this.isEmpty()) {
            return EMPTY;
        } else {
            return new WideEnergyStack(this._system, this._amount);
        }
    }

    public boolean isEmpty() {
        return EMPTY == this || this._amount.isZero();
    }

    public boolean isEnergySystemEqual(final WideEnergyStack other) {
        return this.getEnergySystem() == other.getEnergySystem();
    }

    public static boolean areStacksEqual(final WideEnergyStack stackA, final WideEnergyStack stackB) {

        if (stackA.isEmpty() && stackB.isEmpty()) {
            return true;
        } else {
            return !stackA.isEmpty() && !stackB.isEmpty() && stackA.isStackEqual(stackB);
        }
    }

    public WideAmount getMaxStackSize() {
        return WideAmount.MAX_VALUE;
    }

    public WideAmount getAmount() {
        return this._amount.copy();
    }

    public void setAmount(final WideAmount amount) {
        this._amount = this._amount.set(amount);
    }

    public void grow(final WideAmount increment) {
        this._amount = this._amount.add(increment);
    }

    public void shrink(final WideAmount decrement) {
        this._amount = this._amount.subtract(decrement);
    }

    //region IEnergySystemAware

    /**
     * Get the {@link EnergySystem} used by this entity
     *
     * @return the {@link EnergySystem} in use
     */
    @Override
    public EnergySystem getEnergySystem() {
        return this._system;
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this._amount + " " + this._system;
    }

    //endregion
    //region internals

    private WideEnergyStack() {

        this._system = EnergySystem.REFERENCE;
        this._amount = WideAmount.ZERO;
    }

    private boolean isStackEqual(final WideEnergyStack other) {
        return this._system == other._system && this._amount.equals(other._amount);
    }

    private final EnergySystem _system;
    private WideAmount _amount;

    //endregion
}
