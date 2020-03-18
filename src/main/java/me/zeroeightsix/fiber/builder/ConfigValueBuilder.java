package me.zeroeightsix.fiber.builder;

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

public abstract class ConfigValueBuilder<T, B extends ConfigValueBuilder<T, B>> {

    @SuppressWarnings("unchecked")
    public static <E> Aggregate<E, E[]> aggregate(@Nonnull Class<E[]> arrayType) {
        if (!arrayType.isArray()) throw new IllegalArgumentException(arrayType + " is not a valid array type");
        return new Aggregate<>(arrayType, (Class<E>) arrayType.getComponentType());
    }

    public static <E, C extends Collection<E>> Aggregate<E, C> aggregate(@Nonnull Class<C> collectionType, @Nonnull Class<E> componentType) {
        if (!Collection.class.isAssignableFrom(collectionType)) throw new IllegalArgumentException(collectionType + " is not a valid Collection type");
        return new Aggregate<>(collectionType, componentType);
    }

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

    public B withName(String name) {
        this.name = name;
        return self();
    }

    public B withComment(String comment) {
        this.comment = comment;
        return self();
    }

    public B withListener(BiConsumer<T, T> consumer) {
        final BiConsumer<T, T> prevConsumer = this.consumer; // to avoid confusion
        this.consumer = (t, t2) -> {
            prevConsumer.accept(t, t2);
            consumer.accept(t, t2); // The newest consumer is called last -> listeners are called in the order they are added
        };
        return self();
    }

    public B withDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return self();
    }

    public B setFinal() {
        this.isFinal = true;
        return self();
    }

    public B setFinal(boolean isFinal) {
        this.isFinal = isFinal;
        return self();
    }

    /**
     * Sets the node that the built {@link ConfigValue} will be registered to.
     * @param node  The node this {@link ConfigValue} will be registered to.
     * @return      The builder
     */
    public B withParent(Node node) {
        parentNode = node;
        return self();
    }
    
    @SuppressWarnings("unchecked")
    protected final B self() {
        return (B) this;
    }

    public abstract ConstraintsBuilder<B, T, ?,  ?> constraints();

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

    public static class Scalar<T> extends ConfigValueBuilder<T, Scalar<T>> {
        public Scalar(@Nonnull Class<T> type) {
            super(type);
        }

        @Override
        public ConstraintsBuilder.Scalar<Scalar<T>, T> constraints() {
            return ConstraintsBuilder.scalar(this, constraintList, type);
        }
    }

    public static final class Aggregate<E, T> extends ConfigValueBuilder<T, Aggregate<E, T>> {
        @Nonnull
        private final Class<E> componentType;

        Aggregate(@Nonnull Class<T> type, @Nonnull Class<E> componentType) {
            super(type);
            this.componentType = componentType;
        }

        @Override
        public ConstraintsBuilder.Aggregate<Aggregate<E, T>, T, E> constraints() {
            return ConstraintsBuilder.aggregate(this, constraintList, type, componentType);
        }

    }
}
