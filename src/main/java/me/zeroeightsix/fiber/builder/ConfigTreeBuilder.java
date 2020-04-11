package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.annotation.AnnotatedSettings;
import me.zeroeightsix.fiber.annotation.Setting;
import me.zeroeightsix.fiber.annotation.Settings;
import me.zeroeightsix.fiber.exception.DuplicateChildException;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * A builder for configuration trees/branches.
 *
 * <p> Usage example:
 * <pre>{@code
 * ConfigNode config = new ConfigTreeBuilder()
 *         .beginValue("version", "1.0.0")
 *             .withFinality()
 *             .beginConstraints() // checks the default value
 *                 .regex("\\d+\\.\\d+\\.\\d+")
 *             .finishConstraints()
 *         .finishValue()
 *         .fork("child")
 *             .beginValue("A", 10)
 *                 .beginConstraints()
 *                     .composite(CompositeType.OR)
 *                         .atLeast(3)
 *                         .atMost(0)
 *                     .finishComposite()
 *                 .finishConstraints()
 *             .finishValue()
 *         .finishNode()
 *         .build();
 * }</pre>
 *
 * @see ConfigTree#builder()
 * @see PropertyMirror
 */
public class ConfigTreeBuilder implements ConfigTree {
    private final NodeCollection items = new IndexedNodeCollection(null);
    @Nullable
    protected ConfigTree parent;
    @Nullable
    protected String name;
    @Nullable
    private String comment;
    private boolean serializeSeparately;
    private ConfigBranch built;

    /**
     * Creates a new builder with initial settings.
     *
     * @param parent the initial parent
     * @param name   the initial name
     * @see ConfigTree#builder()
     * @see ConfigBranch#builder(ConfigTree, String)
     */
    public ConfigTreeBuilder(@Nullable ConfigTree parent, @Nullable String name) {
        if (parent != null && name == null) throw new IllegalArgumentException("A child node needs a name");
        this.parent = parent;
        this.name = name;
    }

    /**
     * Returns a collection of this builder's children.
     *
     * <p> The returned collection is guaranteed to have no two nodes with the same name.
     * Elements may be freely added and removed from it.
     *
     * @return the set of children
     * @see NodeCollection#add(ConfigNode, boolean)
     * @see NodeCollection#removeByName(String)
     */
    @Nonnull
    @Override
    public NodeCollection getItems() {
        return items;
    }

    /**
     * Tries to find a child in this builder by name. If a child is found, it will be returned.
     *
     * @param name The name of the child to look for
     * @return the child if found, otherwise {@code null}
     */
    @Nullable
    public ConfigNode lookup(String name) {
        return items.getByName(name);
    }

    public ConfigTreeBuilder withParent(ConfigTreeBuilder parent) {
        if (name == null && parent != null) throw new IllegalStateException("A child node needs a name");
        this.parent = parent;
        return this;
    }

    public ConfigTreeBuilder withName(String name) {
        if (name == null && parent != null) throw new IllegalStateException("Cannot remove the name from a child node");
        this.name = name;
        return this;
    }

