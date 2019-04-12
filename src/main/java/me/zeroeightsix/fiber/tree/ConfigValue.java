package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.builder.ConfigValueBuilder;

import javax.annotation.Nullable;

public class ConfigValue<T> extends ConfigNode implements Property<T> {

    @Nullable
    private T value;

    public ConfigValue(@Nullable String name, @Nullable String comment) {
        super(name, comment);
    }

    @Override
    @Nullable
    public T getValue() {
        return value;
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
