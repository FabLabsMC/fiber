package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.TypeSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.ValueSerializer;
import io.github.fablabsmc.fablabs.impl.fiber.constraint.BooleanConstraintChecker;

/**
 * The {@link SerializableType} for {@code boolean} values.
 */
public final class BooleanSerializableType extends PlainSerializableType<Boolean> {
	public static final BooleanSerializableType BOOLEAN = new BooleanSerializableType();

	private BooleanSerializableType() {
		super(Boolean.class, BooleanConstraintChecker.instance());
	}

	@Override
	public <S> void serialize(TypeSerializer<S> serializer, S target) {
		serializer.serialize(this, target);
	}

	@Override
	public <S> S serializeValue(Boolean value, ValueSerializer<S, ?> serializer) {
		return serializer.serializeBoolean(value, this);
	}

	@Override
	public <S> Boolean deserializeValue(S elem, ValueSerializer<S, ?> serializer) throws ValueDeserializationException {
		return serializer.deserializeBoolean(elem, this);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof BooleanSerializableType;
	}

	@Override
	public int hashCode() {
		return 1337;
	}

	@Override
	public String toString() {
		return BooleanSerializableType.class.getSimpleName();
	}
}
