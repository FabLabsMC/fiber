package io.github.fablabsmc.fablabs.impl.fiber.constraint;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.BooleanSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.TypeCheckResult;

public class BooleanConstraintChecker extends ConstraintChecker<Boolean, BooleanSerializableType> {
	private static final BooleanConstraintChecker INSTANCE = new BooleanConstraintChecker();

	public static BooleanConstraintChecker instance() {
		return INSTANCE;
	}

	private BooleanConstraintChecker() {
	}

	@Override
	public TypeCheckResult<Boolean> test(BooleanSerializableType cfg, Boolean value) {
		return TypeCheckResult.successful(value);
	}

	@Override
	public boolean comprehends(BooleanSerializableType cfg, BooleanSerializableType cfg2) {
		return true;    // all boolean types are equal
	}
}
