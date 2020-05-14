package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * A {@link ParameterizedType} implementation used by {@link ParameterizedSerializableType}.
 */
final class ParameterizedTypeImpl implements ParameterizedType {
	private final Class<?> rawType;
	private final Type[] typeArguments;

	ParameterizedTypeImpl(Class<?> rawType, Type... typeArguments) {
		this.rawType = rawType;
		this.typeArguments = typeArguments;
	}

	@Override
	public Type[] getActualTypeArguments() {
		return this.typeArguments.clone();
	}

	@Override
	public Type getRawType() {
		return this.rawType;
	}

	@Override
	public Type getOwnerType() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;

		if (!rawType.equals(that.rawType)) return false;
		return Arrays.equals(typeArguments, that.typeArguments);
	}

	@Override
	public int hashCode() {
		int result = rawType.hashCode();
		result = 31 * result + Arrays.hashCode(typeArguments);
		return result;
	}
}
