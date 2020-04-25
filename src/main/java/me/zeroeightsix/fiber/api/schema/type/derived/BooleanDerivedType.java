package me.zeroeightsix.fiber.api.schema.type.derived;


import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.BooleanConfigType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

public final class BooleanDerivedType<T> extends DerivedType<T, Boolean, BooleanConfigType> {
    public BooleanDerivedType(Class<T> actualType, Function<Boolean, T> f, Function<T, Boolean> f0) {
        super(BooleanConfigType.BOOLEAN, actualType, f, f0);
    }

    @Override
    public <U> BooleanDerivedType<U> derive(Class<? super U> runtimeType, Function<T, U> g, Function<U, T> g0) {
        @SuppressWarnings("unchecked") Class<U> c = (Class<U>) runtimeType;
        return new BooleanDerivedType<>(c, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public DerivedType<T, Boolean, BooleanConfigType> withType(BooleanConfigType newSpec) {
        // no narrowing possible for booleans
        return this;
    }

    @Override
    public BooleanDerivedType<T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processBoolean(this, annotation, annotated);
    }
}
