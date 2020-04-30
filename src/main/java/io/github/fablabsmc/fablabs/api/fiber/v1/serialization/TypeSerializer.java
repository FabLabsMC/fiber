package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.BooleanSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.DecimalSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.EnumSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.ListSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.MapSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.RecordSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.StringSerializableType;

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
