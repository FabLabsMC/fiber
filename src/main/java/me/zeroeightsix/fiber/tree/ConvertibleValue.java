package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.schema.ConvertibleType;

/**
 * Indicates that this class holds some nullable value.
 * @param <T> The type of the value being held.
 */
public interface ConvertibleValue<T, T0> {

    /**
     * Returns the value being held
     * @return the value
     */
    T getValue();

    T0 getRawValue();

    /**
     * Returns the class of the type of the value being held
     * @return the class of the type of the value
     */
    default Class<T> getType() {
        return this.getConvertibleType().getActualType();
    }

    ConvertibleType<T, T0> getConvertibleType();

}
