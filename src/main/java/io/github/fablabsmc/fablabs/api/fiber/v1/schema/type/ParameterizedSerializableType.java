package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.github.fablabsmc.fablabs.impl.fiber.constraint.ConstraintChecker;

/**
 * A {@link SerializableType} that represents a {@link ParameterizedType}.
 */
public abstract class ParameterizedSerializableType<T> extends SerializableType<T> {
	ParameterizedSerializableType(Class<T> platformType, ConstraintChecker<T, ? extends SerializableType<T>> checker) {
		super(platformType, checker);
	}

	/**
	 * The {@link ParameterizedType} used to represent values of this type.
	 */
	public abstract ParameterizedType getParameterizedType();

	@Override
	public Type getGenericPlatformType() {
		return this.getParameterizedType();
	}
}
