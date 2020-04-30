package io.github.fablabsmc.fablabs.impl.fiber.constraint;

import java.util.Locale;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.EnumSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.TypeCheckResult;

public final class EnumConstraintChecker extends ConstraintChecker<String, EnumSerializableType> {
	private static final EnumConstraintChecker INSTANCE = new EnumConstraintChecker();

	public static EnumConstraintChecker instance() {
		return INSTANCE;
	}

	private EnumConstraintChecker() {
	}

	@Override
	public TypeCheckResult<String> test(EnumSerializableType cfg, String value) {
		if (cfg.getValidValues().contains(value)) {
			return TypeCheckResult.successful(value);
		}

		String corrected = value.toUpperCase(Locale.ROOT);

		if (cfg.getValidValues().contains(corrected)) {
			return TypeCheckResult.failed(corrected);
		}

		return TypeCheckResult.unrecoverable();
	}

	@Override
	public boolean comprehends(EnumSerializableType cfg, EnumSerializableType cfg2) {
		return cfg.getValidValues().containsAll(cfg2.getValidValues());
	}
}
