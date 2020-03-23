package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;

import java.util.Collection;
import java.util.List;

/**
 * A builder for {@code Constraint}s.
 *
 * <p> This is the abstract base class for all builders of this type. Builders implementing this class include {@link Scalar} and {@link Aggregate}.
 *
 * @param <S> the type of this builder's source object (eg. {@code ConfigValueBuilder} or {@code ConstraintsBuilder}
 * @param <T> the type of {@link Constraint} this builder should output
 * @param <B> the type of {@code this}, for chaining
 */
public abstract class ConstraintsBuilder<S, T, B extends ConstraintsBuilder<S, T, B>> extends AbstractConstraintsBuilder<S, T, T, B> {    /**
     * Creates a new scalar constraint builder
     *
     * @param source the {@code ConfigValueBuilder} this {@code ConstraintsBuilder} originates from
     * @param constraints the list of constraints this builder will add to
     * @param type the class of the type of values checked by constraints built by this builder
     * @param <S> the type of {@code source}
     * @param <T> the type {@code type} represents
     * @return the newly created builder
     * @see Scalar
     */

    public static <S, T> Scalar<S, T> scalar(S source, List<Constraint<? super T>> constraints, Class<T> type) {
        return new Scalar<>(source, constraints, type);
    }

    /**
     * Creates a new aggregate constraint builder
     *
     * @param source the {@code ConfigValueBuilder} this {@code ConstraintsBuilder} originates from
     * @param constraints the list of constraints this builder will add to
     * @param aggregateType the type of collection or array {@code source} holds
     * @param componentType the type of all elements in {@code aggregateType}
     * @param <S> the type of {@code source}
     * @param <A> the type of {@code aggregateType}
     * @param <T> the type of {@code componentType}
     * @return the newly created builder
     * @see Aggregate
     */
    public static <S, A, T> Aggregate<S, A, T> aggregate(S source, List<Constraint<? super A>> constraints, Class<A> aggregateType, Class<T> componentType) {
        return new Aggregate<>(source, constraints, aggregateType, componentType);
    }

    ConstraintsBuilder(S source, List<Constraint<? super T>> sourceConstraints, Class<T> type) {
        super(source, sourceConstraints, type);
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
    public CompositeConstraintBuilder<B, T> composite(CompositeType type) {
        return new CompositeConstraintBuilder<>(self(), type, sourceConstraints, this.type);
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

    /**
     * An implementation of {@code ConstraintsBuilder} for scalar constraints.
     * <br>Scalar types are those with only one value, such as {@code Integer} or {@code String}.
     * <br>The other, aggregate types, such as {@code List}s or arrays, are created using {@link Aggregate}
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
     * An implementation of {@code ConstraintsBuilder} for aggregate constraints.
     * <br>Aggregate types are those that hold multiple values, such as {@code List} or arrays.
     * <br>The other, scalar types, such as {@code Integer} or {@code String}, are created using {@link Scalar}
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

        /**
         * Creates a new {@code ComponentConstraintsBuilder}.
         *
         * <p> Component constraints are constraints that test each value in an aggregate type. By default, all tested elements must satisfy the constraint in order for the entire constraint to be satisfied.
         *
         * @return the newly created builder
         */
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
