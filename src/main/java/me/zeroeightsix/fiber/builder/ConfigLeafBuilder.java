package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.FiberId;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.schema.ConfigType;
import me.zeroeightsix.fiber.tree.ConfigLeaf;
import me.zeroeightsix.fiber.tree.ConfigLeafImpl;
import me.zeroeightsix.fiber.tree.ConfigNode;
import me.zeroeightsix.fiber.tree.ConfigTree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A builder for {@code ConfigLeaf}s.
 *
 * @param <T> the type of value the produced {@code ConfigLeaf} will hold
 * @see ConfigLeaf
 */
public class ConfigLeafBuilder<T, T0> extends ConfigNodeBuilder {

    @Nonnull
    protected final ConfigType<T, T0> type;

    @Nullable
    private T defaultValue = null;

    private BiConsumer<T, T> consumer = (t, t2) -> { };
    protected Set<Constraint<? super T0>> constraints = new LinkedHashSet<>();

    /**
     * Creates a new scalar {@code ConfigLeafBuilder}.
     *  @param parentNode the {@code ConfigTreeBuilder} this builder originates from
     * @param name the name of the {@code ConfigLeaf} produced by this builder
     * @param type       the class object representing the type of values this builder will create settings for
     */
    public ConfigLeafBuilder(ConfigTreeBuilder parentNode, @Nonnull String name, @Nonnull ConfigType<T, T0> type) {
        super(parentNode, name);
        this.type = type;
    }

    @Nonnull
    public ConfigType<T, T0> getType() {
        return type;
    }

    /**
     * Sets the {@code ConfigLeaf}'s name.
     *
     * @param name the name
     * @return {@code this} builder
     * @see ConfigTree#lookup
     */
    @Override
    public ConfigLeafBuilder<T, T0> withName(@Nonnull String name) {
        super.withName(name);
        return this;
    }

    /**
     * Sets the {@code ConfigLeaf}'s comment.
     *
     * <p> If {@code null}, or if this method is never called, the {@code ConfigLeaf} won't have a comment.
     * An empty comment (non null, but only consisting of whitespace) will be serialised.
     *
     * @param comment the comment
     * @return {@code this} builder
     */
    @Override
    public ConfigLeafBuilder<T, T0> withComment(String comment) {
        super.withComment(comment);
        return this;
    }

    /**
     * Adds a {@link me.zeroeightsix.fiber.tree.ConfigAttribute} to the built {@code ConfigLeaf}.
     *
     * @param id           the id of the attribute
     * @param type         the class object representing the type of values stored in the attribute
     * @param defaultValue the attribute's default value
     * @param <A>          the type of values stored in the attribute
     * @return {@code this}, for chaining
     * @see ConfigNode#getAttributes()
     */
    @Override
    public <A> ConfigLeafBuilder<T, T0> withAttribute(FiberId id, ConfigType<A, A> type, A defaultValue) {
        super.withAttribute(id, type, defaultValue);
        return this;
    }

    /**
     * Adds a listener to the {@code ConfigLeaf}.
     *
     * <p> Listeners are called when the value of a {@code ConfigLeaf} is changed. They are of type {@link BiConsumer}: the first argument being the old value, and the second argument being the new value.
     *
     * <p> Listeners set with this method are chained: if there was already one specified, a new listener is created that calls the old one first, and then the new one.
     *
     * @param consumer the listener
     * @return {@code this} builder
     */
    public ConfigLeafBuilder<T, T0> withListener(BiConsumer<T, T> consumer) {
        final BiConsumer<T, T> prevConsumer = this.consumer; // to avoid confusion
        this.consumer = (t, t2) -> {
            prevConsumer.accept(t, t2);
            consumer.accept(t, t2); // The newest consumer is called last -> listeners are called in the order they are added
        };
        return this;
    }

    /**
     * Sets the default value.
     *
     * <p> If {@code null}, or if this method is never called, the {@code ConfigLeaf} will have no default value.
     *
     * <p> Note that every {@code ConfigLeaf} created from this builder will share a reference
     * to the given {@code defaultValue}. Immutability is encouraged.
     *
     * @param defaultValue the default value
     * @return {@code this} builder
     */
    public ConfigLeafBuilder<T, T0> withDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Builds the {@code ConfigLeaf}.
     *
     * <p> If a parent was specified in the constructor, the {@code ConfigLeaf} will also be registered to its parent node.
     *
     * <p> This method should not be called multiple times <em>if the default value is intended to be mutated</em>.
     * Multiple calls will result in duplicated references to the default value.
     *
     * @return the {@code ConfigLeaf}
     */
    @Override
    public ConfigLeaf<T, T0> build() {
        if (defaultValue != null) {
            T0 convertedDefault = this.type.toRawType(this.defaultValue);
            for (Constraint<? super T0> constraint : constraints) {
                if (!constraint.test(convertedDefault).hasPassed()) {
                    throw new RuntimeFiberException("Default value '" + defaultValue + "' does not satisfy type constraint " + constraint.getType().getIdentifier());
                }
            }
            for (Constraint<? super T0> constraint : constraints) {
                if (!constraint.test(convertedDefault).hasPassed()) {
                    throw new RuntimeFiberException("Default value '" + defaultValue + "' does not satisfy custom constraint " + constraint.getType().getIdentifier());
                }
            }
        }
        Set<Constraint<? super T0>> constraints = new LinkedHashSet<>(this.constraints);
        ConfigLeaf<T, T0> built = new ConfigLeafImpl<>(Objects.requireNonNull(name, "Cannot build a value without a name"), type, comment, defaultValue, consumer);
        built.getAttributes().putAll(this.attributes);

        if (parent != null) {
            // We don't know what kind of evil collection we're about to add a node to.
            // Though, we don't really want to throw an exception on this method because no developer likes try-catching every setting they build.
            // Let's tread with caution.
            try {
                parent.getItems().add(built);
            } catch (RuntimeFiberException e) {
                throw new RuntimeFiberException("Failed to register leaf to node", e);
            }
        }

        return built;
    }

    public ConfigTreeBuilder finishValue() {
        return finishValue(n -> {});
    }

    public ConfigTreeBuilder finishValue(Consumer<ConfigLeaf<T, T0>> action) {
        if (parent instanceof ConfigTreeBuilder) {
            action.accept(build());
            return (ConfigTreeBuilder) parent;
        } else {
            throw new IllegalStateException("finishValue should not be called for an independent builder. Use build instead.");
        }
    }
}
