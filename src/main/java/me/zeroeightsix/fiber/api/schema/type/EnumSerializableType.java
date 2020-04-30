package me.zeroeightsix.fiber.api.schema.type;

import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.impl.constraint.EnumConstraintChecker;

import java.util.*;

public final class EnumSerializableType extends SerializableType<String> {

    private final Set<String> validValues;

    public EnumSerializableType(String... validValues) {
        this(new HashSet<>(Arrays.asList(validValues)));
    }

    public EnumSerializableType(Set<String> validValues) {
        super(String.class, EnumConstraintChecker.instance());
        this.validValues = Collections.unmodifiableSet(new HashSet<>(validValues));
    }

    public Set<String> getValidValues() {
        return this.validValues;
    }

    @Override
    public <S> void serialize(TypeSerializer<S> serializer, S target) {
        serializer.serialize(this, target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        EnumSerializableType that = (EnumSerializableType) o;
        return Objects.equals(this.validValues, that.validValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.validValues);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EnumSerializableType.class.getSimpleName() + "[", "]")
                .add("validValues=" + validValues)
                .toString();
    }
}
