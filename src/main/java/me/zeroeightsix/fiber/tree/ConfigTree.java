package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.api.builder.ConfigTreeBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface ConfigTree {
    /**
     * @return a new builder for a root config node
     */
    static ConfigTreeBuilder builder() {
        return new ConfigTreeBuilder();
    }

    /**
     * Returns a collection of this node's children.
     *
     * <p> The returned collection is guaranteed to have no two nodes with the same name.
     *
     * @return the set of children
     */
    @Nonnull
    Collection<ConfigNode> getItems();

    /**
     * Tries to find a child in this node by name. If a child is found, it will be returned.
     *
     * @param name The name of the child to look for
     * @return the child if found, otherwise {@code null}
     */
    @Nullable
    ConfigNode lookup(String name);

}
