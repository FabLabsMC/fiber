package me.zeroeightsix.fiber.impl.tree;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.zeroeightsix.fiber.api.schema.type.SerializableType;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;
import me.zeroeightsix.fiber.api.tree.ConfigNode;
import me.zeroeightsix.fiber.api.tree.NodeCollection;
import me.zeroeightsix.fiber.api.tree.PropertyMirror;

/**
 * Class implementing {@link ConfigBranch}.
 */
public class ConfigBranchImpl extends ConfigNodeImpl implements ConfigBranch {
    private final NodeCollection items;
    private final boolean serializeSeparately;

    /**
     * Creates a new {@code ConfigBranch}.
     *
     * @param name                the name for this {@link ConfigBranchImpl}
     * @param comment             the comment for this {@link ConfigBranchImpl}
     * @param items               the node's items
     * @param serializeSeparately whether or not this node should be serialised separately. If {@code true}, it will be ignored during serialisation.
     */
    public ConfigBranchImpl(String name, @Nullable String comment, @Nonnull Collection<ConfigNode> items, boolean serializeSeparately) {
        super(name, comment);
        this.items = new IndexedNodeCollection(this);
        this.serializeSeparately = serializeSeparately;
        // must do 2-step initialization, to avoid leaking uninitialized <this>
        this.items.addAll(items);
    }

    /**
     * Creates a new {@code ConfigBranch} with the provided {@code name} and {@code comment}.
     *
     * <p>This node will not be serialised separately.
     *
     * @param name    the name for this {@link ConfigBranchImpl}
     * @param comment the comment for this {@link ConfigBranchImpl}
     */
    public ConfigBranchImpl(@Nonnull String name, @Nullable String comment) {
        this(name, comment, Collections.emptyList(), false);
    }

    /**
     * Creates a new {@code ConfigBranch} without a name or comment.
     *
     * <p>This node will not be serialised separately.
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
        return this.items.getByName(name);
    }

    @Nullable
    @Override
    public <T> ConfigLeaf<T> lookupLeaf(String name, SerializableType<T> type) {
        ConfigNode child = this.items.getByName(name);

        if (child instanceof ConfigLeaf && type.isAssignableFrom(((ConfigLeaf<?>) child).getConfigType())) {
            @SuppressWarnings("unchecked") ConfigLeaf<T> leaf = (ConfigLeaf<T>) child;
            return leaf;
        }

        return null;
    }

    @Override
    public boolean lookupAndBind(String name, PropertyMirror<?> mirror) {
        ConfigLeaf<?> leaf = this.lookupLeaf(name, mirror.getConverter().getSerializedType());

        if (leaf != null) {
            mirror.mirror(leaf);
            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public ConfigBranch lookupBranch(String name) {
        ConfigNode child = this.items.getByName(name);

        if (child instanceof ConfigBranch) {
            return (ConfigBranch) child;
        }

        return null;
    }

    @Override
    public boolean isSerializedSeparately() {
        return serializeSeparately;
    }
}
