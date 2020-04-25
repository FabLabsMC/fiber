package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.EnumSerializableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

public final class EnumConfigType<T> extends ConfigType<T, String, EnumSerializableType> {
    public EnumConfigType(EnumSerializableType serializedType, Class<T> runtimeType, Function<String, T> f, Function<T, String> f0) {
        super(serializedType, runtimeType, f, f0);
    }

    @Override
    public <U> EnumConfigType<U> derive(Class<? super U> runtimeType, Function<T, U> g, Function<U, T> g0) {
        @SuppressWarnings("unchecked") Class<U> c = (Class<U>) runtimeType;
        return new EnumConfigType<>(this.getSerializedType(), c, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public EnumConfigType<T> withType(EnumSerializableType newSpec) {
        this.checkTypeNarrowing(newSpec);
        return new EnumConfigType<>(newSpec, this.getRuntimeType(), this.f, this.f0);
    }

    @Override
    public EnumConfigType<T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processEnum(this, annotation, annotated);
    }
}
