package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.BooleanSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.DecimalSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.EnumSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.ListSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.MapSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.RecordSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.StringSerializableType;

/**
 * Adapter between a tree serialization library and Fiber. To use a library like
 * GSON, Jankson, etc. with Fiber, simply implement this interface.
 *
 * @param <A> The type of the serialized element representation, e.g. JsonElement.
 * @param <T> The type of the serialized aggregate representation, e.g. JsonObject. For recursive
 *            serialized forms, like JSON, this may be the same as or a subtype of A, but
 *            for non-recursive serialized forms this is may be an unrelated type.
 */
public interface ValueSerializer<A, T> {
	A serializeBoolean(boolean value, BooleanSerializableType type);

	boolean deserializeBoolean(A elem, BooleanSerializableType type) throws ValueDeserializationException;

	A serializeNumber(BigDecimal value, DecimalSerializableType type);

	BigDecimal deserializeNumber(A elem, DecimalSerializableType type) throws ValueDeserializationException;

	A serializeString(String value, StringSerializableType type);

	String deserializeString(A elem, StringSerializableType type) throws ValueDeserializationException;

	A serializeEnum(String value, EnumSerializableType type);

	String deserializeEnum(A elem, EnumSerializableType type) throws ValueDeserializationException;

	<E> A serializeList(List<E> value, ListSerializableType<E> type);

	<E> List<E> deserializeList(A elem, ListSerializableType<E> type) throws ValueDeserializationException;

	<V> A serializeMap(Map<String, V> value, MapSerializableType<V> type);

	<V> Map<String, V> deserializeMap(A elem, MapSerializableType<V> type) throws ValueDeserializationException;

	A serializeRecord(Map<String, Object> value, RecordSerializableType type);

	Map<String, Object> deserializeRecord(A elem, RecordSerializableType type) throws ValueDeserializationException;

	void addElement(String name, A elem, T target);

	void addSubElement(String name, T elem, T target);

	Iterator<Map.Entry<String, A>> elements(T target);

	Iterator<Map.Entry<String, A>> subElements(A elem) throws ValueDeserializationException;

	void writeTarget(T target, OutputStream out) throws IOException;

	T readTarget(InputStream in) throws ValueDeserializationException, IOException;

	T newTarget();
}
