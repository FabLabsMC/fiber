package me.zeroeightsix.fiber.api.schema.type.derived;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.math.BigDecimal;
import java.util.function.Function;

import javax.annotation.Nonnull;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.DecimalSerializableType;

public final class NumberConfigType<T> extends ConfigType<T, BigDecimal, DecimalSerializableType> {
    public NumberConfigType(DecimalSerializableType serializedType, Class<T> runtimeType, Function<BigDecimal, T> f, Function<T, BigDecimal> f0) {
        super(serializedType, runtimeType, f, f0);
    }

    @Override
    public <U> NumberConfigType<U> derive(Class<? super U> runtimeType, Function<T, U> partialDeserializer, Function<U, T> partialSerializer) {
        @SuppressWarnings("unchecked") Class<U> c = (Class<U>) runtimeType;
        return new NumberConfigType<>(this.getSerializedType(), c, s -> partialDeserializer.apply(this.deserializer.apply(s)), u -> this.serializer.apply(partialSerializer.apply(u)));
    }

    @Override
    public NumberConfigType<T> withType(DecimalSerializableType newSpec) {
        this.checkTypeNarrowing(newSpec);
        return new NumberConfigType<>(newSpec, this.getRuntimeType(), this.deserializer, this.serializer);
    }

    @Override
    public NumberConfigType<T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processDecimal(this, annotation, annotated);
    }

    public NumberConfigType<T> withMinimum(Number min) {
        DecimalSerializableType current = this.getSerializedType();
        return this.withType(new DecimalSerializableType(this.toBigDecimal(min), current.getMaximum(), current.getIncrement()));
    }

    public NumberConfigType<T> withMaximum(Number max) {
        DecimalSerializableType current = this.getSerializedType();
        return this.withType(new DecimalSerializableType(current.getMinimum(), this.toBigDecimal(max), current.getIncrement()));
    }

    public NumberConfigType<T> withIncrement(Number step) {
        DecimalSerializableType current = this.getSerializedType();
        return this.withType(new DecimalSerializableType(current.getMinimum(), current.getMaximum(), this.toBigDecimal(step)));
    }

    public NumberConfigType<T> withValidRange(Number min, Number max, Number step) {
        return this.withType(new DecimalSerializableType(this.toBigDecimal(min), this.toBigDecimal(max), this.toBigDecimal(step)));
    }

    @Nonnull
    private BigDecimal toBigDecimal(Number t) {
        return new BigDecimal(t.toString());
    }
}
