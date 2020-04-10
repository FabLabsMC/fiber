package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.constraint.ConstraintType;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.function.ToIntFunction;

/**
 * An implementation of the {@code MINIMUM_LENGTH} and {@code MAXIMUM_LENGTH} constraint types.
 *
 * @param <T> the type of numerical value this constraint checks
 * @see ConstraintType#MINIMUM_LENGTH
 * @see ConstraintType#MAXIMUM_LENGTH
 */
public class LengthConstraint<T> extends ValuedConstraint<Integer, T> {
    public static <T> LengthConstraint<T> min(@Nullable Class<T> type, int min) {
        return new LengthConstraint<>(ConstraintType.MINIMUM_LENGTH, getLengthFunction(type), min);
    }

    public static <T> LengthConstraint<T> max(@Nullable Class<T> type, int min) {
        return new LengthConstraint<>(ConstraintType.MAXIMUM_LENGTH, getLengthFunction(type), min);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> ToIntFunction<T> getLengthFunction(@Nullable Class<T> type) {
        ToIntFunction<T> length;
        if (type == null) {
            // deferred type resolution
            length = t -> getLengthFunction((Class<T>) t.getClass()).applyAsInt(t);
        } else if (type.isArray()) {
            length = Array::getLength;
        } else if (CharSequence.class.isAssignableFrom(type)) {
            length = t -> ((CharSequence) t).length();
        } else if (Collection.class.isAssignableFrom(type)) {
            length = t -> ((Collection<?>) t).size();
        } else if (Map.class.isAssignableFrom(type)) {
            length = t -> ((Map<?, ?>) t).size();
        } else {
            throw new RuntimeFiberException("Instances of " + type + " do not have a known length or size");
        }
        return length;
    }

    private final ToIntFunction<T> lengthGetter;

    private LengthConstraint(ConstraintType type, ToIntFunction<T> lengthGetter, Integer value) {
        super(type, value);
        this.lengthGetter = lengthGetter;
    }

    @Override
    public boolean test(T value) {
        if (value == null) throw new NullPointerException("Cannot test the length of a null value");

        switch (getType()) {
            case MINIMUM_LENGTH:
                return lengthGetter.applyAsInt(value) >= this.getValue();
            case MAXIMUM_LENGTH:
                return lengthGetter.applyAsInt(value) <= this.getValue();
            default:
                throw new RuntimeFiberException("A StringLengthConstraint must be of type " + ConstraintType.MINIMUM_LENGTH + " or " + ConstraintType.MAXIMUM_LENGTH);
        }
    }
}
