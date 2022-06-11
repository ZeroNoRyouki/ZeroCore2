//package it.zerono.mods.zerocore.lib.world;
//
//import com.google.common.base.Preconditions;
//import com.google.common.base.Strings;
//import it.zerono.mods.zerocore.internal.gamecontent.Content;
//import it.zerono.mods.zerocore.lib.block.ModBlock;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.level.levelgen.VerticalAnchor;
//import net.minecraft.world.level.levelgen.feature.Feature;
//import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
//import net.minecraft.world.level.levelgen.placement.CountPlacement;
//import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
//import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
//import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
//import net.minecraftforge.common.util.NonNullFunction;
//
//import java.util.function.Supplier;
//
//public class OreGenRegisteredFeature
//    extends AbstractWorldGenRegisteredFeature<OreGenRegisteredFeature, OreConfiguration, Feature<OreConfiguration>> {
//
//    public static OreGenRegisteredFeature generation(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
//                                                     final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
//                                                     final int oresPerVein) {
//
//        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
//        return new OreGenRegisteredFeature(name + "_gen", idFactory, Content.FEATURE_ORE, oreBlock, matchRule, oresPerVein);
//    }
//
//    public static OreGenRegisteredFeature regeneration(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
//                                                       final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
//                                                       final int oresPerVein) {
//
//        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
//        return new OreGenRegisteredFeature(name + "_regen", idFactory, Content.FEATURE_ORE_REGEN, oreBlock, matchRule, oresPerVein);
//    }
//
//    public OreGenRegisteredFeature standardVein(final int veinsPerChunk, final int minY, final int maxY) {
//        return this.placement(CountPlacement.of(veinsPerChunk), InSquarePlacement.spread(),
//                HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(minY), VerticalAnchor.absolute(maxY)));
//    }
//
//    public OreGenRegisteredFeature deepVein(final int veinsPerChunk) {
//        return this.placement(CountPlacement.of(veinsPerChunk), InSquarePlacement.spread(),
//                HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-48)));
//    }
//
//    //region internals
//
//    protected OreGenRegisteredFeature(final String name, final NonNullFunction<String, ResourceLocation> idFactory,
//                                      final Supplier<Feature<OreConfiguration>> featureSupplier,
//                                      final Supplier<ModBlock> oreBlock, final RuleTest matchRule,
//                                      final int oresPerCluster) {
//        super(name, idFactory, featureSupplier, () -> new OreConfiguration(matchRule, oreBlock.get().defaultBlockState(), oresPerCluster));
//    }
//
//    //endregion
//}
