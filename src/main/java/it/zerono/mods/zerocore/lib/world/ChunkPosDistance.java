/*
 *
 * ChunkPosDistance.java
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

import com.google.common.collect.Sets;
import it.zerono.mods.zerocore.lib.data.AreaShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Set;

@SuppressWarnings({"WeakerAccess"})
public class ChunkPosDistance extends ChunkPos {

    /** The number of chunks between this chunk position and the original chunk */
    public final float distance;

    public ChunkPosDistance(final int x, final int z, final float distance) {

        super(x, z);
        this.distance = distance;
    }

    public ChunkPosDistance(final BlockPos pos, final float distance) {

        super(pos);
        this.distance = distance;
    }

    /**
     * Return the coordinates and relative distance of the chunks around a starting point in the world that lay in the specified area
     *
     * @param startingPoint a point in the first chunk
     * @param area the shape of the area to check
     * @param radiusX the size of the area, on the X axis
     * @param radiusZ the size of the area, on the Z axis (only used for LineSouthNorth, Rectangle and Ellipse)
     * @return the coordinates and distance of the chunks in the requested area in no particular order
     */
    public static Set<ChunkPosDistance> getChunksInRange(final BlockPos startingPoint, final AreaShape area,
                                                         int radiusX, int radiusZ) {
        return getChunksInRange(startingPoint.getX() >> 4, startingPoint.getZ() >> 4, area, radiusX, radiusZ);
    }

    /**
     * Return the coordinates and relative distance of the chunks around a starting point in the world that lay in the specified area
     *
     * @param startingChunkX the X coordinate of the first chunk
     * @param startingChunkZ the Z coordinate of the first chunk
     * @param area the shape of the area to check
     * @param radiusX the size of the area, on the X axis
     * @param radiusZ the size of the area, on the Z axis (only used for LineSouthNorth, Rectangle and Ellipse)
     * @return the coordinates and distance of the chunks in the requested area in no particular order
     */
    public static Set<ChunkPosDistance> getChunksInRange(final int startingChunkX, final int startingChunkZ,
                                                         final AreaShape area, final int radiusX, final int radiusZ) {

        final Set<ChunkPosDistance> chunks = Sets.newHashSet();

        switch (area) {

            case LineEastWest:
                chunks.add(new ChunkPosDistance(startingChunkX, startingChunkZ, 0.0f));
                for (int distance = 1; distance <= radiusX; ++distance) {
                    chunks.add(new ChunkPosDistance(startingChunkX - distance, startingChunkZ, distance));
                    chunks.add(new ChunkPosDistance(startingChunkX + distance, startingChunkZ, distance));
                }
                break;

            case LineUpDown:
                chunks.add(new ChunkPosDistance(startingChunkX, startingChunkZ, 0.0f));
                break;

            case LineSouthNorth:
                chunks.add(new ChunkPosDistance(startingChunkX, startingChunkZ, 0.0f));
                for (int distance = 1; distance <= radiusZ; ++distance) {
                    chunks.add(new ChunkPosDistance(startingChunkX, startingChunkZ - distance, distance));
                    chunks.add(new ChunkPosDistance(startingChunkX, startingChunkZ + distance, distance));
                }
                break;

            case Square:
            case Rectangle:
                for (int rz = AreaShape.Rectangle == area ? radiusZ : radiusX, z = startingChunkZ - rz; z <= startingChunkZ + rz; ++z) {
                    for (int x = startingChunkX - radiusX; x <= startingChunkX + radiusX; ++x){
                        chunks.add(new ChunkPosDistance(x, z, (float)Math.sqrt(x * x + z * z)));
                    }
                }
                break;

            case Circle:
                for (int x = startingChunkX - radiusX; x <= startingChunkX + radiusX; ++x) {
                    for (int z = startingChunkZ - radiusX; z <= startingChunkZ + radiusX; ++z) {

                        int deltaX = x - startingChunkX;
                        int deltaZ = z - startingChunkZ;
                        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

                        if (distance < radiusX) {
                            chunks.add(new ChunkPosDistance(x, z, (float) distance));
                        }
                    }
                }
                break;

            case Ellipse:
                for (int x = startingChunkX - radiusX; x <= startingChunkX + radiusX; ++x) {
                    for (int z = startingChunkZ - radiusZ; z <= startingChunkZ + radiusZ; ++z) {

                        if (((x * x) / (radiusX * radiusX) + (z * z) / (radiusZ * radiusZ)) <= 1) {

                            final int deltaX = x - startingChunkX;
                            final int deltaZ = z - startingChunkZ;
                            final double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

                            chunks.add(new ChunkPosDistance(x, z, (float)distance));
                        }
                    }
                }
                break;
        }

        return chunks;
    }
}
