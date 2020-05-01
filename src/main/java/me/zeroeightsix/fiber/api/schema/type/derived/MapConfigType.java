package me.zeroeightsix.fiber.api.schema.type.derived;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.function.Function;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.MapSerializableType;
import me.zeroeightsix.fiber.api.schema.type.StringSerializableType;

public final class MapConfigType<R, V> extends ConfigType<R, Map<String, V>, MapSerializableType<V>> {
	@SuppressWarnings("unchecked")
	public MapConfigType(MapSerializableType<V> serializedType, Class<? super R> runtimeType, Function<Map<String, V>, R> f, Function<R, Map<String, V>> f0) {
		super(serializedType, (Class<R>) runtimeType, f, f0);
	}

	@Override
	public <U> MapConfigType<U, V> derive(Class<? super U> runtimeType, Function<R, U> partialDeserializer, Function<U, R> partialSerializer) {
		return new MapConfigType<>(this.getSerializedType(), runtimeType, s -> partialDeserializer.apply(this.deserializer.apply(s)), u -> this.serializer.apply(partialSerializer.apply(u)));
	}

	@Override
	public MapConfigType<R, V> withType(MapSerializableType<V> newSpec) {
		this.checkTypeNarrowing(newSpec);
		return new MapConfigType<>(newSpec, this.getRuntimeType(), this.deserializer, this.serializer);
	}

	public MapConfigType<R, V> withMinSize(int min) {
		MapSerializableType<V> current = this.getSerializedType();
		return this.withType(new MapSerializableType<>(StringSerializableType.DEFAULT_STRING, current.getValueType(), min, current.getMaxSize()));
	}

	public MapConfigType<R, V> withMaxSize(int max) {
		MapSerializableType<V> current = this.getSerializedType();
		return this.withType(new MapSerializableType<>(StringSerializableType.DEFAULT_STRING, current.getValueType(), current.getMinSize(), max));
	}

	@Override
	public MapConfigType<R, V> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
		return processor.processMap(this, annotation, annotated);
	}
}
