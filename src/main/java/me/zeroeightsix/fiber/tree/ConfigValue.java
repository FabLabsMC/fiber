package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.builder.ConfigValueBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ConfigValue<T> extends ConfigLeaf implements Property<T> {

    @Nullable
    private T value;
    @Nullable
    private T defaultValue;
    @Nonnull
    private final BiConsumer<T, T> consumer;
    @Nonnull
    private final Predicate<T> restriction;

    @Nonnull
    private final Class<T> type;

    public ConfigValue(@Nullable String name, @Nullable String comment, @Nullable T defaultValue, @Nonnull BiConsumer<T, T> consumer, @Nonnull Predicate<T> restriction, @Nonnull Class<T> type) {
        super(name, comment);
        this.defaultValue = defaultValue;
        this.consumer = consumer;
        this.restriction = restriction;
        this.type = type;
    }

    @Override
    @Nullable
    public T getValue() {
        return value;
    }

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

    @Nonnull
    public BiConsumer<T, T> getListener() {
        return consumer;
    }

    public static <T> ConfigValueBuilder<T> builder(Class<T> type) {
        return new ConfigValueBuilder<>(type);
    }

}
