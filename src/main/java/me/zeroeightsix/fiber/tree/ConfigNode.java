package me.zeroeightsix.fiber.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class ConfigNode extends ConfigLeaf implements Node, Commentable {

    @Nonnull
    private Set<TreeItem> items = new HashSet<>();
    private boolean serializeSeparately;

    public ConfigNode(@Nullable String name, @Nullable String comment, boolean serializeSeparately) {
        super(name, comment);
        this.serializeSeparately = serializeSeparately;
    }

    public ConfigNode(@Nullable String name, @Nullable String comment) {
        this(name, comment, false);
    }

    public ConfigNode() {
        this(null, null);
    }

    @Nonnull
    @Override
    public Set<TreeItem> getItems() {
        return items;
    }

    @Override
    public boolean isSerializedSeparately() {
        return serializeSeparately;
    }
}
