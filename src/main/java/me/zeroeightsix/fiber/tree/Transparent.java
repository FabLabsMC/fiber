package me.zeroeightsix.fiber.tree;

import javax.annotation.Nullable;

/**
 * A transparent node is a node that has been deserialized, but we don't know what metadata the user has given it.
 *
 * <p> We keep around the read value until the real setting is registered.
 */
public interface Transparent extends TreeItem {

    /**
     * Attempts to convert the value of this transparent setting.
     *
     * @param <A> the type to convert to.
     * @param type class of {@code <A>}
     * @return the converted value
     */
    @Nullable
    <A> A marshall(Class<A> type);

}
