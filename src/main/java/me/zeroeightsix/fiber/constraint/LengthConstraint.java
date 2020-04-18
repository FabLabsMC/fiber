package me.zeroeightsix.fiber.constraint;

import java.util.function.ToIntFunction;

/**
 * An implementation of the {@code MINIMUM_LENGTH} and {@code MAXIMUM_LENGTH} constraint types.
 *
 * @param <T> the type of numerical value this constraint checks
 * @see ConstraintType#MINIMUM_LENGTH
 * @see ConstraintType#MAXIMUM_LENGTH
 */
public final class LengthConstraint<T> extends ValuedConstraint<Integer, T> {
    public static <T> LengthConstraint<T> min(ToIntFunction<T> lengthGetter, int min) {
        return new LengthConstraint<>(ConstraintType.MINIMUM_LENGTH, lengthGetter, min);
    }

    public static <T> LengthConstraint<T> max(ToIntFunction<T> lengthGetter, int min) {
        return new LengthConstraint<>(ConstraintType.MAXIMUM_LENGTH, lengthGetter, min);
    }

    private final ToIntFunction<T> lengthGetter;

    private LengthConstraint(ConstraintType type, ToIntFunction<T> lengthGetter, Integer value) {
        super(type, value);
        this.lengthGetter = lengthGetter;
        if (type != ConstraintType.MINIMUM_LENGTH && type != ConstraintType.MAXIMUM_LENGTH) {
            throw new IllegalArgumentException("Invalid constraint type " + type);
        }
    }

    @Override
    public TestResult<T> test(T value) {
        if (value == null) throw new NullPointerException("Cannot test the length of a null value");
        int length = this.lengthGetter.applyAsInt(value);
        switch (this.getType()) {
            case MINIMUM_LENGTH:
                if (length >= this.getConstraintValue()) return TestResult.successful(value);
                break;
            case MAXIMUM_LENGTH:
                if (length <= this.getConstraintValue()) return TestResult.successful(value);
                break;
            default:
                throw new AssertionError("Invalid constraint type");
        }
        return TestResult.unrecoverable();
    }
}
