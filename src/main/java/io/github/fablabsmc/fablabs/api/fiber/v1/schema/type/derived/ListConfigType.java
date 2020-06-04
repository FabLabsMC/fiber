package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.Function;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConstraintAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.ListSerializableType;

/**
 * A {@link ConfigType} for collections of values. This may be used to represent lists, sets, or vectors.
 *
 * @param <R> The runtime type of the underlying {@link List} value.
 * @param <E> The element type of the underlying {@link List} value.
 */
public final class ListConfigType<R, E> extends ConfigType<R, List<E>, ListSerializableType<E>> {
	@SuppressWarnings("unchecked")
	public ListConfigType(ListSerializableType<E> serializedType, Class<? super R> runtimeType, Function<List<E>, R> f, Function<R, List<E>> f0) {
		super(serializedType, (Class<R>) runtimeType, f, f0);
	}

	@Override
	public <U> ListConfigType<U, E> derive(Class<? super U> runtimeType, Function<R, U> partialDeserializer, Function<U, R> partialSerializer) {
		return new ListConfigType<>(this.getSerializedType(), runtimeType, s -> partialDeserializer.apply(this.deserializer.apply(s)), u -> this.serializer.apply(partialSerializer.apply(u)));
	}

	@Override
	public ListConfigType<R, E> withType(ListSerializableType<E> newSpec) {
		this.checkTypeNarrowing(newSpec);
		return new ListConfigType<>(newSpec, this.getRuntimeType(), this.deserializer, this.serializer);
	}

	@Override
	public ListConfigType<R, E> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
		return processor.processList(this, annotation, annotated);
	}

	/**
	 * Returns a new {@link ListConfigType} with a minimum size constraint.
	 */
	public ListConfigType<R, E> withMinSize(int min) {
		ListSerializableType<E> current = this.getSerializedType();
		return this.withType(new ListSerializableType<>(current.getElementType(), min, current.getMaxSize(), current.hasUniqueElements()));
	}

	/**
	 * Returns a new {@link ListConfigType} with a maximum size constraint.
	 */
	public ListConfigType<R, E> withMaxSize(int max) {
		ListSerializableType<E> current = this.getSerializedType();
		return this.withType(new ListSerializableType<>(current.getElementType(), current.getMinSize(), max, current.hasUniqueElements()));
	}

	/**
	 * Returns a new {@link ListConfigType} with a uniqueness constraint.
	 */
	public ListConfigType<R, E> withUniqueElements() {
		ListSerializableType<E> current = this.getSerializedType();
		return this.withType(new ListSerializableType<>(current.getElementType(), current.getMinSize(), current.getMaxSize(), true));
	}
}
