/*
 *
 * WideValue.java
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

package it.zerono.mods.zerocore.lib.data;

import com.google.gson.JsonObject;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.json.JSONHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * A positive number with an integer part defined by an unsigned long, and a decimal part stored in a short
 *
 * Based upon the FloatingLong class from aidancbrady's Mekanism
 */
@SuppressWarnings("unused")
public class WideAmount
        extends Number
        implements Comparable<WideAmount> {

    public static final WideAmount MAX_VALUE;
    public static final WideAmount ZERO;
    public static final WideAmount ONE;

    public static WideAmount from(final long integerPart, final short decimalPart) {
        return getKnowValueOrCreate(integerPart, decimalPart, WideAmount::new);
    }

    public static WideAmount from(final long integerPart) {
        return from(integerPart, (short)0);
    }

    public static WideAmount from(final double value) {
        return from(integerPartFrom(value), decimalPartFrom(value));
    }

    public static WideAmount from(final FriendlyByteBuf buffer) {
        return from(buffer.readVarLong(), buffer.readShort());
    }

    public static WideAmount from(final CompoundTag nbt) {
        return from(nbt.getLong("i"), nbt.getShort("d"));
    }

    public static WideAmount from(final JsonObject json) {
        return from(JSONHelper.jsonGetLong(json, "i"), JSONHelper.jsonGetShort(json, "d"));
    }

    public static WideAmount asImmutable(final long integerPart, final short decimalPart) {
        return getKnowValueOrCreate(integerPart, decimalPart, Immutable::new);
    }

    public static WideAmount asImmutable(final long integerPart) {
        return asImmutable(integerPart, (short)0);
    }

    public static WideAmount asImmutable(final double value) {
        return asImmutable(integerPartFrom(value), decimalPartFrom(value));
    }

    public void serializeTo(final FriendlyByteBuf buffer) {

        buffer.writeVarLong(this.getIntegerPart());
        buffer.writeShort(this.getDecimalPart());
    }

    public JsonObject serializeTo(final JsonObject json) {

        JSONHelper.jsonSetLong(json, "i", this.getIntegerPart());
        JSONHelper.jsonSetShort(json, "d", this.getDecimalPart());
        return json;
    }

    public CompoundTag serializeTo(final CompoundTag nbt) {

        nbt.putLong("i", this.getIntegerPart());
        nbt.putShort("d", this.getDecimalPart());
        return nbt;
    }

    public static WideAmount parse(final String text) {

        final long integerPart;
        final int index = text.indexOf(".");

        if (index == -1) {
            integerPart = Long.parseUnsignedLong(text);
        } else {
            integerPart = Long.parseUnsignedLong(text.substring(0, index));
        }

        return from(integerPart, parseDecimal(text, index));
    }

    public WideAmount toImmutable() {
        return asImmutable(this._integerPart, this._decimalPart);
    }

    public WideAmount copy() {
        return from(this._integerPart, this._decimalPart);
    }

    public long getIntegerPart() {
        return this._integerPart;
    }

    public short getDecimalPart() {
        return this._decimalPart;
    }

    public boolean isZero() {
        return isZero(this._integerPart, this._decimalPart);
    }

    public boolean equals(final WideAmount other) {
        return this._integerPart == other.getIntegerPart() && this._decimalPart == other.getDecimalPart();
    }

    public static WideAmount min(final WideAmount a, final WideAmount b) {
        return a.greaterThan(b) ? b : a;
    }

    public static WideAmount max(final WideAmount a, final WideAmount b) {
        return a.greaterThan(b) ? a : b;
    }

    public boolean smallerThan(final WideAmount other) {
        return this.compareTo(other) < 0;
    }

    public boolean smallerOrEqual(final WideAmount other) {
        return this.compareTo(other) <= 0;
    }

    public boolean greaterThan(final WideAmount other) {
        return this.compareTo(other) > 0;
    }

    public boolean greaterOrEqual(final WideAmount other) {
        return this.compareTo(other) >= 0;
    }

    public WideAmount set(final WideAmount other) {
        return this.set(other.getIntegerPart(), other.getDecimalPart());
    }

    /**
     * Adds the provided {@link WideAmount} to this {@link WideAmount}, clamping the operation to {@link WideAmount#MAX_VALUE}
     *
     * @param other the {@link WideAmount} to add
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#MAX_VALUE} if the operation has overflown
     */
    public WideAmount add(final WideAmount other) {
        return this.add(other.getIntegerPart(), other.getDecimalPart());
    }

    /**
     * Adds the provided unsigned long to this {@link WideAmount}, clamping the operation to {@link WideAmount#MAX_VALUE}
     *
     * @param other the unsigned long to add
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#MAX_VALUE} if the operation has overflown
     */
    public WideAmount add(final long other) {
        return this.add(other, (short)0);
    }

    /**
     * Adds the provided double to this {@link WideAmount}, clamping the operation to {@link WideAmount#MAX_VALUE}
     *
     * @param other the double to add
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#MAX_VALUE} if the operation has overflown
     */
    public WideAmount add(final double other) {

        if (other < 0) {
            throw cantBeNegativeException();
        }

        return this.add(integerPartFrom(other), decimalPartFrom(other));
    }

    /**
     * Subtracts the provided {@link WideAmount} from this {@link WideAmount}, clamping the operation to {@link WideAmount#ZERO}
     *
     * @param other the {@link WideAmount} to subtract
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#ZERO} if the operation has overflown
     */
    public WideAmount subtract(final WideAmount other) {
        return this.subtract(other.getIntegerPart(), other.getDecimalPart());
    }

    /**
     * Subtracts the provided unsigned long from this {@link WideAmount}, clamping the operation to {@link WideAmount#ZERO}
     *
     * @param other the unsigned long to subtract
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#ZERO} if the operation has overflown
     */
    public WideAmount subtract(final long other) {
        return this.subtract(other, (short)0);
    }

    /**
     * Subtracts the provided double from this {@link WideAmount}, clamping the operation to {@link WideAmount#ZERO}
     *
     * @param other the double to subtract
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#ZERO} if the operation has overflown
     */
    public WideAmount subtract(final double other) {

        if (other < 0) {
            throw cantBeNegativeException();
        }

        return this.subtract(integerPartFrom(other), decimalPartFrom(other));
    }

    /**
     * Multiplies the provided {@link WideAmount} with this {@link WideAmount}, clamping the operation to {@link WideAmount#MAX_VALUE}
     *
     * @param other the {@link WideAmount} to multiply by
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#MAX_VALUE} if the operation has overflown
     */
    public WideAmount multiply(final WideAmount other) {
        return this.multiply(other.getIntegerPart(), other.getDecimalPart());
    }

    /**
     * Multiplies the provided unsigned long with this {@link WideAmount}, clamping the operation to {@link WideAmount#MAX_VALUE}
     *
     * @param other the unsigned long to multiply by
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#MAX_VALUE} if the operation has overflown
     */
    public WideAmount multiply(final long other) {
        return this.multiply(other, (short)0);
    }

    /**
     * Multiplies the provided double with this {@link WideAmount}, clamping the operation to {@link WideAmount#MAX_VALUE}
     *
     * @param other the double to multiply by
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#MAX_VALUE} if the operation has overflown
     */
    public WideAmount multiply(final double other) {

        if (other < 0) {
            throw cantBeNegativeException();
        }

        return this.multiply(integerPartFrom(other), decimalPartFrom(other));
    }

    /**
     * Divides this {@link WideAmount} by the provided {@link WideAmount}, clamping the operation to {@link WideAmount#MAX_VALUE}
     *
     * @param other the {@link WideAmount} to divide by
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#MAX_VALUE} if the operation has overflown
     */
    public WideAmount divide(final WideAmount other) {

        if (other.isZero()) {
            throw divisionByZeroException();
        } else if (this.isZero()) {
            return ZERO;
        } else if (0 == other.getDecimalPart()) {
            return divide(other.getIntegerPart());
        }

        final BigDecimal divide = new BigDecimal(this.toString()).divide(new BigDecimal(other.toString()), MAX_DECIMAL_DIGITS, RoundingMode.HALF_UP);

        return this.set(divide.longValue(), parseDecimal(divide.toPlainString()));
    }

    /**
     * Divides this {@link WideAmount} by the provided unsigned long, rounding to the nearest 0.0001
     *
     * @param other the unsigned long to divide by
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#MAX_VALUE} if the operation has overflown
     */
    public WideAmount divide(final long other) {

        if (0 == other) {
            throw divisionByZeroException();
        } else if (this.isZero()) {
            return ZERO;
        }

        final long remainder = Long.remainderUnsigned(this._integerPart, other);
        long integerPart = Long.divideUnsigned(this._integerPart, other);
        long decimalPart;

        if (Long.compareUnsigned(remainder, MAX_LONG_SHIFT / 10) >= 0) {

            decimalPart = Long.divideUnsigned(remainder, Long.divideUnsigned(other, SINGLE_UNIT * 10L));

        } else {

            decimalPart = Long.divideUnsigned(remainder * SINGLE_UNIT * 10L, other);
            decimalPart += Long.divideUnsigned(this._decimalPart * 10L, other);
        }

        if (Long.remainderUnsigned(decimalPart, 10) >= 5) {

            decimalPart += 10;

            if (decimalPart >= SINGLE_UNIT * 10) {

                ++integerPart;
                decimalPart -= SINGLE_UNIT * 10;
            }
        }

        decimalPart /= 10;

        return this.set(integerPart, (short)decimalPart);
    }

    /**
     * Divides this {@link WideAmount} by the provided double, clamping the operation to {@link WideAmount#MAX_VALUE}
     *
     * @param other the double to divide by
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#MAX_VALUE} if the operation has overflown
     */
    public WideAmount divide(final double other) {

        if (other < 0) {
            throw cantBeNegativeException();
        }

        return this.divide(from(other));
    }

    /**
     * Divides this {@link WideAmount} by the provided {@link WideAmount}, rounding down to an unsigned long
     *
     * @param other the {@link WideAmount} to divide by
     * @return an unsigned long representing the result of the division
     */
    public long divideToUnsignedLong(final WideAmount other) {

        if (other.isZero()) {
            throw divisionByZeroException();
        } else if (this.smallerThan(other)) {
            return 0;
        }

        final long otherIntegerPart = other.getIntegerPart();
        final short otherDecimalPart = other.getDecimalPart();

        if (other.greaterOrEqual(ONE)) {

            // we can ignore our decimal part

            if (Long.compareUnsigned(otherIntegerPart, MAX_LONG_SHIFT) <= 0) {

                final long div = otherIntegerPart * SINGLE_UNIT + otherDecimalPart;

                return (Long.divideUnsigned(this._decimalPart, div) * SINGLE_UNIT) +
                        Long.divideUnsigned(Long.remainderUnsigned(this._integerPart, div) * SINGLE_UNIT, div);
            }

            if (Long.compareUnsigned(otherIntegerPart, Long.divideUnsigned(-1L, 2) + 1L) >= 0) {
                return 1L;
            }

            final long quotient = Long.divideUnsigned(this._integerPart, otherIntegerPart);

            if (quotient != Long.divideUnsigned(this._integerPart, otherIntegerPart + 1)) {
                if (otherIntegerPart * quotient + Long.divideUnsigned(otherDecimalPart * quotient, MAX_DECIMAL_VALUE) > this._integerPart) {
                    return quotient - 1;
                }
            }

            return quotient;
        }

        if (Long.compareUnsigned(this._integerPart, MAX_LONG_SHIFT) >= 0) {
            return Long.divideUnsigned(this._integerPart, otherDecimalPart) * MAX_DECIMAL_VALUE
                    + Long.divideUnsigned(Long.remainderUnsigned(this._integerPart, otherDecimalPart) * MAX_DECIMAL_VALUE, otherDecimalPart)
                    + (long)this._decimalPart * MAX_DECIMAL_VALUE / otherDecimalPart;
        }

        return Long.divideUnsigned(this._integerPart * MAX_DECIMAL_VALUE, otherDecimalPart) + ((long)this._decimalPart * MAX_DECIMAL_VALUE / otherDecimalPart);
    }

    /**
     * Divides this {@link WideAmount} by the provided {@link WideAmount}, rounding down to a signed long
     *
     * @param other the {@link WideAmount} to divide by
     * @return an signed long representing the result of the division
     */
    public long divideToSignedLong(final WideAmount other) {
        return CodeHelper.mathClampUnsignedToLong(this.divideToUnsignedLong(other));
    }

    /**
     * Divides this {@link WideAmount} by the provided {@link WideAmount}, rounding down to an int value
     *
     * @param other the {@link WideAmount} to divide by
     * @return an int representing the result of the division
     */
    public int divideToInt(final WideAmount other) {
        return CodeHelper.mathClampUnsignedToInt(this.divideToSignedLong(other));
    }

    /**
     * Return the percentage that this {@link WideAmount} represent over the provided {@link WideAmount}
     *
     * @param total the unsigned long to divide by
     * @return this {@link WideAmount} or a new {@link WideAmount} if this one is immutable or {@link WideAmount#MAX_VALUE} if the operation has overflown
     */
    public double percentage(final WideAmount total) {
        return total.isZero() || this.greaterThan(total) ? 1.0 : this.copy().divide(total).doubleValue();
    }

    public String toString(int decimalPlaces) {

        if (0 == this._decimalPart) {
            return Long.toUnsignedString(this._integerPart);
        }

        if (decimalPlaces > MAX_DECIMAL_DIGITS) {
            decimalPlaces = MAX_DECIMAL_DIGITS;
        }

        String decimalAsString = Short.toString(this._decimalPart);
        int numberOfDigits = decimalAsString.length();

        if (numberOfDigits < MAX_DECIMAL_DIGITS) {

            // We need to prepend some zeros so that 1 -> 0.0001 rather than 0.01 for when we want two decimal places

            decimalAsString = CodeHelper.zeroFilled(MAX_DECIMAL_DIGITS - numberOfDigits) + decimalAsString;
            numberOfDigits = MAX_DECIMAL_DIGITS;
        }

        if (numberOfDigits > decimalPlaces) {
            decimalAsString = decimalAsString.substring(0, decimalPlaces);
        }

        return Long.toUnsignedString(this._integerPart) + "." + decimalAsString;
    }

    //region Comparable<WideValue>

    @Override
    public int compareTo(final WideAmount other) {
        return compareTo(this._integerPart, this._decimalPart, other.getIntegerPart(), other.getDecimalPart());
    }

    //endregion
    //region Number

    /**
     * Returns the value of the specified number as an {@code int},
     * which may involve rounding or truncation.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code int}.
     */
    @Override
    public int intValue() {
        return CodeHelper.mathClampUnsignedToInt(this._integerPart);
    }

    /**
     * Returns the value of the specified number as a {@code long},
     * which may involve rounding or truncation.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code long}.
     */
    @Override
    public long longValue() {
        return CodeHelper.mathClampUnsignedToLong(this._integerPart);
    }

    /**
     * Returns the value of the specified number as a {@code float},
     * which may involve rounding.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code float}.
     */
    @Override
    public float floatValue() {
        return CodeHelper.mathUnsignedLongToFloat(this._integerPart) + this._decimalPart / (float)SINGLE_UNIT;
    }

    /**
     * Returns the value of the specified number as a {@code double},
     * which may involve rounding.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code double}.
     */
    @Override
    public double doubleValue() {
        return CodeHelper.mathUnsignedLongToDouble(this._integerPart) + this._decimalPart / (double)SINGLE_UNIT;
    }

    //endregion
    //region Object

    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof WideAmount && this.equals((WideAmount)other));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this._integerPart, this._decimalPart);
    }

    @Override
    public String toString() {
        return this.toString(MAX_DECIMAL_DIGITS);
    }

    //endregion
    //region Immutable

    private static class Immutable
        extends WideAmount {

        private Immutable(final long integerPart, final short decimalPart) {
            super(integerPart, decimalPart);
        }

        //region WideValue

        @Override
        public WideAmount toImmutable() {
            return this;
        }

        @Override
        public WideAmount copy() {
            return this;
        }

        protected WideAmount set(final long integerPart, final short decimalPart) {
            return from(integerPart, (short)Mth.clamp(decimalPart, 0, MAX_DECIMAL_VALUE));
        }

        //endregion
    }

    //endregion
    //region internals

    private WideAmount(final long integerPart, final short decimalPart) {

        this._integerPart = integerPart;
        this._decimalPart = (short)Mth.clamp(decimalPart, 0, MAX_DECIMAL_VALUE);
    }

    private static WideAmount getKnowValueOrCreate(final long integerPart, final short decimalPart,
                                                   final BiFunction<Long, Short, WideAmount> factory) {

        if (isZero(integerPart, decimalPart)) {
            return ZERO;
        } else if (isOne(integerPart, decimalPart)) {
            return ONE;
        } else if (isMaxValue(integerPart, decimalPart)) {
            return MAX_VALUE;
        } else {
            return factory.apply(integerPart, decimalPart);
        }
    }

    protected WideAmount set(final long integerPart, final short decimalPart) {

        this._integerPart = integerPart;
        this._decimalPart = (short)Mth.clamp(decimalPart, 0, MAX_DECIMAL_VALUE);
        return this;
    }

    private WideAmount add(final long otherIntegerPart, final short otherDecimalPart) {

        if ((this._integerPart < 0 && otherIntegerPart < 0) ||
                ((this._integerPart < 0 || otherIntegerPart < 0) && (this._integerPart + otherIntegerPart >= 0))) {
            return this.set(MAX_VALUE);
        }

        long newIntegerPart = this._integerPart + otherIntegerPart;
        short newDecimalPart = (short)(this._decimalPart + otherDecimalPart);

        if (newDecimalPart > MAX_DECIMAL_VALUE) {

            if (-1 == newIntegerPart) {

                newDecimalPart = MAX_DECIMAL_VALUE;

            } else {

                newDecimalPart -= SINGLE_UNIT;
                ++newIntegerPart;
            }
        }

        return this.set(newIntegerPart, newDecimalPart);
    }

    private WideAmount subtract(final long otherIntegerPart, final short otherDecimalPart) {

        if (compareTo(otherIntegerPart, otherDecimalPart, this._integerPart, this._decimalPart) > 0) {
            // we can never be lower than zero
            return this.set(ZERO);
        }

        long newIntegerPart = this._integerPart - otherIntegerPart;
        short newDecimalPart = (short)(this._decimalPart - otherDecimalPart);

        if (newDecimalPart < 0) {

            newDecimalPart += SINGLE_UNIT;
            --newIntegerPart;
        }

        return this.set(newIntegerPart, newDecimalPart);
    }

    private WideAmount multiply(final long otherIntegerPart, final short otherDecimalPart) {

        if (CodeHelper.mathUnsignedLongMultiplicationWillOverFlow(this._integerPart, otherIntegerPart)) {
            return this.set(MAX_VALUE);
        }

        WideAmount temp = from(clampedMultiplyLongs(this._integerPart, otherIntegerPart));

        temp = temp.add(clampedMultiplyLongAndDecimal(this._integerPart, otherDecimalPart));
        temp = temp.add(clampedMultiplyLongAndDecimal(otherIntegerPart, this._decimalPart));
        temp = temp.add(clampedMultiplyDecimals(this._decimalPart, otherDecimalPart));

        return this.set(temp);
    }

    protected static int compareTo(final long xIntegerPart, final short xDecimalPart,
                                   final long yIntegerPart, final short yDecimalPart) {

        final int comparison = Long.compareUnsigned(xIntegerPart, yIntegerPart);

        return 0 == comparison ? Short.compare(xDecimalPart, yDecimalPart) : comparison;
    }

    protected static boolean isZero(final long integerPart, final short decimalPart) {
        return 0 == integerPart && decimalPart <= 0;
    }

    protected static boolean isOne(final long integerPart, final short decimalPart) {
        return 1 == integerPart && decimalPart == 0;
    }

    protected static boolean isMaxValue(final long integerPart, final short decimalPart) {
        return MAX_VALUE.getIntegerPart() == integerPart && MAX_VALUE.getDecimalPart() == decimalPart;
    }

    private static long clampedMultiplyLongs(final long a, final long b) {

        if (a == 0 || b == 0) {
            return 0;
        } else if (CodeHelper.mathUnsignedLongMultiplicationWillOverFlow(a, b)) {
            return -1;
        } else {
            return a * b;
        }
    }

    private static WideAmount clampedMultiplyDecimals(final short a, final short b) {
        return from(0, (short)((long) a * (long) b / SINGLE_UNIT));
    }

    private static WideAmount clampedMultiplyLongAndDecimal(final long integerPart, final short decimalPart) {

        if (Long.compareUnsigned(integerPart, Long.divideUnsigned(-1, SINGLE_UNIT)) > 0) {
            return from(Long.divideUnsigned(integerPart, SINGLE_UNIT) * decimalPart, (short)(integerPart % SINGLE_UNIT * decimalPart));
        } else {
            return from(Long.divideUnsigned(integerPart * decimalPart, SINGLE_UNIT), (short)(integerPart * decimalPart % SINGLE_UNIT));
        }
    }

    protected static long integerPartFrom(final double value) {

        if (value > MAX_VALUE_AS_DOUBLE) {
            return MAX_VALUE.getIntegerPart();
        } else if (value < 0) {
            return ZERO.getIntegerPart();
        } else {
            return (long)value;
        }
    }

    protected static short decimalPartFrom(final double value) {
        return parseDecimal(DECIMAL_FORMAT.format(value));
    }

    protected static short parseDecimal(final String string) {
        return parseDecimal(string, string.indexOf("."));
    }

    protected static short parseDecimal(final String string, final int index) {

        if (index == -1) {
            return 0;
        }

        String decimals = string.substring(index + 1);
        final int digitsCount = decimals.length();

        if (digitsCount < MAX_DECIMAL_DIGITS) {
            decimals += CodeHelper.zeroFilled(MAX_DECIMAL_DIGITS - digitsCount);
        } else if (digitsCount > MAX_DECIMAL_DIGITS) {
            decimals = decimals.substring(0, MAX_DECIMAL_DIGITS);
        }

        return Short.parseShort(decimals);
    }

    private static RuntimeException cantBeNegativeException() {
        return new IllegalArgumentException("The value provided is a negative number and this is not supported. WideValues are always positive.");
    }

    private static RuntimeException divisionByZeroException() {
        return new ArithmeticException("Division by zero");
    }

    private static final int MAX_DECIMAL_DIGITS = 4;
    private static final short MAX_DECIMAL_VALUE = 9999;
    private static final short SINGLE_UNIT = MAX_DECIMAL_VALUE + 1;
    private static final double MAX_VALUE_AS_DOUBLE;
    private static final long MAX_LONG_SHIFT;
    private static final DecimalFormat DECIMAL_FORMAT;

    static {

        ZERO = new Immutable(0, (short)0);
        ONE = new Immutable(1, (short)0);
        MAX_VALUE = new Immutable(-1, MAX_DECIMAL_VALUE);
        MAX_VALUE_AS_DOUBLE = Double.parseDouble(MAX_VALUE.toString());
        MAX_LONG_SHIFT = Long.divideUnsigned(Long.divideUnsigned(-1L, SINGLE_UNIT), SINGLE_UNIT);

        DECIMAL_FORMAT = new DecimalFormat("0.0000", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }

    private long _integerPart;
    private short _decimalPart;

    //endregion
}
