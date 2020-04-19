package me.zeroeightsix.fiber.impl.tree;

import me.zeroeightsix.fiber.api.builder.ConfigLeafBuilder;
import me.zeroeightsix.fiber.api.constraint.Constraint;
import me.zeroeightsix.fiber.api.constraint.ConstraintType;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;
import me.zeroeightsix.fiber.impl.constraint.FinalConstraint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

public class ConfigLeafImpl<T> extends ConfigNodeImpl implements ConfigLeaf<T> {

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
     * Creates a {@code ConfigLeaf}.
     *
     * @param name         the name for this node
     * @param comment      the comment for this node
     * @param defaultValue the default value for this node
     *                     <p> While the default value should generally satisfy the supplied {@code constraints},
     *                     this is not enforced by this constructor.
     *                     This allows constraints such as {@link ConstraintType#FINAL} to work as intended.
     *                     If this {@code ConfigLeaf} is built by a {@link ConfigLeafBuilder}, this criterion will always be met.
     * @param listener     the consumer or listener for this item. When this item's value changes, the consumer will be called with the old value as first argument and the new value as second argument.
     * @param constraints  the list of constraints for this item. For a value to be accepted, all constraints must be satisfied.
     * @param type         the type of value this item holds
     * @see ConfigLeafBuilder
     * @see me.zeroeightsix.fiber.api.builder.ConfigAggregateBuilder
     */
    public ConfigLeafImpl(@Nonnull String name, @Nullable String comment, @Nullable T defaultValue, @Nonnull BiConsumer<T, T> listener, @Nonnull List<Constraint<? super T>> constraints, @Nonnull Class<T> type) {
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

    @Override
    @Nonnull
    public BiConsumer<T, T> getListener() {
        return listener;
    }

    @Override
    @Nullable
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    @Nonnull
    public List<Constraint<? super T>> getConstraints() {
        return constraints;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '<' + getType().getSimpleName() + ">[name=" + getName() + ", comment=" + getComment() + "]";
    }

}
