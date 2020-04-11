package me.zeroeightsix.fiber.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * Class implementing {@link ConfigBranch}
 */
public class ConfigBranchImpl extends ConfigNodeImpl implements ConfigBranch {

    private final NodeCollection items;
    private final boolean serializeSeparately;

    /**
     * Creates a new {@code ConfigBranch}.
     *
     * @param name the name for this {@link ConfigBranchImpl}
     * @param comment the comment for this {@link ConfigBranchImpl}
     * @param items the node's items
     * @param serializeSeparately whether or not this node should be serialised separately. If {@code true}, it will be ignored during serialisation.
     */
    public ConfigBranchImpl(String name, @Nullable String comment, @Nonnull Collection<ConfigNode> items, boolean serializeSeparately) {
        super(name, comment);
        this.items = new IndexedNodeCollection(this, items);
        this.serializeSeparately = serializeSeparately;
    }

    /**
     * Creates a new {@code ConfigBranch} with the provided {@code name} and {@code comment}.
     *
     * <p> This node will not be serialised separately.
     *
     * @param name the name for this {@link ConfigBranchImpl}
     * @param comment the comment for this {@link ConfigBranchImpl}
     */
    public ConfigBranchImpl(@Nonnull String name, @Nullable String comment) {
        this(name, comment, Collections.emptyList(), false);
    }

    /**
     * Creates a new {@code ConfigBranch} without a name or comment.
     *
     * <p> This node will not be serialised separately.
     */
    public ConfigBranchImpl() {
        this(null, null, Collections.emptyList(), false);
    }

    @Nonnull
    @Override
    public NodeCollection getItems() {
        return items;
    }

    @Nullable
    @Override
    public ConfigNode lookup(String name) {
        return items.getByName(name);
    }

    @Override
    public boolean isSerializedSeparately() {
        return serializeSeparately;
    }
}
