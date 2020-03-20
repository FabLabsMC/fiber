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
     * <br> This can fail and return {@code false} if, for example, the value did not satisfy the constraints of the property.
     *
     * @param value the new value this property should receive
     * @return {@code true} if this property changed as a result of the call
     */
    boolean setValue(T value);

}
