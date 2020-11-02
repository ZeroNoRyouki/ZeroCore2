/*
 *
 * UV.java
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

package it.zerono.mods.zerocore.lib.data.gfx;

@SuppressWarnings({"WeakerAccess"})
public class UV {

    public final float U;
    public final float V;

    public UV(final float u, final float v) {

        this.U = u;
        this.V = v;
    }

    public UV(final UV other) {
        this(other.U, other.V);
    }

    public UV multiply(final float factor) {
        return new UV(this.U * factor, this.V * factor);
    }

    @Override
    public boolean equals(Object other) {

        if (other instanceof UV) {

            UV uv = (UV)other;

            return this.U == uv.U && this.V == uv.V;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("UV (%f, %f)", this.U, this.V);
    }

    private UV() {
        this.U = this.V = 0;
    }
}
