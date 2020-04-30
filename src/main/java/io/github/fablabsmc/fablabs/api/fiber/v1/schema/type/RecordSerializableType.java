package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.TypeSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.impl.fiber.constraint.RecordConstraintChecker;

public final class RecordSerializableType extends SerializableType<ConfigBranch> {
	private final Map<String, SerializableType<?>> fields;

	public RecordSerializableType(Map<String, SerializableType<?>> fields) {
		super(ConfigBranch.class, RecordConstraintChecker.instance());
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
