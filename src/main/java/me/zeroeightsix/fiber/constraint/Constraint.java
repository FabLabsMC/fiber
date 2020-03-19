package me.zeroeightsix.fiber.constraint;

public abstract class Constraint<A> { // A is the type of values we're gonna check

	private final ConstraintType type;

	public Constraint(ConstraintType type) {
		this.type = type;
	}

	public ConstraintType getType() {
		return type;
	}

	public abstract boolean test(A value);

}
