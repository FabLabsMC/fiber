package me.zeroeightsix.fiber.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * Class implementing {@link ConfigGroup}
 */
public class ConfigGroupImpl extends ConfigNodeImpl implements ConfigGroup {

    private final NodeCollection items;
    private final boolean serializeSeparately;

    /**
     * Creates a new {@code ConfigGroup}.
     *
     * @param name the name for this {@link ConfigGroupImpl}
     * @param comment the comment for this {@link ConfigGroupImpl}
     * @param items the node's items
     * @param serializeSeparately whether or not this node should be serialised separately. If {@code true}, it will be ignored during serialisation.
     */
    public ConfigGroupImpl(String name, @Nullable String comment, @Nonnull Collection<ConfigNode> items, boolean serializeSeparately) {
        super(name, comment);
        this.items = new NodeCollection(this, items);
        this.serializeSeparately = serializeSeparately;
    }

    /**
     * Creates a new {@code ConfigGroup} with the provided {@code name} and {@code comment}.
     *
     * <p> This node will not be serialised separately.
     *
     * @param name the name for this {@link ConfigGroupImpl}
     * @param comment the comment for this {@link ConfigGroupImpl}
     * @see ConfigGroupImpl
     */
    public ConfigGroupImpl(@Nonnull String name, @Nullable String comment) {
        this(name, comment, Collections.emptyList(), false);
    }

    /**
     * Creates a new {@code ConfigGroup} without a name or comment.
     *
     * <p> This node will not be serialised separately.
     *
     * @see ConfigGroupImpl
     */
    public ConfigGroupImpl() {
        this(null, null, Collections.emptyList(), false);
    }

    @Nonnull
    @Override
    public Collection<ConfigNode> getItems() {
        return items;
    }

    @Nullable
    @Override
    public ConfigNode lookup(String name) {
        return items.get(name);
    }

    @Override
    public boolean isSerializedSeparately() {
        return serializeSeparately;
    }
}
