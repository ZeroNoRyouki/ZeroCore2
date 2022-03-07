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

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
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

    public static RuleTest oreMatch(final TagKey<Block> tag) {
        return new TagMatchTest(tag);
    }

    public static RuleTest oreMatch(final BlockState state) {
        return new BlockStateMatchTest(state);
    }

    public void addOreVein(final Predicate<PredicateObject> biomeMatcher, final OreGenRegisteredFeature feature) {
        this.add(GenerationStep.Decoration.UNDERGROUND_ORES, biomeMatcher, feature);
    }

    protected AbstractWorldGenFeaturesMap() {

        this._entries = new Object2ObjectArrayMap<>(16);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, EventPriority.HIGHEST, this::clearItems);
    }

    protected void add(final GenerationStep.Decoration stage, final Predicate<PredicateObject> biomeMatcher,
                       final Supplier<Holder<PlacedFeature>> featureSupplier) {
        this._entries.computeIfAbsent(stage, s -> new ObjectArrayList<>(8)).add(Pair.of(biomeMatcher, featureSupplier));
    }

    public void clearItems(final RegistryEvent.Register<Feature<?>> event) {
        this._entries.clear();
    }

    //region internals

    protected final Map<GenerationStep.Decoration, List<Pair<Predicate<PredicateObject>, Supplier<Holder<PlacedFeature>>>>> _entries;

    //endregion
}
