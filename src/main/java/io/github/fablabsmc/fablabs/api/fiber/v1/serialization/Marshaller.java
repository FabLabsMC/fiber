package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

import java.lang.reflect.Type;

public interface Marshaller<T> {
	T marshall(Object value);

	<A> A marshallReverse(Type type, T value);
}
