/*
 *
 * NeighboringPositions.java
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

package it.zerono.mods.zerocore.lib.world;

import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.stream.LongStream;

public class NeighboringPositions {

    public NeighboringPositions() {
        this(0, 0, 0, CodeHelper.DIRECTIONS);
    }

    public NeighboringPositions(final Direction[] directions) {
        this(0, 0, 0, directions);
    }

    public NeighboringPositions(final BlockPos position) {
        this(position.getX(), position.getY(), position.getZ(), CodeHelper.DIRECTIONS);
    }

    public NeighboringPositions(final BlockPos position, final Direction[] directions) {
        this(position.getX(), position.getY(), position.getZ(), directions);
    }

    public NeighboringPositions(final int x, final int y, final int z) {
        this(x, y, z, CodeHelper.DIRECTIONS);
    }

    public NeighboringPositions(final int x, final int y, final int z, final Direction[] directions) {

        final int length = directions.length;

        this._directions = directions;
        this._neighbors = new BlockPos.Mutable[length];
        this._neighborHashes = new long[length];

        for (int i = 0; i < length; ++i) {

            final BlockPos.Mutable position = new BlockPos.Mutable(x, y, z).move(this._directions[i]);

            this._neighbors[i] = position;
            this._neighborHashes[i] = position.asLong();
        }
    }

    public void setTo(final BlockPos position) {
        this.setTo(position.getX(), position.getY(), position.getZ());
    }

    public void setTo(final int x, final int y, final int z) {

        for (int i = 0; i < this._neighbors.length; ++i) {

            final BlockPos.Mutable position = this._neighbors[i].set(x, y, z).move(this._directions[i], 1);

            this._neighborHashes[i] = position.asLong();
        }
    }

    public int size() {
        return this._neighbors.length;
    }

    public BlockPos get(final int index) {
        return this._neighbors[index];
    }

    public long getHash(final int index) {
        return this._neighborHashes[index];
    }

    public LongStream getHashStream() {
        return LongStream.of(this._neighborHashes);
    }

    //region internals

    private final Direction[] _directions;
    private final BlockPos.Mutable[] _neighbors;
    private final long[] _neighborHashes;

    //endregion
}
