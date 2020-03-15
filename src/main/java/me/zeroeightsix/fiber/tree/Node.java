package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.exception.FiberException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public interface Node extends TreeItem {

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

    @Nullable
    default TreeItem lookup(String name) {
        return getItems()
                .stream()
                .filter(treeItem -> treeItem.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    default TreeItem add(@Nonnull TreeItem item) throws FiberException {
        TreeItem existing = lookup(item.getName());
        if (existing == null) {
            getItems().add(item);
        } else {
            if (existing instanceof Transparent) {
                if (item instanceof Property<?>) {
                    Class<?> type = ((Property<?>) item).getType();
                    // cannot add private helper methods to interfaces
                    ((Property) item).setValue(((Transparent) existing).marshall(type));
                    getItems().remove(existing);
                    getItems().add(item);
                } else {
                    throw new FiberException("Attempt to replace transparent node by non-property node " + item.getName());
                }
            } else {
                throw new FiberException("Attempt to replace non-transparent node " + existing.getName());
            }
        }
        return item;
    }

    default TreeItem remove(String name) {
        Optional<TreeItem> itemOptional = getItems().stream().filter(item -> item.getName().equals(name)).findAny();
        if (!itemOptional.isPresent()) return null;
        TreeItem item = itemOptional.get();
        getItems().remove(item);
        return item;
    }

    /**
     * Forks this node, creating a subtree which parent is this node.
     *
     * @param name the name of the new {@code Node}
     * @return the created node
     * @throws FiberException if the new node cannot be added as a child to this node
     */
    default Node fork(String name) throws FiberException {
        return fork(name, false);
    }

    /**
     * Forks this node, creating a subtree which parent is this node.
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
