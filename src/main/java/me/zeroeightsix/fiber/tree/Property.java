package me.zeroeightsix.fiber.tree;

/**
 * Implementing this interface means that this class has a nullable value which can be mutated using the {@link Property#setValue(Object) setValue} method.
 *
 * @param <T> the type of value this property holds
 * @see Property#setValue(Object)
 */
public interface Property<T> extends HasValue<T> {

    /**
     * Sets the value of this property.
     *
     * <p> This can fail and return {@code false} if, for example, the value did not satisfy the constraints of the property.
     *
     * @param value the new value this property should receive
     * @return {@code true} if this property changed as a result of the call
     * @see #accepts(Object)
     */
    boolean setValue(T value);

    /**
     * Returns {@code true} if this property can be set to the given value.
     *
     * @param value the value to check
     * @return {@code true} if this property accepts the given value, {@code false} otherwise.
     * @see #setValue(Object)
     */
    default boolean accepts(T value) {
        return true;
    }
}
