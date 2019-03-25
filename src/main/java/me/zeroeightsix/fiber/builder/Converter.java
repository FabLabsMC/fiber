package me.zeroeightsix.fiber.builder;

public interface Converter<F, T> {

	F serialize(T data);
	T deserialize(F object);

}
