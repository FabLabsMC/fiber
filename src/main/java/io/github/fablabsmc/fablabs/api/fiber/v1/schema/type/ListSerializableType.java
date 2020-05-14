package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.TypeSerializer;
import io.github.fablabsmc.fablabs.impl.fiber.constraint.ListConstraintChecker;

/**
 * @param <E> the type of elements objects of this type hold
 */
public final class ListSerializableType<E> extends ParameterizedSerializableType<List<E>> {
	private final SerializableType<E> elementType;
	private final boolean unique;
	private final int minSize;
	private final int maxSize;

	public ListSerializableType(SerializableType<E> elementType) {
		this(elementType, 0, Integer.MAX_VALUE, false);
	}

	@SuppressWarnings("unchecked")
	public ListSerializableType(SerializableType<E> elementType, int minSize, int maxSize, boolean unique) {
		super((Class<List<E>>) (Class<?>) List.class, ListConstraintChecker.instance());
		this.elementType = elementType;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.unique = unique;
	}

	public SerializableType<E> getElementType() {
		return this.elementType;
	}

	public int getMinSize() {
		return this.minSize;
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	public boolean hasUniqueElements() {
		return this.unique;
	}

	@Override
	public ParameterizedType getParameterizedType() {
		return new ParameterizedTypeImpl(this.getErasedPlatformType(), this.elementType.getGenericPlatformType());
	}

	@Override
	public <S> void serialize(TypeSerializer<S> serializer, S target) {
		serializer.serialize(this, target);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		ListSerializableType<?> that = (ListSerializableType<?>) o;
		return this.unique == that.unique
				&& this.minSize == that.minSize
				&& this.maxSize == that.maxSize
				&& Objects.equals(this.elementType, that.elementType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.elementType, this.unique, this.minSize, this.maxSize);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ListSerializableType.class.getSimpleName() + "<" + this.elementType + ">" + "[", "]")
				.add("unique=" + unique)
				.add("minSize=" + minSize)
				.add("maxSize=" + maxSize)
				.toString();
	}
}
