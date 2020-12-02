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

import it.zerono.mods.zerocore.internal.gamecontent.Content;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraftforge.common.MinecraftForge;
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
        return event -> Biome.Category.NETHER == event.getCategory();
    }

    public static Predicate<BiomeLoadingEvent> exceptNether() {
        return event -> Biome.Category.NETHER != event.getCategory();
    }

    public static Predicate<BiomeLoadingEvent> onlyTheEnd() {
        return event -> Biome.Category.THEEND == event.getCategory();
    }

    public static Predicate<BiomeLoadingEvent> exceptTheEnd() {
        return event -> Biome.Category.THEEND != event.getCategory();
    }

    public static Supplier<ConfiguredFeature<?, ?>> oreFeature(final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                                                               final int clustersAmount, final int oresPerCluster,
                                                               final int placementBottomOffset, final int placementTopOffset,
                                                               final int placementMaximum) {
        return oreFeature(Content.FEATURE_ORE, oreBlock, matchRule,clustersAmount, oresPerCluster, placementBottomOffset,
                placementTopOffset, placementMaximum);
    }

    //region internals

    private WorldGenManager() {
        MinecraftForge.EVENT_BUS.addListener(this::onBiomesLoaded);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomesLoaded(final BiomeLoadingEvent event) {

        final BiomeGenerationSettingsBuilder builder = event.getGeneration();

        for (final GenerationStage.Decoration stage : this._entries.keySet()) {

            final List<Supplier<ConfiguredFeature<?, ?>>> biomeFeatures = builder.getFeatures(stage);

            this._entries.getOrDefault(stage, Collections.emptyList()).stream()
                    .filter(p -> p.getKey().test(event))
                    .forEach(p -> biomeFeatures.add(p.getValue()));
        }
    }

    //endregion
}
