package me.zeroeightsix.fiber.api.annotation.processor;

import me.zeroeightsix.fiber.api.schema.type.ListSerializableType;
import me.zeroeightsix.fiber.api.schema.type.SerializableType;
import me.zeroeightsix.fiber.api.schema.type.derived.ConfigType;

/**
 * A generic config type processor that can output different {@link SerializableType}s
 * for a single class, based on generic type parameters.
 *
 * <p> Example implementation for a {@code List} type processor:
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
