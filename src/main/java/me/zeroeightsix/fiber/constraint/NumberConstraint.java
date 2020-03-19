package me.zeroeightsix.fiber.constraint;

import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import java.math.BigDecimal;

public class NumberConstraint<T extends Number> extends ValuedConstraint<T, T> {

	public NumberConstraint(ConstraintType type, T value) throws RuntimeFiberException {
		super(type, value);
		if (!type.isNumerical()) {
			throw new RuntimeFiberException("Couldn't create numerical constraint: type must be numerical");
		}
	}

	@Override
	public boolean test(T value) {
		// Sadly, because Number doesn't provide anything to compare numbers, we use BigDecimal instead (to capture all possible number types)
		int compared = new BigDecimal(getValue().toString()).compareTo(new BigDecimal(value.toString()));
		switch (getType()) {
			case NUMERICAL_LOWER_BOUND:
				return compared <= 0;
			case NUMERICAL_UPPER_BOUND:
				return compared >= 0;
			default:
				throw new IllegalStateException("A NumberConstraint must be of type NUMERICAL_LOWER_BOUND or NUMERICAL_UPPER_BOUND");
		}
	}

}
