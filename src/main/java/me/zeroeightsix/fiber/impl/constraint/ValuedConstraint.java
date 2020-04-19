package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.constraint.Constraint;
import me.zeroeightsix.fiber.api.constraint.ConstraintType;

/**
 * A constraint that has a value attached to it.
 *
 * <p> For example, a {@link NumberConstraint} can have as value a minimum or maximum.
 *
 * @param <T> the value of this constraint
 * @param <A> the type of values this constraint checks
 */
public abstract class ValuedConstraint<T, A> extends Constraint<A> { // A is the type of values we're gonna check

	private final T value;

	public ValuedConstraint(ConstraintType type, T value) {
		super(type);
		this.value = value;
	}

	public T getValue() {
		return value;
	}

}
