package it.zerono.mods.zerocore.lib.datagen.provider.client.state;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.lib.datagen.provider.client.model.ModelBuilder;
import net.minecraft.Util;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MultiPartBuilder
        implements BlockStateGenerator {

    MultiPartBuilder(ModelBuilder modelBuilder, Block block) {

        Preconditions.checkNotNull(modelBuilder, "Model builder must not be null");
        Preconditions.checkNotNull(block, "Block must not be null");

        this._modelBuilder = modelBuilder;
        this._block = block;

        this._parts = new ObjectArrayList<>(16);
    }

    /**
     * Add one or more existing model {@link Variant}s with no associated {@link Condition}.
     *
     * @param variants The model {@link Variant}s.
     * @return This builder.
     */
    public MultiPartBuilder part(Variant... variants) {

        Preconditions.checkArgument(variants.length > 0, "At least one variant must be provided");

        final var part = new Part();

        part.add(variants);
        this._parts.add(part);
        return this;
    }

    /**
     * Add one or more new model {@link Variant}s with no associated {@link Condition}.
     *
     * @param builder The {@link ModelVariantBuilder} used to build the new model variants.
     * @return This builder.
     */
    public MultiPartBuilder part(Consumer<@NotNull ModelVariantBuilder> builder) {

        Preconditions.checkNotNull(builder, "Builder must not be null");

        final var part = new Part();

        ModelVariantBuilder.build(part, this._modelBuilder, builder);
        this._parts.add(part);
        return this;
    }

    /**
     * Add one or more existing model {@link Variant}s with an existing {@link Condition}.
     *
     * @param condition The model {@link Condition}.
     * @param variants The model {@link Variant}s.
     * @return This builder.
     */
    public MultiPartBuilder part(Condition condition, Variant... variants) {

        Preconditions.checkNotNull(condition, "Condition must not be null");
        Preconditions.checkArgument(variants.length > 0, "At least one variant must be provided");

        final var part = new ConditionalPart(condition);

        part.add(variants);
        this._parts.add(part);
        return this;
    }

    /**
     * Add one or more new model {@link Variant}s with an existing {@link Condition}.
     *
     * @param condition The model {@link Condition}.
     * @param builder The {@link ModelVariantBuilder} used to build the new model variants.
     * @return This builder.
     */
    public MultiPartBuilder part(Condition condition, Consumer<@NotNull ModelVariantBuilder> builder) {

        Preconditions.checkNotNull(condition, "Condition must not be null");
        Preconditions.checkNotNull(builder, "Builder must not be null");

        final var part = new ConditionalPart(condition);

        ModelVariantBuilder.build(part, this._modelBuilder, builder);
        this._parts.add(part);
        return this;
    }

    /**
     * Add one or more existing model {@link Variant}s with a new {@link Condition}.
     *
     * @param builder The {@link ConditionBuilder} used to build the new model condition.
     * @param variants The model {@link Variant}s.
     * @return This builder.
     */
    public MultiPartBuilder part(Consumer<@NotNull ConditionBuilder> builder, Variant... variants) {

        Preconditions.checkNotNull(builder, "Builder must not be null");
        Preconditions.checkArgument(variants.length > 0, "At least one variant must be provided");

        final var part = new ConditionalPart(ConditionBuilder.root(builder));

        part.add(variants);
        this._parts.add(part);
        return this;
    }

    /**
     * Add one or more new model {@link Variant}s with a new {@link Condition} that must match the provided property
     * and value.
     *
     * @param property The block state property that should be matched.
     * @param value The value that the property should have for this predicate to be fulfilled.
     * @param builder The {@link ModelVariantBuilder} used to build the new model variants.
     * @return This builder.
     */
    public <T extends Comparable<T>> MultiPartBuilder part(Property<T> property, T value,
                                                           Consumer<@NotNull ModelVariantBuilder> builder) {

        Preconditions.checkNotNull(property, "Property must not be null");
        Preconditions.checkNotNull(value, "Value must not be null");
        Preconditions.checkNotNull(builder, "Builder must not be null");

        final var part = new ConditionalPart(Condition.condition().term(property, value));

        ModelVariantBuilder.build(part, this._modelBuilder, builder);
        this._parts.add(part);
        return this;
    }

    /**
     * Add one or more existing model {@link Variant}s with a new {@link Condition}.
     *
     * @param property The block state property that should be matched.
     * @param value The value that the property should have for this predicate to be fulfilled.
     * @param variants The model {@link Variant}s.
     * @return This builder.
     */
    public <T extends Comparable<T>> MultiPartBuilder part(Property<T> property, T value, Variant... variants) {

        Preconditions.checkNotNull(property, "Property must not be null");
        Preconditions.checkNotNull(value, "Value must not be null");
        Preconditions.checkArgument(variants.length > 0, "At least one variant must be provided");

        final var part = new ConditionalPart(Condition.condition().term(property, value));

        part.add(variants);
        this._parts.add(part);
        return this;
    }

    /**
     * Add one or more new model {@link Variant}s with a new {@link Condition}.
     *
     * @param conditionBuilder The {@link ConditionBuilder} used to build the new model condition.
     * @param builder The {@link ModelVariantBuilder} used to build the new model variants.
     * @return This builder.
     */
    public MultiPartBuilder part(Consumer<@NotNull ConditionBuilder> conditionBuilder,
                                 Consumer<@NotNull ModelVariantBuilder> builder) {

        Preconditions.checkNotNull(conditionBuilder, "Condition builder must not be null");
        Preconditions.checkNotNull(builder, "Variant builder must not be null");

        final var part = new ConditionalPart(ConditionBuilder.root(conditionBuilder));

        ModelVariantBuilder.build(part, this._modelBuilder, builder);
        this._parts.add(part);
        return this;
    }

    //region BlockStateGenerator

    @Override
    public Block getBlock() {
        return this._block;
    }

    @Override
    public JsonElement get() {

        final var stateDefinition = this.getBlock().getStateDefinition();

        this._parts.forEach((part) -> part.validate(stateDefinition));

        final var parts = new JsonArray();

        this._parts.stream()
                .map(Part::get)
                .forEach(parts::add);

        return Util.make(new JsonObject(), json -> json.add("multipart", parts));
    }

    //endregion
    //region internals

    private static class Part
            extends ModelVariantsList
            implements Supplier<@NotNull JsonElement> {

        public Part() {
            super(16);
        }

        public void validate(StateDefinition<?, ?> states) {
        }

        //region NonNullSupplier<JsonElement>

        @Override
        public @NotNull JsonElement get() {
            return this.apply("apply", new JsonObject());
        }

        //endregion
    }

    private static class ConditionalPart
            extends Part {

        public ConditionalPart(Condition condition) {
            this._condition = condition;
        }

        @Override
        public void validate(StateDefinition<?, ?> states) {
            this._condition.validate(states);
        }

        @Override
        public @NotNull JsonObject apply(@NotNull String name, @NotNull JsonObject parent) {

            super.apply(name, parent);
            parent.add("when", this._condition.get());
            return parent;
        }

        //region internals

        private final Condition _condition;

        //endregion
    }

    private final ModelBuilder _modelBuilder;
    private final Block _block;
    private final List<Part> _parts;

    //endregion
}
