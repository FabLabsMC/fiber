package me.zeroeightsix.fiber.api.serialization;

import me.zeroeightsix.fiber.api.schema.type.BooleanSerializableType;
import me.zeroeightsix.fiber.api.schema.type.DecimalSerializableType;
import me.zeroeightsix.fiber.api.schema.type.EnumSerializableType;
import me.zeroeightsix.fiber.api.schema.type.ListSerializableType;
import me.zeroeightsix.fiber.api.schema.type.MapSerializableType;
import me.zeroeightsix.fiber.api.schema.type.RecordSerializableType;
import me.zeroeightsix.fiber.api.schema.type.SerializableType;
import me.zeroeightsix.fiber.api.schema.type.StringSerializableType;

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
