package me.zeroeightsix.fiber.impl.tree;

import me.zeroeightsix.fiber.api.builder.ConfigLeafBuilder;
import me.zeroeightsix.fiber.api.constraint.Constraint;
import me.zeroeightsix.fiber.api.constraint.ConstraintType;
import me.zeroeightsix.fiber.api.schema.ConfigType;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    private final ConfigType<T, T0> type;

    /**
     * Creates a {@code ConfigLeaf}.
     *
     * @param name         the name for this node
     * @param type         the type of value this item holds
     * @param comment      the comment for this node
     * @param defaultValue the default value for this node
     *                     <p> While the default value should generally satisfy the supplied {@code constraints},
     *                     this is not enforced by this constructor.
     *                     This allows constraints such as {@link ConstraintType#FINAL} to work as intended.
     *                     If this {@code ConfigLeaf} is built by a {@link ConfigLeafBuilder}, this criterion will always be met.
     * @param listener     the consumer or listener for this item. When this item's value changes, the consumer will be called with the old value as first argument and the new value as second argument.
     * @see ConfigLeafBuilder
     */
    public ConfigLeafImpl(@Nonnull String name, @Nonnull ConfigType<T, T0> type, @Nullable String comment, @Nullable T defaultValue, @Nonnull BiConsumer<T, T> listener) {
        super(name, comment);
        this.defaultValue = defaultValue;
        this.listener = listener;
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

    @Override
    public ConfigType<T, T0> getConfigType() {
        return this.type;
    }

    @Override
    public boolean accepts(T value) {
        return this.acceptsRaw(this.type.toSerializedType(value));
    }

    @Override
    public boolean setValue(@Nullable T value) {
        return this.setValueFrom(this.type.toSerializedType(value));
    }

    @Override
    public boolean setValueFrom(T0 rawValue) {
        T0 correctedValue = rawValue;
        for (Constraint<? super T0> constraint : this.type.getConstraints()) {
            @SuppressWarnings("unchecked") Constraint.TestResult<T0> result = (Constraint.TestResult<T0>) constraint.test(correctedValue);
            if (!result.hasPassed()) {
                if (!result.getCorrectedValue().isPresent()) {
                    return false;
                }
                correctedValue = result.getCorrectedValue().get();
            }
        }

        T oldValue = this.value;
        this.value = this.type.toActualType(correctedValue);
        this.listener.accept(oldValue, this.value);
        return true;
    }

    @Override
    public boolean acceptsRaw(@Nullable T0 rawValue) {
        for (Constraint<? super T0> constraint : this.type.getConstraints()) {
            if (!constraint.test(rawValue).hasPassed()) {
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
    public String toString() {
        return this.getClass().getSimpleName()
                + '<' + this.type.getActualType().getSimpleName()
                + ">[name=" + this.getName()
                + ", comment=" + this.getComment()
                + "]";
    }

}
