package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.*;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import javax.annotation.RegEx;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @param <A> the type of {@link Constraint} this builder should output
 * @param <S> the type of this builder's source object (eg. {@code ConfigValueBuilder} or {@code ConstraintsBuilder}
 * @param <T> the type of intermediary objects this builder's constraints should process. May be identical to {@code A}.
 * @param <B> the type of {@code this}, for chaining
 */
public abstract class AbstractConstraintsBuilder<S, A, T, B extends AbstractConstraintsBuilder<S, A, T, B>> {

    protected final S source;
    protected final List<Constraint<? super A>> sourceConstraints;
    protected final Class<T> type;

    final List<Constraint<? super T>> newConstraints = new ArrayList<>();

    AbstractConstraintsBuilder(S source, List<Constraint<? super A>> sourceConstraints, Class<T> type) {
        this.source = source;
        this.sourceConstraints = sourceConstraints;
        this.type = type;
    }

    /**
     * Implies that any value must be bigger than <b>or equal</b> to <code>min</code>
     * @param min The minimum value
     * @return The builder
     * @throws RuntimeFiberException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public B biggerThan(T min) throws RuntimeFiberException {
        checkNumerical();
        checkNumerical(min);
        newConstraints.add(new NumberConstraint(Constraints.NUMERICAL_LOWER_BOUND, (Number) min));
        return self();
    }

    /**
     * Implies that any value must be smaller than <b>or equal</b> to <code>max</code>
     * @param max The maximum value
     * @return The builder
     * @throws RuntimeFiberException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public B smallerThan(T max) throws RuntimeFiberException {
        checkNumerical();
        checkNumerical(max);
        newConstraints.add(new NumberConstraint(Constraints.NUMERICAL_UPPER_BOUND, (Number) max));
        return self();
    }

    @SuppressWarnings("unchecked")
    public B minStringLength(int min) {
        checkCharSequence();
        newConstraints.add((Constraint<? super T>) new StringLengthConstraint(Constraints.STRING_MINIMUM_LENGTH, min));
        return self();
    }

    @SuppressWarnings("unchecked")
    public B maxStringLength(int min) {
        checkCharSequence();
        newConstraints.add((Constraint<? super T>) new StringLengthConstraint(Constraints.STRING_MAXIMUM_LENGTH, min));
        return self();
    }

    @SuppressWarnings("unchecked")
    public B regex(@RegEx String regexPattern) {
        checkCharSequence();
        newConstraints.add((Constraint<? super T>) new RegexConstraint(Pattern.compile(regexPattern)));
        return self();
    }

    private void checkNumerical() {
        if (!Number.class.isAssignableFrom(this.type))
            throw new UnsupportedOperationException("Can't apply numerical constraint to non-numerical setting");
    }

    private void checkNumerical(T value) {
        if (!Number.class.isAssignableFrom(value.getClass()))
            throw new IllegalArgumentException("'" + value + "' is not a number");
    }

    private void checkCharSequence() {
        if (!CharSequence.class.isAssignableFrom(this.type))
            throw new UnsupportedOperationException("Can only apply regex pattern constraint to character sequences");
    }

    @SuppressWarnings("unchecked")
    private B self() {
        return (B) this;
    }
}
