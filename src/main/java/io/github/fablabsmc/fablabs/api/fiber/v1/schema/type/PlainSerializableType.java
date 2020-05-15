package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.lang.reflect.Type;

import io.github.fablabsmc.fablabs.impl.fiber.constraint.ConstraintChecker;

/**
 * A {@link SerializableType} that represents a plain (non-parameterized) type.
 *
 * @param <T> The Java platform type.
 */
public abstract class PlainSerializableType<T> extends SerializableType<T> {
	PlainSerializableType(Class<T> platformType, ConstraintChecker<T, ? extends PlainSerializableType<T>> checker) {
		super(platformType, checker);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getErasedPlatformType() {
		// this unchecked cast is safe when T is not a parameterized type.
		return (Class<T>) super.getErasedPlatformType();
	}

	@Override
	public Type getGenericPlatformType() {
		return this.getErasedPlatformType();
	}
}
