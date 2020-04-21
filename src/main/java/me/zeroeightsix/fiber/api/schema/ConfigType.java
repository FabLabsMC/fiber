package me.zeroeightsix.fiber.api.schema;

import me.zeroeightsix.fiber.api.FiberId;
import me.zeroeightsix.fiber.api.constraint.Constraint;
import me.zeroeightsix.fiber.api.constraint.ConstraintType;
import me.zeroeightsix.fiber.impl.constraint.ValuedConstraint;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * A configuration type, convertible to a config primitive.
 * @param <T> the logical, or runtime, type of config values
 * @param <S> the serialized type of config values
 */
public abstract class ConfigType<T, S> {
    private final Class<T> actualType;
    private final Class<S> serializedType;
    protected final Map<ConstraintType, Constraint<? super S>> indexedConstraints;
    protected final Function<T, S> f0;
    protected final Function<S, T> f;

    ConfigType(Class<T> actualType, Class<S> serializedType, Function<T, S> f0, Function<S, T> f, Map<ConstraintType, Constraint<? super S>> typeConstraints) {
        this.actualType = actualType;
        this.serializedType = serializedType;
        this.indexedConstraints = typeConstraints;
        this.f0 = f0;
        this.f = f;
    }

    public Class<T> getActualType() {
        return this.actualType;
    }

    public Class<S> getSerializedType() {
        return this.serializedType;
    }

    public S toSerializedType(T actualValue) {
        return this.f0.apply(actualValue);
    }

    public T toActualType(S rawValue) {
        return this.f.apply(rawValue);
    }

    public Collection<Constraint<? super S>> getConstraints() {
        return this.indexedConstraints.values();
    }

    public abstract Kind getKind();

    public abstract <T1> ConfigType<T1, S> derive(Class<? super T1> actualType, Function<T1, T> f0, Function<T, T1> f);

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
                Objects.equals(this.serializedType, that.serializedType) &&
                Objects.equals(this.indexedConstraints, that.indexedConstraints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.actualType, this.serializedType, this.indexedConstraints);
    }

    protected <V, C extends ValuedConstraint<V, ? super S>> Map<ConstraintType, Constraint<? super S>> updateConstraints(C added, BiPredicate<V, V> check) {
        Map<ConstraintType, Constraint<? super S>> newConstraints = new EnumMap<>(ConstraintType.class);
        newConstraints.putAll(this.indexedConstraints);
        // Constraints can only be inserted by "trusted" code
        @SuppressWarnings("unchecked") C current = (C) newConstraints.remove(added.getType());
        if (current != null && !check.test(current.getConstraintValue(), added.getConstraintValue())) {
            throw new IllegalArgumentException("Cannot widen an already constrained type (current: " + current.getConstraintValue() + ", added: " + added.getConstraintValue() + ")");
        }
        newConstraints.put(added.getType(), added);
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
