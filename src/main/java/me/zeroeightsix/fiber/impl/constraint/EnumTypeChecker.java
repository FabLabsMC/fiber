package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.schema.type.EnumSerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;

import javax.annotation.Nonnull;
import java.util.Locale;

public final class EnumTypeChecker extends Constraint<String, EnumSerializableType> {
    @Nonnull
    private final EnumSerializableType cfg;

    public EnumTypeChecker(@Nonnull EnumSerializableType cfg) {
        super(cfg);
        this.cfg = cfg;
    }

    @Override
    public TypeCheckResult<String> test(String value) {
        if (this.cfg.getValidValues().contains(value)) {
            return TypeCheckResult.successful(value);
        }
        String corrected = value.toUpperCase(Locale.ROOT);
        if (this.cfg.getValidValues().contains(corrected)) {
            return TypeCheckResult.failed(corrected);
        }
        return TypeCheckResult.unrecoverable();
    }

    @Override
    public boolean comprehends(Constraint<?, ?> constraint) {
        if (!(constraint instanceof EnumTypeChecker)) {
            return false;
        }
        EnumTypeChecker that = (EnumTypeChecker) constraint;
        return this.cfg.getValidValues().containsAll(that.cfg.getValidValues());
    }
}
