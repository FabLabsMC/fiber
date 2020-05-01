package io.github.fablabsmc.fablabs.impl.fiber.constraint;

import java.math.BigDecimal;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.DecimalSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.TypeCheckResult;

/**
 * Checks validity of serialized numbers based on a {@code DecimalConfigType}'s range constraint.
 */
public final class DecimalConstraintChecker extends ConstraintChecker<BigDecimal, DecimalSerializableType> {
	private static final DecimalConstraintChecker INSTANCE = new DecimalConstraintChecker();

	public static DecimalConstraintChecker instance() {
		return INSTANCE;
	}

	private DecimalConstraintChecker() {
	}

	@Override
	public TypeCheckResult<BigDecimal> test(DecimalSerializableType cfg, BigDecimal value) {
		if (cfg.getMinimum() != null && cfg.getMinimum().compareTo(value) > 0) {
			return TypeCheckResult.failed(cfg.getMinimum());
		} else if (cfg.getMaximum() != null && cfg.getMaximum().compareTo(value) < 0) {
			return TypeCheckResult.failed(cfg.getMaximum());
		} else if (cfg.getIncrement() != null && value.remainder(cfg.getIncrement()).intValue() != 0) {
			if (cfg.getMinimum() != null) {
				return TypeCheckResult.failed(fit(value, cfg.getMinimum(), cfg.getIncrement()));
			} else {
				return TypeCheckResult.unrecoverable();
			}
		} else {
			return TypeCheckResult.successful(value);
		}
	}

	@Override
	public boolean comprehends(DecimalSerializableType cfg, DecimalSerializableType cfg2) {
		return (cfg.getMinimum() == null || cfg2.getMinimum() != null && cfg.getMinimum().compareTo(cfg2.getMinimum()) <= 0)
				&& (cfg.getMaximum() == null || cfg2.getMaximum() != null && cfg.getMaximum().compareTo(cfg2.getMaximum()) >= 0)
				&& (cfg.getIncrement() == null || cfg2.getIncrement() != null && cfg2.getIncrement().remainder(cfg.getIncrement()).intValue() == 0);
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
