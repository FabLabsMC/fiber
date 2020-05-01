package io.github.fablabsmc.fablabs.api.fiber.v1.tree;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.impl.fiber.tree.ConfigAttributeImpl;
import io.github.fablabsmc.fablabs.api.fiber.v1.FiberId;

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
