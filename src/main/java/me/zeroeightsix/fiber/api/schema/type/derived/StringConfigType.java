package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.StringSerializableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class StringConfigType<T> extends ConfigType<T, String, StringSerializableType> {
    public StringConfigType(StringSerializableType serializedType, Class<T> runtimeType, Function<String, T> f, Function<T, String> f0) {
        super(serializedType, runtimeType, f, f0);
    }

    @Override
    public <U> StringConfigType<U> derive(Class<? super U> runtimeType, Function<T, U> g, Function<U, T> g0) {
        @SuppressWarnings("unchecked") Class<U> c = (Class<U>) runtimeType;
        return new StringConfigType<>(this.getSerializedType(), c, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public StringConfigType<T> withType(StringSerializableType newSpec) {
        this.checkTypeNarrowing(newSpec);
        return new StringConfigType<>(newSpec, this.getRuntimeType(), this.f, this.f0);
    }

    @Override
    public StringConfigType<T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processString(this, annotation, annotated);
    }

    public StringConfigType<T> withMinLength(int min) {
        StringSerializableType current = this.getSerializedType();
        return this.withType(new StringSerializableType(min, current.getMaxLength(), current.getPattern()));
    }

    public StringConfigType<T> withMaxLength(int max) {
        StringSerializableType current = this.getSerializedType();
        return this.withType(new StringSerializableType(current.getMinLength(), max, current.getPattern()));
    }

    public StringConfigType<T> withPattern(String regex) {
        return this.withPattern(Pattern.compile(regex));
    }

    public StringConfigType<T> withPattern(Pattern pattern) {
        StringSerializableType current = this.getSerializedType();
        return this.withType(new StringSerializableType(current.getMinLength(), current.getMaxLength(), pattern));
    }
}