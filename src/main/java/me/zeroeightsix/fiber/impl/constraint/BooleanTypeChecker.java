package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.constraint.TypeCheckResult;
import me.zeroeightsix.fiber.api.schema.type.BooleanConfigType;

public class BooleanTypeChecker extends Constraint<Boolean, BooleanConfigType> {
    public BooleanTypeChecker(BooleanConfigType cfg) {
        super(cfg);
    }

    @Override
    public TypeCheckResult<Boolean> test(Boolean value) {
        return TypeCheckResult.successful(value);
    }

    @Override
    public boolean comprehends(Constraint<?, ?> constraint) {
        return constraint instanceof BooleanTypeChecker;
    }
}
