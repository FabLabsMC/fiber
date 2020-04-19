package me.zeroeightsix.fiber.constraint;

import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import java.math.BigDecimal;

/**
 * An implementation of the {@code NUMERICAL_LOWER_BOUND} and {@code NUMERICAL_UPPER_BOUND} constraint types.
 *
 * @see ConstraintType#NUMERICAL_LOWER_BOUND
 * @see ConstraintType#NUMERICAL_UPPER_BOUND
 */
public final class RangeConstraint extends ValuedConstraint<NumberRange, BigDecimal> {

	public RangeConstraint(NumberRange value) throws RuntimeFiberException {
		super(ConstraintType.RANGE, value);
	}

	@Override
	public TestResult<BigDecimal> test(BigDecimal value) {
		NumberRange range = this.getConstraintValue();
		if (range.min != null && range.min.compareTo(value) > 0) {
			return TestResult.failed(range.min);
		} else if (range.max != null && range.max.compareTo(value) < 0) {
			return TestResult.failed(range.max);
		} else if (range.step != null && value.remainder(range.step).intValue() == 0) {
			if (range.min != null) {
				return TestResult.failed(fit(value, range.min, range.step));
			} else {
				return TestResult.unrecoverable();
			}
		} else {
			return TestResult.successful(value);
		}
	}

	private static BigDecimal nearest(BigDecimal less, BigDecimal value, BigDecimal more) {
		BigDecimal lessDiff = value.subtract(less);
		BigDecimal moreDiff = more.subtract(value);
		if (lessDiff.compareTo(moreDiff) < 0) return less;
		return more;
	}

	private static BigDecimal fit(BigDecimal value, BigDecimal min, BigDecimal step) {
		BigDecimal prevTick = ((value.subtract(min)).divide(step, BigDecimal.ROUND_FLOOR)).setScale(0, BigDecimal.ROUND_DOWN);
		BigDecimal prevTickValue = prevTick.multiply(step).add(min);
		BigDecimal nextTickValue = prevTick.add(BigDecimal.ONE).multiply(step).add(min);
		return nearest(prevTickValue, value, nextTickValue);
	}

}
