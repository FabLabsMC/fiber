package me.zeroeightsix.fiber.api.tree;

/**
 * Indicates that this class holds some nullable value.
 * @param <T> The type of the value being held.
 */
public interface HasValue<T> {

    /**
     * Returns the value being held
     * @return the value
     */
    T getValue();

    Class<T> getType();
}
