package me.zeroeightsix.fiber.constraint;

public class StringLengthConstraint extends ValuedConstraint<Integer, CharSequence> {

    public StringLengthConstraint(Constraints type, Integer value) {
        super(type, value);
        if (type != Constraints.STRING_MINIMUM_LENGTH && type != Constraints.STRING_MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(type + " is not a string length constraint");
        }
    }

    @Override
    public boolean test(CharSequence value) {
        switch (getType()) {
            case STRING_MINIMUM_LENGTH:
                return value.length() >= this.getValue();
            case STRING_MAXIMUM_LENGTH:
                return value.length() <= this.getValue();
            default:
                throw new IllegalStateException("A StringLengthConstraint must be of type STRING_MINIMUM_LENGTH or STRING_MAXIMUM_LENGTH");
        }
    }
}
