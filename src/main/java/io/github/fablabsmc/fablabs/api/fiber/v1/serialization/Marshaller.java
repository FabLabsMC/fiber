package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

/**
 * Implementors of this interface marshall values to and from a serialized form.
 *
 * @param <T> The type of the serialized form.
 */
public interface Marshaller<T> {
	/**
	 * Serializes a value to the serialized form.
	 *
	 * @param value The value.
	 * @return The serialized form of the value.
	 */
	T marshall(Object value);

	/**
	 * Deserializes a value from the serialized form.
	 *
	 * @param type  The Java type of the value.
	 * @param value The serialized form.
	 * @param <A>   The type of the value.
	 * @return The value.
	 */
	<A> A marshallReverse(Class<A> type, T value);
}
