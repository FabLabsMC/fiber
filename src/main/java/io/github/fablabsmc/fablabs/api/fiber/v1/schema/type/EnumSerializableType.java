package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.TypeSerializer;
import io.github.fablabsmc.fablabs.impl.fiber.constraint.EnumConstraintChecker;

public final class EnumSerializableType extends SerializableType<String> {
	private final Set<String> validValues;

	public EnumSerializableType(String... validValues) {
		this(new LinkedHashSet<>(Arrays.asList(validValues)));
	}

	public EnumSerializableType(Set<String> validValues) {
		super(String.class, EnumConstraintChecker.instance());
		this.validValues = Collections.unmodifiableSet(new LinkedHashSet<>(validValues));
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
