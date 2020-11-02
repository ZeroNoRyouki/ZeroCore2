/*
 *
 * IntegerBitField.java
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

import com.google.common.base.Preconditions;

@SuppressWarnings("WeakerAccess")
public class IntegerBitField {

    public IntegerBitField() {
        this(0);
    }

    public IntegerBitField(final int initialValue) {
        this._bits = initialValue;
    }

    public int getValue() {
        return this._bits;
    }

    public boolean checkBit(final int bitPosition) {
        return 1 == this.getBit(bitPosition);
    }

    public int getBit(final int bitPosition) {

        Preconditions.checkArgument(bitPosition >= 0 && bitPosition < 32);

        int mask = bitPosition > 0 ? 1 << bitPosition : 1;

        return (this._bits & mask) >> bitPosition;
    }

    public int setBit(final int bitPosition) {

        Preconditions.checkArgument(bitPosition >= 0 && bitPosition < 32);

        int mask = bitPosition > 0 ? 1 << bitPosition : 1;

        return this._bits |= mask;
    }

    public int clearBit(final int bitPosition) {

        Preconditions.checkArgument(bitPosition >= 0 && bitPosition < 32);

        int mask = bitPosition > 0 ? ~(1 << bitPosition) : 0;

        return this._bits &= mask;
    }

    public int modifyBit(final int bitPosition, final boolean newValue) {
        return newValue ? this.setBit(bitPosition) : this.clearBit(bitPosition);
    }

    public int modifyBits(int startBitPosition, int rangeLength, int newValue) {

        Preconditions.checkArgument(rangeLength > 0);
        Preconditions.checkArgument(startBitPosition >= 0 && startBitPosition + rangeLength < 32);

        int mask = rangeLength < 32 ? (~0 >>> (32 - rangeLength)) : ~0;

        newValue &= mask;

        if (startBitPosition > 0) {
            newValue = newValue << startBitPosition;
        }

        return this._bits |= newValue;
    }

    //region internals

    private int _bits;

    //endregion
}
