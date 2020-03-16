package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.*;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import javax.annotation.RegEx;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractConstraintsBuilder<T, B extends AbstractConstraintsBuilder<T, B>> {

    final List<Constraint<? super T>> sourceConstraints;
    protected final Class<T> type;

    final List<Constraint<? super T>> newConstraints = new ArrayList<>();

    AbstractConstraintsBuilder(List<Constraint<? super T>> sourceConstraints, Class<T> type) {
        this.sourceConstraints = sourceConstraints;
        this.type = type;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public B minNumerical(T min) throws RuntimeFiberException {
        checkNumerical();
        checkNumerical(min);
        newConstraints.add(new NumberConstraint(Constraints.NUMERICAL_LOWER_BOUND, (Number) min));
        return (B) this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public B maxNumerical(T min) throws RuntimeFiberException {
        checkNumerical();
        checkNumerical(min);
        newConstraints.add(new NumberConstraint(Constraints.NUMERICAL_UPPER_BOUND, (Number) min));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B minStringLength(int min) {
        checkCharSequence();
        newConstraints.add((Constraint<? super T>) new StringLengthConstraint(Constraints.STRING_MINIMUM_LENGTH, min));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B maxStringLength(int min) {
        checkCharSequence();
        newConstraints.add((Constraint<? super T>) new StringLengthConstraint(Constraints.STRING_MAXIMUM_LENGTH, min));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B regex(@RegEx String regexPattern) {
        checkCharSequence();
        newConstraints.add((Constraint<? super T>) new RegexConstraint(Pattern.compile(regexPattern)));
        return (B) this;
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

    void addConstraints() {
        sourceConstraints.addAll(newConstraints);
    }
}
