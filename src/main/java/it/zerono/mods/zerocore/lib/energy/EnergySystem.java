/*
 *
 * EnergySystem.java
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

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum EnergySystem
        implements StringRepresentable {

    ForgeEnergy("Forge Energy", "FE", 1d),
    RedstoneFlux("Redstone Flux", "RF", 1d),
    Tesla("Tesla", "T", 1d),
    EnergyUnit("Energy Units", "EU", 0.25d),
    MinecraftJoules("Minecraft Joules", "MJ", 0.1d),
    Joules("Joules", "J", 2.5d),
    GalacticraftJoules("Galacticraft Joules", "gJ", 1.6d)
    ;

    public static final IntFunction<EnergySystem> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final ModCodecs<EnergySystem, ByteBuf> CODECS = new ModCodecs<>(
            StringRepresentable.fromEnum(EnergySystem::values),
            ByteBufCodecs.idMapper(BY_ID, Enum::ordinal)
    );

    /**
     * The reference system, used for conversions
     */
    public static final EnergySystem REFERENCE = ForgeEnergy;

    /**
     * Construct an EnergySystem
     *
     * @param name the EnergySystem name
     * @param unit the EnergySystem unit of measure
     * @param conversionRatio how many units of this energy system are needed to make 1 unit of the reference system
     */
    EnergySystem(final String name, final String unit, final double conversionRatio) {

        this._name = name;
        this._unit = unit;
        this._conversionRatio = conversionRatio;
        this._conversionRatioWide = WideAmount.asImmutable(conversionRatio);
    }

    public static EnergySystem read(final CompoundTag data, final String key, final EnergySystem defaultValue) {

        if (data.contains(key)) {

            final String value = data.getString(key);

            if (!Strings.isNullOrEmpty(value)) {
                return EnergySystem.valueOf(value);
            }
        }

        return defaultValue;
    }

    public static void write(final CompoundTag data, final String key, final EnergySystem value) {
        data.putString(key, value.name());
    }

    /**
     * Convert the given amount to the given EnergySystem
     *
     * @param target the EnergySystem to convert the amount to
     * @param amount the amount to convert
     * @return the converted amount
     */
    public double convertTo(final EnergySystem target, final double amount) {
        // convert the amount to the REFERENCE system and then to the requested one
        return amount / this.getConversionRatio() * target.getConversionRatio();
    }

    /**
     * Convert the given amount to the given EnergySystem
     *
     * @param target the EnergySystem to convert the amount to
     * @param amount the amount to convert
     * @return the converted amount
     */
    public WideAmount convertTo(final EnergySystem target, final WideAmount amount) {
        // convert the amount to the REFERENCE system and then to the requested one
        return amount.divide(this._conversionRatioWide).multiply(target._conversionRatioWide);
    }

    public String getFullName() {
        return this._name;
    }

    public String getUnit() {
        return this._unit;
    }

    public double getConversionRatio() {
        return this._conversionRatio;
    }

    public String asHumanReadableNumber(final double value) {
        return CodeHelper.formatAsHumanReadableNumber(value, this.getUnit());
    }

    //region StringRepresentable

    @Override
    public String getSerializedName() {
        return this._name;
    }

    //endregion
    //region Object

    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum type should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return this.getUnit();
    }

    //region internals

    private final String _name;
    private final String _unit;
    private final double _conversionRatio;
    private final WideAmount _conversionRatioWide;

    //endregion
}
