package me.zeroeightsix.fiber.tree;

import javax.annotation.Nullable;

/**
 * The building block of a tree: every node implement this interface.
 */
public interface ConfigNode {

    /**
     * Returns this node's name.
     *
     * @return this node's name
     */
    String getName();

    /**
     * Returns this node's parent, if any.
     *
     * @return this node's parent, or {@code null} if it is not part of a config tree
     */
    @Nullable
    ConfigBranch getParent();

}
