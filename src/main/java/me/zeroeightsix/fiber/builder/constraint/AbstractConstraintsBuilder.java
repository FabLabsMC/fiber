package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.*;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import javax.annotation.Nullable;
import javax.annotation.RegEx;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @param <A> the type of {@link Constraint} this builder should output
 * @param <S> the type of this builder's source object (eg. {@code ConfigLeafBuilder} or {@code ConstraintsBuilder})
 * @param <T> the type of intermediary objects this builder's constraints should process. May be identical to {@code A}.
 */
public abstract class AbstractConstraintsBuilder<S, A, T> {

    protected final S source;
    protected final List<Constraint<? super A>> sourceConstraints;
    /*MonotonicNonnull*/ @Nullable protected final Class<T> type;

    final List<Constraint<? super T>> newConstraints = new ArrayList<>();

    AbstractConstraintsBuilder(S source, List<Constraint<? super A>> sourceConstraints, @Nullable Class<T> type) {
        this.source = source;
        this.sourceConstraints = sourceConstraints;
        this.type = type;
    }

    @Nullable
    public final Class<T> getType() {
        return type;
    }

    /**
     * Implies that any value must be bigger than <b>or equal</b> to <code>min</code>
     *
     * @param min The minimum value
     * @return The builder
     * @throws IllegalArgumentException if {@code min} is not a {@link Number}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AbstractConstraintsBuilder<S, A, T> atLeast(T min) throws RuntimeFiberException {
        checkNumerical();
        checkNumerical(min);
        newConstraints.add(new NumberConstraint(ConstraintType.NUMERICAL_LOWER_BOUND, (Number) min));
        return this;
    }

    /**
     * Implies that any value must be smaller than <b>or equal</b> to <code>max</code>
     *
     * @param max The maximum value
     * @return The builder
     * @throws UnsupportedOperationException if this builder is not for a numerical value
     * @throws IllegalArgumentException      if {@code max} is not a {@link Number}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AbstractConstraintsBuilder<S, A, T> atMost(T max) {
        checkNumerical();
        checkNumerical(max);
        newConstraints.add(new NumberConstraint(ConstraintType.NUMERICAL_UPPER_BOUND, (Number) max));
        return this;
    }

    /**
     * Convenience method to specify a range of valid values a number can take.
     *
     * <p> This method behaves as if: {@code this.atLeast(min).atMost(max)}
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return This builder
     * @throws UnsupportedOperationException if this builder is not for a numerical value
     * @throws IllegalArgumentException      if {@code min} or {@code max} is not a {@link Number}
     */
    public AbstractConstraintsBuilder<S, A, T> range(T min, T max) {
        atLeast(min);
        atMost(max);
        return this;
    }

    public AbstractConstraintsBuilder<S, A, T> minLength(int min) {
        if (min < 0) throw new RuntimeFiberException(min + " is not a valid length");
        newConstraints.add(LengthConstraint.min(type, min));
        return this;
    }

    public AbstractConstraintsBuilder<S, A, T> maxLength(int max) {
        if (max < 0) throw new RuntimeFiberException(max + " is not a valid length");
        newConstraints.add(LengthConstraint.max(type, max));
        return this;
    }

    @SuppressWarnings("unchecked")
    public AbstractConstraintsBuilder<S, A, T> regex(@RegEx String regexPattern) {
        checkCharSequence();
        newConstraints.add((Constraint<? super T>) new RegexConstraint(Pattern.compile(regexPattern)));
        return this;
    }

    private void checkNumerical() {
        if (this.type != null && !Number.class.isAssignableFrom(this.type))
            throw new RuntimeFiberException("Can't apply numerical constraint to non-numerical setting");
    }

    private void checkNumerical(T value) {
        if (this.type != null && !Number.class.isAssignableFrom(value.getClass()))
            throw new RuntimeFiberException("'" + value + "' is not a number");
    }

    private void checkCharSequence() {
        if (this.type != null && !CharSequence.class.isAssignableFrom(this.type))
            throw new RuntimeFiberException("Can only apply regex pattern constraint to character sequences");
    }

}
