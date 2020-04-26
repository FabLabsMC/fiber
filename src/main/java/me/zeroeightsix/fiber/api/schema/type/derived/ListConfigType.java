package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.ListSerializableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.Function;

public final class ListConfigType<R, E> extends ConfigType<R, List<E>, ListSerializableType<E>> {

    @SuppressWarnings("unchecked")
    public ListConfigType(ListSerializableType<E> serializedType, Class<? super R> runtimeType, Function<List<E>, R> f, Function<R, List<E>> f0) {
        super(serializedType, (Class<R>) runtimeType, f, f0);
    }

    @Override
    public <U> ListConfigType<U, E> derive(Class<? super U> runtimeType, Function<R, U> g, Function<U, R> g0) {
        return new ListConfigType<>(this.getSerializedType(), runtimeType, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public ListConfigType<R, E> withType(ListSerializableType<E> newSpec) {
        this.checkTypeNarrowing(newSpec);
        return new ListConfigType<>(newSpec, this.getRuntimeType(), this.f, this.f0);
    }

    @Override
    public ListConfigType<R, E> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processList(this, annotation, annotated);
    }

    public ListConfigType<R, E> withMinSize(int min) {
        ListSerializableType<E> current = this.getSerializedType();
        return this.withType(new ListSerializableType<>(current.getElementType(), min, current.getMaxSize(), current.hasUniqueElements()));
    }

    public ListConfigType<R, E> withMaxSize(int max) {
        ListSerializableType<E> current = this.getSerializedType();
        return this.withType(new ListSerializableType<>(current.getElementType(), current.getMinSize(), max, current.hasUniqueElements()));
    }

    public ListConfigType<R, E> withUniqueElements() {
        ListSerializableType<E> current = this.getSerializedType();
        return this.withType(new ListSerializableType<>(current.getElementType(), current.getMinSize(), current.getMaxSize(), true));
    }
}
