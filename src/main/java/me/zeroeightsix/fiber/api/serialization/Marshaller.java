package me.zeroeightsix.fiber.api.serialization;

public interface Marshaller<T> {

    T marshall(Object value);
    <A> A marshallReverse(Class<A> type, T value);

}
