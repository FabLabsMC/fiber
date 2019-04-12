package me.zeroeightsix.fiber.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class ConfigNode extends ConfigLeaf implements Node, Commentable {

    @Nonnull
    private Set<TreeItem> items = new HashSet<>();

    public ConfigNode(@Nullable String name, @Nullable String comment) {
        super(name, comment);
    }

    public ConfigNode() {
        this(null, null);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Nonnull
    @Override
    public Set<TreeItem> getItems() {
        return items;
    }

}
