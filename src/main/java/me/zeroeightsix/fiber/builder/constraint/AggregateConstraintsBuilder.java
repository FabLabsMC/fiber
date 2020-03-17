package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;

import java.util.Collection;
import java.util.List;

public class AggregateConstraintsBuilder<S, A, T> extends AbstractConstraintsBuilder<S, A, A, AggregateConstraintsBuilder<S, A, T>> {
    private final Class<T> componentType;

    public AggregateConstraintsBuilder(S source, List<Constraint<? super A>> sourceConstraints, Class<A> type, Class<T> componentType) {
        super(source, sourceConstraints, type);
        this.componentType = componentType;
    }

    public CompositeConstraintBuilder<AggregateConstraintsBuilder<S, A, T>, A> composite(CompositeType type) {
        return new CompositeConstraintBuilder<>(this, type, sourceConstraints, this.type);
    }

    @SuppressWarnings("unchecked")
    public ComponentConstraintsBuilder<AggregateConstraintsBuilder<S, A, T>, A, T> component() {
        if (this.componentType.isArray()) {
            List<Constraint<? super T[]>> sourceConstraints = (List<Constraint<? super T[]>>) this.sourceConstraints;
            return (ComponentConstraintsBuilder<AggregateConstraintsBuilder<S, A, T>, A, T>) ComponentConstraintsBuilder.array(this, sourceConstraints, this.componentType);
        } else {
            List<Constraint<? super Collection<T>>> sourceConstraints = (List<Constraint<? super Collection<T>>>) this.sourceConstraints;
            return (ComponentConstraintsBuilder<AggregateConstraintsBuilder<S, A, T>, A, T>) ComponentConstraintsBuilder.collection(this, sourceConstraints, this.componentType);
        }
    }

    public S finish() {
        sourceConstraints.addAll(newConstraints);
        return source;
    }
}
