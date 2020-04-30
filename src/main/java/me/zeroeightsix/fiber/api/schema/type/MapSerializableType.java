package me.zeroeightsix.fiber.api.schema.type;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.impl.constraint.MapConstraintChecker;

public final class MapSerializableType<V> extends SerializableType<Map<String, V>> {
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

    @SuppressWarnings("unchecked")
    public MapSerializableType(StringSerializableType keyType, SerializableType<V> valueType, int minSize, int maxSize) {
        super((Class<Map<String, V>>) (Class<?>) Map.class, MapConstraintChecker.instance());
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
    public <S> void serialize(TypeSerializer<S> serializer, S target) {
        serializer.serialize(this, target);
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
