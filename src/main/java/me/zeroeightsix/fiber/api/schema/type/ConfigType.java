package me.zeroeightsix.fiber.api.schema.type;

import me.zeroeightsix.fiber.api.constraint.TypeCheckResult;
import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.impl.constraint.Constraint;

/**
 * A configuration type, convertible to a config primitive.
 * @param <T>
 */
public abstract class ConfigType<T> {
    private final Class<T> platformType;

    ConfigType(Class<T> platformType) {
        this.platformType = platformType;
    }

    public Class<T> getPlatformType() {
        return this.platformType;
    }

    public final boolean isAssignableFrom(ConfigType<?> type) {
        if (this.getClass() != type.getClass()) {
            return false;
        }
        return this.getConstraint().comprehends(type.getConstraint());
    }

    public final boolean accepts(T serializedValue) {
        return this.test(serializedValue).hasPassed();
    }

    public final TypeCheckResult<T> test(T serializedValue) {
        return this.getConstraint().test(serializedValue);
    }

    public abstract <S> void serialize(TypeSerializer<S> serializer, S target);

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.getPlatformType().getSimpleName() + ")";
    }

    @Override
    public abstract boolean equals(Object o);

    public abstract int hashCode();

    protected abstract Constraint<T, ?> getConstraint();
}
