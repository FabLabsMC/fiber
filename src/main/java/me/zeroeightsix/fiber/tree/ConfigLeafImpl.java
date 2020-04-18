package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.builder.ConfigLeafBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.ConstraintType;
import me.zeroeightsix.fiber.schema.ConvertibleType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.BiConsumer;

public class ConfigLeafImpl<T, T0> extends ConfigNodeImpl implements ConfigLeaf<T, T0> {

    @Nullable
    private T value;
    @Nullable
    private T0 rawValue;
    @Nullable
    private final T defaultValue;
    @Nonnull
    private final BiConsumer<T, T> listener;
    @Nonnull
    private final Set<Constraint<? super T0>> constraints;
    @Nonnull
    private final ConvertibleType<T, T0> type;

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
     * @see me.zeroeightsix.fiber.builder.ConfigAggregateBuilder
     */
    public ConfigLeafImpl(@Nonnull String name, @Nullable String comment, @Nullable T defaultValue, @Nonnull BiConsumer<T, T> listener, @Nonnull Set<Constraint<? super T0>> constraints, @Nonnull ConvertibleType<T, T0> type) {
        super(name, comment);
        this.defaultValue = defaultValue;
        this.listener = listener;
        this.constraints = constraints;
        this.type = type;
        this.setValue(defaultValue);
    }

    @Override
    @Nullable
    public T getValue() {
        return this.value;
    }

    @Override
    @Nullable
    public T0 getRawValue() {
        return rawValue;
    }

    @Nonnull
    @Override
    public Class<T> getType() {
        return this.type.getActualType();
    }

    @Override
    public ConvertibleType<T, T0> getConvertibleType() {
        return this.type;
    }

    @Override
    public boolean setValue(@Nullable T value) {
        T0 convertedValue = this.type.toRawType(value);
        for (Constraint<? super T0> constraint : this.constraints) {
            @SuppressWarnings("unchecked") Constraint.TestResult<T0> result = (Constraint.TestResult<T0>) constraint.test(convertedValue);
            if (!result.hasPassed()) {
                if (!result.getCorrectedValue().isPresent()) {
                    return false;
                }
                convertedValue = result.getCorrectedValue().get();
            }
        }

        T oldValue = this.value;
        this.value = value;
        this.listener.accept(oldValue, value);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p> A value is accepted if it satisfies every constraint
     * this setting has.
     *
     * @param value the value to check
     * @see #setValue(Object)
     * @see #getConstraints()
     */
    @Override
    public boolean accepts(@Nullable T value) {
        T0 converted = this.type.toRawType(value);
        for (Constraint<? super T0> constraint : this.constraints) {
            if (!constraint.test(converted).hasPassed()) {
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
    public Set<Constraint<? super T0>> getConstraints() {
        return constraints;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '<' + getType().getSimpleName() + ">[name=" + getName() + ", comment=" + getComment() + "]";
    }

}
