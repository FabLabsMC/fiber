package me.zeroeightsix.fiber.tree;

public class ConfigAttributeImpl<T> implements ConfigAttribute<T> {
    private final Class<T> type;
    private T value;

    public ConfigAttributeImpl(Class<T> type, T value) {
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
    public Class<T> getType() {
        return this.type;
    }
}
