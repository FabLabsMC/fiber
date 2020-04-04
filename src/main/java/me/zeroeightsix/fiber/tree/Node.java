package me.zeroeightsix.fiber.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A member of a tree that can hold any amount of children
 *
 * @see ConfigNode
 */
public interface Node extends ConfigTree, TreeItem {

    /**
     * Returns this node's name.
     *
     * <p> If this node is the root of a config tree, it may not have a name.
     * In this case, this method returns {@code null}.
     *
     * @return this node's name, or {@code null} if it does not have any.
     */
    @Nullable
    @Override
    String getName();

    /**
     * Tries to find a child in this node by name. If a child is found, it will be returned.
     *
     * <p> The value returned for a given {@code name} is always the same.
     *
     * @param name The name of the child to look for
     * @return the child if found, otherwise {@code null}
     */
    @Nullable
    @Override
    TreeItem lookup(String name);

    /**
     * Returns a collection of this node's children.
     *
     * <p> The returned collection is unmodifiable, and guaranteed not to have two nodes with the same name.
     *
     * @return the set of children
     */
    @Nonnull
    @Override
    Collection<TreeItem> getItems();

    /**
     * Returns {@code true} if this node should be serialized separately to its parent.
     *
     * <p> If a node is serialized separately, it should not appear in the serialized representation of
     * its parent. This setting has no effect if this node is a root.
     *
     * @return {@code true} if this node should be serialized separately, and {@code false} otherwise
     */
    default boolean isSerializedSeparately() {
        return false;
    }
}
