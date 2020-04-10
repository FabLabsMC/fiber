package me.zeroeightsix.fiber.api.builder.constraint;

import me.zeroeightsix.fiber.api.builder.ConfigAggregateBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.ConstraintType;
import me.zeroeightsix.fiber.constraint.ValuedConstraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * A builder for component constraints.
 *
 * <p> Component constraints are satisfied only if all elements in the aggregate type satisfy the constraint.
 *
 * @param <A> the type of the array or collection to create a constraint for
 * @param <S> the type of this builder's source
 * @param <T> the type of elements processed by this builder's constraints
 */
public final class ComponentConstraintsBuilder<S, A, T> extends AbstractConstraintsBuilder<S, A, T> {
    @Nullable
    private final Class<A> aggregateType;

    public ComponentConstraintsBuilder(S source, List<Constraint<? super A>> sourceConstraints, @Nullable Class<A> aggregateType, @Nullable Class<T> componentType) {
        super(source, sourceConstraints, componentType);
        this.aggregateType = aggregateType;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T> atLeast(T min) throws RuntimeFiberException {
        super.atLeast(min);
        return this;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T> atMost(T max) {
        super.atMost(max);
        return this;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T> range(T min, T max) {
        super.range(min, max);
        return this;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T> minLength(int min) {
        super.minLength(min);
        return this;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T> maxLength(int max) {
        super.maxLength(max);
        return this;
    }

    @Override
    public ComponentConstraintsBuilder<S, A, T> regex(String regexPattern) {
        super.regex(regexPattern);
        return this;
    }

    public S finishComponent() {
        this.sourceConstraints.add(new ComponentConstraint<>(newConstraints, this.aggregateType));
        return source;
    }

    /**
     * A component constraints is satisfied only if all elements in the aggregate type it checks satisfy the constraint.
     *
     * @param <A> the type of aggregate this constraint checks
     * @param <T> the type of elements {@code <A>} holds
     */
    public static class ComponentConstraint<A, T> extends ValuedConstraint<List<Constraint<? super T>>, A> {
        private final BiPredicate<Constraint<? super T>, A> allMatch;

        public ComponentConstraint(List<Constraint<? super T>> constraints, Class<A> type) {
            super(ConstraintType.COMPONENTS_MATCH, constraints);
            this.allMatch = getAggregateMatcher(type);
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        private BiPredicate<Constraint<? super T>, A> getAggregateMatcher(@Nullable Class<A> type) {
            BiPredicate<Constraint<? super T>, A> allMatch;
            if (type == null) {
                // deferred type resolution
                allMatch = (constraint, value) -> getAggregateMatcher((Class<A>) value.getClass()).test(constraint, value);
            } else if (type.isArray()) {
                allMatch = (constraint, value) -> {
                    // Because arrays are reified and may contain primitives, we cannot use generics, and no iterator either.
                    for (int i = 0; i < Array.getLength(value); i++) {
                        T t = (T) Array.get(value, i);
                        if (!constraint.test(t)) {
                            return false;
                        }
                    }
                    return true;
                };
            } else if (Collection.class.isAssignableFrom(type)) {
                allMatch = (constraint, value) -> {
                    for (T t : ((Collection<T>) value)) {
                        if (!constraint.test(t)) {
                            return false;
                        }
                    }
                    return true;
                };
            } else {
                assert !ConfigAggregateBuilder.isAggregate(type);
                throw new RuntimeFiberException(type + " is not an aggregate type not have a known length or size");
            }
            return allMatch;
        }

        @Override
        public boolean test(A value) {
            for (Constraint<? super T> constraint : this.getValue()) {
                if (!allMatch.test(constraint, value)) {
                    return false;
                }
            }
            return true;
        }
    }
}
