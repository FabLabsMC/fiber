package me.zeroeightsix.fiber.constraint;

/**
 *
 *
 * @param <T>
 * @param <A>
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
