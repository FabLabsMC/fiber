package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.builder.ConfigAggregateBuilder;
import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * A {@code ConfigLeaf} with some value of type {@code T}.
 *
 * @param <T> The type of value this class holds
 * @see ConfigLeaf
 */
public class ConfigValue<T> extends ConfigLeaf implements Property<T> {

    @Nullable
    private T value;
    @Nullable
    private final T defaultValue;
    @Nonnull
    private final BiConsumer<T, T> consumer;
    @Nonnull
    private final List<Constraint<? super T>> constraintList;
    @Nonnull
    private final Class<T> type;
    private final Predicate<T> restriction;

    /**
     * Creates a {@code ConfigValue}.
     *
     * @param name the name for this item
     * @param comment the comment for this item
     * @param value the current value
     * @param defaultValue the default value for this item
     *                     <br> Preferably, if this item's value has constraints, the default value should satisfy those constraints.
     * @param consumer the consumer or listener for this item. When this item's value changes, the consumer will be called with the old value as first argument and the new value as second argument.
     * @param constraintList the list of constraints for this item. For a value to be accepted, all constraints must be satisfied.
     * @param type the type of value this item holds
     * @param isFinal whether or not this value can be change. If {@code true}, {@link #setValue(Object)} will always return {@code false}, implying {@code this} was not mutated.
     */
    public ConfigValue(@Nullable String name, @Nullable String comment, @Nullable T value, @Nullable T defaultValue, @Nonnull BiConsumer<T, T> consumer, @Nonnull List<Constraint<? super T>> constraintList, @Nonnull Class<T> type, final boolean isFinal) {
        super(name, comment);
        this.value = value;
        this.defaultValue = defaultValue;
        this.consumer = consumer;
        this.constraintList = constraintList;
        this.type = type;
        if (isFinal) {
            restriction = t -> false;
        } else {
            restriction = t -> constraintList.stream().allMatch(constraint -> constraint.test(t));
        }
    }

    @Override
    @Nullable
    public T getValue() {
        return value;
    }

    @Nonnull
    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public boolean setValue(@Nullable T value) {
        if (!restriction.test(value)) return false;
        T oldValue = this.value;
        this.value = value;
        this.consumer.accept(oldValue, value);
        return true;
    }

    /**
     * Returns the listener for this item.
     *
     * <p> When this item's value changes, the consumer will be called with the old value as first argument and the new value as second argument.
     *
     * @return the listener
     */
    @Nonnull
    public BiConsumer<T, T> getListener() {
        return consumer;
    }

    /**
     * Returns the default value for this item.
     *
     * @return the default value
     */
    @Nullable
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the list of constraints for this item.
     *
     * <p> For a call to {@link #setValue} to pass (and such, return {@code true}), the value must satisfy all constraints in this list.
     *
     * @return the list of constraints
     */
    @Nonnull
    public List<Constraint<? super T>> getConstraints() {
        return constraintList;
    }

    /**
     * Creates a scalar {@code ConfigValueBuilder}.
     *
     * @param type the class of the type of value the {@link ConfigValue} produced by the builder holds
     * @param <T> the type {@code type} represents
     * @return the newly created builder
     * @see me.zeroeightsix.fiber.builder.ConfigValueBuilder ConfigValueBuilder
     */
    public static <T> ConfigValueBuilder<T> builder(@Nonnull Class<T> type) {
        return new ConfigValueBuilder<>(type);
    }

    /**
     * Creates a scalar {@code ConfigValueBuilder}.
     *
     * @param defaultValue the default value of the {@link ConfigValue} that will be produced by the created builder.
     * @param <T> the type of value the {@link ConfigValue} produced by the builder holds
     * @return the newly created builder
     * @see me.zeroeightsix.fiber.builder.ConfigValueBuilder ConfigValueBuilder
     */
    public static <T> ConfigValueBuilder<T> builder(@Nonnull T defaultValue) {
        @SuppressWarnings("unchecked") Class<T> type = (Class<T>) defaultValue.getClass();
        return new ConfigValueBuilder<>(type).withDefaultValue(defaultValue);
    }

    /**
     * Creates an aggregate {@code ConfigValueBuilder}.
     *
     * @param defaultValue the default array of values the {@link ConfigValue} will hold.
     * @param <E> the type of elements {@code defaultValue} holds
     * @return the newly created builder
     * @see ConfigAggregateBuilder Aggregate
     */
    public static <E> ConfigAggregateBuilder<E[], E> builder(@Nonnull E[] defaultValue) {
        @SuppressWarnings("unchecked") Class<E[]> type = (Class<E[]>) defaultValue.getClass();
        return ConfigAggregateBuilder.create(type).withDefaultValue(defaultValue);
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
    public static <C extends Collection<E>, E> ConfigAggregateBuilder<C, E> builder(@Nonnull C defaultValue, Class<E> elementType) {
        @SuppressWarnings("unchecked") Class<C> type = (Class<C>) defaultValue.getClass();
        return ConfigAggregateBuilder.create(type, elementType).withDefaultValue(defaultValue);
    }

}
