package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.builder.ConfigTreeBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A node that can hold any amount of children
 *
 * <p> A branch may represent an entire config tree,
 * or a subtree grouping further configuration items.
 *
 * @see me.zeroeightsix.fiber.builder.ConfigTreeBuilder
 */
public interface ConfigBranch extends ConfigTree, ConfigNode, Commentable {

    static ConfigTreeBuilder builder(ConfigTreeBuilder parent, String name) {
        return new ConfigTreeBuilder(parent, name);
    }

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
     * Tries to find a child in this group by name. If a child is found, it will be returned.
     *
     * <p> The node returned for a given {@code name} is always the same.
     *
     * @param name The name of the child to look for
     * @return the child if found, otherwise {@code null}
     */
    @Nullable
    @Override
    ConfigNode lookup(String name);

    /**
     * Returns a collection of this branch's children.
     *
     * <p> The returned collection is guaranteed not to have two nodes with the same name.
     *
     * @return the set of children
     */
    @Nonnull
    @Override
    NodeCollection getItems();

    /**
     * Returns {@code true} if this node should be serialized separately from its parent.
     *
     * <p> If a node is serialized separately, it should not appear in the serialized representation of
     * its ancestors. This property should be ignored if this node is the
     * root of the currently serialised tree.
     *
     * @return {@code true} if this node should be serialized separately, and {@code false} otherwise
     */
    default boolean isSerializedSeparately() {
        return false;
    }
}
