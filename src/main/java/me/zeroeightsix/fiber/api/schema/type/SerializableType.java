package me.zeroeightsix.fiber.api.schema.type;

import java.math.BigDecimal;

import me.zeroeightsix.fiber.api.schema.type.derived.ConfigType;
import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.impl.constraint.ConstraintChecker;

/**
 * A data type that is convertible to a config primitive.
 *
 * <p>A {@code SerializableType} constrains the range of possible values
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
	private final ConstraintChecker<T, SerializableType<T>> checker;

	@SuppressWarnings("unchecked")
	SerializableType(Class<T> platformType, ConstraintChecker<T, ? extends SerializableType<T>> checker) {
		this.platformType = platformType;
		this.checker = (ConstraintChecker<T, SerializableType<T>>) checker;
	}

	public Class<T> getPlatformType() {
		return this.platformType;
	}

	/**
	 * Determines if the data type represented by this {@code SerializableType}
	 * object is either the same as, or is a more general description of, the data
	 * type represented by the specified {@code type} parameter.
	 *
	 * <p>Specifically, this checks whether every value assignable to {@code type}
	 * can also be assigned to this {@code SerializableType}.
	 * <pre>forall x, type.accepts(x) =&gt; this.accepts(x)</pre>
	 *
	 * @param type the type to be checked
	 * @return {@code true} if properties of {@code this} type are assignable from values of {@code type}.
	 */
	public final boolean isAssignableFrom(SerializableType<?> type) {
		if (this.getClass() != type.getClass()) {
			return false;
		}

		@SuppressWarnings("unchecked") SerializableType<T> that = (SerializableType<T>) type;
		return this.checker.comprehends(this, that);
	}

	public final boolean accepts(T serializedValue) {
		return this.test(serializedValue).hasPassed();
	}

	public final TypeCheckResult<T> test(T serializedValue) {
		return this.checker.test(this, serializedValue);
	}

	public abstract <S> void serialize(TypeSerializer<S> serializer, S target);

	@Override
	public abstract String toString();

	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();
}
