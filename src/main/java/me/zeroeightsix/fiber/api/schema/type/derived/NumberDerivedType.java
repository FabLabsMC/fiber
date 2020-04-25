package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.DecimalConfigType;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.math.BigDecimal;
import java.util.function.Function;

public final class NumberDerivedType<T> extends DerivedType<T, BigDecimal, DecimalConfigType> {
    public NumberDerivedType(DecimalConfigType serializedType, Class<T> runtimeType, Function<BigDecimal, T> f, Function<T, BigDecimal> f0) {
        super(serializedType, runtimeType, f, f0);
    }

    @Override
    public <U> NumberDerivedType<U> derive(Class<? super U> runtimeType, Function<T, U> g, Function<U, T> g0) {
        @SuppressWarnings("unchecked") Class<U> c = (Class<U>) runtimeType;
        return new NumberDerivedType<>(this.getSerializedType(), c, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public NumberDerivedType<T> withType(DecimalConfigType newSpec) {
        this.checkTypeNarrowing(newSpec);
        return new NumberDerivedType<>(newSpec, this.getRuntimeType(), this.f, this.f0);
    }

    @Override
    public NumberDerivedType<T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processDecimal(this, annotation, annotated);
    }

    public NumberDerivedType<T> withMinimum(Number min) {
        DecimalConfigType current = this.getSerializedType();
        return this.withType(new DecimalConfigType(this.toBigDecimal(min), current.getMaximum(), current.getIncrement()));
    }

    public NumberDerivedType<T> withMaximum(Number max) {
        DecimalConfigType current = this.getSerializedType();
        return this.withType(new DecimalConfigType(current.getMinimum(), this.toBigDecimal(max), current.getIncrement()));
    }

    public NumberDerivedType<T> withIncrement(Number step) {
        DecimalConfigType current = this.getSerializedType();
        return this.withType(new DecimalConfigType(current.getMinimum(), current.getMaximum(), this.toBigDecimal(step)));
    }

    public NumberDerivedType<T> withValidRange(Number min, Number max, Number step) {
        return this.withType(new DecimalConfigType(this.toBigDecimal(min), this.toBigDecimal(max), this.toBigDecimal(step)));
    }

    @Nonnull
    private BigDecimal toBigDecimal(Number t) {
        return new BigDecimal(t.toString());
    }
}
