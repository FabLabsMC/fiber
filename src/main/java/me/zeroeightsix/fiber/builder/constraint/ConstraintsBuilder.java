package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.builder.ConfigLeafBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.schema.ConfigType;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * A builder for {@code Constraint}s.
 *
 * <p> The constraints created by this builder consider constrained values as atomic, and do not allow the specification
 * of component-level constraints.
 * Settings with aggregate types, such as arrays and collections, should be created using {@link AggregateConstraintsBuilder}.
 *
 * @param <T> the type of {@link Constraint} this builder should output
 * @see AggregateConstraintsBuilder
 */
public class ConstraintsBuilder<T, T0> extends AbstractConstraintsBuilder<ConfigLeafBuilder<T, T0>, T, T0, T, T0> {

    /**
     * Creates a new scalar constraint builder
     *
     * @param source      the {@code ConfigLeafBuilder} this {@code ConstraintsBuilder} originates from
     * @param constraints the list of constraints this builder will add to
     * @param type        the class of the type of values checked by constraints built by this builder
     */
    public ConstraintsBuilder(ConfigLeafBuilder<T, T0> source, Set<Constraint<? super T0>> constraints, ConfigType<T, T0> type) {
        super(source, constraints, type);
    }

    @Override
    public ConstraintsBuilder<T, T0> atLeast(T min) throws RuntimeFiberException {
        super.atLeast(min);
        return this;
    }

    @Override
    public ConstraintsBuilder<T, T0> atMost(T max) {
        super.atMost(max);
        return this;
    }

    @Override
    public ConstraintsBuilder<T, T0> range(T min, T max, @Nullable T step) {
        super.range(min, max);
        return this;
    }

    @Override
    public ConstraintsBuilder<T, T0> minLength(int min) {
        super.minLength(min);
        return this;
    }

    @Override
    public ConstraintsBuilder<T, T0> maxLength(int max) {
        super.maxLength(max);
        return this;
    }

    @Override
    public ConstraintsBuilder<T, T0> regex(String regexPattern) {
        super.regex(regexPattern);
        return this;
    }

    /**
     * Finishes building constraints.
     *
     * <p> As a result of this call, the built constraints will be added to the source {@code ConfigLeaf} builder.
     *
     * @return the source builder
     */
    public ConfigLeafBuilder<T, T0> finishConstraints() {
        sourceConstraints.addAll(newConstraints);
        return source;
    }

}
