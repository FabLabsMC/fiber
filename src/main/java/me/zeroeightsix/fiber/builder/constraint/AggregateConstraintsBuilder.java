package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import java.util.Collection;
import java.util.List;

/**
 * An implementation of {@code ConstraintsBuilder} for aggregate constraints.
 *
 * <p>Aggregate types are those that hold multiple values, such as {@code List} or arrays.
 * The other, scalar types, such as {@code Integer} or {@code String}, are created using {@link ConstraintsBuilder}.
 *
 * @param <S> the type of this builder's source object (eg. {@code ConfigValueBuilder} or {@code ConstraintsBuilder}
 * @param <T> the type of {@link Constraint} this builder should output
 * @param <C> the type of the components in T
 * @see ConstraintsBuilder
 */
public class AggregateConstraintsBuilder<S, T, C> extends ConstraintsBuilder<S, T> {
    private final Class<C> componentType;

    /**
     * Creates a new aggregate constraint builder
     *
     * @param source        the {@code ConfigValueBuilder} this {@code ConstraintsBuilder} originates from
     * @param constraints   the list of constraints this builder will add to
     * @param aggregateType the type of collection or array {@code source} holds
     * @param componentType the type of all elements in {@code aggregateType}
     */
    public AggregateConstraintsBuilder(S source, List<Constraint<? super T>> constraints, Class<T> aggregateType, Class<C> componentType) {
        super(source, constraints, aggregateType);
        this.componentType = componentType;
    }

    @Override
    public AggregateConstraintsBuilder<S, T, C> atLeast(T min) throws RuntimeFiberException {
        super.atLeast(min);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<S, T, C> atMost(T max) {
        super.atMost(max);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<S, T, C> range(T min, T max) {
        super.range(min, max);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<S, T, C> minLength(int min) {
        super.minLength(min);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<S, T, C> maxLength(int max) {
        super.maxLength(max);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<S, T, C> regex(String regexPattern) {
        super.regex(regexPattern);
        return this;
    }

    @Override
    public CompositeConstraintsBuilder<AggregateConstraintsBuilder<S, T, C>, T> composite(CompositeType type) {
        return new CompositeConstraintsBuilder<>(this, type, sourceConstraints, this.type);
    }

    /**
     * Creates a new {@code ComponentConstraintsBuilder}.
     *
     * <p> Component constraints are constraints that test each value in an aggregate type. By default, all tested elements must satisfy the constraint in order for the entire constraint to be satisfied.
     *
     * @return the newly created builder
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ComponentConstraintsBuilder<AggregateConstraintsBuilder<S, T, C>, T, C> component() {
        if (this.type.isArray()) {
            List<Constraint<? super C[]>> sourceConstraints = (List) this.sourceConstraints;
            return (ComponentConstraintsBuilder<AggregateConstraintsBuilder<S, T, C>, T, C>) ComponentConstraintsBuilder.array(this, sourceConstraints, this.componentType);
        } else {
            List<Constraint<? super Collection<C>>> sourceConstraints = (List) this.sourceConstraints;
            return (ComponentConstraintsBuilder<AggregateConstraintsBuilder<S, T, C>, T, C>) ComponentConstraintsBuilder.collection(this, sourceConstraints, this.componentType);
        }
    }
}
