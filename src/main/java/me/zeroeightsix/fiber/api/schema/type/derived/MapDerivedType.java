package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.MapConfigType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.function.Function;

public final class MapDerivedType<R, V> extends DerivedType<R, Map<String, V>, MapConfigType<V>> {

    @SuppressWarnings("unchecked")
    public MapDerivedType(MapConfigType<V> serializedType, Class<? super R> runtimeType, Function<Map<String, V>, R> f, Function<R, Map<String, V>> f0) {
        super(serializedType, (Class<R>) runtimeType, f, f0);
    }

    @Override
    public <U> MapDerivedType<U, V> derive(Class<? super U> runtimeType, Function<R, U> g, Function<U, R> g0) {
        return new MapDerivedType<>(this.getSerializedType(), runtimeType, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public MapDerivedType<R, V> withType(MapConfigType<V> newSpec) {
        this.checkTypeNarrowing(newSpec);
        return new MapDerivedType<>(newSpec, this.getRuntimeType(), this.f, this.f0);
    }

    public MapDerivedType<R, V> withMinSize(int min) {
        MapConfigType<V> current = this.getSerializedType();
        return this.withType(new MapConfigType<>(current.getValueType(), min, current.getMaxSize()));
    }

    public MapDerivedType<R, V> withMaxSize(int max) {
        MapConfigType<V> current = this.getSerializedType();
        return this.withType(new MapConfigType<>(current.getValueType(), current.getMinSize(), max));
    }

    @Override
    public MapDerivedType<R, V> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processMap(this, annotation, annotated);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "(" +
                this.getSerializedType().getPlatformType().getSimpleName() +
                "<String, " + this.getSerializedType().getValueType().getPlatformType().getSimpleName() + ">" +
                " : " + this.getRuntimeType().getSimpleName() +
                ")";
    }
}
