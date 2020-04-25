package me.zeroeightsix.fiber.api.tree;

import me.zeroeightsix.fiber.api.schema.type.SerializableType;

public interface ConfigAttribute<T> extends Property<T> {

    @Override
    default Class<T> getType() {
        return this.getConfigType().getPlatformType();
    }

    SerializableType<T> getConfigType();
}
