/*
 *
 * ChunkCache.java
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

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.zerono.mods.zerocore.ZeroCore;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = ZeroCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChunkCache {

    @Nullable
    public static ChunkCache get(final World world) {
        return s_caches.get(world);
    }

    public static ChunkCache getOrCreate(final World world) {
        return s_caches.computeIfAbsent(world, w -> new ChunkCache(Objects.requireNonNull(world)));
    }

    @Nullable
    public Chunk get(final BlockPos position) {

        if (!World.isInWorldBounds(position)) {
            return null;
        }

        final int chunkX = position.getX() >> 4;
        final int chunkZ = position.getZ() >> 4;
        final long chunkHash = ChunkPos.asLong(chunkX, chunkZ);
        Chunk chunk = this._chunks.get(chunkHash);

        if (null == chunk) {

            chunk = (Chunk)this._world.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);

            if (null != chunk) {
                this._chunks.put(chunkHash, chunk);
            }
        }

        return chunk;
    }

    public void remove(final Chunk chunk) {
        this._chunks.remove(chunk.getPos().toLong());
    }

    public void clear() {
        this._chunks.clear();
    }

    @SubscribeEvent
    public static void onChunkUnload(final ChunkEvent.Unload event) {

        final IChunk chunk = Objects.requireNonNull(event.getChunk());
        final IWorld world = chunk.getWorldForge();

        if (null != world) {

            final ChunkCache cache = s_caches.get(world);

            if (null != cache) {
                cache.remove((Chunk)chunk);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(final WorldEvent.Unload event) {

        final IWorld world = Objects.requireNonNull(event.getWorld());
        final ChunkCache cache = s_caches.get(world);

        if (null != cache) {

            cache.clear();
            s_caches.remove(world);
        }
    }

    //region internals

    private ChunkCache(final World world) {

        this._world = Objects.requireNonNull(world);
        this._chunks = new Long2ObjectOpenHashMap<>(256 * 256 / (16 * 2), 0.75f);
    }

    private static final Map<IWorld, ChunkCache> s_caches = new Reference2ObjectArrayMap<>(2 * 8);

    private final Long2ObjectMap<Chunk> _chunks;
    private final World _world;

    //endregion
}
