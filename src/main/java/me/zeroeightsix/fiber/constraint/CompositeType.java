package me.zeroeightsix.fiber.constraint;

public enum CompositeType {

	AND("and"),
	OR("or"),
	INVERT("invert");

	String name;

	CompositeType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
