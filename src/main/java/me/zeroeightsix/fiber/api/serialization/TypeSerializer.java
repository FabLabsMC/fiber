package me.zeroeightsix.fiber.api.serialization;

import me.zeroeightsix.fiber.api.schema.type.*;

public interface TypeSerializer<T> {
    void serialize(BooleanSerializableType type, T target);
    void serialize(DecimalSerializableType type, T target);
    void serialize(EnumSerializableType type, T target);
    void serialize(ListSerializableType<?> type, T target);
    void serialize(MapSerializableType<?> type, T target);
    void serialize(RecordSerializableType type, T target);
    void serialize(StringSerializableType type, T target);

    default void serializeType(SerializableType<?> type, T target) {
        // named differently to ensure unrecognized subclass -> compiler error
        type.serialize(this, target);
    }
}
