package me.zeroeightsix.fiber.api.constraint;

import me.zeroeightsix.fiber.api.FiberId;

import java.util.Collection;

/**
 * A set of constraint types Fiber supports.
 */
public enum ConstraintType {

	/**
	 * Indicates that the value can never be changed after initialization
	 */
	FINAL(identifier("final")),

	/**
	 * Specifies a numerical lower bound.
	 *
	 * <p> Values must be equal to or greater than the constraint's value to satisfy the constraint.
	 */
	NUMERICAL_LOWER_BOUND(identifier("min")),

	/**
	 * Specifies a numerical upper bound.
	 *
	 * <p> Values must be equal to or lesser than the constraint's value to satisfy the constraint.
	 */
	NUMERICAL_UPPER_BOUND(identifier("max")),

	NUMERICAL_STEP(identifier("multiple")),

	/**
	 * Specifies a minimum length. This constraint applies to {@link CharSequence}s, {@link Collection}s and arrays.
	 *
	 * <p> Values must be of equal or longer length than the constraint's value to satisfy the constraint.
	 * For example: a constraint that is checking strings has this constraint type and value 3.
	 * <ul>
	 *     <li>
	 *         {@code "AB"} would not satisfy the constraint
	 *     </li>
	 *     <li>
	 *         {@code "ABC"} and {@code "ABCD"} would satisfy the constraint
	 *     </li>
	 * </ul>
	 *
	 * @see #MAXIMUM_LENGTH
	 */
	MINIMUM_LENGTH(identifier("min_length")),

	/**
	 * Specifies a maximum length. This constraint applies to {@link CharSequence}s, {@link Collection}s and arrays.
	 *
	 * <p> Values must be of equal or shorter length than the constraint's value to satisfy the constraint.
	 * For example: a constraint that is checking strings has this constraint type and value 3.
	 * <ul>
	 *     <li>
	 *         {@code "AB"} and {@code "ABC"} would satisfy the constraint
	 *     </li>
	 *     <li>
	 *         {@code "ABCD"} would not satisfy the constraint
	 *     </li>
	 * </ul>
	 *
	 * @see #MINIMUM_LENGTH
	 */
	MAXIMUM_LENGTH(identifier("max_length")),

	/**
	 * Specifies a pattern that must match.
	 *
	 * <p> Values must match the constraint's value, which is a regular expression (regex).
	 */
	STRING_MATCHING(identifier("regex")),

	/**
	 * Specifies that all elements of a collection or array must satisfy all of a constraint's child constraints in order to satisfy that constraint.
	 */
	COMPONENTS_MATCH(identifier("component")),

	RANGE(identifier("range")),

	UNIQUE(identifier("unique")),

	ONE_OF(identifier("enumeration"));

	private final FiberId identifier;

	ConstraintType(FiberId identifier) {
		this.identifier = identifier;
	}

	private static FiberId identifier(String name) {
		return new FiberId("fiber", name);
	}

	/**
	 * Returns the unique identifier for this {@code ConstraintType}.
	 *
	 * @return the identifier
	 */
	public FiberId getIdentifier() {
		return identifier;
	}

}