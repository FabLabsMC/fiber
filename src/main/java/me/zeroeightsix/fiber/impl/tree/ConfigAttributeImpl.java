package me.zeroeightsix.fiber.impl.tree;

import me.zeroeightsix.fiber.api.schema.ConfigType;
import me.zeroeightsix.fiber.api.tree.ConfigAttribute;

public class ConfigAttributeImpl<T> implements ConfigAttribute<T> {
    private final ConfigType<T, T> type;
    private T value;

    public ConfigAttributeImpl(ConfigType<T, T> type, T value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean setValue(T value) {
        this.value = value;
        return true;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public ConfigType<T, T> getConfigType() {
        return this.type;
    }
}
