package it.zerono.mods.zerocore.lib.datagen.provider.client.state;

import com.google.common.base.Preconditions;
import it.zerono.mods.zerocore.lib.datagen.provider.client.model.ModelBuilder;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.NonNullFunction;

/**
 * A builder for a block state variant.
 *
 * <p>After one variant is built, then builder can be reused to build another.</p>
 */
public class ModelVariantBuilder {

    ModelVariantBuilder(ModelVariantsList sink, ModelBuilder modelBuilder) {

        this._sink = sink;
        this._modelBuilder = modelBuilder;
        this._variant = Variant.variant();
        this._built = false;
    }

    /**
     * Build the current variant and reset this builder so a new variant could be built afterward.
     *
     * @return This builder.
     */
    public ModelVariantBuilder build() {

        this._sink.add(this._variant);
        this._variant = Variant.variant();
        this._built = true;
        return this;
    }

    boolean isBuilt() {
        return this._built;
    }

    /**
     * Add the properties of the provided {@link Variant} to the one currently being built.
     *
     * @param variant The existing {@link Variant} to merge in.
     * @return This builder.
     */
    public ModelVariantBuilder merge(Variant variant) {

        Preconditions.checkNotNull(variant, "Variant must not be null");

        this._variant = Variant.merge(this._variant, variant);
        this._built = false;
        return this;
    }

    /**
     * Sets the model for the variant currently being built.
     *
     * @param model The model.
     * @return This builder.
     */
    public ModelVariantBuilder model(ResourceLocation model) {

        Preconditions.checkNotNull(model, "Model must not be null");

        this._variant.with(VariantProperties.MODEL, model);
        this._built = false;
        return this;
    }

    /**
     * Sets the model for the variant currently being built.
     *
     * @param builder A builder used to create or select the model.
     * @return This builder.
     */
    public ModelVariantBuilder model(NonNullFunction<ModelBuilder, ResourceLocation> builder) {

        Preconditions.checkNotNull(builder, "Builder must not be null");

        final var blockModel = builder.apply(this._modelBuilder);

        return this.model(blockModel);
    }

    /**
     * Sets the X rotation for the variant currently being built.
     * The default rotation is 0.
     *
     * @param rotation The rotation on the X axis.
     * @return This builder.
     */
    public ModelVariantBuilder xRotation(VariantProperties.Rotation rotation) {

        Preconditions.checkNotNull(rotation, "Rotation must not be null");

        this._variant.with(VariantProperties.X_ROT, rotation);
        this._built = false;
        return this;
    }

    /**
     * Sets the X rotation for the variant currently being built.
     * The default rotation is 0.
     *
     * @param rotation The rotation on the X axis. Valid values are 0, 90, 180 or 270.
     * @return This builder.
     */
    public ModelVariantBuilder xRotation(int rotation) {

        Preconditions.checkArgument(0 == rotation || 90 == rotation || 180 == rotation || 270 == rotation,
                "Rotation can only be 0, 90, 180 or 270");

        return this.xRotation(VariantProperties.Rotation.valueOf("R" + rotation));
    }

    /**
     * Sets the Y rotation for the variant currently being built.
     * The default rotation is 0.
     *
     * @param rotation The rotation on the Y axis.
     * @return This builder.
     */
    public ModelVariantBuilder yRotation(VariantProperties.Rotation rotation) {

        Preconditions.checkNotNull(rotation, "Rotation must not be null");

        this._variant.with(VariantProperties.Y_ROT, rotation);
        this._built = false;
        return this;
    }

    /**
     * Sets the Y rotation for the variant currently being built.
     * The default rotation is 0.
     *
     * @param rotation The rotation on the Y axis. Valid values are 0, 90, 180 or 270.
     * @return This builder.
     */
    public ModelVariantBuilder yRotation(int rotation) {

        Preconditions.checkArgument(0 == rotation || 90 == rotation || 180 == rotation || 270 == rotation,
                "Rotation can only be 0, 90, 180 or 270");

        return this.yRotation(VariantProperties.Rotation.valueOf("R" + rotation));
    }

    /**
     * Sets the uv lock for the variant currently being built to true.
     * The default is false.
     *
     * @return This builder.
     */
    public ModelVariantBuilder uvLock() {
        return this.uvLock(true);
    }

    /**
     * Sets the uv lock for the variant currently being built.
     * The default is false.
     *
     * @return This builder.
     */
    public ModelVariantBuilder uvLock(boolean enabled) {

        this._variant.with(VariantProperties.UV_LOCK, enabled);
        this._built = false;
        return this;
    }

    /**
     * Sets the probability of the variant for being used in the game.
     * If more than one variant is used, the probability is calculated by dividing the individual
     * variant's weight by the sum of the weights of all variants.
     * The default probability is 1 (100%).
     *
     * @param weight The probability. Must be >= 1.
     * @return This builder.
     */
    public ModelVariantBuilder weight(int weight) {

        Preconditions.checkArgument(weight >= 1, "Weight must be greater than or equal to one.");
        this._variant.with(VariantProperties.WEIGHT, weight);
        this._built = false;
        return this;
    }

    //region internals

    private final ModelVariantsList _sink;
    private final ModelBuilder _modelBuilder;
    private Variant _variant;
    private boolean _built;

    //endregion
}
