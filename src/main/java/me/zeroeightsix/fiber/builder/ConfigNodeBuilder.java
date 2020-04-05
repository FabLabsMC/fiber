package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.annotation.AnnotatedSettings;
import me.zeroeightsix.fiber.annotation.Setting;
import me.zeroeightsix.fiber.annotation.Settings;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigNodeBuilder implements NodeLike {
    private final Map<String, TreeItem> items = new HashMap<>();
    @Nullable
    protected ConfigNodeBuilder parent;
    @Nullable
    protected String name;
    @Nullable
    private String comment;
    private boolean serializeSeparately;
    private ConfigNode built;

    public ConfigNodeBuilder() {
        this.parent = null;
        this.name = null;
    }

    /**
     * Returns a collection of this node's children.
     *
     * <p> The returned collection is guaranteed to have no two nodes with the same name.
     * Elements may be removed from it, but no elements may be added directly.
     *
     * @return the set of children
     * @see #add(TreeItem)
     */
    @Nonnull
    @Override
    public Collection<TreeItem> getItems() {
        return items.values();
    }

    /**
     * Tries to find a child in this node by name. If a child is found, it will be returned.
     *
     * @param name The name of the child to look for
     * @return the child if found, otherwise {@code null}
     */
    @Nullable
    public TreeItem lookup(String name) {
        return items.get(name);
    }

    public ConfigNodeBuilder withParent(ConfigNodeBuilder parent) {
        if (name == null && parent != null) throw new IllegalStateException("A child node needs a name");
        this.parent = parent;
        return this;
    }

    public ConfigNodeBuilder withName(String name) {
        if (name == null && parent != null) throw new IllegalStateException("Cannot remove the name from a child node");
        this.name = name;
        return this;
    }

    public ConfigNodeBuilder withComment(@Nullable String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Marks the built node as being serialized separately
     */
    public ConfigNodeBuilder withSeparateSerialization() {
        withSeparateSerialization(true);
        return this;
    }

    /**
     * @param serializeSeparately if {@code true}, the subtree will not appear in the
     *                            serialized representation of the built {@code Node}
     * @return {@code this}, for chaining
     */
    public ConfigNodeBuilder withSeparateSerialization(boolean serializeSeparately) {
        this.serializeSeparately = serializeSeparately;
        return this;
    }

    /**
     * Configure this builder using a POJO (Plain Old Java Object).
     *
     * <p> The node's structure will be based on the {@code pojo}'s fields,
     * recursively generating settings. The generated settings can be configured
     * in the {@code pojo}'s class declaration, using annotations such as {@link Setting}.
     *
     * <p> The generated {@link ConfigValue}s will be bound to their respective fields,
     * setting the latter when the former's value is {@linkplain ConfigValue#setValue(Object) updated}.
     *
     * @param pojo an object serving as a base to reflectively generate a config tree
     * @return {@code this}, for chaining
     * @see Setting
     * @see Settings
     * @see AnnotatedSettings#applyToNode(ConfigNodeBuilder, Object)
     */
    public ConfigNodeBuilder applyFromPojo(Object pojo) throws FiberException {
        return applyFromPojo(pojo, new AnnotatedSettings());
    }

    /**
     * Configure this builder using a POJO (Plain Old Java Object).
     *
     * <p> The node's structure will be based on the {@code pojo}'s fields,
     * recursively generating settings. The generated settings can be configured
     * in the {@code pojo}'s class declaration, using annotations such as {@link me.zeroeightsix.fiber.annotation.Setting}.
     *
     * <p> The generated {@link ConfigValue}s will be bound to their respective fields,
     * setting the latter when the former's value is {@linkplain ConfigValue#setValue(Object) updated}.
     *
     * @param pojo an object serving as a base to reflectively generate a config tree
     * @param settings
     * @return {@code this}, for chaining
     * @see me.zeroeightsix.fiber.annotation.Setting
     * @see me.zeroeightsix.fiber.annotation.Settings
     * @see me.zeroeightsix.fiber.annotation.AnnotatedSettings#applyToNode(ConfigNodeBuilder, Object)
     */
    public ConfigNodeBuilder applyFromPojo(Object pojo, AnnotatedSettings settings) throws FiberException {
        settings.applyToNode(this, pojo);
        return this;
    }

    /**
     * Creates a scalar {@code ConfigValueBuilder}.
     *
     * @param type the class of the type of value the {@link ConfigValue} produced by the builder holds
     * @param <T> the type {@code type} represents
     * @return the newly created builder
     * @see ConfigValueBuilder ConfigValueBuilder
     */
    public <T> ConfigValueBuilder<T> beginValue(@Nonnull String name, @Nonnull Class<T> type) {
        return new ConfigValueBuilder<>(this, name, type);
    }

    /**
     * Creates a {@code ConfigValueBuilder} with the given default value.
     *
     * @param defaultValue the default value of the {@link ConfigValue} that will be produced by the created builder.
     * @param <T> the type of value the {@link ConfigValue} produced by the builder holds
     * @return the newly created builder
     * @see ConfigValueBuilder
     * @see ConfigAggregateBuilder
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> ConfigValueBuilder<T> beginValue(@Nonnull String name, @Nonnull T defaultValue) {
        Class<T> type = (Class<T>) defaultValue.getClass();
        if (ConfigAggregateBuilder.isAggregate(type)) {
            if (type.isArray()) {
                return ConfigAggregateBuilder.create(this, name, (Class) type);
            } else {
                return ConfigAggregateBuilder.create(this, name, (Class) type, null);
            }
        } else {
            return new ConfigValueBuilder<>(this, name, type).withDefaultValue(defaultValue);
        }
    }

    /**
     * Creates an aggregate {@code ConfigValueBuilder}.
     *
     * @param defaultValue the default array of values the {@link ConfigValue} will hold.
     * @param <E> the type of elements {@code defaultValue} holds
     * @return the newly created builder
     * @see ConfigAggregateBuilder Aggregate
     */
    public <E> ConfigAggregateBuilder<E[], E> beginAggregateValue(@Nonnull String name, @Nonnull E[] defaultValue) {
        @SuppressWarnings("unchecked") Class<E[]> type = (Class<E[]>) defaultValue.getClass();
        return ConfigAggregateBuilder.create(this, name, type).withDefaultValue(defaultValue);
    }

    /**
     * Creates an aggregate {@code ConfigValueBuilder}.
     *
     * @param defaultValue the default collection of values the {@link ConfigValue} will hold.
     * @param elementType the class of the type of elements {@code defaultValue} holds
     * @param <C> the type of collection {@code defaultValue} is
     * @param <E> the type {@code elementType} represents
     * @return the newly created builder
     */
    public <C extends Collection<E>, E> ConfigAggregateBuilder<C, E> beginAggregateValue(@Nonnull String name, @Nonnull C defaultValue, @Nullable Class<E> elementType) {
        @SuppressWarnings("unchecked") Class<C> type = (Class<C>) defaultValue.getClass();
        return ConfigAggregateBuilder.create(this, name, type, elementType).withDefaultValue(defaultValue);
    }

    /**
     * Attempts to introduce a new child to this node.
     *
     * @param item The child to add
     * @throws FiberException if there was already a child by the same name
     * @see Property
     */
    public ConfigNodeBuilder add(@Nonnull TreeItem item) throws FiberException {
        add(item, false);
        return this;
    }

    /**
     * Attempts to introduce a new child to this node.
     *
     * @param item      The child to add
     * @param overwrite whether existing items should be overwritten
     * @throws FiberException if there was already a child by the same name
     * @see Property
     */
    public ConfigNodeBuilder add(@Nonnull TreeItem item, boolean overwrite) throws FiberException {
        if (!overwrite && items.containsKey(item.getName())) {
            throw new FiberException("Attempt to replace node " + item.getName());
        }
        items.put(item.getName(), item);
        return this;
    }

    /**
     * Attempts to remove an item from this node by name.
     *
     * @param name the name of the child that should be removed
     * @return the child if removed, otherwise {@code null}
     */
    public TreeItem remove(String name) {
        return items.remove(name);
    }

    /**
     * Forks this builder, creating a subtree whose parent is this node.
     *
     * @param name the name of the new {@code Node}
     * @return the created node builder
     */
    public ConfigNodeBuilder fork(String name) {
        return new ConfigNodeBuilder().withName(name).withParent(this);
    }

    /**
     * Construct a new {@code ConfigNode} based on this builder's specifications.
     *
     * <p> This method cannot be called more than once, as allowing multiple nodes to be
     * built would result in duplicated references.
     * To guard against this, usually undesirable, behaviour, this method will throw an exception on successive calls.
     *
     * @return a new {@code ConfigNode}
     * @throws IllegalStateException if this builder already built a node
     */
    public ConfigNode build() {
        if (built != null) {
            throw new IllegalStateException("Cannot build a node more than once");
        }
        built = new ConfigNode(this.name, this.comment, this.items, this.serializeSeparately);
        if (this.parent != null) {
            assert name != null;
            try {
                this.parent.add(built);
            } catch (FiberException e) {
                throw new RuntimeFiberException("Failed to attach built node to parent", e);
            }
        }
        return built;
    }

    public ConfigNodeBuilder finishNode() {
        return finishNode(n -> { });
    }

    public ConfigNodeBuilder finishNode(Consumer<ConfigNode> action) {
        if (parent == null) {
            throw new IllegalStateException("finishNode should not be called for a root node. Use build instead.");
        }
        action.accept(build());
        return parent;
    }

}
