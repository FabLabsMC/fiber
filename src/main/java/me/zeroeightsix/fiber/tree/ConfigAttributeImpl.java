package me.zeroeightsix.fiber.tree;

public class ConfigAttributeImpl<T> implements ConfigAttribute<T> {
    private T value;
    private final Class<T> type;

    public ConfigAttributeImpl(T value, Class<T> type) {
        this.value = value;
        this.type = type;
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
    public Class<T> getType() {
        return this.type;
    }
}
