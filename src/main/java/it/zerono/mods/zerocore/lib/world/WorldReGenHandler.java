/*
 *
 * WorldReGenHandler.java
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

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.zerono.mods.zerocore.internal.Log;
import it.zerono.mods.zerocore.internal.gamecontent.Content;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class WorldReGenHandler
    extends AbstractWorldGenFeaturesMap<Biome> {

    public WorldReGenHandler(final String worldGenVersionTagName, final IntSupplier worldGenCurrentVersionSupplier,
                             final BooleanSupplier enabledCheck) {

        this._enabled = enabledCheck;
        this._worldGenVersionTagName = worldGenVersionTagName;
        this._worldGenCurrentVersion = worldGenCurrentVersionSupplier;

        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(this::onChunkDataSave);
        bus.addListener(this::onChunkDataLoad);
        bus.addListener(this::onServerStopped);
        bus.addListener(this::onWorldTick);
    }

    public static Predicate<Biome> matchAll() {
        return biome -> true;
    }

    public static Predicate<Biome> matchOnly(final ResourceLocation biomeId) {
        return biome -> biome.getRegistryName().equals(biomeId);
    }

    public static Predicate<Biome> anyExcept(final ResourceLocation biomeId) {
        return biome -> !biome.getRegistryName().equals(biomeId);
    }

    public static Predicate<Biome> onlyNether() {
        return biome -> Biome.BiomeCategory.NETHER == biome.getBiomeCategory();
    }

    public static Predicate<Biome> exceptNether() {
        return biome -> Biome.BiomeCategory.NETHER != biome.getBiomeCategory();
    }

    public static Predicate<Biome> onlyTheEnd() {
        return biome -> Biome.BiomeCategory.THEEND == biome.getBiomeCategory();
    }

    public static Predicate<Biome> exceptTheEnd() {
        return biome -> Biome.BiomeCategory.THEEND != biome.getBiomeCategory();
    }

    public static ConfiguredFeature<?, ?> oreFeature(final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                                                     final int clustersAmount, final int oresPerCluster,
                                                     final int placementBottomOffset, final int placementTopOffset,
                                                     final int placementMaximum) {
        return oreFeature(Content.FEATURE_ORE_REGEN, oreBlock, matchRule,clustersAmount, oresPerCluster, placementBottomOffset,
                placementTopOffset, placementMaximum);
    }

    public static Pair<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>>
        oreGenAndRegenFeatures(final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                               final int clustersAmount, final int oresPerCluster, final int placementBottomOffset,
                               final int placementTopOffset, final int placementMaximum) {

        final ConfiguredFeature<?, ?> gen = WorldGenManager.oreFeature(oreBlock, matchRule, clustersAmount, oresPerCluster,
                placementBottomOffset, placementTopOffset,placementMaximum);

        final ConfiguredFeature<?, ?> regen = oreFeature(oreBlock, matchRule, clustersAmount, oresPerCluster,
                placementBottomOffset, placementTopOffset,placementMaximum);

        return Pair.of(gen, regen);
    }

    public void addGenAndRegenOre(final Pair<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> suppliers,
                                  final Predicate<BiomeLoadingEvent> genBiomeMatcher, final Predicate<Biome> reGenBiomeMatcher) {

        WorldGenManager.INSTANCE.addOre(genBiomeMatcher, suppliers.getLeft());
        this.addOre(reGenBiomeMatcher, suppliers.getRight());
    }

    //region internals

    private boolean enabled() {
        return this._enabled.getAsBoolean();
    }

    private String getWorldGenVersionTagName() {
        return "zcwg_" + this._worldGenVersionTagName;
    }

    private synchronized void onChunkDataLoad(final ChunkDataEvent.Load event) {

        final LevelAccessor world = event.getWorld();

        if (this.enabled() && world instanceof Level && !world.isClientSide() &&
                (!event.getData().contains(this.getWorldGenVersionTagName()) ||
                event.getData().getInt(this.getWorldGenVersionTagName()) < this._worldGenCurrentVersion.getAsInt())) {
            this.addChunkToRegen(((Level) world).dimension(), event.getChunk().getPos());
        }
    }

    private void onChunkDataSave(final ChunkDataEvent.Save event) {

        if (this.enabled() && null != event.getWorld() && !event.getWorld().isClientSide()) {
            event.getData().putInt(this.getWorldGenVersionTagName(), this._worldGenCurrentVersion.getAsInt());
        }
    }

    private void onServerStopped(final FMLServerStoppedEvent event) {

        if (null != this._chunksToRegen) {
            this._chunksToRegen.clear();
        }
    }

    private void onWorldTick(final TickEvent.WorldTickEvent event) {

        if (this.enabled() && event.side.isServer() && TickEvent.Phase.END == event.phase) {
            this.processChunks((ServerLevel)event.world);
        }
    }

    private void addChunkToRegen(final ResourceKey<Level> dimension, final ChunkPos chunkPosition) {

        if (null == this._chunksToRegen) {
            this._chunksToRegen = new Object2ObjectArrayMap<>();
        }

        final Queue<ChunkPos> positions = this._chunksToRegen.computeIfAbsent(dimension.location(), k -> new LinkedList<>());

        if (!positions.contains(chunkPosition)) {
            positions.add(chunkPosition);
        }
    }

    private void processChunks(final ServerLevel world) {

        if (!world.isClientSide && null != this._chunksToRegen) {

            final ResourceLocation dimensionId = world.dimension().location();

            if (this._chunksToRegen.containsKey(dimensionId)) {

                final Queue<ChunkPos> chunksToGen = this._chunksToRegen.get(dimensionId);
                final long startTime = System.nanoTime();

                while (System.nanoTime() - startTime < MAX_CHUNKS_PROCESS_TIME && !chunksToGen.isEmpty()) {

                    final ChunkPos nextChunk = chunksToGen.poll();

                    if (null == nextChunk) {
                        break;
                    }

                    final Random random = new Random(world.getSeed());
                    final long xSeed = random.nextLong() >> 2 + 1L;
                    final long zSeed = random.nextLong() >> 2 + 1L;

                    random.setSeed((xSeed * nextChunk.x + zSeed * nextChunk.z) ^ world.getSeed());

                    this.regenerateChunk(world, random, nextChunk.x, nextChunk.z);
                }

                if (chunksToGen.isEmpty()) {
                    this._chunksToRegen.remove(dimensionId);
                }
            }
        }
    }

    private void regenerateChunk(final ServerLevel world, final Random random, final int chunkX, final int chunkZ) {

        if (!world.hasChunk(chunkX, chunkZ)) {
            return;
        }

        final ChunkGenerator chunkGenerator = world.getChunkSource().getGenerator();
        final BlockPos position = new BlockPos(chunkX * 16, 0, chunkZ * 16);
        final Biome biome = world.getBiome(position);

        for (final GenerationStep.Decoration stage : this._entries.keySet()) {

            boolean processed = false;

            for (Pair<Predicate<Biome>, ConfiguredFeature<?, ?>> pair : this._entries.get(stage)) {

                if (pair.getKey().test(biome)) {
                    processed |= pair.getValue().place(world, chunkGenerator, random, position);
                }
            }

            if (processed) {
                Log.LOGGER.info(Log.CORE, "Retro-gen run on chunk {}, {}", chunkX, chunkZ);
            }
        }
    }

    private static final long MAX_CHUNKS_PROCESS_TIME = 16000000; // 16 milliseconds

    private final BooleanSupplier _enabled;
    private final String _worldGenVersionTagName;
    private final IntSupplier _worldGenCurrentVersion;

    private Map<ResourceLocation, Queue<ChunkPos>> _chunksToRegen;

    //endregion
}
