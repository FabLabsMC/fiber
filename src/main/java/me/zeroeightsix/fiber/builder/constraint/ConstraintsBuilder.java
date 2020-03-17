package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;

import java.util.List;

/**
 *
 * @param <S> the type of this builder's source object (eg. {@code ConfigValueBuilder} or {@code ConstraintsBuilder}
 * @param <T> the type of {@link Constraint} this builder should output
 */
public final class ConstraintsBuilder<S, T> extends AbstractConstraintsBuilder<S, T, T, ConstraintsBuilder<S, T>> {

	public ConstraintsBuilder(S source, List<Constraint<? super T>> sourceConstraints, Class<T> type) {
		super(source, sourceConstraints, type);
	}

	public CompositeConstraintBuilder<ConstraintsBuilder<S, T>, T> composite(CompositeType type) {
		return new CompositeConstraintBuilder<>(this, type, sourceConstraints, this.type);
	}

	public S finish() {
		sourceConstraints.addAll(newConstraints);
		return source;
	}
}
