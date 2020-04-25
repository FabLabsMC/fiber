package me.zeroeightsix.fiber.api.schema.type.derived;


import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.BooleanSerializableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

public final class BooleanConfigType<T> extends ConfigType<T, Boolean, BooleanSerializableType> {
    public BooleanConfigType(Class<T> actualType, Function<Boolean, T> f, Function<T, Boolean> f0) {
        super(BooleanSerializableType.BOOLEAN, actualType, f, f0);
    }

    @Override
    public <U> BooleanConfigType<U> derive(Class<? super U> runtimeType, Function<T, U> g, Function<U, T> g0) {
        @SuppressWarnings("unchecked") Class<U> c = (Class<U>) runtimeType;
        return new BooleanConfigType<>(c, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public ConfigType<T, Boolean, BooleanSerializableType> withType(BooleanSerializableType newSpec) {
        // no narrowing possible for booleans
        return this;
    }

    @Override
    public BooleanConfigType<T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processBoolean(this, annotation, annotated);
    }
}
