package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.ListConfigType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.Function;

public final class ListDerivedType<R, E> extends DerivedType<R, List<E>, ListConfigType<E>> {

    @SuppressWarnings("unchecked")
    public ListDerivedType(ListConfigType<E> serializedType, Class<? super R> runtimeType, Function<List<E>, R> f, Function<R, List<E>> f0) {
        super(serializedType, (Class<R>) runtimeType, f, f0);
    }

    @Override
    public <U> ListDerivedType<U, E> derive(Class<? super U> runtimeType, Function<R, U> g, Function<U, R> g0) {
        return new ListDerivedType<>(this.getSerializedType(), runtimeType, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public ListDerivedType<R, E> withType(ListConfigType<E> newSpec) {
        this.checkTypeNarrowing(newSpec);
        return new ListDerivedType<>(newSpec, this.getRuntimeType(), this.f, this.f0);
    }

    @Override
    public ListDerivedType<R, E> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processList(this, annotation, annotated);
    }

    public ListDerivedType<R, E> withMinSize(int min) {
        ListConfigType<E> current = this.getSerializedType();
        return this.withType(new ListConfigType<>(current.getElementType(), min, current.getMaxSize(), current.hasUniqueElements()));
    }

    public ListDerivedType<R, E> withMaxSize(int max) {
        ListConfigType<E> current = this.getSerializedType();
        return this.withType(new ListConfigType<>(current.getElementType(), current.getMinSize(), max, current.hasUniqueElements()));
    }

    public ListDerivedType<R, E> withUniqueElements() {
        ListConfigType<E> current = this.getSerializedType();
        return this.withType(new ListConfigType<>(current.getElementType(), current.getMinSize(), current.getMaxSize(), true));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "(" +
                this.getSerializedType().getPlatformType().getSimpleName() +
                "<" + this.getSerializedType().getElementType().getPlatformType().getSimpleName() + ">" +
                " : " + this.getRuntimeType().getSimpleName() +
                ")";
    }
}
