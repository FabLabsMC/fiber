package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.StringConfigType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class StringDerivedType<T> extends DerivedType<T, String, StringConfigType> {
    public StringDerivedType(StringConfigType serializedType, Class<T> runtimeType, Function<String, T> f, Function<T, String> f0) {
        super(serializedType, runtimeType, f, f0);
    }

    @Override
    public <U> StringDerivedType<U> derive(Class<? super U> runtimeType, Function<T, U> g, Function<U, T> g0) {
        @SuppressWarnings("unchecked") Class<U> c = (Class<U>) runtimeType;
        return new StringDerivedType<>(this.getSerializedType(), c, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public StringDerivedType<T> withType(StringConfigType newSpec) {
        this.checkTypeNarrowing(newSpec);
        return new StringDerivedType<>(newSpec, this.getRuntimeType(), this.f, this.f0);
    }

    @Override
    public StringDerivedType<T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processString(this, annotation, annotated);
    }

    public StringDerivedType<T> withMinLength(int min) {
        StringConfigType current = this.getSerializedType();
        return this.withType(new StringConfigType(min, current.getMaxLength(), current.getPattern()));
    }

    public StringDerivedType<T> withMaxLength(int max) {
        StringConfigType current = this.getSerializedType();
        return this.withType(new StringConfigType(current.getMinLength(), max, current.getPattern()));
    }

    public StringDerivedType<T> withPattern(String regex) {
        return this.withPattern(Pattern.compile(regex));
    }

    public StringDerivedType<T> withPattern(Pattern pattern) {
        StringConfigType current = this.getSerializedType();
        return this.withType(new StringConfigType(current.getMinLength(), current.getMaxLength(), pattern));
    }
}