    public ConfigTreeBuilder withComment(@Nullable String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Marks the built subtree as being serialized separately.
     *
     * <p> A subtree marked for separate serialization will not appear in the
     * serialized representation of its ancestors. This property can be useful
     * when partitioning a big configuration tree into several files.
     *
     * <p> This method has no effect if the built node is a tree root.
     *
     * @return {@code this}, for chaining
     * @see #withSeparateSerialization()
     * @see ConfigBranch#isSerializedSeparately()
     */
    public ConfigTreeBuilder withSeparateSerialization() {
        withSeparateSerialization(true);
        return this;
    }

    /**
     * Sets whether a subtree should be serialized separately.
     *
     * <p> If {@code serializeSeparately} is {@code true}, the subtree created
     * from this builder will not appear in the serialized representation of the
     * ancestor. This property can be especially useful when partitioning a
     * big configuration tree into several files.
     *
     * <p> This method has no effect if the built node is a tree root.
     *
     * @param serializeSeparately {@code true} if the built tree should be serialized separately
     * @return {@code this}, for chaining
     */
    public ConfigTreeBuilder withSeparateSerialization(boolean serializeSeparately) {
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
     * <p> The generated {@link ConfigLeaf}s will be bound to their respective fields,
     * setting the latter when the former's value is {@linkplain ConfigLeaf#setValue(Object) updated}.
     *
     * @param pojo an object serving as a base to reflectively generate a config tree
     * @return {@code this}, for chaining
     * @see Setting
     * @see Settings
     * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
     */
    public ConfigTreeBuilder applyFromPojo(Object pojo) throws FiberException {
        return applyFromPojo(pojo, AnnotatedSettings.DEFAULT_SETTINGS);
    }

    /**
     * Configure this builder using a POJO (Plain Old Java Object).
     *
     * <p> The node's structure will be based on the {@code pojo}'s fields,
     * recursively generating settings. The generated settings can be configured
     * in the {@code pojo}'s class declaration, using annotations such as {@link Setting}.
     *
     * <p> The generated {@link ConfigLeaf}s will be bound to their respective fields,
     * setting the latter when the former's value is {@linkplain ConfigLeaf#setValue(Object) updated}.
     *
     * @param pojo an object serving as a base to reflectively generate a config tree
     * @param settings an {@link AnnotatedSettings} instance used to configure this builder
     * @return {@code this}, for chaining
     * @see Setting @Setting
     * @see Settings @Settings
     * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
     */
    public ConfigTreeBuilder applyFromPojo(Object pojo, AnnotatedSettings settings) throws FiberException {
        settings.applyToNode(this, pojo);
        return this;
    }

    /**
     * Creates a scalar {@code ConfigLeafBuilder}.
     *
     * @param type the class of the type of value the {@link ConfigLeaf} produced by the builder holds
     * @param <T>  the type {@code type} represents
     * @return the newly created builder
     * @see ConfigLeafBuilder ConfigLeafBuilder
     */
    public <T> ConfigLeafBuilder<T> beginValue(@Nonnull String name, @Nonnull Class<T> type) {
        return new ConfigLeafBuilder<>(this, name, type);
    }

    /**
     * Creates a {@code ConfigLeafBuilder} with the given default value.
     *
     * @param defaultValue the default value of the {@link ConfigLeaf} that will be produced by the created builder.
     * @param <T>          the type of value the {@link ConfigLeaf} produced by the builder holds
     * @return the newly created builder
     * @see ConfigLeafBuilder
     * @see ConfigAggregateBuilder
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> ConfigLeafBuilder<T> beginValue(@Nonnull String name, @Nonnull T defaultValue) {
        Class<T> type = (Class<T>) defaultValue.getClass();
        if (ConfigAggregateBuilder.isAggregate(type)) {
            if (type.isArray()) {
                return ConfigAggregateBuilder.create(this, name, (Class) type);
            } else {
                return ConfigAggregateBuilder.create(this, name, (Class) type, null);
            }
        } else {
            return new ConfigLeafBuilder<>(this, name, type).withDefaultValue(defaultValue);
        }
    }

    /**
     * Creates an aggregate {@code ConfigLeafBuilder}.
     *
     * @param defaultValue the default array of values the {@link ConfigLeaf} will hold.
     * @param <E>          the type of elements {@code defaultValue} holds
     * @return the newly created builder
     * @see ConfigAggregateBuilder Aggregate
     */
    public <E> ConfigAggregateBuilder<E[], E> beginAggregateValue(@Nonnull String name, @Nonnull E[] defaultValue) {
        @SuppressWarnings("unchecked") Class<E[]> type = (Class<E[]>) defaultValue.getClass();
        return ConfigAggregateBuilder.create(this, name, type).withDefaultValue(defaultValue);
    }

    /**
     * Creates an aggregate {@code ConfigLeafBuilder}.
     *
     * @param defaultValue the default collection of values the {@link ConfigLeaf} will hold.
     * @param elementType  the class of the type of elements {@code defaultValue} holds
     * @param <C>          the type of collection {@code defaultValue} is
     * @param <E>          the type {@code elementType} represents
     * @return the newly created builder
     */
    public <C extends Collection<E>, E> ConfigAggregateBuilder<C, E> beginAggregateValue(@Nonnull String name, @Nonnull C defaultValue, @Nullable Class<E> elementType) {
        @SuppressWarnings("unchecked") Class<C> type = (Class<C>) defaultValue.getClass();
        return ConfigAggregateBuilder.create(this, name, type, elementType).withDefaultValue(defaultValue);
    }

    /**
     * Attempts to introduce a new child to this builder.
     *
     * @param item The child to add
     * @return {@code this}, for chaining
     * @throws DuplicateChildException if there was already a child by the same name
     * @see Property
     */
    public ConfigTreeBuilder withChild(@Nonnull ConfigNode item) throws DuplicateChildException {
        this.items.add(item);
        return this;
    }

    /**
     * Attempts to introduce a new child to this builder.
     *
     * @param item      The child to add
     * @param overwrite whether existing items should be overwritten
     * @return {@code this}, for chaining
     * @throws DuplicateChildException if there was already a child by the same name
     */
    public ConfigTreeBuilder withChild(@Nonnull ConfigNode item, boolean overwrite) throws DuplicateChildException {
        this.items.add(item, overwrite);
        return this;
    }

    /**
     * Attempts to remove an item from this node by name.
     *
     * @param name the name of the child that should be removed
     * @return the child if removed, otherwise {@code null}
     */
    public ConfigNode remove(String name) {
        return items.removeByName(name);
    }

    /**
     * Forks this builder, creating a subtree whose parent is this node.
     *
     * @param name the name of the new {@code Node}
     * @return the created node builder
     */
    public ConfigTreeBuilder fork(String name) {
        return new ConfigTreeBuilder(this, name);
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
    public ConfigBranch build() {
        if (built != null) {
            throw new IllegalStateException("Cannot build a node more than once");
        }
        built = new ConfigBranchImpl(this.name, this.comment, this.items, this.serializeSeparately);
        if (this.parent != null) {
            assert name != null;
            try {
                this.parent.getItems().add(built);
            } catch (RuntimeFiberException e) {
                throw new RuntimeFiberException("Failed to attach built node to parent", e);
            }
        }
        return built;
    }

    public ConfigTreeBuilder finishBranch() {
        return finishBranch(n -> { });
    }

    public ConfigTreeBuilder finishBranch(Consumer<ConfigBranch> action) {
        if (parent instanceof ConfigTreeBuilder) {
            action.accept(build());
            return (ConfigTreeBuilder) parent;
        } else {
            throw new IllegalStateException("finishNode should not be called for a root builder. Use build instead.");
        }
    }

}
