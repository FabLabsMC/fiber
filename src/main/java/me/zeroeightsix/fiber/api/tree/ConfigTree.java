package me.zeroeightsix.fiber.api.tree;

import me.zeroeightsix.fiber.api.NodeOperations;
import me.zeroeightsix.fiber.api.builder.ConfigTreeBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ConfigTree {

    /**
     * @return a new builder for a root config node
     */
    static ConfigTreeBuilder builder() {
        return builder(null, null);
    }

    /**
     * Creates a new non-root {@link ConfigTreeBuilder} with a name.
     *
     * @param parent    the parent of the builder to create
     * @param name      the name of the {@link ConfigTree} created by the builder
     * @return          the newly created builder
     */
    static ConfigTreeBuilder builder(@Nullable ConfigTree parent, @Nullable String name) {
        return new ConfigTreeBuilder(parent, name);
    }

    /**
     * Returns a collection of this node's children.
     *
     * <p> The returned collection is guaranteed to have no two nodes with the same name.
     * Any changes made to it will be reflected by this tree.
     *
     * @return the set of children
     * @see NodeOperations
     */
    @Nonnull
    NodeCollection getItems();

    /**
     * Tries to find a child in this node by name. If a child is found, it will be returned.
     *
     * @param name The name of the child to look for
     * @return the child if found, otherwise {@code null}
     */
    @Nullable
    ConfigNode lookup(String name);

}
