package me.zeroeightsix.fiber;

public interface Converter<F, T> {

	F serialize(T data);
	T deserialize(F object);

}
