package me.zeroeightsix.fiber.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A {@code ConfigLeaf} with children
 */
public class ConfigNode extends ConfigLeaf implements Node {

    private final SortedMap<String, TreeItem> indexedItems;
    private final SortedSet<TreeItem> items;
    private final boolean serializeSeparately;

    /**
     * Creates a new {@code ConfigNode}.
     *
     * @param name the name for this {@link ConfigNode}
     * @param comment the comment for this {@link ConfigNode}
     * @param items the node's items
     * @param serializeSeparately whether or not this node should be serialised separately. If {@code true}, it will be ignored during serialisation.
     */
    public ConfigNode(String name, @Nullable String comment, @Nonnull Collection<TreeItem> items, boolean serializeSeparately) {
        super(name, comment);
        SortedSet<TreeItem> copy = new TreeSet<>(Comparator.comparing(TreeItem::getName));
        SortedMap<String, TreeItem> indexedItems = new TreeMap<>();
        for (TreeItem item : items) {
            indexedItems.put(item.getName(), item);
            boolean added = copy.add(item);
            if (!added) {
                throw new IllegalArgumentException(items + " has multiple items with the same name (" + item.getName() + ")");
            }
        }
        this.items = Collections.unmodifiableSortedSet(copy);
        this.indexedItems = Collections.unmodifiableSortedMap(indexedItems);
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
        this(name, comment, new HashSet<>(), false);
    }

    /**
     * Creates a new {@code ConfigNode} without a name or comment.
     *
     * <p> This node will not be serialised separately.
     *
     * @see ConfigNode
     */
    public ConfigNode() {
        this(null, null, new HashSet<>(), false);
    }

    @Nonnull
    @Override
    public Set<TreeItem> getItems() {
        return items;
    }

    @Nullable
    @Override
    public TreeItem lookup(String name) {
        return indexedItems.get(name);
    }

    /**
     * Returns if this node should be serialised separately.
     *
     * <p> If {@code true}, it should be ignored during serialisation.
     * @return whether or not it is serialised separately
     */
    @Override
    public boolean isSerializedSeparately() {
        return serializeSeparately;
    }
}
