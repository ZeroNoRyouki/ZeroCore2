/*
 *
 * WorldGenManager.java
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

import it.zerono.mods.zerocore.lib.block.ModBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class WorldGenManager
        extends AbstractWorldGenFeaturesMap<BiomeLoadingEvent> {

    public static final WorldGenManager INSTANCE = new WorldGenManager();

    public static Predicate<BiomeLoadingEvent> matchAll() {
        return id -> true;
    }

    public static Predicate<BiomeLoadingEvent> matchOnly(final ResourceLocation biomeId) {
        return event -> event.getName().equals(biomeId);
    }

    public static Predicate<BiomeLoadingEvent> anyExcept(final ResourceLocation biomeId) {
        return event -> !event.getName().equals(biomeId);
    }

    public static Predicate<BiomeLoadingEvent> onlyNether() {
        return event -> Biome.BiomeCategory.NETHER == event.getCategory();
    }

    public static Predicate<BiomeLoadingEvent> exceptNether() {
        return event -> Biome.BiomeCategory.NETHER != event.getCategory();
    }

    public static Predicate<BiomeLoadingEvent> onlyTheEnd() {
        return event -> Biome.BiomeCategory.THEEND == event.getCategory();
    }

    public static Predicate<BiomeLoadingEvent> exceptTheEnd() {
        return event -> Biome.BiomeCategory.THEEND != event.getCategory();
    }

    public static OreGenRegisteredFeature oreVein(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
                                                  final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                                                  final int oresPerVein, final int veinsPerChunk, final int minY, final int maxY) {
        return OreGenRegisteredFeature.generation(name, idFactory, oreBlock, matchRule, oresPerVein)
                .standardVein(veinsPerChunk, minY, maxY);
    }

    public static OreGenRegisteredFeature oreDeepVein(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
                                                      final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                                                      final int oresPerVein, final int veinsPerChunk) {
        return OreGenRegisteredFeature.generation(name, idFactory, oreBlock, matchRule, oresPerVein)
                .deepVein(veinsPerChunk);
    }

    //region internals

    private WorldGenManager() {
        MinecraftForge.EVENT_BUS.addListener(this::onBiomesLoaded);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomesLoaded(final BiomeLoadingEvent event) {

        final BiomeGenerationSettingsBuilder builder = event.getGeneration();

        for (final GenerationStep.Decoration stage : this._entries.keySet()) {

            final List<Supplier<PlacedFeature>> biomeFeatures = builder.getFeatures(stage);

            this._entries.getOrDefault(stage, Collections.emptyList()).stream()
                    .filter(p -> p.getKey().test(event))
                    .forEach(p -> biomeFeatures.add(p.getValue()));
        }
    }

    //endregion
}
