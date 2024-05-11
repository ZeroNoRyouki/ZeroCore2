package it.zerono.mods.zerocore.lib.datagen.provider.client.state;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.util.NonNullConsumer;
import net.neoforged.neoforge.common.util.NonNullFunction;

import java.util.List;

/**
 * A builder for a multipart block state {@link Condition}.
 *
 * <p>A root {@link Condition} builder can be used to build only one {@link Condition} (and, or, terminal).</p>
 * <p>A nested {@link Condition} builder can be used to build one or more {@link Condition}s.</p>
 */
public class ConditionBuilder {

    static Condition root(NonNullConsumer<ConditionBuilder> rootConditionBuilder) {

        final List<Condition> conditions = new ObjectArrayList<>(16);
        final var builder = new ConditionBuilder(condition -> {

            if (!conditions.isEmpty()) {
                throw new IllegalStateException("A Condition was already added by this builder");
            }

            conditions.add(condition);
        });

        rootConditionBuilder.accept(builder);

        if (conditions.isEmpty()) {
            throw new IllegalStateException("No Condition were added by this builder");
        }

        return conditions.get(0);
    }

    static List<Condition> nested(NonNullConsumer<ConditionBuilder> nestedConditionBuilder) {

        final List<Condition> conditions = new ObjectArrayList<>(16);
        final var builder = new ConditionBuilder(conditions::add);

        nestedConditionBuilder.accept(builder);

        if (conditions.isEmpty()) {
            throw new IllegalStateException("No Condition were added by this builder");
        }

        return conditions;
    }

    public void build() {

        Preconditions.checkState(null != this._condition, "No Condition to build");

        this._sink.accept(this._condition);
        this._condition = null;
    }

    /**
     * Add an AND {@link Condition} to this builder filled with the {@link Condition}s built by the nested builder.
     *
     * @param nestedConditionBuilder The {@link ConditionBuilder} to be used to build the nested conditions.
     * @return This builder.
     */
    public ConditionBuilder and(NonNullConsumer<ConditionBuilder> nestedConditionBuilder) {
        return this.composite(Condition::and, nestedConditionBuilder);
    }

    /**
     * Add an OR {@link Condition} to this builder filled with the {@link Condition}s built by the nested builder.
     *
     * @param nestedConditionBuilder The {@link ConditionBuilder} to be used to build the nested conditions.
     * @return This builder.
     */
    public ConditionBuilder or(NonNullConsumer<ConditionBuilder> nestedConditionBuilder) {
        return this.composite(Condition::or, nestedConditionBuilder);
    }

    /**
     * Add a predicate to the current terminal {@link Condition}. The predicate is fulfilled when the
     * block state property has the provided value.
     *
     * <p>If no terminal {@link Condition} is currently being build, a new one is create.</p>
     * <p>If another type of {@link Condition} is currently being build, an {@link IllegalStateException} will be thrown.</p>
     *
     * @param property The block state property that should be matched.
     * @param value The value that the property should have for this predicate to be fulfilled.
     * @return This builder.
     */
    public <T extends Comparable<T>> ConditionBuilder match(Property<T> property, T value) {

        Preconditions.checkNotNull(property, "Property must not be null");
        Preconditions.checkNotNull(value, "Value must not be null");

        this.getCurrentTerminalConditionOrThrow().term(property, value);
        return this;
    }

    /**
     * Add a predicate to the current terminal {@link Condition}. The predicate is fulfilled when the
     * block state property has one of the provided values.
     *
     * <p>If no terminal {@link Condition} is currently being build, a new one is create.</p>
     * <p>If another type of {@link Condition} is currently being build, an {@link IllegalStateException} will be thrown.</p>
     *
     * @param property The block state property that should be matched.
     * @param value One value that the property can have for this predicate to be fulfilled.
     * @param otherValues Other values that the property can have for this predicate to be fulfilled.
     * @return This builder.
     */
    @SafeVarargs
    public final <T extends Comparable<T>> ConditionBuilder match(Property<T> property, T value, T... otherValues) {

        Preconditions.checkNotNull(property, "Property must not be null");
        Preconditions.checkNotNull(value, "Value must not be null");

        this.getCurrentTerminalConditionOrThrow().term(property, value, otherValues);
        return this;
    }

    /**
     * Add a predicate to the current terminal {@link Condition}. The predicate is fulfilled when the
     * block state property DO NOT have the provided value.
     *
     * <p>If no terminal {@link Condition} is currently being build, a new one is create.</p>
     * <p>If another type of {@link Condition} is currently being build, an {@link IllegalStateException} will be thrown.</p>
     *
     * @param property The block state property that should be matched.
     * @param value The value that the property must NOT have for this predicate to be fulfilled.
     * @return This builder.
     */
    public <T extends Comparable<T>> ConditionBuilder dontMatch(Property<T> property, T value) {

        Preconditions.checkNotNull(property, "Property must not be null");
        Preconditions.checkNotNull(value, "Value must not be null");

        this.getCurrentTerminalConditionOrThrow().negatedTerm(property, value);
        return this;
    }

    /**
     * Add a predicate to the current terminal {@link Condition}. The predicate is fulfilled when the
     * block state property DO NOT have the provided values.
     *
     * <p>If no terminal {@link Condition} is currently being build, a new one is create.</p>
     * <p>If another type of {@link Condition} is currently being build, an {@link IllegalStateException} will be thrown.</p>
     *
     * @param property The block state property that should be matched.
     * @param value One value that the property must NOT have for this predicate to be fulfilled.
     * @param otherValues Other values that the property must NOT have for this predicate to be fulfilled.
     * @return This builder.
     */
    @SafeVarargs
    public final <T extends Comparable<T>> ConditionBuilder dontMatch(Property<T> property, T value, T... otherValues) {

        Preconditions.checkNotNull(property, "Property must not be null");
        Preconditions.checkNotNull(value, "Value must not be null");

        this.getCurrentTerminalConditionOrThrow().negatedTerm(property, value, otherValues);
        return this;
    }

    //region internals

    private ConditionBuilder(NonNullConsumer<Condition> conditionSink) {
        this._sink = conditionSink;
    }

    private ConditionBuilder composite(NonNullFunction<Condition[], Condition> factory,
                                       NonNullConsumer<ConditionBuilder> nestedConditionBuilder) {

        Preconditions.checkNotNull(nestedConditionBuilder);
        this._condition = factory.apply(nested(nestedConditionBuilder).toArray(Condition[]::new));
        return this;
    }

    private Condition.TerminalCondition getCurrentTerminalConditionOrThrow() {

        if (null == this._condition) {

            final var condition = Condition.condition();

            this._condition = condition;
            return condition;
        }

        if (!(this._condition instanceof Condition.TerminalCondition)) {
            throw new IllegalStateException("A non-terminal Condition is already being build");
        }

        return (Condition.TerminalCondition)this._condition;
    }

    private final NonNullConsumer<Condition> _sink;
    private Condition _condition;

    //endregion
}
