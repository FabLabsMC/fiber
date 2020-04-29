package me.zeroeightsix.fiber.api.tree;

import me.zeroeightsix.fiber.api.FiberId;
import me.zeroeightsix.fiber.api.schema.type.SerializableType;
import me.zeroeightsix.fiber.impl.tree.ConfigAttributeImpl;

public interface ConfigAttribute<T> extends Property<T> {
    static <T> ConfigAttribute<T> create(FiberId id, SerializableType<T> type, T defaultValue) {
        return new ConfigAttributeImpl<>(id, type, defaultValue);
    }

    @Override
    default Class<T> getType() {
        return this.getConfigType().getPlatformType();
    }

    SerializableType<T> getConfigType();

    FiberId getIdentifier();
}
