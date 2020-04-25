package me.zeroeightsix.fiber.api.serialization;

import me.zeroeightsix.fiber.api.schema.type.*;

public interface TypeSerializer<T> {
    void serialize(BooleanConfigType type, T target);
    void serialize(DecimalConfigType type, T target);
    void serialize(EnumConfigType    type, T target);
    void serialize(ListConfigType<?> type, T target);
    void serialize(MapConfigType<?>  type, T target);
    void serialize(RecordConfigType  type, T target);
    void serialize(StringConfigType  type, T target);

    default void serializeType(ConfigType<?> type, T target) {
        // named differently to ensure unrecognized subclass -> compiler error
        type.serialize(this, target);
    }
}
