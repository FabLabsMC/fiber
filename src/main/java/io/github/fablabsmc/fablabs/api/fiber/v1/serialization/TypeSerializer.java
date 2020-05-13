package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.BooleanSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.DecimalSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.EnumSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.ListSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.MapSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.RecordSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.StringSerializableType;

/**
 * A serializer visitor for {@link SerializableType} schemas.
 *
 * @param <T> The type of the serialized form.
 */
public interface TypeSerializer<T> {
	/**
	 * Serializes a boolean schema to the target.
	 */
	void serialize(BooleanSerializableType type, T target);

	/**
	 * Serializes a numeric range schema to the target.
	 */
	void serialize(DecimalSerializableType type, T target);

	/**
	 * Serializes a fixed value schema to the target.
	 */
	void serialize(EnumSerializableType type, T target);

	/**
	 * Serializes a list schema to the target.
	 */
	void serialize(ListSerializableType<?> type, T target);

	/**
	 * Serializes a map schema to the target.
	 */
	void serialize(MapSerializableType<?> type, T target);

	/**
	 * Serializes a record schema to the target.
	 */
	void serialize(RecordSerializableType type, T target);

	/**
	 * Serializes a regex-defined string schema to the target.
	 */
	void serialize(StringSerializableType type, T target);

	/**
	 * Serializes the given {@link SerializableType} to the target.
	 *
	 * <p>This method polymorphically dispatches over the other methods of this type.
	 */
	default void serializeType(SerializableType<?> type, T target) {
		// named differently to ensure unrecognized subclass -> compiler error
		type.serialize(this, target);
	}
}
