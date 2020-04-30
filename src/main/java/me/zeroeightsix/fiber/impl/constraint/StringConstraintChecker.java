package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.schema.type.StringSerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;

import java.util.regex.Pattern;

/**
 * Checks validity of serialized strings based on a {@code StringConfigType}'s length range and pattern.
 */
public final class StringConstraintChecker extends ConstraintChecker<String, StringSerializableType> {

    private static final StringConstraintChecker INSTANCE = new StringConstraintChecker();

    public static StringConstraintChecker instance() {
        return INSTANCE;
    }

    private StringConstraintChecker() { }

    @Override
    public TypeCheckResult<String> test(StringSerializableType cfg, String value) {
        if (value.length() < cfg.getMinLength()) {
            return TypeCheckResult.unrecoverable();
        }
        if (value.length() > cfg.getMaxLength()) {
            return TypeCheckResult.unrecoverable();
        }
        Pattern pattern = cfg.getPattern();
        if (pattern != null && !pattern.matcher(value).matches()) {
            return TypeCheckResult.unrecoverable();
        }
        return TypeCheckResult.successful(value);
    }

    @Override
    public boolean comprehends(StringSerializableType cfg, StringSerializableType cfg2) {
        if (cfg.getMinLength() > cfg2.getMinLength()) {
            return false;
        }
        if (cfg.getMaxLength() < cfg2.getMaxLength()) {
            return false;
        }
        // TODO detect if this pattern matches every string the other pattern can?
        return cfg.getPattern() == null || cfg.getPattern().equals(cfg2.getPattern());
    }
}
