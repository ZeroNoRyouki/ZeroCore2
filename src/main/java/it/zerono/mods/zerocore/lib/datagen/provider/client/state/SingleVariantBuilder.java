package it.zerono.mods.zerocore.lib.datagen.provider.client.state;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.zerono.mods.zerocore.lib.datagen.provider.client.model.ModelBuilder;
import net.minecraft.Util;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.NonNullConsumer;

public class SingleVariantBuilder
        implements BlockStateGenerator {

    SingleVariantBuilder(ModelBuilder modelBuilder, Block block) {

        Preconditions.checkNotNull(modelBuilder, "Model builder must not be null");
        Preconditions.checkNotNull(block, "Block must not be null");

        this._modelBuilder = modelBuilder;
        this._block = block;
        this._modelVariants = new ModelVariantsList(8);
    }

    /**
     * Create a single model variant with the given model.
     *
     * @param model The model.
     */
    public void model(ResourceLocation model) {

        Preconditions.checkNotNull(model, "Model must not be null");

        this._modelVariants.add(Variant.variant().with(VariantProperties.MODEL, model));
    }

    /**
     * Add one or more existing model {@link Variant}s to the block state being built.
     *
     * @param variants The model {@link Variant}s.
     */
    public void variant(Variant... variants) {

        Preconditions.checkArgument(variants.length > 0, "At least one model variant must be provided");

        this._modelVariants.add(variants);
    }

    /**
     * Add one or more new model {@link Variant}s to the block state being built.
     *
     * @param variantBuilder The {@link ModelVariantBuilder} used to build the new model variants.
     */
    public void variant(NonNullConsumer<ModelVariantBuilder> variantBuilder) {

        Preconditions.checkNotNull(variantBuilder, "Variant builder must not be null");

        final var builder = new ModelVariantBuilder(this._modelVariants, this._modelBuilder);

        variantBuilder.accept(builder);

        if (!builder.isBuilt()) {
            builder.build();
        }
    }

    //region BlockStateGenerator

    @Override
    public Block getBlock() {
        return this._block;
    }

    @Override
    public JsonElement get() {
        return Util.make(new JsonObject(),
                json -> json.add("variants", this._modelVariants.apply("", new JsonObject())));
    }

    //endregion
    //region internals

    private final ModelBuilder _modelBuilder;

    private final Block _block;
    private final ModelVariantsList _modelVariants;

    //endregion
}
