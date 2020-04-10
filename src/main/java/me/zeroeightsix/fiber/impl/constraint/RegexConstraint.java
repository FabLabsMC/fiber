package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.constraint.ConstraintType;

import java.util.regex.Pattern;

/**
 * An implementation of the {@code STRING_MATCHING} constraint type.
 *
 * @see ConstraintType#STRING_MATCHING
 */
public class RegexConstraint extends ValuedConstraint<Pattern, CharSequence> {
    public RegexConstraint(Pattern value) {
        super(ConstraintType.STRING_MATCHING, value);
    }

    @Override
    public boolean test(CharSequence value) {
        return this.getValue().matcher(value).matches();
    }
}
