package me.zeroeightsix.fiber.constraint;

import java.util.regex.Pattern;

public class RegexConstraint extends ValuedConstraint<Pattern, CharSequence> {
    public RegexConstraint(Pattern value) {
        super(Constraints.STRING_MATCHING, value);
    }

    @Override
    public boolean test(CharSequence value) {
        return this.getValue().matcher(value).matches();
    }
}
