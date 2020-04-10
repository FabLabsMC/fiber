package me.zeroeightsix.fiber.api.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.ConstraintType;
import me.zeroeightsix.fiber.constraint.ValuedConstraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import java.util.List;

/**
 * A composite constraint builder.
 *
 * <p> Composite constraints are constraints that have 0 or more child constraints.
 *
 * <p> Whether or not a value satisfies a composite constraint is specified by the composite's {@link CompositeType}.
 *
 * @param <S> the type of this builder's source object
 * @param <T> the type of aggregate type this builder will check
 */
public final class CompositeConstraintsBuilder<S, T> extends AbstractConstraintsBuilder<S, T, T> {

    private final CompositeType compositeType;

    public CompositeConstraintsBuilder(S source, CompositeType compositeType, List<Constraint<? super T>> sourceConstraints, Class<T> type) {
        super(source, sourceConstraints, type);
        this.compositeType = compositeType;
    }

    @Override
    public CompositeConstraintsBuilder<S, T> atLeast(T min) throws RuntimeFiberException {
        super.atLeast(min);
        return this;
    }

    @Override
    public CompositeConstraintsBuilder<S, T> atMost(T max) {
        super.atMost(max);
        return this;
    }

    @Override
    public CompositeConstraintsBuilder<S, T> range(T min, T max) {
        super.range(min, max);
        return this;
    }

    @Override
    public CompositeConstraintsBuilder<S, T> minLength(int min) {
        super.minLength(min);
        return this;
    }

    @Override
    public CompositeConstraintsBuilder<S, T> maxLength(int max) {
        super.maxLength(max);
        return this;
    }

    @Override
    public CompositeConstraintsBuilder<S, T> regex(String regexPattern) {
        super.regex(regexPattern);
        return this;
    }

    public S finishComposite() {
        this.sourceConstraints.add(createConstraint(newConstraints));
        return source;
    }

    private AbstractCompositeConstraint<T> createConstraint(List<Constraint<? super T>> constraints) {
        switch (compositeType) {
            case OR:
                return new OrCompositeConstraint<>(constraints);
            case AND:
                return new AndCompositeConstraint<>(constraints);
            case INVERT:
                return new InvertCompositeConstraint<>(constraints);
            default:
                throw new AssertionError();
        }
    }

    public static abstract class AbstractCompositeConstraint<T> extends ValuedConstraint<String, T> {

        public final List<Constraint<? super T>> constraints;

        public AbstractCompositeConstraint(CompositeType type, List<Constraint<? super T>> constraints) {
            super(ConstraintType.COMPOSITE, type.getName());
            this.constraints = constraints;
        }

    }

    private static final class AndCompositeConstraint<T> extends AbstractCompositeConstraint<T> {

        public AndCompositeConstraint(List<Constraint<? super T>> constraints) {
            super(CompositeType.AND, constraints);
        }

        @Override
        public boolean test(T value) {
            return constraints.stream().allMatch(constraint -> constraint.test(value));
        }

    }

    private static final class OrCompositeConstraint<T> extends AbstractCompositeConstraint<T> {

        public OrCompositeConstraint(List<Constraint<? super T>> constraints) {
            super(CompositeType.OR, constraints);
        }

        @Override
        public boolean test(T value) {
            return constraints.stream().anyMatch(constraint -> constraint.test(value));
        }

    }

    private static final class InvertCompositeConstraint<T> extends AbstractCompositeConstraint<T> {

        public InvertCompositeConstraint(List<Constraint<? super T>> constraints) {
            super(CompositeType.INVERT, constraints);
        }

        @Override
        public boolean test(T value) {
            return constraints.stream().noneMatch(constraint -> constraint.test(value));
        }

    }

}
