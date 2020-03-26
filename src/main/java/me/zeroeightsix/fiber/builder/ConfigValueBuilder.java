package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.builder.constraint.ConstraintsBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.ConfigValue;
import me.zeroeightsix.fiber.tree.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A builder for scalar {@code ConfigValue}s.
 *
 * <p> The settings created by this builder are considered atomic, and do not allow specifications at the component level.
 * Settings with aggregate types, such as arrays and collections, should be created using {@link ConfigAggregateBuilder}.
 *
 * <p><strong>This builder should not be reused if the default values are intended to be mutated!</strong>
 * Multiple calls to {@link #build()} will result in duplicated references.
 *
 * @param <T> the type of value the produced {@code ConfigValue} will hold
 * @see ConfigValue
 */
public class ConfigValueBuilder<T> {

    @Nonnull
    protected final Class<T> type;
    @Nullable
    private String name;
    @Nullable
    private String comment = null;
    @Nullable
    private T defaultValue = null;
    private boolean isFinal = false;
    private BiConsumer<T, T> consumer = (t, t2) -> { };
    protected List<Constraint<? super T>> constraintList = new ArrayList<>();

    // Special snowflake that doesn't really belong in a builder.
    // Used to easily register nodes to another node.
    private ConfigNodeBuilder parentNode = null;

    /**
     * Creates a new scalar {@code ConfigValueBuilder}.
     *
     * @param type the class object representing the type of values this builder will create settings for
     */
    public ConfigValueBuilder(@Nonnull Class<T> type) {
        this.type = type;
    }

    /**
     * Sets the {@code ConfigValue}'s name.
     *
     * <p> If {@code null}, or if this method is never called, the {@code ConfigValue} won't have a name. Thus, it might be ignored during (de)serialisation. It also won't be able to be found by name in its parent node.
     *
     * @param name the name
     * @return {@code this} builder
     * @see Node#lookup
     */
    public ConfigValueBuilder<T> withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the {@code ConfigValue}'s comment.
     *
     * <p> If {@code null}, or if this method is never called, the {@code ConfigValue} won't have a comment. An empty comment (non null, but only consisting of whitespace) will be serialised.
     *
     * @param comment the comment
     * @return {@code this} builder
     */
    public ConfigValueBuilder<T> withComment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Adds a listener to the {@code ConfigValue}.
     *
     * <p> Listeners are called when the value of a {@code ConfigValue} is changed. They are of type {@link BiConsumer}: the first argument being the old value, and the second argument being the new value.
     *
     * <p> Listeners set with this method are chained: if there was already one specified, a new listener is created that calls the old one first, and then the new one.
     *
     * @param consumer the listener
     * @return {@code this} builder
     */
    public ConfigValueBuilder<T> withListener(BiConsumer<T, T> consumer) {
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
     * <p> If {@code null}, or if this method is never called, the {@code ConfigValue} will have no default value.
     *
     * @param defaultValue the default value
     * @return {@code this} builder
     */
    public ConfigValueBuilder<T> withDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Marks a setting as final.
     *
     * <p> As a result of this method, any attempt to update the value of the resulting setting will fail.
     * This method behaves as if: {@code this.setFinal(true)}.
     *
     * @return {@code this} builder
     * @see #setFinal(boolean)
     */
    public ConfigValueBuilder<T> setFinal() {
        this.isFinal = true;
        return this;
    }

    /**
     * Sets the finality.
     *
     * <p> If {@code true}, the produced setting can not be changed. It will be initialised with its default value, if there is one. Afterwards, it can not be changed again.
     *
     * @param isFinal the finality
     * @return {@code this} builder
     */
    public ConfigValueBuilder<T> setFinal(boolean isFinal) {
        this.isFinal = isFinal;
        return this;
    }

    /**
     * Sets the node that the {@code ConfigValue} will be registered to.
     *
     * @param node The node the {@link ConfigValue} will be registered to.
     * @return The builder
     */
    public ConfigValueBuilder<T> withParent(ConfigNodeBuilder node) {
        parentNode = node;
        return this;
    }

    /**
     * Creates a constraint builder for this {@code ConfigValueBuilder}.
     *
     * @return the created builder
     * @see ConstraintsBuilder
     */
    public ConstraintsBuilder<? extends ConfigValueBuilder<T>, T> constraints() {
        return new ConstraintsBuilder<>(this, constraintList, type);
    }

    /**
     * Builds the {@code ConfigValue}.
     *
     * <p> If a parent was specified using {@link #withParent}, the {@code ConfigValue} will also be registered to its parent node.
     *
     * @return the {@code ConfigValue}
     */
    public ConfigValue<T> build() {
        ConfigValue<T> built = new ConfigValue<>(name, comment, defaultValue, defaultValue, consumer, constraintList, type, isFinal);

        if (parentNode != null) {
            // We don't know what kind of evil collection we're about to add a node to.
            // Though, we don't really want to throw an exception on this method because no developer likes try-catching every setting they build.
            // Let's tread with caution.
            try {
                parentNode.add(built);
            } catch (FiberException e) {
                throw new RuntimeFiberException("Failed to register leaf to node", e);
            }
        }

        return built;
    }

}
