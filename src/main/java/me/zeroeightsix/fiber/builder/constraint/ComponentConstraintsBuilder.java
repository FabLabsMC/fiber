package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.ComponentConstraint;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.schema.ConfigType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * A builder for component constraints.
 *
 * <p> Component constraints are satisfied only if all elements in the aggregate type satisfy the constraint.
 *
 * @param <A> the type of the array or collection to create a constraint for
 * @param <S> the type of this builder's source
 * @param <T> the type of elements processed by this builder's constraints
 */
public final class ComponentConstraintsBuilder<S, A, T, T0> extends AbstractConstraintsBuilder<S, A, List<T0>, T, T0> {
    public ComponentConstraintsBuilder(S source, Set<Constraint<? super List<T0>>> sourceConstraints, ConfigType<T, T0> type) {
        super(source, sourceConstraints, type);
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T, T0> atLeast(T min) throws RuntimeFiberException {
        super.atLeast(min);
        return this;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T, T0> atMost(T max) {
        super.atMost(max);
        return this;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T, T0> range(T min, T max, @Nullable T step) {
        super.range(min, max);
        return this;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T, T0> minLength(int min) {
        super.minLength(min);
        return this;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T, T0> maxLength(int max) {
        super.maxLength(max);
        return this;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T, T0> regex(String regexPattern) {
        super.regex(regexPattern);
        return this;
    }

    public S finishComponent() {
        this.sourceConstraints.add(new ComponentConstraint<>(newConstraints));
        return source;
    }

}
