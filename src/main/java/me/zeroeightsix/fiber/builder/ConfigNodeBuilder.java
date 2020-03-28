package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <p><strong>This builder should not be reused!</strong>
 * Multiple calls to {@link #build()} will result in duplicated references.
 */
public class ConfigNodeBuilder implements NodeLike {
    private final Map<String, TreeItem> items = new HashMap<>();
    @Nullable
    protected ConfigNodeBuilder parent;
    @Nullable
    protected String name;
    @Nullable
    private String comment;
    private boolean serializeSeparately;

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

    public ConfigNodeBuilder parent(ConfigNodeBuilder parent) {
        if (name == null && parent != null) throw new IllegalStateException("A child node needs a name");
        this.parent = parent;
        return this;
    }

    public ConfigNodeBuilder name(String name) {
        if (name == null && parent != null) throw new IllegalStateException("Cannot remove the name from a child node");
        this.name = name;
        return this;
    }

    public ConfigNodeBuilder comment(@Nullable String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Marks the built node as being serialized separately
     */
    public ConfigNodeBuilder serializeSeparately() {
        serializeSeparately(true);
        return this;
    }

    /**
     * @param serializeSeparately if {@code true}, the subtree will not appear in the
     *                            serialized representation of the built {@code Node}
     * @return {@code this}, for chaining
     */
    public ConfigNodeBuilder serializeSeparately(boolean serializeSeparately) {
        this.serializeSeparately = serializeSeparately;
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
    public <T> ConfigValueBuilder<? extends ConfigNodeBuilder, T> value(@Nonnull String name, @Nonnull Class<T> type) {
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
    public <T> ConfigValueBuilder<? extends ConfigNodeBuilder, T> value(@Nonnull String name, @Nonnull T defaultValue) {
        Class<T> type = (Class<T>) defaultValue.getClass();
        if (ConfigAggregateBuilder.isAggregate(type)) {
            if (type.isArray()) {
                return ConfigAggregateBuilder.create(this, name, (Class) type);
            } else {
                return ConfigAggregateBuilder.create(this, name, (Class) type, null);
            }
        } else {
            return new ConfigValueBuilder<>(this, name, type).defaultValue(defaultValue);
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
    public <E> ConfigAggregateBuilder<? extends ConfigNodeBuilder, E[], E> aggregateValue(@Nonnull String name, @Nonnull E[] defaultValue) {
        @SuppressWarnings("unchecked") Class<E[]> type = (Class<E[]>) defaultValue.getClass();
        return ConfigAggregateBuilder.create(this, name, type).defaultValue(defaultValue);
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
    public <C extends Collection<E>, E> ConfigAggregateBuilder<? extends ConfigNodeBuilder, C, E> aggregateValue(@Nonnull String name, @Nonnull C defaultValue, @Nullable Class<E> elementType) {
        @SuppressWarnings("unchecked") Class<C> type = (Class<C>) defaultValue.getClass();
        return ConfigAggregateBuilder.create(this, name, type, elementType).defaultValue(defaultValue);
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
    public ConfigNodeBuilder.Forked<? extends ConfigNodeBuilder> fork(String name) {
        return new ConfigNodeBuilder.Forked<>(this, name);
    }

    public ConfigNode build() {
        ConfigNode built = new ConfigNode(this.name, this.comment, this.items, this.serializeSeparately);
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

    public static class Forked<S extends ConfigNodeBuilder> extends ConfigNodeBuilder {
        private final S source;

        public Forked(S parent, String name) {
            if (parent == null) throw new NullPointerException();
            if (name == null) throw new NullPointerException();

            this.source = parent;
            this.parent = parent;
            this.name = name;
        }

        @Override
        public Forked<S> parent(ConfigNodeBuilder parent) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Forked<S> name(String name) {
            super.name(name);
            return this;
        }

        @Override
        public Forked<S> comment(@Nullable String comment) {
            super.comment(comment);
            return this;
        }

        @Override
        public Forked<S> serializeSeparately() {
            super.serializeSeparately();
            return this;
        }

        @Override
        public Forked<S> serializeSeparately(boolean serializeSeparately) {
            super.serializeSeparately(serializeSeparately);
            return this;
        }

        @Override
        public <T> ConfigValueBuilder<Forked<S>, T> value(@Nonnull String name, @Nonnull Class<T> type) {
            return new ConfigValueBuilder<>(this, name, type);
        }

        @Override
        public <T> ConfigValueBuilder<Forked<S>, T> value(@Nonnull String name, @Nonnull T defaultValue) {
            @SuppressWarnings("unchecked") Class<T> type = (Class<T>) defaultValue.getClass();
            return new ConfigValueBuilder<>(this, name, type).defaultValue(defaultValue);
        }

        @Override
        public <E> ConfigAggregateBuilder<Forked<S>, E[], E> aggregateValue(@Nonnull String name, @Nonnull E[] defaultValue) {
            @SuppressWarnings("unchecked") Class<E[]> type = (Class<E[]>) defaultValue.getClass();
            return ConfigAggregateBuilder.create(this, name, type).defaultValue(defaultValue);
        }

        @Override
        public <C extends Collection<E>, E> ConfigAggregateBuilder<Forked<S>, C, E> aggregateValue(@Nonnull String name, @Nonnull C defaultValue, @Nullable Class<E> elementType) {
            @SuppressWarnings("unchecked") Class<C> type = (Class<C>) defaultValue.getClass();
            return ConfigAggregateBuilder.create(this, name, type, elementType).defaultValue(defaultValue);
        }

        @Override
        public Forked<S> add(@Nonnull TreeItem item) throws FiberException {
            super.add(item);
            return this;
        }

        @Override
        public Forked<S> add(@Nonnull TreeItem item, boolean overwrite) throws FiberException {
            super.add(item, overwrite);
            return this;
        }

        @Override
        public Forked<Forked<S>> fork(String name) {
            return new ConfigNodeBuilder.Forked<>(this, name);
        }

        public S finishNode() {
            return finishNode(n -> {});
        }

        public S finishNode(Consumer<ConfigNode> action) {
            action.accept(build());
            return source;
        }
    }

}
