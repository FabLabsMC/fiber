package me.zeroeightsix.fiber.tree;

import javax.annotation.Nullable;

public class ConfigLeaf implements TreeItem, Commentable {

    @Nullable
    private final String name;
    @Nullable
    private final String comment;

    public ConfigLeaf(@Nullable String name, @Nullable String comment) {
        this.name = name;
        this.comment = comment;
    }

    @Override
    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + getName() + ", comment=" + getComment() + "]";
    }
}
