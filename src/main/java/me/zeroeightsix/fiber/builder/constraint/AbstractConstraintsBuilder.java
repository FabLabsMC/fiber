package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.*;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractConstraintsBuilder<T> {

	final List<Constraint<? super T>> sourceConstraints;
	protected final Class<T> type;

	final List<Constraint<? super T>> newConstraints = new ArrayList<>();

	AbstractConstraintsBuilder(List<Constraint<? super T>> sourceConstraints, Class<T> type) {
		this.sourceConstraints = sourceConstraints;
		this.type = type;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addNumericalLowerBound(T bound) throws RuntimeFiberException {
		checkNumerical();
		checkNumerical(bound);
		newConstraints.add(new NumberConstraint(Constraints.NUMERICAL_LOWER_BOUND, (Number) bound));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addNumericalUpperBound(T bound) throws RuntimeFiberException {
		checkNumerical();
		checkNumerical(bound);
		newConstraints.add(new NumberConstraint(Constraints.NUMERICAL_UPPER_BOUND, (Number) bound));
	}

	@SuppressWarnings("unchecked")
	public void addStringLengthLowerBound(int bound) throws RuntimeFiberException {
		checkCharSequence();
		newConstraints.add((Constraint<? super T>) new StringLengthConstraint(Constraints.STRING_MINIMUM_LENGTH, bound));
	}

	@SuppressWarnings("unchecked")
	public void addStringLengthUpperBound(int bound) throws RuntimeFiberException {
		checkCharSequence();
		newConstraints.add((Constraint<? super T>) new StringLengthConstraint(Constraints.STRING_MAXIMUM_LENGTH, bound));
	}

	@SuppressWarnings("unchecked")
	public void addStringPattern(Pattern pattern) {
		checkCharSequence();
		newConstraints.add((Constraint<? super T>) new RegexConstraint(pattern));
	}

	private void checkNumerical() {
		if (!Number.class.isAssignableFrom(this.type)) throw new UnsupportedOperationException("Can't apply numerical constraint to non-numerical setting");
	}

	private void checkNumerical(T value) {
		if (!Number.class.isAssignableFrom(value.getClass())) throw new IllegalArgumentException("'" + value + "' is not a number");
	}

	private void checkCharSequence() {
		if (!CharSequence.class.isAssignableFrom(this.type)) throw new UnsupportedOperationException("Can only apply regex pattern constraint to character sequences");
	}

	void addConstraints() {
		sourceConstraints.addAll(newConstraints);
	}
}
