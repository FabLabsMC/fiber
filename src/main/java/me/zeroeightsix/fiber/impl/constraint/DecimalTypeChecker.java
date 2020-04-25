package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.constraint.TypeCheckResult;
import me.zeroeightsix.fiber.api.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.api.schema.type.DecimalConfigType;

import java.math.BigDecimal;

/**
 * Checks validity of serialized numbers based on a {@code DecimalConfigType}'s range constraint.
 */
public final class DecimalTypeChecker extends Constraint<BigDecimal, DecimalConfigType> {

	public DecimalTypeChecker(DecimalConfigType cfg) throws RuntimeFiberException {
		super(cfg);
	}

	@Override
	public TypeCheckResult<BigDecimal> test(BigDecimal value) {
		if (this.cfg.getMinimum() != null && this.cfg.getMinimum().compareTo(value) > 0) {
			return TypeCheckResult.failed(this.cfg.getMinimum());
		} else if (this.cfg.getMaximum() != null && this.cfg.getMaximum().compareTo(value) < 0) {
			return TypeCheckResult.failed(this.cfg.getMaximum());
		} else if (this.cfg.getIncrement() != null && value.remainder(this.cfg.getIncrement()).intValue() != 0) {
			if (this.cfg.getMinimum() != null) {
				return TypeCheckResult.failed(fit(value, this.cfg.getMinimum(), this.cfg.getIncrement()));
			} else {
				return TypeCheckResult.unrecoverable();
			}
		} else {
			return TypeCheckResult.successful(value);
		}
	}

	@Override
	public boolean comprehends(Constraint<?, ?> constraint) {
		if (constraint instanceof DecimalTypeChecker) {
			DecimalConfigType value = this.cfg;
			DecimalConfigType other = ((DecimalTypeChecker) constraint).cfg;
			return (value.getMinimum() == null || other.getMinimum() != null && value.getMinimum().compareTo(other.getMinimum()) <= 0) &&
					(value.getMaximum() == null || other.getMaximum() != null && value.getMaximum().compareTo(other.getMaximum()) >= 0) &&
					(value.getIncrement() == null || other.getIncrement() != null && other.getIncrement().remainder(value.getIncrement()).intValue() == 0);
		}

		return false;
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
