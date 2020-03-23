package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.annotation.AnnotatedSettings;
import me.zeroeightsix.fiber.builder.constraint.ConstraintsBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.ConfigValue;
import me.zeroeightsix.fiber.tree.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A builder for {@code ConfigValue}s.
 *
 * <p> This is the abstract base class for all builders of this type. Builders implementing this class include
 * {@link Scalar Scalar} and {@link Aggregate Aggregate} for scalar (one-value) and aggregate (collection/array) values, respectively.
 *
 * @param <T> the type of value the produced {@code ConfigValue} will hold
 * @param <B> the type of {@code this}
 * @see ConfigValue
 */
public abstract class ConfigValueBuilder<T, B extends ConfigValueBuilder<T, B>> {

    /**
     * Determines if a {@code Class} object represents an aggregate type,
     * ie. if it is an {@linkplain Class#isArray() Array} or a {@linkplain Collection}.
     *
     * @param type the type to check
     * @return {@code true} if {@code type} is an aggregate type;
     * {@code false} otherwise
     */
    public static boolean isAggregate(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    /**
     * Creates and returns an {@link ConfigValueBuilder.Aggregate aggregate builder} for an array type.
     *
     * @param arrayType the class of the array used for this aggregate builder
     * @param <E> the type of values held by {@code arrayType}
     * @return the newly created builder
     * @see #isAggregate
     */
    @SuppressWarnings("unchecked")
    public static <E> Aggregate<E[], E> aggregate(@Nonnull Class<E[]> arrayType) {
        if (!arrayType.isArray()) throw new IllegalArgumentException(arrayType + " is not a valid array type");
        return new Aggregate<>(arrayType, (Class<E>) AnnotatedSettings.wrapPrimitive(arrayType.getComponentType()));
    }

    /**
     * Creates and returns an {@link ConfigValueBuilder.Aggregate aggregate builder} for a collection type.
     *
     * @param collectionType the class of the collection used for this aggregate builder
     * @param componentType the class of the type of elements {@code collectionType} holds
     * @param <C> the type {@code collectionType} represents. eg. {@code List}
     * @param <E> the type {@code componentType} represents. eg. {@code Integer}
     * @return the newly created builder
     */
    @SuppressWarnings("unchecked")
    public static <C extends Collection<E>, E> Aggregate<C, E> aggregate(@Nonnull Class<? super C> collectionType, @Nonnull Class<E> componentType) {
        if (!Collection.class.isAssignableFrom(collectionType)) throw new IllegalArgumentException(collectionType + " is not a valid Collection type");
        return new Aggregate<>((Class<C>) collectionType, componentType);
    }

    /**
     * Creates and returns a scalar {@code ConfigValueBuilder}.
     *
     * @param type the class of the type used for this builder
     * @param <T> the type {@code type} represents. For example, this could be {@code Integer}
     * @return the newly created builder
     * @see Scalar
     */
    public static <T> Scalar<T> scalar(Class<T> type) {
        return new Scalar<>(type);
    }

    @Nonnull
    protected final Class<T> type;
    @Nullable
    private String name;
    @Nullable
    private String comment = null;
    @Nullable
    private T defaultValue = null;
    private boolean isFinal = false;
    private BiConsumer<T, T> consumer = (t, t2) -> {};
    protected List<Constraint<? super T>> constraintList = new ArrayList<>();

    // Special snowflake that doesn't really belong in a builder.
    // Used to easily register nodes to another node.
    private Node parentNode = null;

    /**
     * @see #aggregate(Class)
     * @see #aggregate(Class, Class)
     * @see #scalar(Class)
     */
    ConfigValueBuilder(@Nonnull Class<T> type) {
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
    public B withName(String name) {
        this.name = name;
        return self();
    }

    /**
     * Sets the {@code ConfigValue}'s comment.
     *
     * <p> If {@code null}, or if this method is never called, the {@code ConfigValue} won't have a comment. An empty comment (non null, but only consisting of whitespace) will be serialised.
     *
     * @param comment the comment
     * @return {@code this} builder
     */
    public B withComment(String comment) {
        this.comment = comment;
        return self();
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
    public B withListener(BiConsumer<T, T> consumer) {
        final BiConsumer<T, T> prevConsumer = this.consumer; // to avoid confusion
        this.consumer = (t, t2) -> {
            prevConsumer.accept(t, t2);
            consumer.accept(t, t2); // The newest consumer is called last -> listeners are called in the order they are added
        };
        return self();
    }

    /**
     * Sets the default value.
     *
     * <p> If {@code null}, or if this method is never called, the {@code ConfigValue} will have no default value.
     *
     * @param defaultValue the default value
     * @return {@code this} builder
     */
    public B withDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return self();
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
    public B setFinal() {
        this.isFinal = true;
        return self();
    }

    /**
     * Sets the finality.
     *
     * <p> If {@code true}, the produced setting can not be changed. It will be initialised with its default value, if there is one. Afterwards, it can not be changed again.
     *
     * @param isFinal the finality
     * @return {@code this} builder
     */
    public B setFinal(boolean isFinal) {
        this.isFinal = isFinal;
        return self();
    }

    /**
     * Sets the node that the {@code ConfigValue} will be registered to.
     *
     * @param node The node the {@link ConfigValue} will be registered to.
     * @return The builder
     */
    public B withParent(Node node) {
        parentNode = node;
        return self();
    }

    @SuppressWarnings("unchecked")
    protected final B self() {
        return (B) this;
    }

    /**
     * Creates a constraint builder for this {@code ConfigValueBuilder}.
     *
     * @return the created builder
     * @see ConstraintsBuilder
     */
    public abstract ConstraintsBuilder<B, T, ?> constraints();

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
            } catch (Exception e) {
                throw new RuntimeFiberException("Failed to register leaf to node", e);
            }
        }

        return built;
    }

    /**
     * A {@code ConfigValueBuilder} that produces scalar {@code ConfigValue}s.
     *
     * <p>Scalar types are those with only one value, such as {@code Integer} or {@code String}.
     * Settings with aggregate types, such as {@code List}s or arrays, are created using {@link Aggregate}
     *
     * @param <T> the type of scalar value
     * @see #scalar
     */
    public static class Scalar<T> extends ConfigValueBuilder<T, Scalar<T>> {
        Scalar(@Nonnull Class<T> type) {
            super(type);
        }

        @Override
        public ConstraintsBuilder.Scalar<Scalar<T>, T> constraints() {
            return ConstraintsBuilder.scalar(this, constraintList, type);
        }
    }

    /**
     * A {@code ConfigValueBuilder} that produces aggregate {@code ConfigValue}s.
     *
     * <p>Aggregate types are those that hold multiple values, such as {@code List} or arrays.
     * Settings with scalar types, such as {@code Integer} or {@code String}, are created using {@link Scalar}.
     *
     * @param <A> the type of aggregate value
     * @param <E> the type of values held by {@code <A>}
     * @see #aggregate
     */
    public static final class Aggregate<A, E> extends ConfigValueBuilder<A, Aggregate<A, E>> {
        @Nonnull
        private final Class<E> componentType;

        Aggregate(@Nonnull Class<A> type, @Nonnull Class<E> componentType) {
            super(type);
            this.componentType = componentType;
        }

        @Override
        public ConstraintsBuilder.Aggregate<Aggregate<A, E>, A, E> constraints() {
            return ConstraintsBuilder.aggregate(this, constraintList, type, componentType);
        }

    }
}
