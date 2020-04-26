package me.zeroeightsix.fiber.api.schema.type;

import me.zeroeightsix.fiber.api.schema.type.derived.ConfigType;
import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.impl.constraint.Constraint;

import java.math.BigDecimal;

/**
 * A data type that is convertible to a config primitive.
 *
 * <p> A {@code SerializableType} constrains the range of possible values
 * through additional properties. This means that for each kind of
 * {@code SerializableType} there may be an arbitrary amount of different
 * sets of valid values, all being mapped to the same platform type.
 * For example, {@link Integer} and {@link Double} are both represented
 * as {@link DecimalSerializableType}s and stored as {@link BigDecimal}s,
 * but with different constraints.
 *
 * @param <T> the actual platform type used to store the serializable data
 * @see ConfigType
 * @see BooleanSerializableType
 * @see EnumSerializableType
 * @see ListSerializableType
 * @see MapSerializableType
 * @see DecimalSerializableType
 * @see RecordSerializableType
 * @see StringSerializableType
 */
public abstract class SerializableType<T> {
    private final Class<T> platformType;

    SerializableType(Class<T> platformType) {
        this.platformType = platformType;
    }

    public Class<T> getPlatformType() {
        return this.platformType;
    }

    public final boolean isAssignableFrom(SerializableType<?> type) {
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
    public abstract String toString();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    protected abstract Constraint<T, ?> getConstraint();
}
