package me.zeroeightsix.fiber.api.schema.type;

import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.impl.constraint.EnumTypeChecker;

import java.util.*;

public final class EnumSerializableType extends SerializableType<String> {

    private final Set<String> validValues;
    private final EnumTypeChecker constraint;

    public EnumSerializableType(String... validValues) {
        this(new HashSet<>(Arrays.asList(validValues)));
    }

    public EnumSerializableType(Set<String> validValues) {
        super(String.class);
        this.validValues = Collections.unmodifiableSet(validValues);
        this.constraint = new EnumTypeChecker(this);
    }

    public Set<String> getValidValues() {
        return this.validValues;
    }

    @Override
    public <S> void serialize(TypeSerializer<S> serializer, S target) {
        serializer.serialize(this, target);
    }

    @Override
    protected EnumTypeChecker getConstraint() {
        return this.constraint;
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
}
