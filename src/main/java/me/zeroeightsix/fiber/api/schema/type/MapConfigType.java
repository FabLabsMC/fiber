package me.zeroeightsix.fiber.api.schema.type;

import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.impl.constraint.MapTypeChecker;

import java.util.Map;
import java.util.Objects;

public final class MapConfigType<V> extends ConfigType<Map<String, V>> {
    private final ConfigType<V> valueType;
    private final int minSize;
    private final int maxSize;
    private final MapTypeChecker<V> constraint;

    @SuppressWarnings("unchecked")
    public MapConfigType(ConfigType<V> valueType, int minSize, int maxSize) {
        super((Class<Map<String, V>>) (Class<?>) Map.class);
        this.valueType = valueType;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.constraint = new MapTypeChecker<>(this);
    }

    public ConfigType<V> getValueType() {
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
    protected MapTypeChecker<V> getConstraint() {
        return this.constraint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        MapConfigType<?> that = (MapConfigType<?>) o;
        return this.minSize == that.minSize &&
                this.maxSize == that.maxSize &&
                Objects.equals(this.valueType, that.valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.valueType, this.minSize, this.maxSize);
    }
}
