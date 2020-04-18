package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.FiberId;
import me.zeroeightsix.fiber.annotation.AnnotatedSettings;
import me.zeroeightsix.fiber.builder.constraint.AggregateConstraintsBuilder;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.schema.ConfigType;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A {@code ConfigLeafBuilder} that produces aggregate {@code ConfigLeaf}s.
 *
 * <p> Aggregate types are those that hold multiple values, such as {@code List} or arrays.
 * Settings with scalar types, such as {@code Integer} or {@code String}, are created using {@link ConfigLeafBuilder}.
 *
 * @param <A> the type of aggregate value
 * @param <E> the type of values held by {@code <A>}
 * @see #create
 */
public final class ConfigAggregateBuilder<A, E, E0> extends ConfigLeafBuilder<A, List<E0>> {
    /**
     * Determines if a {@code Class} object represents an aggregate type,
     * ie. if it is an {@linkplain Class#isArray() Array} or a {@linkplain Collection}.
     *
     * @param type the type to check
     * @return {@code true} if {@code type} is an aggregate type;
     * {@code false} otherwise
     */
    public static boolean isAggregate(Class<?> type) {
        // TODO replace with ConvertibleType.isAggregate
        // TODO for annotation handling, add generic type processing (ParameterizedType -> ConvertibleType)
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    /**
     * Creates and returns an {@link ConfigAggregateBuilder aggregate builder} for an array type.
     *
     * @param arrayType the class of the array used for this aggregate builder
     * @param <E>       the type of values held by {@code arrayType}
     * @return the newly created builder
     * @see #isAggregate
     */
    @SuppressWarnings("unchecked")
    public static <E, E0> ConfigAggregateBuilder<E[], E> create(ConfigTreeBuilder source, @Nonnull String name, @Nonnull Class<E[]> arrayType) {
        if (!arrayType.isArray()) throw new RuntimeFiberException(arrayType + " is not a valid array type");
        return new ConfigAggregateBuilder<>(source, name, arrayType, (Class<E>) AnnotatedSettings.wrapPrimitive(arrayType.getComponentType()));
    }

    /**
     * Creates and returns an {@link ConfigAggregateBuilder aggregate builder} for a collection type.
     *
     * @param collectionType the class of the collection used for this aggregate builder
     * @param componentType  the class of the type of elements {@code collectionType} holds
     * @param <C>            the type {@code collectionType} represents. eg. {@code List}
     * @param <E>            the type {@code componentType} represents. eg. {@code Integer}
     * @return the newly created builder
     */
    public static <C, E, E0> ConfigAggregateBuilder<C, E, E0> create(ConfigTreeBuilder source, @Nonnull String name, @Nonnull ConfigType<C, List<E0>> type) {
        if (!type.isList()) throw new IllegalArgumentException(type + " is not a valid list type");
        return new ConfigAggregateBuilder<>(source, name, type);
    }

    private ConfigAggregateBuilder(ConfigTreeBuilder source, @Nonnull String name, @Nonnull ConfigType<A, List<E0>> type) {
        super(source, name, type);
    }

    @Override
    public ConfigAggregateBuilder<A, E, E0> withName(@Nonnull String name) {
        super.withName(name);
        return this;
    }

    @Override
    public ConfigAggregateBuilder<A, E, E0> withComment(String comment) {
        super.withComment(comment);
        return this;
    }

    @Override
    public <A1> ConfigAggregateBuilder<A, E, E0> withAttribute(FiberId id, ConfigType<A1, A1> type, A1 defaultValue) {
        super.withAttribute(id, type, defaultValue);
        return this;
    }

    @Override
    public ConfigAggregateBuilder<A, E, E0> withListener(BiConsumer<A, A> consumer) {
        super.withListener(consumer);
        return this;
    }

    @Override
    public ConfigAggregateBuilder<A, E, E0> withDefaultValue(A defaultValue) {
        super.withDefaultValue(defaultValue);
        return this;
    }

    @Override
    public AggregateConstraintsBuilder<A, E, E0> beginConstraints() {
        return new AggregateConstraintsBuilder<>(this, constraints, type);
    }
}
