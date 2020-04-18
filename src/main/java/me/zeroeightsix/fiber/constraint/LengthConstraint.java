package me.zeroeightsix.fiber.constraint;

import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.schema.ConvertibleType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * An implementation of the {@code MINIMUM_LENGTH} and {@code MAXIMUM_LENGTH} constraint types.
 *
 * @param <T> the type of numerical value this constraint checks
 * @see ConstraintType#MINIMUM_LENGTH
 * @see ConstraintType#MAXIMUM_LENGTH
 */
public class LengthConstraint<T> extends ValuedConstraint<Integer, T> {
    public static <T> LengthConstraint<T> min(ConvertibleType<?, T> type, int min) {
        return new LengthConstraint<>(ConstraintType.MINIMUM_LENGTH, getLengthFunction(type), min);
    }

    public static <T> LengthConstraint<T> max(ConvertibleType<?, T> type, int min) {
        return new LengthConstraint<>(ConstraintType.MAXIMUM_LENGTH, getLengthFunction(type), min);
    }

    @Nonnull
    private static <T> ToIntFunction<T> getLengthFunction(ConvertibleType<?, T> type) {
        ToIntFunction<T> length;
        if (type.isList()) {
            length = t -> ((List<?>) t).size();
        } else if (type.isString()) {
            length = t -> ((CharSequence) t).length();
        } else {
            throw new RuntimeFiberException("Instances of " + type + " do not have a known length or size");
        }
        return length;
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
