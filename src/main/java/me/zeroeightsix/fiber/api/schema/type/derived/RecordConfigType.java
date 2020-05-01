package me.zeroeightsix.fiber.api.schema.type.derived;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.RecordSerializableType;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;

public final class RecordConfigType<R> extends ConfigType<R, ConfigBranch, RecordSerializableType> {
	@SuppressWarnings("unchecked")
	public RecordConfigType(RecordSerializableType serializedType, Class<? super R> runtimeType, Function<ConfigBranch, R> f, Function<R, ConfigBranch> f0) {
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
