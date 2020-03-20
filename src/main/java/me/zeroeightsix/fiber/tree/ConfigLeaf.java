package me.zeroeightsix.fiber.tree;

import javax.annotation.Nullable;

/**
 * A commentable tree item.
 */
public class ConfigLeaf implements TreeItem, Commentable {

    @Nullable
    private final String name;
    @Nullable
    private final String comment;

    /**
     * Creates a new {@code ConfigLeaf}.
     *
     * @param name the name for this leaf
     * @param comment the comment for this leaf
     * @see ConfigLeaf
     */
    public ConfigLeaf(@Nullable String name, @Nullable String comment) {
        this.name = name;
        this.comment = comment;
    }

    @Override
    @Nullable
    public String getName() {
        return name;
    }

    @Override
    @Nullable
    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + getName() + ", comment=" + getComment() + "]";
    }
}
