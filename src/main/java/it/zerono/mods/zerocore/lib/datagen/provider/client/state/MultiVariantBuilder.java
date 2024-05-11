package it.zerono.mods.zerocore.lib.datagen.provider.client.state;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.zerono.mods.zerocore.lib.datagen.provider.client.model.ModelBuilder;
import it.zerono.mods.zerocore.lib.functional.NonNullBiConsumer;
import net.minecraft.Util;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.util.NonNullConsumer;

import java.util.*;

public class MultiVariantBuilder
        implements BlockStateGenerator {

    MultiVariantBuilder(ModelBuilder modelBuilder, Block block) {

        Preconditions.checkNotNull(modelBuilder, "Model builder must not be null");
        Preconditions.checkNotNull(block, "Block must not be null");

        this._modelBuilder = modelBuilder;
        this._block = block;

        this._properties = new TreeSet<>(Comparator.comparing(Property::getName));
        this._properties.addAll(block.getStateDefinition().getProperties());
        this._selectors = new Object2ObjectArrayMap<>(64);
    }

    MultiVariantBuilder(ModelBuilder modelBuilder, Block block, Set<Property<?>> ignoredProperties) {

        this(modelBuilder, block);

        Preconditions.checkNotNull(ignoredProperties, "Ignored properties must not be null");

        this._properties.removeAll(ignoredProperties);
    }

    /**
     * Add a new block state properties selector to this builder.
     *
     * @param builder A builder used to create the new selector.
     * @return This builder.
     */
    public MultiVariantBuilder selector(NonNullConsumer<SelectorBuilder> builder) {

        Preconditions.checkNotNull(builder, "Builder must not be null");

        final var selectorBuilder = new SelectorBuilder(this._block.defaultBlockState(), this._properties, this._modelBuilder);

        builder.accept(selectorBuilder);
        selectorBuilder.build(this::addSelector);
        return this;
    }

    /**
     * Add a new block state properties selector to this builder that contains a single property.
     *
     * @param property The block state property.
     * @param value The value of the property.
     * @param variants The model {@link Variant}s for the new selector.
     * @return This builder.
     */
    public <T extends Comparable<T>, V extends T> MultiVariantBuilder selector(Property<T> property, V value,
                                                                               Variant... variants) {

        Preconditions.checkNotNull(property, "Property must not be null");
        Preconditions.checkNotNull(value, "Value must not be null");
        Preconditions.checkArgument(variants.length > 0, "At least one variant must be provided");

        return this.selector(selector -> selector
                .state(property, value)
                .variant(variants));
    }

    /**
     * Add a new block state properties selector to this builder that contains a single property.
     *
     * @param property The block state property.
     * @param value The value of the property.
     * @param builder The {@link ModelVariantBuilder} used to build the model variants for the new selector.
     * @return This builder.
     */
    public <T extends Comparable<T>, V extends T> MultiVariantBuilder selector(Property<T> property, V value,
                                                                               NonNullConsumer<ModelVariantBuilder> builder) {

        Preconditions.checkNotNull(property, "Property must not be null");
        Preconditions.checkNotNull(value, "Value must not be null");
        Preconditions.checkNotNull(builder, "The variant builder must not be null");

        return this.selector(selector -> selector
                .state(property, value)
                .variant(builder));
    }

    /**
     * Add one or more model {@link Variant}s for each block state.
     *
     * @param builder A builder used to create the model {@link Variant}s for each {@link BlockState}s.
     */
    public void all(NonNullBiConsumer<BlockState, ModelVariantBuilder> builder) {

        Preconditions.checkNotNull(builder, "Builder must not be null");

        final var possibleStates = this._block.getStateDefinition().getPossibleStates();
        final var states = new ObjectArraySet<BlockState>(possibleStates.size());

        for (final var state : possibleStates) {

            if (states.stream().noneMatch(seenState -> this.matchValidProperties(seenState, state))) {
                states.add(state);
            }
        }

        for (final var state : states) {

            final var variants = new ModelVariantsList(8);

            ModelVariantBuilder.build(variants, this._modelBuilder, vb -> builder.accept(state, vb));
            this._selectors.put(state, variants);
        }
    }

    //region BlockStateGenerator

    @Override
    public Block getBlock() {
        return this._block;
    }

    @Override
    public JsonElement get() {

        final var defined = this._selectors.keySet();
        final var missing = new LinkedList<>(this._block.getStateDefinition().getPossibleStates());

        // remove the states explicitly defined
        missing.removeAll(defined);

        // remove any states that match a defined state except for one or more ignored property
        if (!missing.isEmpty()) {

            for (final var definedState : defined) {

                missing.removeIf(missingState -> this.matchValidProperties(missingState, definedState));

                if (missing.isEmpty()) {
                    break;
                }
            }
        }

        // still missing some states?
        Preconditions.checkState(missing.isEmpty(), "The following block states for block %s were not defined: %s",
                this._block, missing);

        // serialize...

        final var variants = new JsonObject();

        for (final var entry : this._selectors.entrySet()) {

            final var name = this.getSelectorsString(entry.getKey());

            entry.getValue().apply(name, variants);
        }

        return Util.make(new JsonObject(), json -> json.add("variants", variants));
    }

    //endregion
    //region Selector builder

    public static class SelectorBuilder {

        SelectorBuilder(BlockState defaultState, Set<Property<?>> validProperties, ModelBuilder modelBuilder) {

            this._validProperties = validProperties;
            this._modelVariants = new ModelVariantsList(4);
            this._modelBuilder = modelBuilder;
            this._state = defaultState;
        }

        /**
         * Change the value of a {@link Property} of the selector being built.
         *
         * @param property The {@link Property} whole value is to be changed.
         * @param value The new value for the {@link Property}.
         * @return This builder.
         */
        public <T extends Comparable<T>, V extends T> SelectorBuilder state(Property<T> property, V value) {

            if (!this._validProperties.contains(property)) {
                throw new IllegalArgumentException(String.format("The provided property is invalid for the block or was ignored: %s", property));
            }

            this._state = this._state.setValue(property, value);
            return this;
        }

        /**
         * Add one or more existing model {@link Variant}s to the selector being built.
         *
         * @param variants The model {@link Variant}s.
         * @return This builder.
         */
        public SelectorBuilder variant(Variant... variants) {

            this._modelVariants.add(variants);
            return this;
        }

        /**
         * Add one or more new model {@link Variant}s to the selector being built.
         *
         * <p>If you are building a single variant, you can skip calling {@link ModelVariantBuilder#build()} build} on
         * the variant builder. If you are building multiple variants, call {@link ModelVariantBuilder#build()} build}
         * on the variant builder every time a variant is completed.</p>
         *
         * @param builder The {@link ModelVariantBuilder} used to build the new model variants.
         * @return This builder.
         */
        public SelectorBuilder variant(NonNullConsumer<ModelVariantBuilder> builder) {

            ModelVariantBuilder.build(this._modelVariants, this._modelBuilder, builder);
            return this;
        }

        protected void build(NonNullBiConsumer<BlockState, ModelVariantsList> sink) {
            sink.accept(this._state, this._modelVariants);
        }

        //region internals

        private final Set<Property<?>> _validProperties;
        private final ModelVariantsList _modelVariants;
        private final ModelBuilder _modelBuilder;
        private BlockState _state;

        //endregion
    }

    //endregion
    //region internals

    private void addSelector(BlockState state, ModelVariantsList variants) {

        if (null != this._selectors.put(state, variants)) {
            throw new IllegalStateException("Added duplicate block state definition for " + state.getBlock());
        }
    }

    private String getSelectorsString(BlockState state) {

        final var builder = new StringBuilder();

        for (final var property : this._properties) {

            if (builder.length() > 0) {
                builder.append(',');
            }

            builder.append(property.getName())
                    .append('=')
                    .append(((Property)property).getName(state.getValue(property)));
        }

        return builder.toString();
    }

    /**
     * Return {@code true} if the valid properties, and only them, in both block states match.
     *
     * @param state1 The first block state to check.
     * @param state2 The second block state to check.
     * @return {@code true} if the states matches, {@code false} otherwise.
     */
    private boolean matchValidProperties(BlockState state1, BlockState state2) {

        for (final var property : this._properties) {

            if (state1.getValue(property) != state2.getValue(property)) {
                return false;
            }
        }

        return true;
    }

    private final ModelBuilder _modelBuilder;
    private final Block _block;
    private final SortedSet<Property<?>> _properties;
    private final Map<BlockState, ModelVariantsList> _selectors;

    //endregion
}
