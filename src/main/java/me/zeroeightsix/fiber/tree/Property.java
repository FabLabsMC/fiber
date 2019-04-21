package me.zeroeightsix.fiber.tree;

public interface Property<T> extends HasValue<T> {

    /**
     * @param value
     * @return <code>true</code> if successful
     */
    boolean setValue(T value);

}
