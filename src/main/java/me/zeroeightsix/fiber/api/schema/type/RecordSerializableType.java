package me.zeroeightsix.fiber.api.schema.type;

import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.impl.constraint.RecordTypeChecker;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public final class RecordSerializableType extends SerializableType<ConfigBranch> {
    private final Map<String, SerializableType<?>> fields;
    private final RecordTypeChecker constraint;

    public RecordSerializableType(Map<String, SerializableType<?>> fields) {
        super(ConfigBranch.class);
        this.fields = fields;
        this.constraint = new RecordTypeChecker(this);
    }

    public Map<String, SerializableType<?>> getFields() {
        return this.fields;
    }

    @Override
    public <S> void serialize(TypeSerializer<S> serializer, S target) {
        serializer.serialize(this, target);
    }

    @Override
    protected RecordTypeChecker getConstraint() {
        return this.constraint;
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
