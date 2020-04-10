package me.zeroeightsix.fiber.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A commentable node.
 *
 * @see ConfigGroupImpl
 * @see ConfigLeaf
 */
public abstract class ConfigNodeImpl implements ConfigNode, Commentable {

    @Nonnull
    private final String name;
    @Nullable
    private final String comment;
    @Nullable
    private ConfigGroup parent;

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
    public ConfigGroup getParent() {
        return this.parent;
    }

    public void detach() {
        this.parent = null;
    }

    public void setParent(ConfigGroup parent) {
        if (this.parent != null) {
            throw new IllegalStateException(this + " needs to be detached before changing the parent");
        }
        this.parent = parent;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + getName() + ", comment=" + getComment() + "]";
    }
}
