package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;

import java.util.Collection;
import java.util.List;

/**
 *
 * @param <S> the type of this builder's source object (eg. {@code ConfigValueBuilder} or {@code ConstraintsBuilder}
 * @param <T> the type of {@link Constraint} this builder should output
 * @param <B> the type of {@code this}, for chaining
 */
public abstract class ConstraintsBuilder<S, T, B extends ConstraintsBuilder<S, T, B>> extends AbstractConstraintsBuilder<S, T, T, B> {
    public static <S, T> Scalar<S, T> scalar(S source, List<Constraint<? super T>> constraints, Class<T> type) {
        return new Scalar<>(source, constraints, type);
    }

    public static <S, A, T> Aggregate<S, A, T> aggregate(S source, List<Constraint<? super A>> constraints, Class<A> aggregateType, Class<T> componentType) {
        return new Aggregate<>(source, constraints, aggregateType, componentType);
    }

    ConstraintsBuilder(S source, List<Constraint<? super T>> sourceConstraints, Class<T> type) {
        super(source, sourceConstraints, type);
    }

    public CompositeConstraintBuilder<B, T> composite(CompositeType type) {
        return new CompositeConstraintBuilder<>(self(), type, sourceConstraints, this.type);
    }

    public S finish() {
        sourceConstraints.addAll(newConstraints);
        return source;
    }

    /**
     *
     * @param <S> the type of this builder's source object (eg. {@code ConfigValueBuilder} or {@code ConstraintsBuilder}
     * @param <T> the type of {@link Constraint} this builder should output
     */
    public static final class Scalar<S, T> extends ConstraintsBuilder<S, T, Scalar<S, T>> {

        private Scalar(S source, List<Constraint<? super T>> sourceConstraints, Class<T> type) {
            super(source, sourceConstraints, type);
        }
    }

    /**
     *
     * @param <S> the type of this builder's source object (eg. {@code ConfigValueBuilder} or {@code ConstraintsBuilder}
     * @param <T> the type of {@link Constraint} this builder should output
     * @param <C> the type of the components in T
     */
    public static class Aggregate<S, T, C> extends ConstraintsBuilder<S, T, Aggregate<S, T, C>> {
        private final Class<C> componentType;

        private Aggregate(S source, List<Constraint<? super T>> sourceConstraints, Class<T> type, Class<C> componentType) {
            super(source, sourceConstraints, type);
            this.componentType = componentType;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public ComponentConstraintsBuilder<Aggregate<S, T, C>, T, C> component() {
            if (this.type.isArray()) {
                List<Constraint<? super C[]>> sourceConstraints = (List) this.sourceConstraints;
                return (ComponentConstraintsBuilder<Aggregate<S, T, C>, T, C>) ComponentConstraintsBuilder.array(this, sourceConstraints, this.componentType);
            } else {
                List<Constraint<? super Collection<C>>> sourceConstraints = (List) this.sourceConstraints;
                return (ComponentConstraintsBuilder<Aggregate<S, T, C>, T, C>) ComponentConstraintsBuilder.collection(this, sourceConstraints, this.componentType);
            }
        }
    }
}
