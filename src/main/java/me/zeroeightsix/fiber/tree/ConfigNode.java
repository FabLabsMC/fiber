package me.zeroeightsix.fiber.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * A {@code ConfigLeaf} with children
 */
public class ConfigNode extends ConfigLeaf implements Node {

    private final Map<String, TreeItem> items;
    private final boolean serializeSeparately;

    /**
     * Creates a new {@code ConfigNode}.
     *
     * @param name the name for this {@link ConfigNode}
     * @param comment the comment for this {@link ConfigNode}
     * @param items the node's items
     * @param serializeSeparately whether or not this node should be serialised separately. If {@code true}, it will be ignored during serialisation.
     */
    public ConfigNode(String name, @Nullable String comment, @Nonnull Map<String, TreeItem> items, boolean serializeSeparately) {
        super(name, comment);
        this.items = Collections.unmodifiableMap(new TreeMap<>(items));
        this.serializeSeparately = serializeSeparately;
    }

    /**
     * Creates a new {@code ConfigNode} with the provided {@code name} and {@code comment}.
     *
     * <p> This node will not be serialised separately.
     *
     * @param name the name for this {@link ConfigNode}
     * @param comment the comment for this {@link ConfigNode}
     * @see ConfigNode
     */
    public ConfigNode(@Nonnull String name, @Nullable String comment) {
        this(name, comment, Collections.emptyMap(), false);
    }

    /**
     * Creates a new {@code ConfigNode} without a name or comment.
     *
     * <p> This node will not be serialised separately.
     *
     * @see ConfigNode
     */
    public ConfigNode() {
        this(null, null, Collections.emptyMap(), false);
    }

    @Nonnull
    @Override
    public Collection<TreeItem> getItems() {
        return items.values();
    }

    @Nullable
    @Override
    public TreeItem lookup(String name) {
        return items.get(name);
    }

    /**
     * Returns whether this node should be serialised separately as a subtree.
     *
     * <p> If {@code true}, this node should be ignored during an ancestor's
     * serialisation. This property should be ignored when this node is the
     * root of the currently serialised tree.
     *
     * @return whether or not this node is serialised separately
     */
    @Override
    public boolean isSerializedSeparately() {
        return serializeSeparately;
    }
}
