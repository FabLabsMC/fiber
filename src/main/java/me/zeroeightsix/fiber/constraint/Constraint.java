package me.zeroeightsix.fiber.constraint;

/**
 * Specifies a condition values must satisfy before being set to a {@code ConfigValue}.
 *
 * @param <A>
 * @see me.zeroeightsix.fiber.tree.ConfigValue
 */
public abstract class Constraint<A> { // A is the type of values we're gonna check

	private final ConstraintType type;

	public Constraint(ConstraintType type) {
		this.type = type;
	}

	public ConstraintType getType() {
		return type;
	}

	/**
	 * Tests a value against this {@code ConfigValue}.
	 *
	 * @param value the value
	 * @return {@code true} if {@code value} satisfies the constraint
	 */
	public abstract boolean test(A value);

}
