package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.exceptions.RuntimeFiberException;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.Constraints;
import me.zeroeightsix.fiber.constraint.NumberConstraint;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractConstraintsBuilder<T> {

	final List<Constraint> sourceConstraints;
	protected final Class<T> type;

	final List<Constraint> newConstraints = new ArrayList<>();

	AbstractConstraintsBuilder(List<Constraint> sourceConstraints, Class<T> type) {
		this.sourceConstraints = sourceConstraints;
		this.type = type;
	}

	void addNumericalLowerBound(T bound) throws RuntimeFiberException {
		checkNumerical(bound);
		newConstraints.add(new NumberConstraint<>(Constraints.NUMERICAL_LOWER_BOUND, (Number) bound));
	}

	void addNumericalUpperBound(T bound) throws RuntimeFiberException {
		checkNumerical(bound);
		newConstraints.add(new NumberConstraint<>(Constraints.NUMERICAL_UPPER_BOUND, (Number) bound));
	}

	private void checkNumerical(T value) {
		if (!Number.class.isAssignableFrom(value.getClass())) throw new IllegalStateException("Can't apply numerical constraint to non-numerical setting");
	}

	void addConstraints() {
		sourceConstraints.addAll(newConstraints);
	}

}
