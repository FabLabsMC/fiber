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
public class ConfigGroupImpl extends ConfigNodeImpl implements ConfigGroup {

    private final Map<String, ConfigNode> items;
    private final boolean serializeSeparately;

    /**
     * Creates a new {@code ConfigNode}.
     *
     * @param name the name for this {@link ConfigGroupImpl}
     * @param comment the comment for this {@link ConfigGroupImpl}
     * @param items the node's items
     * @param serializeSeparately whether or not this node should be serialised separately. If {@code true}, it will be ignored during serialisation.
     */
    public ConfigGroupImpl(String name, @Nullable String comment, @Nonnull Map<String, ConfigNode> items, boolean serializeSeparately) {
        super(name, comment);
        this.items = Collections.unmodifiableMap(new TreeMap<>(items));
        this.serializeSeparately = serializeSeparately;
    }

    /**
     * Creates a new {@code ConfigNode} with the provided {@code name} and {@code comment}.
     *
     * <p> This node will not be serialised separately.
     *
     * @param name the name for this {@link ConfigGroupImpl}
     * @param comment the comment for this {@link ConfigGroupImpl}
     * @see ConfigGroupImpl
     */
    public ConfigGroupImpl(@Nonnull String name, @Nullable String comment) {
        this(name, comment, Collections.emptyMap(), false);
    }

    /**
     * Creates a new {@code ConfigNode} without a name or comment.
     *
     * <p> This node will not be serialised separately.
     *
     * @see ConfigGroupImpl
     */
    public ConfigGroupImpl() {
        this(null, null, Collections.emptyMap(), false);
    }

    @Nonnull
    @Override
    public Collection<ConfigNode> getItems() {
        return items.values();
    }

    @Nullable
    @Override
    public ConfigNode lookup(String name) {
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
