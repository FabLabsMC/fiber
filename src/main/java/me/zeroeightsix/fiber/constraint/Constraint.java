package me.zeroeightsix.fiber.constraint;

import me.zeroeightsix.fiber.tree.ConfigLeaf;

/**
 * Specifies a condition values must satisfy before being set to a {@code ConfigLeaf}.
 *
 * @param <A> the type of values this constraint checks
 * @see ConfigLeaf
 */
public abstract class Constraint<A> {

	private final ConstraintType type;

	public Constraint(ConstraintType type) {
		this.type = type;
	}

	public ConstraintType getType() {
		return type;
	}

	/**
	 * Tests a value against this {@code ConfigLeaf}.
	 *
	 * @param value the value
	 * @return {@code true} if {@code value} satisfies the constraint
	 */
	public abstract boolean test(A value);

}
