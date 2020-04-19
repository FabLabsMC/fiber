package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.constraint.ConstraintType;

import java.util.regex.Pattern;

/**
 * An implementation of the {@code STRING_MATCHING} constraint type.
 *
 * @see ConstraintType#STRING_MATCHING
 */
public final class RegexConstraint extends ValuedConstraint<Pattern, String> {
    public RegexConstraint(Pattern value) {
        super(ConstraintType.STRING_MATCHING, value);
    }

    @Override
    public TestResult<String> test(String value) {
        if (this.getConstraintValue().matcher(value).matches()) {
            return new TestResult<>(true, value);
        }
        return TestResult.unrecoverable();
    }
}
