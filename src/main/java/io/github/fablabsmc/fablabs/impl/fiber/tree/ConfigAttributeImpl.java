package io.github.fablabsmc.fablabs.impl.fiber.tree;

import java.util.Objects;

import io.github.fablabsmc.fablabs.api.fiber.v1.FiberId;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigAttribute;

public class ConfigAttributeImpl<T> implements ConfigAttribute<T> {
	private final FiberId identifier;
	private final SerializableType<T> type;
	private T value;

	public ConfigAttributeImpl(FiberId identifier, SerializableType<T> type, T value) {
		this.identifier = identifier;
		this.type = type;
		this.value = Objects.requireNonNull(value);
	}

	@Override
	public boolean setValue(T value) {
		this.value = Objects.requireNonNull(value);
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
