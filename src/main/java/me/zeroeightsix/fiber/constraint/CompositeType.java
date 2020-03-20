package me.zeroeightsix.fiber.constraint;

/**
 * A set of composite types Fiber supports.
 * <br> Composite types can best be described using logical operators:
 * <ul>
 *     <li>{@link #AND} represents {@code &&}; all constraints in the composite must be satisfied</li>
 *     <li>{@link #OR} represents {@code ||}; at least one constraint in the composite must be satisfied</li>
 *     <li>{@link #INVERT} represents a negation ({@code !}); none of the constraints in the composite must be satisfied</li>
 * </ul>
 */
public enum CompositeType {

	/**
	 * Represents a logical AND.
	 * <br> For a composite constraint to be satisfied with this type, all of its children must be satisfied.
	 */
	AND("and"),
	/**
	 * Represents a logical OR.
	 * <br> For a composite constraint to be satisfied with this type, at least one of its children should be satisfied.
	 */
	OR("or"),
	/**
	 * Represents a logical negation.
	 * <br> For a composite constraint to be satisfied with this type, none of its children should be satisfied.
	 */
	INVERT("invert");

	String name;

	CompositeType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
