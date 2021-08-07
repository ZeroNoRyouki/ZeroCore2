/*
 *
 * EnergyStack.java
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

package it.zerono.mods.zerocore.lib.energy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

public final class EnergyStack
    implements IEnergySystemAware {

    public static final EnergyStack EMPTY = new EnergyStack();

    public EnergyStack(final EnergySystem system) {
        this(system, 0.0);
    }

    public EnergyStack(final EnergySystem system, final double amount) {

        isValidAmount(amount);
        this._system = system;
        this._amount = amount;
    }

    public EnergyStack copy() {

        if (this.isEmpty()) {
            return EMPTY;
        } else {
            return new EnergyStack(this._system, this._amount);
        }
    }

    public boolean isEmpty() {
        return EMPTY == this || Double.isNaN(this._amount);
    }

    public boolean isEnergySystemEqual(final EnergyStack other) {
        return !other.isEmpty() && this.getEnergySystem() == other.getEnergySystem();
    }

    public static boolean areItemStacksEqual(final EnergyStack stackA, final EnergyStack stackB) {

        if (stackA.isEmpty() && stackB.isEmpty()) {
            return true;
        } else {
            return !stackA.isEmpty() && !stackB.isEmpty() && stackA.isStackEqual(stackB);
        }
    }

    public double getMaxStackSize() {
        return Double.MAX_VALUE;
    }

    public double getAmount() {
        return this.isEmpty() ? 0 : this._amount;
    }

    public void setAmount(final double amount) {

        isValidAmount(amount);
        this._amount = amount;
    }

    public void grow(final double amount) {
        this.setAmount(this._amount + amount);
    }

    public void shrink(final double amount) {
        this.grow(-amount);
    }

    public CompoundNBT serializeTo(final CompoundNBT nbt) {

        NBTHelper.nbtSetEnum(nbt, "sys", this._system);
        nbt.putDouble("amount", this._amount);
        return nbt;
    }

    public static EnergyStack from(final CompoundNBT nbt) {

        if (nbt.contains("sys") && nbt.contains("amount")) {
            return new EnergyStack(NBTHelper.nbtGetEnum(nbt, "sys", EnergySystem.class), nbt.getDouble("amount"));
        }

        Log.LOGGER.info(Log.CORE, "Tried to read an EnergyStack from invalid NBT data");
        return EMPTY;
    }

    public void serializeTo(final PacketBuffer buffer) {

        buffer.writeEnum(this._system);
        buffer.writeDouble(this._amount);
    }

    public static EnergyStack from(final PacketBuffer buffer) {

        try {

            return new EnergyStack(buffer.readEnum(EnergySystem.class), buffer.readDouble());

        } catch (RuntimeException ex) {

            Log.LOGGER.info(Log.CORE, "Tried to read an EnergyStack from invalid packet data");
            return EMPTY;
        }
    }

    public JsonElement serializeTo() {

        final JsonObject json = new JsonObject();

        JSONHelper.jsonSetEnum(json, "sys", this._system);
        JSONHelper.jsonSetDouble(json, "amount", this._amount);
        return json;
    }

    public static EnergyStack from(final JsonElement element) {

        try {

            final JsonObject json = element.getAsJsonObject();

            return new EnergyStack(JSONHelper.jsonGetEnum(json, "sys", EnergySystem.class),
                    JSONHelper.jsonGetDouble(json, "amount"));

        } catch (RuntimeException ex) {

            Log.LOGGER.info(Log.CORE, "Tried to read an EnergyStack from invalid JSON data");
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

    private EnergyStack() {

        this._system = EnergySystem.REFERENCE;
        this._amount = Double.NaN;
    }

    private boolean isStackEqual(final EnergyStack other) {
        return this._system == other._system && this._amount == other._amount;
    }

    private static void isValidAmount(final double value) {

        if (value < 0.0 || Double.isNaN(value)) {
            throw new IllegalArgumentException("Illegal energy amount");
        }
    }

    private final EnergySystem _system;
    private double _amount;

    //endregion
}
