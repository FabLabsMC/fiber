package me.zeroeightsix.fiber.impl.tree;

import me.zeroeightsix.fiber.api.builder.ConfigLeafBuilder;
import me.zeroeightsix.fiber.api.constraint.TypeCheckResult;
import me.zeroeightsix.fiber.api.schema.type.ConfigType;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public final class ConfigLeafImpl<T> extends ConfigNodeImpl implements ConfigLeaf<T> {

    @Nullable
    private T value;
    @Nullable
    private final T defaultValue;
    @Nonnull
    private BiConsumer<T, T> listener;
    @Nonnull
    private final ConfigType<T> type;

    /**
     * Creates a {@code ConfigLeaf}.
     *
     * @param name         the name for this node
     * @param type         the type of value this item holds
     * @param comment      the comment for this node
     * @param defaultValue the default value for this node
     * @param listener     the consumer or listener for this item. When this item's value changes, the consumer will be called with the old value as first argument and the new value as second argument.
     * @see ConfigLeafBuilder
     */
    public ConfigLeafImpl(@Nonnull String name, @Nonnull ConfigType<T> type, @Nullable String comment, @Nullable T defaultValue, @Nonnull BiConsumer<T, T> listener) {
        super(name, comment);
        this.defaultValue = defaultValue;
        this.listener = listener;
        this.type = type;
        if (defaultValue != null) {
            this.setValue(defaultValue);
        }
    }

    @Override
    @Nullable
    public T getValue() {
        return this.value;
    }

    @Override
    public ConfigType<T> getConfigType() {
        return this.type;
    }

    @Override
    public boolean accepts(T value) {
        // ensure ClassCastException comes sooner than later
        // maybe accept any Object and return false if not an instance?
        return this.type.accepts(this.type.getPlatformType().cast(value));
    }

    @Override
    public boolean setValue(@Nullable T value) {
        // ensure ClassCastException comes sooner than later
        // maybe accept any Object and return false if not an instance?
        T correctedValue;
        TypeCheckResult<T> result = this.type.test(this.type.getPlatformType().cast(value));
        if (result.hasPassed()) {
            correctedValue = value;
        } else {
            if (!result.getCorrectedValue().isPresent()) {
                return false;
            }
            correctedValue = result.getCorrectedValue().get();
        }

        T oldValue = this.value;
        this.value = correctedValue;
        this.listener.accept(oldValue, this.value);
        return true;
    }

    @Override
    @Nonnull
    public BiConsumer<T, T> getListener() {
        return listener;
    }

    @Override
    public void addChangeListener(BiConsumer<T, T> listener) {
        this.listener = this.listener.andThen(listener);
    }

    @Override
    @Nullable
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + '<' + this.type.getPlatformType().getSimpleName()
                + ">[name=" + this.getName()
                + ", comment=" + this.getComment()
                + ", value=" + this.getValue()
                + "]";
    }
}
