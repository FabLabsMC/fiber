package me.zeroeightsix.fiber.api.tree;

import me.zeroeightsix.fiber.api.FiberId;
import me.zeroeightsix.fiber.api.schema.type.SerializableType;
import me.zeroeightsix.fiber.api.schema.type.derived.ConfigType;
import me.zeroeightsix.fiber.impl.tree.ConfigAttributeImpl;

public interface ConfigAttribute<T> extends Property<T> {
    static <R, A> ConfigAttribute<A> create(FiberId id, ConfigType<R, A, ?> type, R defaultValue) {
        return create(id, type.getSerializedType(), type.toSerializedType(defaultValue));
    }

    static <A> ConfigAttribute<A> create(FiberId id, SerializableType<A> type, A defaultValue) {
        return new ConfigAttributeImpl<>(id, type, defaultValue);
    }

    @Override
    default Class<T> getType() {
        return this.getConfigType().getPlatformType();
    }

    SerializableType<T> getConfigType();

    FiberId getIdentifier();
}
