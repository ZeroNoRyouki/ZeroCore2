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
import it.zerono.mods.zerocore.lib.world.feature.ModOreFeatureConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.BlockStateMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
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
        return new BlockMatchRuleTest(block);
    }

    public static RuleTest oreMatch(final ITag<Block> tag) {
        return new TagMatchRuleTest(tag);
    }

    public static RuleTest oreMatch(final BlockState state) {
        return new BlockStateMatchRuleTest(state);
    }

    public void addOre(final Predicate<PredicateObject> biomeMatcher, final ConfiguredFeature<?, ?> configSupplier) {
        this.add(GenerationStage.Decoration.UNDERGROUND_ORES, biomeMatcher, configSupplier);
    }

    protected AbstractWorldGenFeaturesMap() {

        this._entries = Maps.newHashMap();
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, EventPriority.HIGHEST, this::clearItems);
    }

    protected void add(final GenerationStage.Decoration stage, final Predicate<PredicateObject> biomeMatcher,
                       final ConfiguredFeature<?, ?> configSupplier) {
        this._entries.computeIfAbsent(stage, s -> Lists.newLinkedList()).add(Pair.of(biomeMatcher, configSupplier));
    }

    protected static ConfiguredFeature<?, ?> oreFeature(final Supplier<Feature<ModOreFeatureConfig>> oreFeature,
                                                        final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
                                                        final int clustersAmount, final int oresPerCluster,
                                                        final int placementBottomOffset, final int placementTopOffset,
                                                        final int placementMaximum) {
        return oreFeature.get().configured(new ModOreFeatureConfig(matchRule, oreBlock.get().defaultBlockState(), oresPerCluster))
                .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(placementBottomOffset, placementTopOffset, placementMaximum))
                        .squared()
                        .count/* repeat */(clustersAmount));
    }

    public void clearItems(final RegistryEvent.Register<Feature<?>> event) {
        this._entries.clear();
    }

    protected final Map<GenerationStage.Decoration, List<Pair<Predicate<PredicateObject>, ConfiguredFeature<?, ?>>>> _entries;
}
