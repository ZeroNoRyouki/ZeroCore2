package it.zerono.mods.zerocore.base.multiblock.client.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Arrays;

public interface IMultiblockModelBuilder {

    static ModelResourceLocation getModelResourceLocation(ResourceLocationBuilder modelRoot, String modelName) {

        Preconditions.checkNotNull(modelRoot, "Model root must not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(modelName), "Model name must not be null or empty");

        return ModelResourceLocation.standalone(modelRoot.buildWithSuffix(modelName));
    }

    void build();

    ResourceLocationBuilder getModelRoot();

    void addBlock(int id, ModelResourceLocation originalModel, boolean hasGeneralQuads,
                  ModelResourceLocation... additionalVariants);

    default ModelResourceLocation getBlockStateResourceLocation(MultiblockPartBlock<?, ?> part) {
        return new ModelResourceLocation(BuiltInRegistries.BLOCK.getKey(part), "");
    }

    default ModelResourceLocation getModelResourceLocation(String modelName) {
        return getModelResourceLocation(this.getModelRoot(), modelName);
    }

    default void addBlock(MultiblockPartBlock<?, ?> part, boolean hasGeneralQuads, String... additionalVariants) {

        final ModelResourceLocation[] additionalModels = new ModelResourceLocation[additionalVariants.length];

        Arrays.setAll(additionalModels, idx -> this.getModelResourceLocation(additionalVariants[idx]));

        this.addBlock(part.getPartType().getByteHashCode(), this.getBlockStateResourceLocation(part), hasGeneralQuads,
                additionalModels);
    }
}
