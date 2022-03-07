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
import it.zerono.mods.zerocore.lib.block.ModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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
    extends AbstractWorldGenFeaturesMap<Holder<Biome>> {

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

    public static Predicate<Holder<Biome>> matchAll() {
        return biome -> true;
    }

    public static Predicate<Holder<Biome>> matchOnly(final ResourceLocation biomeId) {
        return biomeHolder -> biomeHolder.is(biomeId);
    }

    public static Predicate<Holder<Biome>> anyExcept(final ResourceLocation biomeId) {
        return biomeHolder -> !biomeHolder.is(biomeId);
    }

    public static Predicate<Holder<Biome>> onlyNether() {
        return biomeHolder -> Biome.BiomeCategory.NETHER == Biome.getBiomeCategory(biomeHolder);
    }

    public static Predicate<Holder<Biome>> exceptNether() {
        return biomeHolder -> Biome.BiomeCategory.NETHER != Biome.getBiomeCategory(biomeHolder);
    }

    public static Predicate<Holder<Biome>> onlyTheEnd() {
        return biomeHolder -> Biome.BiomeCategory.THEEND == Biome.getBiomeCategory(biomeHolder);
    }

    public static Predicate<Holder<Biome>> exceptTheEnd() {
        return biomeHolder -> Biome.BiomeCategory.THEEND != Biome.getBiomeCategory(biomeHolder);
    }

    public static OreGenRegisteredFeature oreVein(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
                                                  final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                                                  final int oresPerVein, final int veinsPerChunk, final int minY, final int maxY) {
        return OreGenRegisteredFeature.regeneration(name, idFactory, oreBlock, matchRule, oresPerVein)
                .standardVein(veinsPerChunk, minY, maxY);
    }

    public static OreGenRegisteredFeature oreDeepVein(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
                                                      final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                                                      final int oresPerVein, final int veinsPerChunk) {
        return OreGenRegisteredFeature.regeneration(name, idFactory, oreBlock, matchRule, oresPerVein)
                .deepVein(veinsPerChunk);
    }

    public static Pair<OreGenRegisteredFeature, OreGenRegisteredFeature> oreVeinWithRegen(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
                                                                                          final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                                                                                          final int oresPerVein, final int veinsPerChunk, final int minY, final int maxY) {
        return Pair.of(WorldGenManager.oreVein(name, idFactory, oreBlock, matchRule, oresPerVein, veinsPerChunk, minY, maxY),
                oreVein(name, idFactory, oreBlock, matchRule, oresPerVein, veinsPerChunk, minY, maxY));
    }

    public static Pair<OreGenRegisteredFeature, OreGenRegisteredFeature> oreDeepVeinWithRegen(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
                                                                                              final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                                                                                              final int oresPerVein, final int veinsPerChunk) {
        return Pair.of(WorldGenManager.oreDeepVein(name, idFactory, oreBlock, matchRule, oresPerVein, veinsPerChunk),
                oreDeepVein(name, idFactory, oreBlock, matchRule, oresPerVein, veinsPerChunk));
    }

    public void addOreVein(final Pair<OreGenRegisteredFeature, OreGenRegisteredFeature> veins,
                           final Predicate<BiomeLoadingEvent> genBiomeMatcher, final Predicate<Holder<Biome>> reGenBiomeMatcher) {

        WorldGenManager.INSTANCE.addOreVein(genBiomeMatcher, veins.getLeft());
        this.addOreVein(reGenBiomeMatcher, veins.getRight());
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

    private void onServerStopped(final ServerStoppedEvent event) {

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
        final Holder<Biome> biome = world.getBiome(position);

        for (final GenerationStep.Decoration stage : this._entries.keySet()) {

            boolean processed = false;

            for (Pair<Predicate<Holder<Biome>>, Supplier<Holder<PlacedFeature>>> pair : this._entries.get(stage)) {

                if (pair.getKey().test(biome)) {
                    processed |= pair.getValue().get().value().place(world, chunkGenerator, random, position);
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
