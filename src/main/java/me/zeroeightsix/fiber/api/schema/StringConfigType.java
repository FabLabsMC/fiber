package me.zeroeightsix.fiber.api.schema;

import me.zeroeightsix.fiber.api.constraint.Constraint;
import me.zeroeightsix.fiber.api.constraint.ConstraintType;
import me.zeroeightsix.fiber.impl.constraint.EnumConstraint;
import me.zeroeightsix.fiber.impl.constraint.LengthConstraint;
import me.zeroeightsix.fiber.impl.constraint.RegexConstraint;
import me.zeroeightsix.fiber.impl.constraint.ValuedConstraint;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class StringConfigType<T> extends ConfigType<T, String> {
    public static final StringConfigType<String> STRING = new StringConfigType<>(String.class, Function.identity(), Function.identity(), Collections.emptyMap());

    private StringConfigType(Class<T> actualType, Function<T, String> f0, Function<String, T> f, Map<ConstraintType, Constraint<? super String>> typeConstraints) {
        super(actualType, String.class, f0, f, typeConstraints);
    }

    @Override
    public Kind getKind() {
        return Kind.STRING;
    }

    @Override
    public <T1> StringConfigType<T1> derive(Class<? super T1> actualType, Function<T1, T> f0, Function<T, T1> f) {
        @SuppressWarnings("unchecked") Class<T1> c = (Class<T1>) actualType;
        return new StringConfigType<>(c, v -> this.toRawType(f0.apply(v)), v -> f.apply(this.toActualType(v)), this.indexedConstraints);
    }

    public StringConfigType<T> withMinLength(int min) {
        return this.withConstraints(LengthConstraint.min(String::length, min), (current, added) -> current < min);
    }

    public StringConfigType<T> withMaxLength(int max) {
        return this.withConstraints(LengthConstraint.max(String::length, max), (current, added) -> current > max);
    }

    public StringConfigType<T> withPattern(String regex) {
        return this.withPattern(Pattern.compile(regex));
    }

    public StringConfigType<T> withPattern(Pattern pattern) {
        // TODO detect if the current pattern matches every string the new pattern can?
        return this.withConstraints(new RegexConstraint(pattern), (current, added) -> false);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public final StringConfigType<T> withValidValues(T... validValues) {
        return this.withValidValues(new HashSet<>(Arrays.asList(validValues)));
    }

    public StringConfigType<T> withValidValues(Set<T> validValues) {
        Set<String> rawValidValues = validValues.stream().map(this::toRawType).collect(Collectors.toSet());
        return this.withConstraints(new EnumConstraint<>(rawValidValues), Set::containsAll);
    }

    private <V, C extends ValuedConstraint<V, ? super String>> StringConfigType<T> withConstraints(C added, BiPredicate<V, V> check) {
        return new StringConfigType<>(this.getActualType(), this.f0, this.f, this.updateConstraints(added, check));
    }
}
