package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.exception.FiberConversionException;
import me.zeroeightsix.fiber.api.schema.type.ConfigType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

public abstract class DerivedType<R, S, T extends ConfigType<S>> {
    private final T serializedType;
    private final Class<R> runtimeType;
    protected final Function<S, R> f;
    protected final Function<R, S> f0;

    DerivedType(T serializedType, Class<R> runtimeType, Function<S, R> f, Function<R, S> f0) {
        this.runtimeType = runtimeType;
        this.serializedType = serializedType;
        this.f = f;
        this.f0 = f0;
    }

    public abstract  <U> DerivedType<U, S, T> derive(Class<? super U> runtimeType, Function<R, U> g, Function<U, R> g0);

    public abstract DerivedType<R, S, T> withType(T newSpec);

    public S toSerializedType(R runtimeValue) {
        return this.f0.apply(runtimeValue);
    }

    /**
     * Converts a serialized value to this converter's runtime type.
     *
     * @param serializedValue the new value this property should receive
     * @return a runtime equivalent of the serialized value
     * @throws FiberConversionException if the serialized value does not fit this converter's constraints
     */
    public R toRuntimeType(S serializedValue) throws FiberConversionException {
        if (!this.serializedType.accepts(serializedValue)) {
            throw new FiberConversionException("Invalid serialized value " + serializedValue);
        }
        return this.f.apply(serializedValue);
    }

    public Class<R> getRuntimeType() {
        return this.runtimeType;
    }

    public T getSerializedType() {
        return this.serializedType;
    }

    public abstract DerivedType<R, S, T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated);

    void checkTypeNarrowing(T newSpec) {
        if (!this.serializedType.isAssignableFrom(newSpec)) {
            throw new IllegalStateException("Cannot widen the constraints on a TypeConverter");
        }
    }
}
