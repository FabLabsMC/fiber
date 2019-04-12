package me.zeroeightsix.fiber.tree;

import javax.annotation.Nullable;

/**
 * A transparent node is a node that has been deserialized, but we don't know what metadata the user has given it.
 * We keep around the read value until the real setting is registered.
 */
public interface Transparent extends TreeItem {

    /**
     * Attempts to convert the value of this transparent setting.
     * @param <A>   The type to convert to.
     * @return      The converted value
     */
    @Nullable
    <A> A marshal(Class<A> type);

}
