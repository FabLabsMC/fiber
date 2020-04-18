package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.schema.ConvertibleType;

public class ConfigAttributeImpl<T> implements ConfigAttribute<T> {
    private final ConvertibleType<T, T> type;
    private T value;

    public ConfigAttributeImpl(ConvertibleType<T, T> type, T value) {
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
    public T getRawValue() {
        return this.value;
    }

    @Override
    public ConvertibleType<T, T> getConvertibleType() {
        return this.type;
    }
}
