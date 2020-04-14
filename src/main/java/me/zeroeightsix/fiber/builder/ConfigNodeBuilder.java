package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.FiberId;
import me.zeroeightsix.fiber.tree.ConfigAttribute;
import me.zeroeightsix.fiber.tree.ConfigAttributeImpl;
import me.zeroeightsix.fiber.tree.ConfigNode;
import me.zeroeightsix.fiber.tree.ConfigTree;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class ConfigNodeBuilder {
    @Nullable
    protected ConfigTree parent;
    @Nullable
    protected String name;
    @Nullable
    protected String comment = null;
    protected Map<FiberId, ConfigAttribute<?>> attributes;

    public ConfigNodeBuilder(@Nullable ConfigTree parent, @Nullable String name) {
        if (parent != null && name == null) throw new IllegalArgumentException("A child node needs a name");
        this.parent = parent;
        this.name = name;
        this.attributes = new HashMap<>();
    }

    /**
     * Sets the {@code ConfigNode}'s name.
     *
     * @param name the name
     * @return {@code this} builder
     * @see ConfigTree#lookup
     */
    public ConfigNodeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the {@code ConfigNode}'s comment.
     *
     * <p> If {@code null}, or if this method is never called, the {@code ConfigNode} will not have a comment.
     * An empty comment (non null, but only consisting of whitespace) will be serialised.
     *
     * @param comment the comment
     * @return {@code this} builder
     */
    public ConfigNodeBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public <A> ConfigNodeBuilder withAttribute(FiberId attribute, Class<A> type, A defaultValue) {
        this.attributes.put(attribute, new ConfigAttributeImpl<>(type, defaultValue));
        return this;
    }

    public abstract ConfigNode build();
}
