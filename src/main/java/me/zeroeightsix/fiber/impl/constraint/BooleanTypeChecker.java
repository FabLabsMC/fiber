package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.schema.type.BooleanSerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;

public class BooleanTypeChecker extends Constraint<Boolean, BooleanSerializableType> {
    public BooleanTypeChecker(BooleanSerializableType cfg) {
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
