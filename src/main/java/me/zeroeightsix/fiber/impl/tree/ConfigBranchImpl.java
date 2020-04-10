package me.zeroeightsix.fiber.impl.tree;

import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class implementing {@link ConfigBranch}
 */
public class ConfigBranchImpl extends ConfigNodeImpl implements ConfigBranch {

    private final Map<String, ConfigNode> items;
    private final boolean serializeSeparately;

    /**
     * Creates a new {@code ConfigGroup}.
     *
     * @param name the name for this {@link ConfigBranchImpl}
     * @param comment the comment for this {@link ConfigBranchImpl}
     * @param items the node's items
     * @param serializeSeparately whether or not this node should be serialised separately. If {@code true}, it will be ignored during serialisation.
     */
    public ConfigBranchImpl(String name, @Nullable String comment, @Nonnull Map<String, ConfigNode> items, boolean serializeSeparately) {
        super(name, comment);
        this.items = Collections.unmodifiableMap(new TreeMap<>(items));
        this.serializeSeparately = serializeSeparately;
    }

    /**
     * Creates a new {@code ConfigGroup} with the provided {@code name} and {@code comment}.
     *
     * <p> This node will not be serialised separately.
     *
     * @param name the name for this {@link ConfigBranchImpl}
     * @param comment the comment for this {@link ConfigBranchImpl}
     */
    public ConfigBranchImpl(@Nonnull String name, @Nullable String comment) {
        this(name, comment, Collections.emptyMap(), false);
    }

    /**
     * Creates a new {@code ConfigGroup} without a name or comment.
     *
     * <p> This node will not be serialised separately.
     */
    public ConfigBranchImpl() {
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

    @Override
    public boolean isSerializedSeparately() {
        return serializeSeparately;
    }
}
