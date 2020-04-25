package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.EnumConfigType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

public final class EnumDerivedType<T> extends DerivedType<T, String, EnumConfigType> {
    public EnumDerivedType(EnumConfigType serializedType, Class<T> runtimeType, Function<String, T> f, Function<T, String> f0) {
        super(serializedType, runtimeType, f, f0);
    }

    @Override
    public <U> EnumDerivedType<U> derive(Class<? super U> runtimeType, Function<T, U> g, Function<U, T> g0) {
        @SuppressWarnings("unchecked") Class<U> c = (Class<U>) runtimeType;
        return new EnumDerivedType<>(this.getSerializedType(), c, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public EnumDerivedType<T> withType(EnumConfigType newSpec) {
        this.checkTypeNarrowing(newSpec);
        return new EnumDerivedType<>(newSpec, this.getRuntimeType(), this.f, this.f0);
    }

    @Override
    public EnumDerivedType<T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processEnum(this, annotation, annotated);
    }
}
