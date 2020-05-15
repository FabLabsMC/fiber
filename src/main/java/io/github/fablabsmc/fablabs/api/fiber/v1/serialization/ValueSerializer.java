package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;

/**
 * Adapter between a tree serialization library and Fiber. To use a library like
 * JSON, Jankson, etc. with Fiber, simply implement this interface.
 *
 * @param <A> The type of the serialized element representation, e.g. JsonElement.
 *            This can be the same as T, but may be different.
 * @param <T> The type of the serialized object representation, e.g. JsonObject.
 */
public interface ValueSerializer<A, T> {
	A serializeBoolean(boolean value);

	boolean deserializeBoolean(A elem) throws ValueDeserializationException;

	A serializeNumber(BigDecimal value);

	BigDecimal deserializeNumber(A elem) throws ValueDeserializationException;

	A serializeString(String value);

	String deserializeString(A elem) throws ValueDeserializationException;

	A serializeList(List<A> value);

	List<A> deserializeList(A elem) throws ValueDeserializationException;

	A serializeMap(Map<String, A> value);

	Map<String, A> deserializeMap(A elem) throws ValueDeserializationException;

	void putElement(String name, A elem, T target);

	Optional<A> getElement(String name, T target);

	void writeTarget(T target, OutputStream out) throws IOException;

	T readTarget(InputStream in) throws ValueDeserializationException, IOException;

	T newTarget();
}
