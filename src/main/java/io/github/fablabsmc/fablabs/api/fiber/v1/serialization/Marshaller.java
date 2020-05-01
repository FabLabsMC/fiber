package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

public interface Marshaller<T> {
	T marshall(Object value);

	<A> A marshallReverse(Class<A> type, T value);
}
