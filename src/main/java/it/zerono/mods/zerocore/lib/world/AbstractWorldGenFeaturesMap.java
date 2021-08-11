/*
 *
 * AbstractWorldGenFeaturesMap.java
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class AbstractWorldGenFeaturesMap<PredicateObject> {

    public static RuleTest oreMatch(final Block block) {
        return new BlockMatchTest(block);
    }

    public static RuleTest oreMatch(final Tag<Block> tag) {
        return new TagMatchTest(tag);
    }

    public static RuleTest oreMatch(final BlockState state) {
        return new BlockStateMatchTest(state);
    }

    public void addOre(final Predicate<PredicateObject> biomeMatcher, final ConfiguredFeature<?, ?> configSupplier) {
        this.add(GenerationStep.Decoration.UNDERGROUND_ORES, biomeMatcher, configSupplier);
    }

    protected AbstractWorldGenFeaturesMap() {

        this._entries = Maps.newHashMap();
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, EventPriority.HIGHEST, this::clearItems);
    }

    protected void add(final GenerationStep.Decoration stage, final Predicate<PredicateObject> biomeMatcher,
                       final ConfiguredFeature<?, ?> configSupplier) {
        this._entries.computeIfAbsent(stage, s -> Lists.newLinkedList()).add(Pair.of(biomeMatcher, configSupplier));
    }

    protected static ConfiguredFeature<?, ?> oreFeature(final Supplier<Feature<OreConfiguration>> oreFeature,
                                                        final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                                                        final int clustersAmount, final int oresPerCluster,
                                                        final int placementBottomOffset, final int placementTopOffset,
                                                        final int placementMaximum) {
        return oreFeature.get().configured(new OreConfiguration(matchRule, oreBlock.get().defaultBlockState(), oresPerCluster))
                .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(
                        UniformHeight.of(VerticalAnchor.aboveBottom(placementBottomOffset), VerticalAnchor.absolute(placementMaximum - placementTopOffset))))
                        .squared()
                        .count(clustersAmount));
    }

    public void clearItems(final RegistryEvent.Register<Feature<?>> event) {
        this._entries.clear();
    }

    protected final Map<GenerationStep.Decoration, List<Pair<Predicate<PredicateObject>, ConfiguredFeature<?, ?>>>> _entries;
}
