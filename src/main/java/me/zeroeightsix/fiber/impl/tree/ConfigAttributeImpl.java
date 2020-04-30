package me.zeroeightsix.fiber.impl.tree;

import me.zeroeightsix.fiber.api.FiberId;
import me.zeroeightsix.fiber.api.schema.type.SerializableType;
import me.zeroeightsix.fiber.api.tree.ConfigAttribute;

public class ConfigAttributeImpl<T> implements ConfigAttribute<T> {
    private final FiberId identifier;
    private final SerializableType<T> type;
    private T value;

    public ConfigAttributeImpl(FiberId identifier, SerializableType<T> type, T value) {
        this.identifier = identifier;
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
    public SerializableType<T> getConfigType() {
        return this.type;
    }

    @Override
    public FiberId getIdentifier() {
        return this.identifier;
    }
}
