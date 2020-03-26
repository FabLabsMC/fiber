package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import java.util.List;

/**
 * A builder for {@code Constraint}s.
 *
 * <p> The constraints created by this builder consider constrained values as atomic, and do not allow the specification
 * of component-level constraints.
 * Settings with aggregate types, such as arrays and collections, should be created using {@link AggregateConstraintsBuilder}.
 *
 * @param <S> the type of this builder's source object (eg. {@code ConfigValueBuilder} or {@code ConstraintsBuilder}
 * @param <T> the type of {@link Constraint} this builder should output
 * @see AggregateConstraintsBuilder
 */
public class ConstraintsBuilder<S, T> extends AbstractConstraintsBuilder<S, T, T> {

    /**
     * Creates a new scalar constraint builder
     *
     * @param source      the {@code ConfigValueBuilder} this {@code ConstraintsBuilder} originates from
     * @param constraints the list of constraints this builder will add to
     * @param type        the class of the type of values checked by constraints built by this builder
     */
    public ConstraintsBuilder(S source, List<Constraint<? super T>> constraints, Class<T> type) {
        super(source, constraints, type);
    }

    @Override
    public ConstraintsBuilder<S, T> atLeast(T min) throws RuntimeFiberException {
        super.atLeast(min);
        return this;
    }

    @Override
    public ConstraintsBuilder<S, T> atMost(T max) {
        super.atMost(max);
        return this;
    }

    @Override
    public ConstraintsBuilder<S, T> range(T min, T max) {
        super.range(min, max);
        return this;
    }

    @Override
    public ConstraintsBuilder<S, T> minLength(int min) {
        super.minLength(min);
        return this;
    }

    @Override
    public ConstraintsBuilder<S, T> maxLength(int max) {
        super.maxLength(max);
        return this;
    }

    @Override
    public ConstraintsBuilder<S, T> regex(String regexPattern) {
        super.regex(regexPattern);
        return this;
    }

    /**
     * Creates a new {@code CompositeConstraintBuilder} from this {@code ConstraintsBuilder}.
     *
     * <p> Composite constraints are constraints that have 0 or more child constraints.
     *
     * <p> Whether or not a value satisfies a composite constraint is specified by the composite's {@link CompositeType}.
     *
     * @param type the type of composite to create
     * @return the newly created builder
     */
    public CompositeConstraintsBuilder<? extends ConstraintsBuilder<S, T>, T> composite(CompositeType type) {
        return new CompositeConstraintsBuilder<>(this, type, sourceConstraints, this.type);
    }

    /**
     * Finishes building constraints.
     *
     * <p> As a result of this call, the built constraints will be added to the source {@code ConfigValue} builder.
     *
     * @return the source builder
     */
    public S finish() {
        sourceConstraints.addAll(newConstraints);
        return source;
    }

}
