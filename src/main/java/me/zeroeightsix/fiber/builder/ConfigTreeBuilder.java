package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.FiberId;
import me.zeroeightsix.fiber.annotation.AnnotatedSettings;
import me.zeroeightsix.fiber.annotation.Setting;
import me.zeroeightsix.fiber.annotation.Settings;
import me.zeroeightsix.fiber.exception.DuplicateChildException;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.exception.IllegalTreeStateException;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.schema.ConfigType;
import me.zeroeightsix.fiber.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
public class ConfigTreeBuilder extends ConfigNodeBuilder implements ConfigTree {
    private final NodeCollection items = new IndexedNodeCollection(null);
    @Nullable
    private String name;
    @Nullable
    private String comment;
    private boolean serializeSeparately;

    /**
     * Creates a new builder with initial settings.
     *
     * @param parent the initial parent
     * @param name   the initial name
     * @see ConfigTree#builder()
     * @see ConfigBranch#builder(ConfigTree, String)
     */
    public ConfigTreeBuilder(@Nullable ConfigTree parent, @Nullable String name) {
        super(parent, name);
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

    /**
     * Sets the {@code ConfigBranch}'s name.
     *
     * @param name the name
     * @return {@code this} builder
     * @see ConfigTree#lookup
     */
    @Override
    public ConfigTreeBuilder withName(String name) {
        if (name == null && parent != null) throw new IllegalStateException("Cannot remove the name from a child node");
        this.name = name;
        return this;
    }

    /**
     * Sets the {@code ConfigBranch}'s comment.
     *
     * <p> If {@code null}, or if this method is never called, the {@code ConfigLeaf} won't have a comment.
     * An empty comment (non null, but only consisting of whitespace) will be serialised.
     *
     * @param comment the comment
     * @return {@code this} builder
     */
    @Override
    public ConfigTreeBuilder withComment(@Nullable String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Adds a {@link me.zeroeightsix.fiber.tree.ConfigAttribute} to the built {@code ConfigBranch}.
     *
     * @param id           the id of the attribute
     * @param type         the class object representing the type of values stored in the attribute
     * @param defaultValue the attribute's default value
     * @param <A>          the type of values stored in the attribute
     * @return {@code this}, for chaining
     * @see ConfigNode#getAttributes()
     */
    @Override
    public <A> ConfigTreeBuilder withAttribute(FiberId id, ConfigType<A, A> type, A defaultValue) {
        super.withAttribute(id, type, defaultValue);
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
     * @param pojo     an object serving as a base to reflectively generate a config tree
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
     * Creates a scalar {@code ConfigLeafBuilder} with the given default value.
     *
     * @param type the class of the type of value the {@link ConfigLeaf} produced by the builder holds
     * @param <T>  the type {@code type} represents
     * @return the newly created builder
     * @see ConfigLeafBuilder ConfigLeafBuilder
     * @see #withValue(String, Class, Object)
     * @see #beginAggregateValue(String, Class, Class, Collection)
     * @see #beginListValue(String, Class, Object[])
     * @see #beginSetValue(String, Class, Object[])
     */
    public <T, T0> ConfigLeafBuilder<T, T0> beginValue(@Nonnull String name, @Nonnull ConfigType<T, T0> type, @Nullable T defaultValue) {
        return new ConfigLeafBuilder<>(this, name, type).withDefaultValue(defaultValue);
    }

    /**
     * Adds a {@code ConfigLeaf} with the given type and default value to the tree.
     *
     * <p> This method allows only basic configuration of the created leaf.
     * For more flexibility, one of the {@code begin*Value} methods can be used.
     *
     * @param name         the name of the child leaf
     * @param type         the type of values held by the leaf
     * @param defaultValue the default value of the {@link ConfigLeaf} to create.
     * @param <T>          the type of value the {@link ConfigLeaf} holds.
     * @return {@code this}, for chaining
     * @see #beginValue(String, Class, Object)
     * @see #beginAggregateValue(String, Class, Class, Collection)
     * @see #beginListValue(String, Class, Object[])
     * @see #beginSetValue(String, Class, Object[])
     */
    public <T, T0> ConfigTreeBuilder withValue(@Nonnull String name, @Nonnull ConfigType<T, T0> type, @Nullable T defaultValue) {
        this.items.add(new ConfigLeafImpl<>(name, null, defaultValue, (a, b) -> { }, Collections.emptySet(), type));
        return this;
    }

    /**
     * Creates a {@code ConfigAggregateBuilder} for a {@link List} settings with the given default elements.
     *
     * <p> This method should not be called by intermediary generic methods
     * (eg. {@code <T> void f(ConfigTreeBuilder b, T t) {b.beginListValue("", t);}}),
     * as it will prevent type checking while building the tree.
     * Use {@link #beginAggregateValue(String, Class, Class, Collection)} in those cases instead.
     *
     * @param defaultValues the default values of the
     * @param <E>            the type of elements contained by the set
     * @return the newly created builder
     */
    @SuppressWarnings("unchecked")
    public <E, E0> ConfigAggregateBuilder<List<E>, E, E0> beginListValue(@Nonnull String name, ConfigType<E, E0> elementType, E... defaultValues) {
        return this.beginAggregateValue(name, List.class, elementType, Collections.unmodifiableList(Arrays.asList(defaultValues)));
    }

    /**
     * Creates an aggregate {@code ConfigLeafBuilder}.
     *
     * @param collectionType the class object representing the type of collection to create a setting for
     * @param elementType    the class object representing the type of elements {@code defaultValue} holds
     * @param defaultValue   the default collection of values the {@link ConfigLeaf} will hold.
     * @param <C>            the type of collection {@code defaultValue} is
     * @param <E>            the type {@code elementType} represents
     * @return the newly created builder
     */
    public <C, E, E0> ConfigAggregateBuilder<C, E, E0> beginAggregateValue(@Nonnull String name, @Nonnull ConfigType<C, List<E0>> collectionType, @Nonnull C defaultValue) {
        return ConfigAggregateBuilder.<C, E, E0>create(this, name, collectionType).withDefaultValue(defaultValue);
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
     * <p> Calling this method more than once with the same parameters (specifically same parent and/or children)
     * may result in exceptions being thrown, as the resulting tree structure will be invalid.
     *
     * @return a new {@code ConfigNode}
     * @throws RuntimeFiberException if building the node results in an invalid tree
     */
    @Override
    public ConfigBranch build() throws RuntimeFiberException {
        try {
            ConfigBranch built = new ConfigBranchImpl(this.name, this.comment, this.items, this.serializeSeparately);
            built.getAttributes().putAll(this.attributes);
            if (this.parent != null) {
                assert name != null;
                this.parent.getItems().add(built);
            }
            return built;
        } catch (IllegalTreeStateException e) {
            throw new RuntimeFiberException("Failed to build branch '" + this.name + "'", e);
        }
    }

    public ConfigTreeBuilder finishBranch() {
        return finishBranch(n -> {});
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
