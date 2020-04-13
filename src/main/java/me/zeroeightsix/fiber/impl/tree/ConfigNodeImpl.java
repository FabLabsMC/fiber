package me.zeroeightsix.fiber.impl.tree;

import me.zeroeightsix.fiber.api.tree.Commentable;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigNode;

import me.zeroeightsix.fiber.api.exception.IllegalTreeStateException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A commentable node.
 *
 * @see ConfigNode
 * @see ConfigBranchImpl
 * @see ConfigLeafImpl
 */
public abstract class ConfigNodeImpl implements ConfigNode, Commentable {

    @Nonnull
    private final String name;
    @Nullable
    private final String comment;
    @Nullable
    private ConfigBranch parent;

    /**
     * Creates a new {@code ConfigLeaf}.
     *
     * @param name    the name for this leaf
     * @param comment the comment for this leaf
     */
    public ConfigNodeImpl(@Nonnull String name, @Nullable String comment) {
        this.name = name;
        this.comment = comment;
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    @Nullable
    public String getComment() {
        return comment;
    }

    @Nullable
    @Override
    public ConfigBranch getParent() {
        return this.parent;
    }

    @Override
    public void detach() {
        // Note: infinite recursion between ConfigNode#detach() and NodeCollection#remove() could occur here,
        // but the latter performs the actual collection removal before detaching
        if (this.parent != null) {
            // here, we also need to avoid triggering a ConcurrentModificationException
            // thankfully, remove does not cause a CME if it's a no-op
            this.parent.getItems().remove(this);
        }
        this.parent = null;
    }

    @Override
    public void attachTo(ConfigBranch parent) {
        if (this.parent != null && this.parent != parent) {
            throw new IllegalTreeStateException(this + " needs to be detached before changing the parent");
        }
        // this node may have already been added by the collection
        if (parent != null && !parent.getItems().contains(this)) {
            parent.getItems().add(this);
        }
        this.parent = parent;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + getName() + ", comment=" + getComment() + "]";
    }
}
