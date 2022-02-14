/*
 *
 * LightMap.java
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

import java.util.Objects;

@SuppressWarnings({"WeakerAccess"})
public class LightMap {

    public final int U;
    public final int V;

    public LightMap(final int u, final int v) {

        this.U = u;
        this.V = v;
    }

    public LightMap(final int combined) {
        this(combined & '\uffff', combined >> 16 & '\uffff');
    }

    public LightMap(final LightMap other) {
        this(other.U, other.V);
    }

    @Override
    public boolean equals(Object other) {

        if (other instanceof LightMap) {

            LightMap map = (LightMap)other;

            return this.U == map.U && this.V == map.V;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.U, this.V);
    }

    @Override
    public String toString() {
        return String.format("LightMap (U 0x%08x, V 0x%08x)", this.U, this.V);
    }

    //region internals

    private LightMap() {
        this.V = this.U = 0;
    }

    //endregion
}
