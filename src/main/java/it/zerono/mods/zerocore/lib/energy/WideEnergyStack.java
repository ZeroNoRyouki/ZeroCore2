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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;

public class WideEnergyStack
        implements IEnergySystemAware {

    public static final WideEnergyStack EMPTY = new WideEnergyStack();

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

    public static boolean areItemStacksEqual(final WideEnergyStack stackA, final WideEnergyStack stackB) {

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
        this._amount.set(amount);
    }

    public void grow(final WideAmount increment) {
        this._amount.add(increment);
    }

    public void shrink(final WideAmount decrement) {
        this._amount.subtract(decrement);
    }

    public CompoundNBT serializeTo(final CompoundNBT nbt) {

        NBTHelper.nbtSetEnum(nbt, "sys", this._system);
        nbt.put("amount", this._amount.serializeTo(new CompoundNBT()));
        return nbt;
    }

    public static WideEnergyStack from(final CompoundNBT nbt) {

        if (NBTHelper.nbtContainsEnum(nbt, "sys") && nbt.contains("amount", Constants.NBT.TAG_COMPOUND)) {
            return new WideEnergyStack(NBTHelper.nbtGetEnum(nbt, "sys", EnergySystem.class),
                    WideAmount.from(nbt.getCompound("amount")));
        }

        Log.LOGGER.info(Log.CORE, "Tried to read an WideEnergyStack from invalid NBT data");
        return EMPTY;
    }

    public void serializeTo(final PacketBuffer buffer) {

        buffer.writeEnum(this._system);
        this._amount.serializeTo(buffer);
    }

    public static WideEnergyStack from(final PacketBuffer buffer) {

        try {

            return new WideEnergyStack(buffer.readEnum(EnergySystem.class), WideAmount.from(buffer));

        } catch (RuntimeException ex) {

            Log.LOGGER.info(Log.CORE, "Tried to read an WideEnergyStack from invalid packet data");
            return EMPTY;
        }
    }

    public JsonElement serializeTo() {

        final JsonObject json = new JsonObject();

        JSONHelper.jsonSetEnum(json, "sys", this._system);
        json.add("amount", this._amount.serializeTo(json));
        return json;
    }

    public static WideEnergyStack from(final JsonElement element) {

        try {

            final JsonObject json = element.getAsJsonObject();

            return new WideEnergyStack(JSONHelper.jsonGetEnum(json, "sys", EnergySystem.class),
                    WideAmount.from(json.getAsJsonObject("amount")));

        } catch (RuntimeException ex) {

            Log.LOGGER.info(Log.CORE, "Tried to read an WideEnergyStack from invalid JSON data");
            return EMPTY;
        }
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
    private final WideAmount _amount;

    //endregion
}
