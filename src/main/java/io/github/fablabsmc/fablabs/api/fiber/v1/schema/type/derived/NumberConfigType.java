package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.math.BigDecimal;
import java.util.function.Function;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConstraintAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.DecimalSerializableType;

/**
 * A {@link ConfigType} for numeric ranges.
 *
 * @param <T> The runtime type of the underlying {@link BigDecimal} value.
 */
public final class NumberConfigType<T> extends ConfigType<T, BigDecimal, DecimalSerializableType> {
	public NumberConfigType(DecimalSerializableType serializedType, Class<T> runtimeType, Function<BigDecimal, T> f, Function<T, BigDecimal> f0) {
		super(serializedType, runtimeType, f, f0);
	}

	@Override
	public <U> NumberConfigType<U> derive(Class<? super U> runtimeType, Function<T, U> partialDeserializer, Function<U, T> partialSerializer) {
		@SuppressWarnings("unchecked") Class<U> c = (Class<U>) runtimeType;
		return new NumberConfigType<>(this.getSerializedType(), c, s -> partialDeserializer.apply(this.deserializer.apply(s)), u -> this.serializer.apply(partialSerializer.apply(u)));
	}

	@Override
	public NumberConfigType<T> withType(DecimalSerializableType newSpec) {
		this.checkTypeNarrowing(newSpec);
		return new NumberConfigType<>(newSpec, this.getRuntimeType(), this.deserializer, this.serializer);
	}

	@Override
	public NumberConfigType<T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
		return processor.processDecimal(this, annotation, annotated);
	}

	/**
	 * Returns a {@link NumberConfigType} with the given minimum value.
	 */
	public NumberConfigType<T> withMinimum(T min) {
		DecimalSerializableType current = this.getSerializedType();
		return this.withType(new DecimalSerializableType(this.toSerializedType(min), current.getMaximum(), current.getIncrement()));
	}

	/**
	 * Returns a {@link NumberConfigType} with the given maximum value.
	 */
	public NumberConfigType<T> withMaximum(T max) {
		DecimalSerializableType current = this.getSerializedType();
		return this.withType(new DecimalSerializableType(current.getMinimum(), this.toSerializedType(max), current.getIncrement()));
	}

	/**
	 * Returns a {@link NumberConfigType} with the given step.
	 */
	public NumberConfigType<T> withIncrement(T step) {
		DecimalSerializableType current = this.getSerializedType();
		return this.withType(new DecimalSerializableType(current.getMinimum(), current.getMaximum(), this.toSerializedType(step)));
	}

	/**
	 * Returns a {@link NumberConfigType} with the given range.
	 */
	public NumberConfigType<T> withValidRange(T min, T max, T step) {
		return this.withType(new DecimalSerializableType(this.toSerializedType(min), this.toSerializedType(max), this.toSerializedType(step)));
	}
}
