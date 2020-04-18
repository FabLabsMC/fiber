package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.LengthConstraint;
import me.zeroeightsix.fiber.constraint.RegexConstraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.schema.ConfigType;

import javax.annotation.Nullable;
import javax.annotation.RegEx;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @param <A> the type of {@link Constraint} this builder should output
 * @param <S> the type of this builder's source object (eg. {@code ConfigLeafBuilder} or {@code ConstraintsBuilder})
 * @param <T> the type of intermediary objects this builder's constraints should process. May be identical to {@code A}.
 */
public abstract class AbstractConstraintsBuilder<S, A, A0, T, T0> {

    protected final S source;
    protected T numberMin;
    protected T numberMax;
    protected T numberStep;
    protected final Set<Constraint<? super A0>> sourceConstraints;
    protected final ConfigType<T, T0> type;

    final Set<Constraint<? super T0>> newConstraints = new LinkedHashSet<>();

    AbstractConstraintsBuilder(S source, Set<Constraint<? super A0>> sourceConstraints, ConfigType<T, T0> type) {
        this.source = source;
        this.sourceConstraints = sourceConstraints;
        this.type = type;
    }

    @Nullable
    public final Class<T> getType() {
        return type.getActualType();
    }

    /**
     * Implies that any value must be bigger than <strong>or equal</strong> to <code>min</code>
     *
     * @param min The minimum value
     * @return The builder
     * @throws IllegalArgumentException if {@code min} is not a {@link Number}
     */
    public AbstractConstraintsBuilder<S, A, A0, T, T0> atLeast(T min) throws RuntimeFiberException {
        checkNumerical();
        checkNumerical(min);
        this.numberMin = min;
        return this;
    }

    /**
     * Implies that any value must be smaller than <strong>or equal</strong> to <code>max</code>
     *
     * @param max The maximum value
     * @return The builder
     * @throws UnsupportedOperationException if this builder is not for a numerical value
     * @throws IllegalArgumentException      if {@code max} is not a {@link Number}
     */
    public AbstractConstraintsBuilder<S, A, A0, T, T0> atMost(T max) {
        checkNumerical();
        checkNumerical(max);
        this.numberMax = max;
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
    public AbstractConstraintsBuilder<S, A, A0, T, T0> range(T min, T max) {
        checkNumerical();
        checkNumerical(max);
        return range(min, max, null);
    }

    /**
     * Convenience method to specify a range of valid values a number can take.
     *
     * <p> This method behaves as if: {@code this.atLeast(min).atMost(max)}
     *
     * @param min The minimum value
     * @param max The maximum value
     * @param step
     * @return This builder
     * @throws UnsupportedOperationException if this builder is not for a numerical value
     * @throws IllegalArgumentException      if {@code min} or {@code max} is not a {@link Number}
     */
    public AbstractConstraintsBuilder<S, A, A0, T, T0> range(T min, T max, @Nullable T step) {
        checkNumerical();
        checkNumerical(max);
        this.atLeast(min);
        this.atMost(max);
        this.numberStep = step;
        return this;
    }

    public AbstractConstraintsBuilder<S, A, A0, T, T0> minLength(int min) {
        if (min < 0) throw new RuntimeFiberException(min + " is not a valid length");
        newConstraints.add(LengthConstraint.min(type, min));
        return this;
    }

    public AbstractConstraintsBuilder<S, A, A0, T, T0> maxLength(int max) {
        if (max < 0) throw new RuntimeFiberException(max + " is not a valid length");
        newConstraints.add(LengthConstraint.max(type, max));
        return this;
    }

    @SuppressWarnings("unchecked")
    public AbstractConstraintsBuilder<S, A, A0, T, T0> regex(@RegEx String regexPattern) {
        checkCharSequence();
        newConstraints.add((Constraint<? super T0>) new RegexConstraint(Pattern.compile(regexPattern)));
        return this;
    }

    private void checkNumerical() {
        if (!this.type.isNumber()) {
            throw new RuntimeFiberException("Can't apply numerical constraint to non-numerical setting");
        }
    }

    private void checkNumerical(T value) {
        if (!Number.class.isAssignableFrom(value.getClass())) {
            throw new RuntimeFiberException("'" + value + "' is not a number");
        }
    }

    private void checkCharSequence() {
        if (!this.type.isString()) {
            throw new RuntimeFiberException("Can only apply regex pattern constraint to character sequences");
        }
    }

}
