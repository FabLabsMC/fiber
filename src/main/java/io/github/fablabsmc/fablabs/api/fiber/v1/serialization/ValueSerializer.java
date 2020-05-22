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
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.StringSerializableType;

/**
 * Adapter between a tree serialization library and Fiber. To use a library like
 * GSON, Jankson, etc. with Fiber, simply implement this interface.
 *
 * <p>The values passed to the {@code serialize*} methods fulfill the constraints of their
 * corresponding types. However, in general, the serialized form passed to the {@code deserialize*}
 * methods may not fulfill the constraints, nor even be a correct representation of the platform type.
 * If a serializer cannot deserialize a value of the correct platform type as determined by
 * {@link SerializableType#cast(Object)}, then it shall throw a {@link ValueDeserializationException}
 * containing the element that could not be converted and the erased class type it was trying to convert to.
 * However, a serializer is not expected to produce values that satisfy the ancillary constraints of
 * the passed type. Client code is responsible for coercing deserialized values into a form consistent
 * with its type's constraints.
 *
 * <p>The methods of this interface which operate on serialized elements or targets may consume or otherwise
 * mutate the serialized representations passed to them.
 *
 * @param <A> The type of the serialized element representation, e.g. JsonElement.
 * @param <T> The type of the serialized aggregate representation, e.g. JsonObject. For recursive
 *            serialized forms, like JSON, this may be the same as or a subtype of A, but
 *            for non-recursive serialized forms this is may be an unrelated type.
 * @see SerializableType#serializeValue(Object, ValueSerializer)
 * @see SerializableType#deserializeValue(Object, ValueSerializer)
 */
public interface ValueSerializer<A, T> {
	/**
	 * Converts a boolean into the serialized form.
	 *
	 * @param value The boolean value.
	 * @param type  The type of the boolean value.
	 */
	A serializeBoolean(boolean value, BooleanSerializableType type);

	/**
	 * Converts the serialized form to a boolean.
	 *
	 * @param elem The serialized form.
	 * @param type The type of the boolean value.
	 * @throws ValueDeserializationException If the serialized form cannot be converted into an object of
	 *                                       the correct platform type.
	 */
	boolean deserializeBoolean(A elem, BooleanSerializableType type) throws ValueDeserializationException;

	/**
	 * Converts a number into the serialized form.
	 *
	 * @param value The numeric value.
	 * @param type  The type of the numeric value.
	 */
	A serializeNumber(BigDecimal value, DecimalSerializableType type);

	/**
	 * Converts the serialized form to a number.
	 *
	 * @param elem The serialized form.
	 * @param type The type of the numeric value.
	 * @throws ValueDeserializationException If the serialized form cannot be converted into an object of
	 *                                       the correct platform type.
	 */
	BigDecimal deserializeNumber(A elem, DecimalSerializableType type) throws ValueDeserializationException;

	/**
	 * Converts a string into the serialized form.
	 *
	 * @param value The string value.
	 * @param type  The type of the string value.
	 */
	A serializeString(String value, StringSerializableType type);

	/**
	 * Converts the serialized form to a string.
	 *
	 * @param elem The serialized form.
	 * @param type The type of the string value.
	 * @throws ValueDeserializationException If the serialized form cannot be converted into an object of
	 *                                       the correct platform type.
	 */
	String deserializeString(A elem, StringSerializableType type) throws ValueDeserializationException;

	/**
	 * Converts a enumerated string into the serialized form.
	 *
	 * @param value The enum value.
	 * @param type  The type of the enum value.
	 */
	A serializeEnum(String value, EnumSerializableType type);

	/**
	 * Converts the serialized form to a enumerated value.
	 *
	 * @param elem The serialized form.
	 * @param type The type of the enumerated value.
	 * @throws ValueDeserializationException If the serialized form cannot be converted into an object of
	 *                                       the correct platform type.
	 */
	String deserializeEnum(A elem, EnumSerializableType type) throws ValueDeserializationException;

	/**
	 * Converts a list into the serialized form.
	 *
	 * @param value The list value.
	 * @param type  The type of the list value.
	 */
	<E> A serializeList(List<E> value, ListSerializableType<E> type);

	/**
	 * Converts the serialized form to a list of values.
	 *
	 * @param elem The serialized form.
	 * @param type The type of the list value.
	 * @throws ValueDeserializationException If the serialized form cannot be converted into an object of
	 *                                       the correct platform type.
	 */
	<E> List<E> deserializeList(A elem, ListSerializableType<E> type) throws ValueDeserializationException;

	/**
	 * Converts a map into the serialized form.
	 *
	 * @param value The map value.
	 * @param type  The type of the map value.
	 */
	<V> A serializeMap(Map<String, V> value, MapSerializableType<V> type);

	/**
	 * Converts the serialized form to a map.
	 *
	 * @param elem The serialized form.
	 * @param type The type of the map value.
	 * @throws ValueDeserializationException If the serialized form cannot be converted into an object of
	 *                                       the correct platform type.
	 */
	<V> Map<String, V> deserializeMap(A elem, MapSerializableType<V> type) throws ValueDeserializationException;

	/**
	 * Converts a record into the serialized form.
	 *
	 * @param value The record value.
	 * @param type  The type of the record value.
	 */
	A serializeRecord(Map<String, Object> value, RecordSerializableType type);

	/**
	 * Converts the serialized form to a record.
	 *
	 * @param elem The serialized form.
	 * @param type The type of the record value.
	 * @throws ValueDeserializationException If the serialized form cannot be converted into an object of
	 *                                       the correct platform type.
	 */
	Map<String, Object> deserializeRecord(A elem, RecordSerializableType type) throws ValueDeserializationException;

	/**
	 * Aggregates a serialized value, associated with the given key, into the target. It is unspecified
	 * whether duplicate keys are allowed.
	 *
	 * @param name   The key with which to associate the value.
	 * @param elem   The serialized value.
	 * @param target The target aggregate.
	 */
	void addElement(String name, A elem, T target);

	/**
	 * Aggregates an existing target, associated with the given key, into the target as a sub-target. It
	 * is unspecified whether duplicate keys are allowed.
	 *
	 * @param name   The key with which to associate the sub-target.
	 * @param elem   The sub-target.
	 * @param target The target aggregate.
	 */
	void addSubElement(String name, T elem, T target);

	/**
	 * Extracts an {@link Iterator} over all aggregated key-value pairs in the given target.
	 *
	 * @param target The target.
	 * @return An Iterator over the elements in the target.
	 */
	Iterator<Map.Entry<String, A>> elements(T target);

	/**
	 * Extracts an {@link Iterator} over all aggregated key-value pairs in the given sub-target element.
	 *
	 * @param elem The serialized sub-target element.
	 * @return An Iterator over the elements in the sub-target.
	 * @throws ValueDeserializationException If elem does not represent a sub-target.
	 */
	Iterator<Map.Entry<String, A>> subElements(A elem) throws ValueDeserializationException;

	/**
	 * Writes a aggregate target to the given output stream.
	 *
	 * @param target The target.
	 * @param out    The output stream.
	 * @throws IOException If an IO error occurs while writing to the stream.
	 */
	void writeTarget(T target, OutputStream out) throws IOException;

	/**
	 * Reads an aggregate target from the given input stream.
	 *
	 * @param in The input stream.
	 * @return The target read from the stream.
	 * @throws ValueDeserializationException If a target cannot be read from the stream.
	 * @throws IOException                   If an IO error occurs while reading from the stream.
	 */
	T readTarget(InputStream in) throws ValueDeserializationException, IOException;

	/**
	 * Creates a new, empty aggregate target.
	 */
	T newTarget();
}
