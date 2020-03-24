package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.NodeOperations;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConfigNodeBuilder implements NodeLike {
    private final Map<String, TreeItem> items = new HashMap<>();
    @Nullable
    private final ConfigNodeBuilder parent;
    @Nullable
    private final String name;
    @Nullable
    private String comment;
    private boolean serializeSeparately;

    public ConfigNodeBuilder() {
        this.parent = null;
        this.name = null;
    }

    public ConfigNodeBuilder(Node node) {
        this.parent = null;
        this.name = node.getName();
        NodeOperations.mergeTo(node, this);
    }

    private ConfigNodeBuilder(@Nonnull ConfigNodeBuilder parent, @Nonnull String name) {
        this.parent = parent;
        this.name = name;
    }

    /**
     * Returns a collection of this node's children.
     *
     * <p> The returned collection is guaranteed to have no two nodes with the same name.
     * Elements may be removed from it, but no elements may be added directly.
     *
     * @return the set of children
     * @see #add(TreeItem)
     */
    @Nonnull
    @Override
    public Collection<TreeItem> getItems() {
        return items.values();
    }

    /**
     * Tries to find a child in this node by name. If a child is found, it will be returned.
     *
     * @param name The name of the child to look for
     * @return the child if found, otherwise {@code null}
     */
    @Nullable
    public TreeItem lookup(String name) {
        return items.get(name);
    }

    /**
     *  Marks the built node as being serialized separately
     */
    public ConfigNodeBuilder serializeSeparately() {
        serializeSeparately(true);
        return this;
    }

    /**
     *
     * @param serializeSeparately if {@code true}, the subtree will not appear in the
     *                            serialized representation of the built {@code Node}
     * @return {@code this}, for chaining
     */
    public ConfigNodeBuilder serializeSeparately(boolean serializeSeparately) {
        this.serializeSeparately = serializeSeparately;
        return this;
    }

    /**
     * Attempts to introduce a new child to this node.
     *
     * @param item The child to add
     * @throws FiberException if there was already a non-transparent child by the same name or if {@code item} was a non-property item with the same name as a transparent item.
     * @see Property
     */
    public void add(@Nonnull TreeItem item) throws FiberException {
        TreeItem existing = lookup(item.getName());
        if (existing != null) {
            throw new FiberException("Attempt to replace non-transparent node " + existing.getName());
        }
        items.put(item.getName(), item);
    }

    /**
     * Attempts to remove an item from this node by name.
     *
     * @param name the name of the child that should be removed
     * @return the child if removed, otherwise {@code null}
     */
    public TreeItem remove(String name) {
        return items.remove(name);
    }

    /**
     * Forks this builder, creating a subtree whose parent is this node.
     *
     * @param name the name of the new {@code Node}
     * @return the created node builder
     */
    public ConfigNodeBuilder fork(String name) {
        return new ConfigNodeBuilder(this, name);
    }

    public ConfigNode build() {
        ConfigNode built = new ConfigNode(this.name, this.comment, this.items.values(), this.serializeSeparately);
        if (this.parent != null) {
            assert name != null;
            try {
                this.parent.add(built);
            } catch (FiberException e) {
                throw new RuntimeFiberException("Failed to attach built node to parent", e);
            }
        }
        return built;
    }

}
