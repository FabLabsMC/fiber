package me.zeroeightsix.fiber;

public interface Marshaller<T> {

    T marshall(Object value);
    <A> A marshallReverse(Class<A> type, T value);

}
