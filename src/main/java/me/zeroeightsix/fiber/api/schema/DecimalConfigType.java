package me.zeroeightsix.fiber.api.schema;

import me.zeroeightsix.fiber.api.constraint.Constraint;
import me.zeroeightsix.fiber.api.constraint.ConstraintType;
import me.zeroeightsix.fiber.impl.constraint.EnumConstraint;
import me.zeroeightsix.fiber.impl.constraint.NumberRange;
import me.zeroeightsix.fiber.impl.constraint.RangeConstraint;
import me.zeroeightsix.fiber.impl.constraint.ValuedConstraint;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DecimalConfigType<T> extends ConfigType<T, BigDecimal> {
    public static final DecimalConfigType<BigDecimal> UNBOUNDED = new DecimalConfigType<>(BigDecimal.class, Function.identity(), Function.identity(), Collections.emptyMap());

    private DecimalConfigType(Class<T> actualType, Function<T, BigDecimal> f0, Function<BigDecimal, T> f, Map<ConstraintType, Constraint<? super BigDecimal>> typeConstraints) {
        super(actualType, BigDecimal.class, f0, f, typeConstraints);
    }

    @Override
    public Kind getKind() {
        return Kind.DECIMAL;
    }

    @Override
    public <T1> DecimalConfigType<T1> derive(Class<? super T1> actualType, Function<T1, T> f0, Function<T, T1> f) {
        @SuppressWarnings("unchecked") Class<T1> c = (Class<T1>) actualType;
        return new DecimalConfigType<>(c, v -> this.toSerializedType(f0.apply(v)), v -> f.apply(this.toActualType(v)), this.indexedConstraints);
    }

    public DecimalConfigType<T> withMinimum(T min) {
        RangeConstraint current = (RangeConstraint) this.indexedConstraints.get(ConstraintType.RANGE);
        NumberRange currentRange = current == null ? NumberRange.UNBOUNDED : current.getConstraintValue();
        NumberRange acceptedValues = new NumberRange(this.toSerializedType(min), currentRange.max, currentRange.step);
        return this.withConstraints(new RangeConstraint(acceptedValues), NumberRange::contains);
    }

    public DecimalConfigType<T> withMaximum(T max) {
        RangeConstraint current = (RangeConstraint) this.indexedConstraints.get(ConstraintType.RANGE);
        NumberRange currentRange = current == null ? NumberRange.UNBOUNDED : current.getConstraintValue();
        NumberRange acceptedValues = new NumberRange(currentRange.min, this.toSerializedType(max), currentRange.step);
        return this.withConstraints(new RangeConstraint(acceptedValues), NumberRange::contains);
    }

    public DecimalConfigType<T> withIncrement(T step) {
        RangeConstraint current = (RangeConstraint) this.indexedConstraints.get(ConstraintType.RANGE);
        NumberRange currentRange = current == null ? NumberRange.UNBOUNDED : current.getConstraintValue();
        NumberRange acceptedValues = new NumberRange(currentRange.min, currentRange.max, this.toSerializedType(step));
        return this.withConstraints(new RangeConstraint(acceptedValues), NumberRange::contains);
    }

    public DecimalConfigType<T> withValidRange(T min, T max, T step) {
        NumberRange acceptedValues = new NumberRange(this.toSerializedType(min), this.toSerializedType(max), this.toSerializedType(step));
        return this.withConstraints(new RangeConstraint(acceptedValues), NumberRange::contains);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public final DecimalConfigType<T> withValidValues(T... validValues) {
        return this.withValidValues(new HashSet<>(Arrays.asList(validValues)));
    }

    public DecimalConfigType<T> withValidValues(Set<T> validValues) {
        Set<BigDecimal> rawValidValues = validValues.stream().map(this::toSerializedType).collect(Collectors.toSet());
        return this.withConstraints(new EnumConstraint<>(rawValidValues), Set::containsAll);
    }

    private <V, C extends ValuedConstraint<V, ? super BigDecimal>> DecimalConfigType<T> withConstraints(C added, BiPredicate<V, V> check) {
        return new DecimalConfigType<>(this.getActualType(), this::toSerializedType, this::toActualType, this.updateConstraints(added, check));
    }
}