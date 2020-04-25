package me.zeroeightsix.fiber.api.annotation.processor;

import me.zeroeightsix.fiber.api.schema.type.ConfigType;
import me.zeroeightsix.fiber.api.schema.type.ListConfigType;
import me.zeroeightsix.fiber.api.schema.type.derived.DerivedType;

/**
 * A generic config type processor that can output different {@link ConfigType}s
 * for a single class, based on generic type parameters.
 *
 * <p> Example implementation for a {@code List} type processor:
 * <pre>{@code (typeArguments) -> ConfigTypes.makeList(typeArguments[0])}</pre>
 *
 * @param <T> the bare type being processed, eg. {@code List<?>}.
 * @see ListConfigType
 */
@FunctionalInterface
public interface ParameterizedTypeProcessor<T> {

    /**
     * Produces usable config type information using the given {@code typeArguments}.
     *
     * @param typeArguments the generic type parameters
     * @return a ConfigType representing the parameterized type
     */
    DerivedType<? extends T, ?, ?> process(DerivedType<?, ?, ?>[] typeArguments);
}
