package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.builder.ConfigAggregateBuilder;
import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
public class AggregateConstraintsBuilder<A, E> extends ConstraintsBuilder<A> {

    @Nullable
    private final Class<E> componentType;

    /**
     * Creates a new aggregate constraint builder
     *
     * @param source        the {@code ConfigValueBuilder} this {@code ConstraintsBuilder} originates from
     * @param constraints   the list of constraints this builder will add to
     * @param aggregateType the type of collection or array {@code source} holds
     * @param componentType the type of all elements in {@code aggregateType}
     */
    public AggregateConstraintsBuilder(ConfigAggregateBuilder<A, E> source, List<Constraint<? super A>> constraints, @Nonnull Class<A> aggregateType, @Nullable Class<E> componentType) {
        super(source, constraints, aggregateType);
        this.componentType = componentType;
    }

    @Override
    public AggregateConstraintsBuilder<A, E> atLeast(A min) throws RuntimeFiberException {
        super.atLeast(min);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E> atMost(A max) {
        super.atMost(max);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E> range(A min, A max) {
        super.range(min, max);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E> minLength(int min) {
        super.minLength(min);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E> maxLength(int max) {
        super.maxLength(max);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E> regex(String regexPattern) {
        super.regex(regexPattern);
        return this;
    }

    @Override
    public CompositeConstraintsBuilder<AggregateConstraintsBuilder<A, E>, A> composite(CompositeType type) {
        return new CompositeConstraintsBuilder<>(this, type, sourceConstraints, this.type);
    }

    /**
     * Creates a new {@code ComponentConstraintsBuilder}.
     *
     * <p> Component constraints are constraints that test each value in an aggregate type. By default, all tested elements must satisfy the constraint in order for the entire constraint to be satisfied.
     *
     * @return the newly created builder
     */
    public ComponentConstraintsBuilder<AggregateConstraintsBuilder<A, E>, A, E> component() {
        return new ComponentConstraintsBuilder<>(this, sourceConstraints, this.type, this.componentType);
    }

    @Override
    public ConfigAggregateBuilder<A, E> finishConstraints() {
        return (ConfigAggregateBuilder<A, E>) super.finishConstraints();
    }
}
