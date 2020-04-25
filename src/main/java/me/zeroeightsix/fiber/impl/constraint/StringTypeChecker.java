package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.constraint.TypeCheckResult;
import me.zeroeightsix.fiber.api.schema.type.StringConfigType;

import java.util.regex.Pattern;

/**
 * Checks validity of serialized strings based on a {@code StringConfigType}'s length range and pattern.
 */
public final class StringTypeChecker extends Constraint<String, StringConfigType> {

    public StringTypeChecker(StringConfigType cfg) {
        super(cfg);
    }

    @Override
    public TypeCheckResult<String> test(String value) {
        if (value.length() < this.cfg.getMinLength()) {
            return TypeCheckResult.unrecoverable();
        }
        if (value.length() > this.cfg.getMaxLength()) {
            return TypeCheckResult.unrecoverable();
        }
        Pattern pattern = this.cfg.getPattern();
        if (pattern != null && !pattern.matcher(value).matches()) {
            return TypeCheckResult.unrecoverable();
        }
        return new TypeCheckResult<>(true, value);
    }

    @Override
    public boolean comprehends(Constraint<?, ?> constraint) {
        if (!(constraint instanceof StringTypeChecker)) {
            return false;
        }
        StringTypeChecker that = (StringTypeChecker) constraint;
        if (this.cfg.getMinLength() > that.cfg.getMinLength()) {
            return false;
        }
        if (this.cfg.getMaxLength() < that.cfg.getMaxLength()) {
            return false;
        }
        // TODO detect if this pattern matches every string the other pattern can?
        return this.cfg.getPattern() == null || this.cfg.getPattern().equals(that.cfg.getPattern());
    }
}
