package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.exception.FiberException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

/**
 * A member of a tree that can hold any amount of children
 *
 * @see ConfigNode
 */
public interface Node extends TreeItem {

    /**
     * Returns a set of this node's children.
     * @return the set of children
     */
    @Nonnull
    Set<TreeItem> getItems();

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

    /**
     * Tries to find a child in this node by name. If a child is found, it will be returned.
     *
     * @param name The name of the child to look for
     * @return the child if found, otherwise {@code null}
     */
    @Nullable
    default TreeItem lookup(String name) {
        return getItems()
                .stream()
                .filter(treeItem -> treeItem.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Attempts to introduce a new child to this node.
     *
     * @param item The child to add
     * @return the child
     * @throws FiberException if there was already a child by the same name
     * @see Property
     */
    default TreeItem add(@Nonnull TreeItem item) throws FiberException {
        TreeItem existing = lookup(item.getName());
        if (existing == null) {
            getItems().add(item);
        } else {
            throw new FiberException("Attempt to replace node " + existing.getName());
        }
        return item;
    }

    /**
     * Attempts to remove an item from this node by name.
     *
     * @param name the name of the child that should be removed
     * @return the child if removed, otherwise {@code null}
     */
    default TreeItem remove(String name) {
        Optional<TreeItem> itemOptional = getItems().stream().filter(item -> item.getName().equals(name)).findAny();
        if (!itemOptional.isPresent()) return null;
        TreeItem item = itemOptional.get();
        getItems().remove(item);
        return item;
    }

    /**
     * Forks this node, creating a subtree whose parent is this node.
     *
     * @param name the name of the new {@code Node}
     * @return the created node
     * @throws FiberException if the new node cannot be added as a child to this node
     */
    default Node fork(String name) throws FiberException {
        return fork(name, false);
    }

    /**
     * Forks this node, creating a subtree whose parent is this node.
     *
     * @param name the name of the new {@code Node}
     * @param serializeSeparately if {@code true}, the subtree will not appear in the
     *                            serialized representation of this {@code Node}
     * @return the created node
     * @throws FiberException if the new node cannot be added as a child to this node
     */
    default Node fork(String name, boolean serializeSeparately) throws FiberException {
        return (Node) add(new ConfigNode(name, null, serializeSeparately));
    }

}
