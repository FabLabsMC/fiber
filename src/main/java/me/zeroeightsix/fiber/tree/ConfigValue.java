package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.ConstraintType;
import me.zeroeightsix.fiber.constraint.FinalConstraint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A {@code ConfigLeaf} with some value of type {@code T}.
 *
 * @param <T> The type of value this class holds
 * @see ConfigLeaf
 * @see ConfigValueBuilder
 * @see me.zeroeightsix.fiber.builder.ConfigAggregateBuilder
 */
public class ConfigValue<T> extends ConfigLeaf implements Property<T> {

    @Nullable
    private T value;
    @Nullable
    private final T defaultValue;
    @Nonnull
    private final BiConsumer<T, T> listener;
    @Nonnull
    private final List<Constraint<? super T>> constraints;
    @Nonnull
    private final Class<T> type;

    /**
     * Creates a {@code ConfigValue}.
     *
     * @param name         the name for this item
     * @param comment      the comment for this item
     * @param defaultValue the default value for this item
     *                     <p> While the default value should generally satisfy the supplied {@code constraints},
     *                     this is not enforced by this constructor.
     *                     This allows constraints such as {@link ConstraintType#FINAL} to work as intended.
     *                     If this {@code ConfigValue} is built by a {@link ConfigValueBuilder}, this criterion will always be met.
     * @param listener     the consumer or listener for this item. When this item's value changes, the consumer will be called with the old value as first argument and the new value as second argument.
     * @param constraints  the list of constraints for this item. For a value to be accepted, all constraints must be satisfied.
     * @param type         the type of value this item holds
     * @see ConfigValueBuilder
     * @see me.zeroeightsix.fiber.builder.ConfigAggregateBuilder
     */
    public ConfigValue(@Nonnull String name, @Nullable String comment, @Nullable T defaultValue, @Nonnull BiConsumer<T, T> listener, @Nonnull List<Constraint<? super T>> constraints, @Nonnull Class<T> type) {
        super(name, comment);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.listener = listener;
        this.constraints = constraints;
        this.type = type;
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
        if (!this.accepts(value)) return false;

        T oldValue = this.value;
        this.value = value;
        this.listener.accept(oldValue, value);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p> A value is accepted if it satisfies every constraint
     * this setting has. Note that some constraints such as {@link FinalConstraint}
     * will cause even the current {@linkplain #getValue() value} to be rejected.
     *
     * @param value the value to check
     * @see #setValue(Object)
     * @see #getConstraints()
     */
    @Override
    public boolean accepts(@Nullable T value) {
        for (Constraint<? super T> constraint : this.constraints) {
            if (!constraint.test(value)) {
                return false;
            }
        }
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
        return listener;
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
        return constraints;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '<' + getType().getSimpleName() + ">[name=" + getName() + ", comment=" + getComment() + "]";
    }

}
