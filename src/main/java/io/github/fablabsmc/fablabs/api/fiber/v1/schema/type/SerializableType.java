package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Objects;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.TypeSerializer;
import io.github.fablabsmc.fablabs.impl.fiber.constraint.ConstraintChecker;

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
	private final Class<? super T> platformType;
	private final ConstraintChecker<T, SerializableType<T>> checker;

	@SuppressWarnings("unchecked")
	SerializableType(Class<? super T> platformType, ConstraintChecker<T, ? extends SerializableType<T>> checker) {
		this.platformType = platformType;
		this.checker = (ConstraintChecker<T, SerializableType<T>>) checker;
	}

	/**
	 * The (erased) Java platform type used to represent values of this type.
	 *
	 * @deprecated Use {@link #getErasedPlatformType()} instead.
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public Class<T> getPlatformType() {
		return (Class<T>) this.getErasedPlatformType();
	}

	/**
	 * The (erased) Java platform type used to represent values of this type.
	 */
	public Class<? super T> getErasedPlatformType() {
		return this.platformType;
	}

	/**
	 * The generic Java platform type used to represent values of this type.
	 */
	public abstract Type getGenericPlatformType();

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
		return this.checker.test(this, Objects.requireNonNull(serializedValue));
	}

	public abstract <S> void serialize(TypeSerializer<S> serializer, S target);

	@Override
	public abstract String toString();

	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();
}
