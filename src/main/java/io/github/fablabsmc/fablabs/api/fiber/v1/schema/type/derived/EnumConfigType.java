package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConstraintAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.EnumSerializableType;

/**
 * A {@link ConfigType} for a finite set of values. These are often the constants of a Java enum,
 * but are represented as strings here in order to support serialization and deserialization.
 *
 * @param <T> The runtime type of the underlying finite string value.
 */
public final class EnumConfigType<T> extends ConfigType<T, String, EnumSerializableType> {
	public EnumConfigType(EnumSerializableType serializedType, Class<T> runtimeType, Function<String, T> f, Function<T, String> f0) {
		super(serializedType, runtimeType, f, f0);
	}

	@Override
	public <U> EnumConfigType<U> derive(Class<? super U> runtimeType, Function<T, U> partialDeserializer, Function<U, T> partialSerializer) {
		@SuppressWarnings("unchecked") Class<U> c = (Class<U>) runtimeType;
		return new EnumConfigType<>(this.getSerializedType(), c, s -> partialDeserializer.apply(this.deserializer.apply(s)), u -> this.serializer.apply(partialSerializer.apply(u)));
	}

	@Override
	public EnumConfigType<T> withType(EnumSerializableType newSpec) {
		this.checkTypeNarrowing(newSpec);
		return new EnumConfigType<>(newSpec, this.getRuntimeType(), this.deserializer, this.serializer);
	}

	@Override
	public EnumConfigType<T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
		return processor.processEnum(this, annotation, annotated);
	}

	/**
	 * Returns a new {@link EnumConfigType} that represents the given set of values. The values
	 * are converted to the serialized type {@link String} before being stored as the new type's constraints.
	 *
	 * @param values A Set of values of the runtime type whose serialized forms are the acceptable values.
	 * @return A new EnumConfigType with the provided constraint.
	 * @see #withValues(Object[])
	 */
	public EnumConfigType<T> withValues(Set<? extends T> values) {
		Set<String> strValues = values.stream().map(this::toSerializedType).collect(Collectors.toSet());
		return this.withType(new EnumSerializableType(strValues));
	}

	/**
	 * Returns a new {@link EnumConfigType} that represents the given set of values. The values
	 * are converted to the serialized type {@link String} before being stored as the new type's constraints.
	 *
	 * @param values A Set of values of the runtime type whose serialized forms are the acceptable values.
	 * @return A new EnumConfigType with the provided constraint.
	 * @see #withValues(Set)
	 */
	@SafeVarargs
	public final EnumConfigType<T> withValues(T... values) {
		Set<String> strValues = Arrays.stream(values).map(this::toSerializedType).collect(Collectors.toSet());
		return this.withType(new EnumSerializableType(strValues));
	}
}
