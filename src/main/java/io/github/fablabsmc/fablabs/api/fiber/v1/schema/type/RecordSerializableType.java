package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.TypeSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.ValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigNode;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.NodeCollection;
import io.github.fablabsmc.fablabs.impl.fiber.constraint.RecordConstraintChecker;
import io.github.fablabsmc.fablabs.impl.fiber.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.impl.fiber.tree.ConfigBranchImpl;
import io.github.fablabsmc.fablabs.impl.fiber.tree.ConfigLeafImpl;

public final class RecordSerializableType extends PlainSerializableType<ConfigBranch> {
	private final Map<String, SerializableType<?>> fields;

	public RecordSerializableType(Map<String, SerializableType<?>> fields) {
		super(ConfigBranch.class, RecordConstraintChecker.instance());
		fields.keySet().forEach(Objects::requireNonNull);
		this.fields = fields;
	}

	public Map<String, SerializableType<?>> getFields() {
		return this.fields;
	}

	@Override
	public <S> void serialize(TypeSerializer<S> serializer, S target) {
		serializer.serialize(this, target);
	}

	@Override
	public <S> S serializeValue(ConfigBranch value, ValueSerializer<S, ?> serializer) {
		return FiberSerialization.serializeNode(value, serializer);
	}

	@Override
	public <S> ConfigBranch deserializeValue(S elem, ValueSerializer<S, ?> serializer) throws ValueDeserializationException {
		Map<String, S> map = serializer.deserializeMap(elem);
		ConfigBranch branch = new ConfigBranchImpl();
		NodeCollection nodes = branch.getItems();

		for (Map.Entry<String, SerializableType<?>> entry : this.fields.entrySet()) {
			S childElem = map.get(entry.getKey());

			if (childElem != null) {
				nodes.add(deserializeChildNode(entry.getKey(), entry.getValue(), childElem, serializer));
			}
		}

		return branch;
	}

	private <S, T> ConfigNode deserializeChildNode(String name, SerializableType<T> type, S elem, ValueSerializer<S, ?> ctx) throws ValueDeserializationException {
		T value = type.deserializeValue(elem, ctx);

		if (value instanceof ConfigNode) {
			return (ConfigNode) value;
		} else {
			return new ConfigLeafImpl<>(name, type, null, value, (a, b) -> {
			});
		}
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", RecordSerializableType.class.getSimpleName() + "[", "]")
				.add("fields=" + this.fields.entrySet().stream()
						.map(entry -> entry.getKey() + ':' + entry.getValue())
						.collect(Collectors.joining(", ", "{", "}")))
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		RecordSerializableType that = (RecordSerializableType) o;
		return Objects.equals(this.fields, that.fields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.fields);
	}
}
