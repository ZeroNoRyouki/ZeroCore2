/*
 *
 * Orientation.java
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

package it.zerono.mods.zerocore.lib.client.gui;

public enum Orientation {

    LeftToRight(1, 2),
    RightToLeft(0, 3),
    TopToBottom(3, 0),
    BottomToTop(2, 1)
    ;

    Orientation(final int mirrored, final int flipped) {

        this._mirrored = (byte)mirrored;
        this._flipped = (byte)flipped;
    }

    public Orientation mirror() {
        return Orientation.values()[this._mirrored];
    }

    public Orientation flip() {
        return Orientation.values()[this._flipped];
    }

    public boolean isHorizontal() {
        return this.ordinal() < 2;
    }

    public boolean isVertical() {
        return this.ordinal() > 1;
    }

    //region internals

    private final byte _mirrored;
    private final byte _flipped;

    //endregion
}
