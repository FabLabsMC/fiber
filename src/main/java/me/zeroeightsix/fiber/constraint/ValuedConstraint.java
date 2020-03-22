package me.zeroeightsix.fiber.constraint;

/**
 * A constraint that has a value attached to it.
 * <br> For example, a {@link NumberConstraint} can have as value a minimum or maximum.
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
