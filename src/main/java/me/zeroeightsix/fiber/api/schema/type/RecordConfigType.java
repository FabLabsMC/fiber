package me.zeroeightsix.fiber.api.schema.type;

import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.impl.constraint.RecordTypeChecker;

import java.util.Map;
import java.util.Objects;

public final class RecordConfigType extends ConfigType<ConfigBranch> {
    private final Map<String, ConfigType<?>> fields;
    private final RecordTypeChecker constraint;

    public RecordConfigType(Map<String, ConfigType<?>> fields) {
        super(ConfigBranch.class);
        this.fields = fields;
        this.constraint = new RecordTypeChecker(this);
    }

    public Map<String, ConfigType<?>> getFields() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        RecordConfigType that = (RecordConfigType) o;
        return Objects.equals(this.fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fields);
    }
}
