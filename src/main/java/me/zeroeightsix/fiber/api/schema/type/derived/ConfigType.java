package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.exception.FiberConversionException;
import me.zeroeightsix.fiber.api.schema.type.SerializableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * A {@code ConfigType} contains serializable information describing
 * a data type as well as the information necessary to convert a serialized
 * value of that type to a friendlier runtime type.
 *
 * @param <R> the runtime type used by the client application
 * @param <S> the serialized type used by config trees and serializers
 * @param <T> the representation of the serialized type
 * @see SerializableType
 * @see BooleanConfigType
 * @see EnumConfigType
 * @see ListConfigType
 * @see MapConfigType
 * @see NumberConfigType
 * @see RecordConfigType
 * @see StringConfigType
 */
public abstract class ConfigType<R, S, T extends SerializableType<S>> {
    private final T serializedType;
    private final Class<R> runtimeType;
    protected final Function<S, R> f;
    protected final Function<R, S> f0;

    ConfigType(T serializedType, Class<R> runtimeType, Function<S, R> f, Function<R, S> f0) {
        this.runtimeType = runtimeType;
        this.serializedType = serializedType;
        this.f = f;
        this.f0 = f0;
    }

    /**
     * Derive a {@code ConfigType} from this type object.
     *
     * <p> The new {@code ConfigType} will have the same serialized type (with the same constraints),
     * but a different runtime type. Values will be converted between the two
     * types using composed functions: {@code toSerializedType(x) = this.toSerializedType(g0(x))}
     * and {@code toRuntimeType(y) = g(this.toRuntimeType(y))}.
     *
     * @param runtimeType a class object representing the runtime type of the new {@code ConfigType}
     * @param g           a partial deserialization function
     * @param g0          a partial serialization function
     * @param <U>         the runtime type of the new {@code ConfigType}
     * @return a derived {@code ConfigType} with the given {@code runtimeType}
     */
    public abstract <U> ConfigType<U, S, T> derive(Class<? super U> runtimeType, Function<R, U> g, Function<U, R> g0);

    public abstract ConfigType<R, S, T> withType(T newSpec);

    /**
     * Converts a runtime value from a client application to this {@code ConfigType}'s
     * serialized type.
     *
     * @param runtimeValue the value to convert to serializable form
     * @return a serializable equivalent of the runtime value
     * @see SerializableType#getPlatformType()
     */
    public S toSerializedType(R runtimeValue) {
        return this.f0.apply(runtimeValue);
    }

    /**
     * Converts a serialized value to this {@code ConfigType}'s runtime type.
     *
     * @param serializedValue the value to convert to runtime form
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

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("serializedType=" + this.serializedType)
                .add("runtimeType=" + this.runtimeType)
                .toString();
    }

    public abstract ConfigType<R, S, T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated);

    void checkTypeNarrowing(T newSpec) {
        if (!this.serializedType.isAssignableFrom(newSpec)) {
            throw new IllegalStateException("Cannot widen the constraints on a TypeConverter");
        }
    }
}
