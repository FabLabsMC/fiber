package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.builder.ConfigAggregateBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.schema.ConvertibleType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * An implementation of {@code ConstraintsBuilder} for aggregate constraints.
 *
 * <p>Aggregate types are those that hold multiple values, such as {@code List} or arrays.
 * The other, scalar types, such as {@code Integer} or {@code String}, are created using {@link ConstraintsBuilder}.
 *
 * @param <A> the type of {@link Constraint} this builder should output
 * @param <E> the type of the components in T
 * @see ConstraintsBuilder
 */
public class AggregateConstraintsBuilder<A, E, E0> extends ConstraintsBuilder<A, List<E0>> {

    /**
     * Creates a new aggregate constraint builder
     *  @param aggregateType the type of collection or array {@code source} holds
     * @param componentType the type of all elements in {@code aggregateType}
     * @param source        the {@code ConfigLeafBuilder} this {@code ConstraintsBuilder} originates from
     * @param constraints   the list of constraints this builder will add to
     */
    public AggregateConstraintsBuilder(ConfigAggregateBuilder<A, E, E0> source, Set<Constraint<? super List<E0>>> constraints, @Nonnull ConvertibleType<A, List<E0>> type) {
        super(source, constraints, type);
    }

    @Override
    public AggregateConstraintsBuilder<A, E, E0> atLeast(A min) throws RuntimeFiberException {
        super.atLeast(min);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E, E0> atMost(A max) {
        super.atMost(max);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E, E0> range(A min, A max, @Nullable A step) {
        super.range(min, max);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E, E0> minLength(int min) {
        super.minLength(min);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E, E0> maxLength(int max) {
        super.maxLength(max);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E, E0> regex(String regexPattern) {
        super.regex(regexPattern);
        return this;
    }

    /**
     * Creates a new {@code ComponentConstraintsBuilder}.
     *
     * <p> Component constraints are constraints that test each value in an aggregate type. By default, all tested elements must satisfy the constraint in order for the entire constraint to be satisfied.
     *
     * @return the newly created builder
     */
    public ComponentConstraintsBuilder<AggregateConstraintsBuilder<A, E, E0>, A, E, E0> component() {
        @SuppressWarnings("unchecked") ConvertibleType<E, E0> elementType = (ConvertibleType<E, E0>) this.type.getElementType();
        return new ComponentConstraintsBuilder<>(this, sourceConstraints, elementType);
    }

    @Override
    public ConfigAggregateBuilder<A, E, E0> finishConstraints() {
        return (ConfigAggregateBuilder<A, E, E0>) super.finishConstraints();
    }
}
