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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
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
    public static ChunkCache get(final Level world) {
        return s_caches.get(world);
    }

    public static ChunkCache getOrCreate(final Level world) {
        return s_caches.computeIfAbsent(world, w -> new ChunkCache(Objects.requireNonNull(world)));
    }

    @Nullable
    public LevelChunk get(final BlockPos position) {

        if (!this._world.isInWorldBounds(position)) {
            return null;
        }

        final int chunkX = position.getX() >> 4;
        final int chunkZ = position.getZ() >> 4;
        final long chunkHash = ChunkPos.asLong(chunkX, chunkZ);
        LevelChunk chunk = this._chunks.get(chunkHash);

        if (null == chunk) {

            chunk = (LevelChunk)this._world.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);

            if (null != chunk) {
                this._chunks.put(chunkHash, chunk);
            }
        }

        return chunk;
    }

    public void remove(final LevelChunk chunk) {
        this._chunks.remove(chunk.getPos().toLong());
    }

    public void clear() {
        this._chunks.clear();
    }

    @SubscribeEvent
    public static void onChunkUnload(final ChunkEvent.Unload event) {

        final ChunkAccess chunk = Objects.requireNonNull(event.getChunk());
        final LevelAccessor world = chunk.getWorldForge();

        if (null != world) {

            final ChunkCache cache = s_caches.get(world);

            if (null != cache) {
                cache.remove((LevelChunk)chunk);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(final WorldEvent.Unload event) {

        final LevelAccessor world = Objects.requireNonNull(event.getWorld());
        final ChunkCache cache = s_caches.get(world);

        if (null != cache) {

            cache.clear();
            s_caches.remove(world);
        }
    }

    public Level getWorld() {
        return this._world;
    }

    //region internals

    private ChunkCache(final Level world) {

        this._world = Objects.requireNonNull(world);
        this._chunks = new Long2ObjectOpenHashMap<>(256 * 256 / (16 * 2), 0.75f);
    }

    private static final Map<LevelAccessor, ChunkCache> s_caches = new Reference2ObjectArrayMap<>(2 * 8);

    private final Long2ObjectMap<LevelChunk> _chunks;
    private final Level _world;

    //endregion
}
