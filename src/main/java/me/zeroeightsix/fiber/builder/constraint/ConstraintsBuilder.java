package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;

import java.util.Collection;
import java.util.List;

/**
 *
 * @param <A> the type of {@link Constraint} this builder should output
 * @param <S> the type of this builder's source object (eg. {@code ConfigValueBuilder} or {@code ConstraintsBuilder}
 * @param <T> the type of intermediary objects this builder's constraints should process. May be identical to {@code A}.
 * @param <B> the type of {@code this}, for chaining
 */
public abstract class ConstraintsBuilder<S, A, T, B extends ConstraintsBuilder<S, A, T, B>> extends AbstractConstraintsBuilder<S, A, T, B> {
    public static <S, T> Scalar<S, T> scalar(S source, List<Constraint<? super T>> constraints, Class<T> type) {
        return new Scalar<>(source, constraints, type);
    }

    public static <S, A, T> Aggregate<S, A, T> aggregate(S source, List<Constraint<? super A>> constraints, Class<A> aggregateType, Class<T> componentType) {
        return new Aggregate<>(source, constraints, aggregateType, componentType);
    }

    ConstraintsBuilder(S source, List<Constraint<? super A>> sourceConstraints, Class<T> type) {
        super(source, sourceConstraints, type);
    }

    public abstract CompositeConstraintBuilder<B, A> composite(CompositeType type);

    public abstract S finish();

    /**
     *
     * @param <S> the type of this builder's source object (eg. {@code ConfigValueBuilder} or {@code ConstraintsBuilder}
     * @param <T> the type of {@link Constraint} this builder should output
     */
    public static final class Scalar<S, T> extends ConstraintsBuilder<S, T, T, Scalar<S, T>> {

        private Scalar(S source, List<Constraint<? super T>> sourceConstraints, Class<T> type) {
            super(source, sourceConstraints, type);
        }

        @Override
        public CompositeConstraintBuilder<Scalar<S, T>, T> composite(CompositeType type) {
            return new CompositeConstraintBuilder<>(this, type, sourceConstraints, this.type);
        }

        @Override
        public S finish() {
            sourceConstraints.addAll(newConstraints);
            return source;
        }
    }

    public static class Aggregate<S, A, T> extends ConstraintsBuilder<S, A, A, Aggregate<S, A, T>> {
        private final Class<T> componentType;

        private Aggregate(S source, List<Constraint<? super A>> sourceConstraints, Class<A> type, Class<T> componentType) {
            super(source, sourceConstraints, type);
            this.componentType = componentType;
        }

        @Override
        public CompositeConstraintBuilder<Aggregate<S, A, T>, A> composite(CompositeType type) {
            return new CompositeConstraintBuilder<>(this, type, sourceConstraints, this.type);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public ComponentConstraintsBuilder<Aggregate<S, A, T>, A, T> component() {
            if (this.type.isArray()) {
                List<Constraint<? super T[]>> sourceConstraints = (List) this.sourceConstraints;
                return (ComponentConstraintsBuilder<Aggregate<S, A, T>, A, T>) ComponentConstraintsBuilder.array(this, sourceConstraints, this.componentType);
            } else {
                List<Constraint<? super Collection<T>>> sourceConstraints = (List) this.sourceConstraints;
                return (ComponentConstraintsBuilder<Aggregate<S, A, T>, A, T>) ComponentConstraintsBuilder.collection(this, sourceConstraints, this.componentType);
            }
        }

        public S finish() {
            sourceConstraints.addAll(newConstraints);
            return source;
        }
    }
}
