package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.ListSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;

/**
 * A generic config type processor that can output different {@link SerializableType}s
 * for a single class, based on generic type parameters.
 *
 * <p>Example implementation for a {@code List} type processor:
 * <pre>{@code (typeArguments) -> ConfigTypes.makeList(typeArguments[0])}</pre>
 *
 * @param <T> the bare type being processed, eg. {@code List<?>}.
 * @see ListSerializableType
 */
@FunctionalInterface
public interface ParameterizedTypeProcessor<T> {
	/**
	 * Produces usable config type information using the given {@code typeArguments}.
	 *
	 * @param typeArguments the generic type parameters
	 * @return a ConfigType representing the parameterized type
	 */
	ConfigType<? extends T, ?, ?> process(ConfigType<?, ?, ?>[] typeArguments);
}
