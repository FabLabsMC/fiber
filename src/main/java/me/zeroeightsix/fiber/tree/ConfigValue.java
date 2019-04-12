package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.builder.ConfigValueBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigValue<T> extends ConfigNode implements Property<T> {

    @Nullable
    private T value;

    @Nonnull
    private final Class<T> type;

    public ConfigValue(@Nullable String name, @Nullable String comment, @Nonnull Class<T> type) {
        super(name, comment);
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
        this.value = value;
        return true;
    }

    public static <T> ConfigValueBuilder<T> builder(Class<T> type) {
        return new ConfigValueBuilder<>(type);
    }

}
