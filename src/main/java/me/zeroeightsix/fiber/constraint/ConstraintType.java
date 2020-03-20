package me.zeroeightsix.fiber.constraint;

import me.zeroeightsix.fiber.Identifier;

/**
 * A set of constraint types Fiber supports.
 */
public enum ConstraintType {
	/**
	 * Specifies a numerical lower bound.
	 * <br> Values must be equal to or greater than the constraint's value to satisfy the constraint.
	 */
	NUMERICAL_LOWER_BOUND(true, identifier("min")),
	/**
	 * Specifies a numerical upper bound.
	 * <br> Values must be equal to or lesser than the constraint's value to satisfy the constraint.
	 */
	NUMERICAL_UPPER_BOUND(true, identifier("max")),
	/**
	 * Specifies a minimum length.
	 * <br> Values must be of equal or longer length than the constraint's value to satisfy the constraint.
	 * <br> For example: a constraint that is checking strings has this constraint type and value 3.
	 * <ul>
	 *     <li>
	 *         {@code "AB"} would not satisfy the constraint
	 *     </li>
	 *     <li>
	 *         {@code "ABC"} and {@code "ABCD"} would satisfy the constraint
	 *     </li>
	 * </ul>
	 * Of course, this doesn't apply only to strings. Collections and arrays are also applicable.
	 *
	 * @see #MAXIMUM_LENGTH
	 */
	MINIMUM_LENGTH(true, identifier("min_length")),
	/**
	 * Specifies a minimum length.
	 * <br> Values must be of equal or shorter length than the constraint's value to satisfy the constraint.
	 * <br> For example: a constraint that is checking strings has this constraint type and value 3.
	 * <ul>
	 *     <li>
	 *         {@code "AB"} and {@code "ABC"} would satisfy the constraint
	 *     </li>
	 *     <li>
	 *         {@code "ABCD"} would not satisfy the constraint
	 *     </li>
	 * </ul>
	 * Of course, this doesn't apply only to strings. Collections and arrays are also applicable.
	 *
	 * @see #MINIMUM_LENGTH
	 */
	MAXIMUM_LENGTH(true, identifier("max_length")),
	/**
	 * Specifies a pattern that must match.
	 * <br> Values must match the constraint's value, which is a regular expression (regex).
	 */
	STRING_MATCHING(false, identifier("regex")),
	@Deprecated
	FINAL(false, identifier("final")),
	/**
	 * Specifies that all elements of a collection or array must satisfy all of a constraint's child constraints in order to satisfy that constraint.
	 */
	COMPONENTS_MATCH(false, identifier("component")),
	/**
	 * Specifies that a constraint is a composite constraint.
	 * <br> Whether or not a value satisfies a composite constraint is specified by the composite's {@link CompositeType}.
	 */
	COMPOSITE(false, identifier("composite"));

	private final boolean numerical;
	private final Identifier identifier;

	ConstraintType(boolean numerical, Identifier identifier) {
		this.numerical = numerical;
		this.identifier = identifier;
	}

	private static Identifier identifier(String name) {
		return new Identifier("fabric", name);
	}

	/**
	 * Returns whether or not this is a {@code ConstraintType} that operates only on numerical values
	 *
	 * @return {@code true} if this type is numerical
	 */
	public boolean isNumerical() {
		return numerical;
	}

	/**
	 * Returns the unique identifier for this {@code ConstraintType}.
	 *
	 * @return the identifier
	 */
	public Identifier getIdentifier() {
		return identifier;
	}

}
