package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import javax.annotation.Nonnull;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.TypeSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.ValueSerializer;
import io.github.fablabsmc.fablabs.impl.fiber.constraint.MapConstraintChecker;

public final class MapSerializableType<V> extends ParameterizedSerializableType<Map<String, V>> {
	private final StringSerializableType keyType;
	private final SerializableType<V> valueType;
	private final int minSize;
	private final int maxSize;

	public MapSerializableType(SerializableType<V> valueType) {
		this(StringSerializableType.DEFAULT_STRING, valueType);
	}

	public MapSerializableType(StringSerializableType keyType, SerializableType<V> valueType) {
		this(keyType, valueType, 0, Integer.MAX_VALUE);
	}

	public MapSerializableType(StringSerializableType keyType, SerializableType<V> valueType, int minSize, int maxSize) {
		super(Map.class, MapConstraintChecker.instance());
		this.keyType = keyType;
		this.valueType = valueType;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	public StringSerializableType getKeyType() {
		return this.keyType;
	}

	public SerializableType<V> getValueType() {
		return this.valueType;
	}

	public int getMinSize() {
		return this.minSize;
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	@Override
	public ParameterizedType getParameterizedType() {
		return new ParameterizedTypeImpl(this.getErasedPlatformType(), String.class, this.valueType.getGenericPlatformType());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, V> cast(@Nonnull Object value) {
		Map<?, ?> map = (Map<?, ?>) value;

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof String)) {
				throw new ClassCastException("non-String map key " + entry.getKey());
			}

			try {
				this.valueType.cast(entry.getValue());
			} catch (ClassCastException e) {
				ClassCastException ex = new ClassCastException("map value " + entry.getValue());
				ex.initCause(e);
				throw ex;
			}
		}

		return (Map<String, V>) map;
	}

	@Override
	public <S> void serialize(TypeSerializer<S> serializer, S target) {
		serializer.serialize(this, target);
	}

	@Override
	public <S> S serializeValue(Map<String, V> value, ValueSerializer<S, ?> serializer) {
		return serializer.serializeMap(value, this);
	}

	@Override
	public <S> Map<String, V> deserializeValue(S elem, ValueSerializer<S, ?> serializer) throws ValueDeserializationException {
		return serializer.deserializeMap(elem, this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		MapSerializableType<?> that = (MapSerializableType<?>) o;
		return this.minSize == that.minSize
				&& this.maxSize == that.maxSize
				&& Objects.equals(this.valueType, that.valueType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.valueType, this.minSize, this.maxSize);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", MapSerializableType.class.getSimpleName() + "[", "]")
				.add("minSize=" + minSize)
				.add("maxSize=" + maxSize)
				.toString();
	}
}
