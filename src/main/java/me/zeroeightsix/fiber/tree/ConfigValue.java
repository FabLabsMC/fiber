package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.schema.ConfigType;

/**
 * Indicates that this class holds some nullable value.
 * @param <T> The type of the value being held.
 */
public interface ConfigValue<T> {

    /**
     * Returns the value being held
     * @return the value
     */
    T getValue();

    ConfigType<T, ?> getConfigType();
}
