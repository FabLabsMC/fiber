package io.github.fablabsmc.fablabs.api.fiber.v1.tree;

import javax.annotation.Nonnull;

import io.github.fablabsmc.fablabs.api.fiber.v1.FiberId;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.impl.fiber.tree.ConfigAttributeImpl;

public interface ConfigAttribute<T> extends Property<T> {
	static <R, A> ConfigAttribute<A> create(FiberId id, ConfigType<R, A, ?> type, @Nonnull R defaultValue) {
		return create(id, type.getSerializedType(), type.toSerializedType(defaultValue));
	}

	static <A> ConfigAttribute<A> create(FiberId id, SerializableType<A> type, @Nonnull A defaultValue) {
		return new ConfigAttributeImpl<>(id, type, defaultValue);
	}

	@Override
	default Class<T> getType() {
		return this.getConfigType().getPlatformType();
	}

	SerializableType<T> getConfigType();

	FiberId getIdentifier();
}
