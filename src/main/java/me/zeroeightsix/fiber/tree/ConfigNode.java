package me.zeroeightsix.fiber.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link ConfigLeaf} with children
 */
public class ConfigNode extends ConfigLeaf implements Node {

    @Nonnull
    private Set<TreeItem> items = new HashSet<>();
    private boolean serializeSeparately;

    /**
     * Creates a new {@link ConfigNode}
     * @param name the name for this {@link ConfigNode}
     * @param comment the comment for this {@link ConfigNode}
     * @param serializeSeparately whether or not this node should be serialised separately. If {@code true}, it will be ignored during serialisation.
     */
    public ConfigNode(@Nullable String name, @Nullable String comment, boolean serializeSeparately) {
        super(name, comment);
        this.serializeSeparately = serializeSeparately;
    }

    /**
     * Creates a new {@link ConfigNode} with the provided {@code name} and {@code comment}. This node will not be serialised separately.
     * @param name the name for this {@link ConfigNode}
     * @param comment the comment for this {@link ConfigNode}
     */
    public ConfigNode(@Nullable String name, @Nullable String comment) {
        this(name, comment, false);
    }

    /**
     * Creates a new {@link ConfigNode} without a name or comment
     */
    public ConfigNode() {
        this(null, null);
    }

    @Nonnull
    @Override
    public Set<TreeItem> getItems() {
        return items;
    }

    /**
     * Returns if this node should be serialised separately. If {@code true}, it should be ignored during serialisation.
     * @return whether or not it is serialised separately
     */
    @Override
    public boolean isSerializedSeparately() {
        return serializeSeparately;
    }
}
