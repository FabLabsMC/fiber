package me.zeroeightsix.fiber.schema;

import me.zeroeightsix.fiber.FiberId;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.ConstraintType;
import me.zeroeightsix.fiber.constraint.ValuedConstraint;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * A configuration type, convertible to a config primitive.
 * @param <T>
 * @param <T0>
 */
public abstract class ConfigType<T, T0> {
    private final Class<T> actualType;
    private final Class<T0> rawType;
    protected final Map<ConstraintType, Constraint<? super T0>> indexedConstraints;
    protected final Function<T, T0> f0;
    protected final Function<T0, T> f;

    ConfigType(Class<T> actualType, Class<T0> rawType, Function<T, T0> f0, Function<T0, T> f, Map<ConstraintType, Constraint<? super T0>> typeConstraints) {
        this.actualType = actualType;
        this.rawType = rawType;
        this.indexedConstraints = typeConstraints;
        this.f0 = f0;
        this.f = f;
    }

    public Class<T> getActualType() {
        return this.actualType;
    }

    public Class<T0> getRawType() {
        return this.rawType;
    }

    public T0 toRawType(T actualValue) {
        return this.f0.apply(actualValue);
    }

    public T toActualType(T0 rawValue) {
        return this.f.apply(rawValue);
    }

    public Collection<Constraint<? super T0>> getConstraints() {
        return this.indexedConstraints.values();
    }

    public abstract Kind getKind();

    public abstract <T1> ConfigType<T1, T0> derive(Class<? super T1> actualType, Function<T1, T> f0, Function<T, T1> f);

    @Override
    public String toString() {
        return this.getActualType().getTypeName() + "(" + this.getKind() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ConfigType<?, ?> that = (ConfigType<?, ?>) o;
        return Objects.equals(this.actualType, that.actualType) &&
                Objects.equals(this.rawType, that.rawType) &&
                Objects.equals(this.indexedConstraints, that.indexedConstraints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.actualType, this.rawType, this.indexedConstraints);
    }

    protected <V, C extends ValuedConstraint<V, ? super T0>> Map<ConstraintType, Constraint<? super T0>> updateConstraints(C added, BiPredicate<V, V> check) {
        Map<ConstraintType, Constraint<? super T0>> newConstraints = new EnumMap<>(ConstraintType.class);
        newConstraints.putAll(this.indexedConstraints);
        // Constraints can only be inserted by "trusted" code
        @SuppressWarnings("unchecked") C current = (C) newConstraints.remove(added.getType());
        if (current != null && !check.test(current.getConstraintValue(), added.getConstraintValue())) {
            throw new IllegalArgumentException("Cannot widen an already constrained type (current: " + current.getConstraintValue() + ", added: " + added.getConstraintValue() + ")");
        }
        newConstraints.put(ConstraintType.MINIMUM_LENGTH, added);
        return Collections.unmodifiableMap(newConstraints);
    }

    public enum Kind {
        BOOLEAN("boolean"), DECIMAL("number"), LIST("list"), STRING("string");

        private final FiberId identifier;

        Kind(String name) {
            this.identifier = new FiberId("fiber", name);
        }

        public FiberId getIdentifier() {
            return this.identifier;
        }
    }
}
