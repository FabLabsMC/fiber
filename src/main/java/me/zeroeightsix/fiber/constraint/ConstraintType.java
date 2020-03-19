package me.zeroeightsix.fiber.constraint;

import me.zeroeightsix.fiber.Identifier;

public enum ConstraintType {
	NUMERICAL_LOWER_BOUND(true, identifier("min")),
	NUMERICAL_UPPER_BOUND(true, identifier("max")),
	MINIMUM_LENGTH(true, identifier("min_length")),
	MAXIMUM_LENGTH(true, identifier("max_length")),
	STRING_MATCHING(false, identifier("regex")),
	FINAL(false, identifier("final")),
	COMPONENTS_MATCH(false, identifier("component")),
	COMPOSITE(false, identifier("composite"));

	private final boolean numerical;
	private final Identifier identifier;

	ConstraintType(boolean numerical, Identifier identifier) {
		this.numerical = numerical;
		this.identifier = identifier;
	}

	private static Identifier identifier(String name) {
		return new Identifier("fiber", name);
	}

	public boolean isNumerical() {
		return numerical;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

}
