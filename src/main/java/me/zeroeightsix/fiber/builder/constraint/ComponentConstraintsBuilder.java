package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.ConstraintType;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @param <A> the type of the array or collection to create a constraint for
 * @param <S> the type of this builder's source
 * @param <T> the type of elements processed by this builder's constraints
 */
public final class ComponentConstraintsBuilder<S, A, T> extends AbstractConstraintsBuilder<S, A, T, ComponentConstraintsBuilder<S, A, T>> {
    private final Function<List<Constraint<? super T>>, Constraint<? super A>> collector;

    public static <S, T> ComponentConstraintsBuilder<S, T[], T> array(final S source, final List<Constraint<? super T[]>> sourceConstraints, final Class<T> type) {
        return new ComponentConstraintsBuilder<>(source, sourceConstraints, type, ArrayComponentConstraint::new);
    }

    public static <S, T, C extends Collection<T>> ComponentConstraintsBuilder<S, C, T> collection(final S source, final List<Constraint<? super C>> sourceConstraints, final Class<T> type) {
        return new ComponentConstraintsBuilder<>(source, sourceConstraints, type, CollectionComponentConstraint::new);
    }

    private ComponentConstraintsBuilder(S source, List<Constraint<? super A>> sourceConstraints, Class<T> type, Function<List<Constraint<? super T>>, Constraint<? super A>> collector) {
        super(source, sourceConstraints, type);
        this.collector = collector;
    }

    public S finishComponent() {
        this.sourceConstraints.add(collector.apply(newConstraints));
        return source;
    }

    public static abstract class ComponentConstraint<A, T> extends Constraint<A> {
        private final List<Constraint<? super T>> constraints;

        ComponentConstraint(List<Constraint<? super T>> constraints) {
            super(ConstraintType.COMPONENTS_MATCH);
            this.constraints = constraints;
        }

        @Override
        public boolean test(A value) {
            for (Constraint<? super T> constraint : constraints) {
                if (!allMatch(constraint, value)) {
                    return false;
                }
            }
            return true;
        }

        protected abstract boolean allMatch(Constraint<? super T> constraint, A value);
    }

    private static class ArrayComponentConstraint<A, T> extends ComponentConstraint<A, T> {
        ArrayComponentConstraint(List<Constraint<? super T>> constraints) {
            super(constraints);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected boolean allMatch(Constraint<? super T> constraint, A value) {
            // Because arrays are reified and may contain primitives, we cannot use generics, and no iterator either.
            for (int i = 0; i < Array.getLength(value); i++) {
                T t = (T) Array.get(value, i);
                if (!constraint.test(t)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class CollectionComponentConstraint<T> extends ComponentConstraint<Iterable<T>, T> {
        public CollectionComponentConstraint(List<Constraint<? super T>> constraints) {
            super(constraints);
        }

        @Override
        protected boolean allMatch(Constraint<? super T> constraint, Iterable<T> value) {
            for (T t : value) {
                if (!constraint.test(t)) {
                    return false;
                }
            }
            return true;
        }
    }
}
