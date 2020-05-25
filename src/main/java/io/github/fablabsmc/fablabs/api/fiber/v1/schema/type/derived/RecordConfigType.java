package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.function.Function;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConstraintAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.RecordSerializableType;

/**
 * A {@link ConfigType} for fixed heterogeneous records. This is typically used to store POJOs or tuples,
 * but may be used for any parametric serialized form.
 *
 * @param <R> The runtime type of the underlying {@link ConfigBranch} value.
 */
public final class RecordConfigType<R> extends ConfigType<R, Map<String, Object>, RecordSerializableType> {
	@SuppressWarnings("unchecked")
	public RecordConfigType(RecordSerializableType serializedType, Class<? super R> runtimeType, Function<Map<String, Object>, R> f, Function<R, Map<String, Object>> f0) {
		super(serializedType, (Class<R>) runtimeType, f, f0);
	}

	@Override
	public <U> RecordConfigType<U> derive(Class<? super U> runtimeType, Function<R, U> partialDeserializer, Function<U, R> partialSerializer) {
		return new RecordConfigType<>(this.getSerializedType(), runtimeType, s -> partialDeserializer.apply(this.deserializer.apply(s)), u -> this.serializer.apply(partialSerializer.apply(u)));
	}

	@Override
	public RecordConfigType<R> withType(RecordSerializableType newSpec) {
		this.checkTypeNarrowing(newSpec);
		return new RecordConfigType<>(newSpec, this.getRuntimeType(), this.deserializer, this.serializer);
	}

	@Override
	public RecordConfigType<R> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
		return processor.processRecord(this, annotation, annotated);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName()
				+ "("
				+ "["
				+ String.join(", ", this.getSerializedType().getFields().keySet())
				+ "]"
				+ " : " + this.getRuntimeType().getSimpleName()
				+ ")";
	}
}
