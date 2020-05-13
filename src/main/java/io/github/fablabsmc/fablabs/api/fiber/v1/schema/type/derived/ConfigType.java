package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConstraintAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberConversionException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;

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
	protected final Function<S, R> deserializer;
	protected final Function<R, S> serializer;

	ConfigType(T serializedType, Class<R> runtimeType, Function<S, R> deserializer, Function<R, S> serializer) {
		this.runtimeType = runtimeType;
		this.serializedType = serializedType;
		this.deserializer = deserializer;
		this.serializer = serializer;
	}

	/**
	 * Derive a {@code ConfigType} from this type object.
	 *
	 * <p>The new {@code ConfigType} will have the same serialized type (with the same constraints),
	 * but a different runtime type. Values will be converted between the two
	 * types using composed functions: {@code toSerializedType(x) = this.toSerializedType(partialSerializer(x))}
	 * and {@code toRuntimeType(y) = partialDeserializer(this.toRuntimeType(y))}.
	 *
	 * @param runtimeType         a class object representing the runtime type of the new {@code ConfigType}
	 * @param partialDeserializer a partial deserialization function
	 * @param partialSerializer   a partial serialization function
	 * @param <U>                 the runtime type of the new {@code ConfigType}
	 * @return a derived {@code ConfigType} with the given {@code runtimeType}
	 */
	public abstract <U> ConfigType<U, S, T> derive(Class<? super U> runtimeType, Function<R, U> partialDeserializer, Function<U, R> partialSerializer);

	/**
	 * Replace the current serialized type used for specification with the given serialized type.
	 * The given serialized type must be the same or more constrained than the current
	 * serialized type.
	 *
	 * @param newSpec The new type specification.
	 * @return A ConfigType with the new type specification.
	 * @see SerializableType#isAssignableFrom(SerializableType)
	 */
	public abstract ConfigType<R, S, T> withType(T newSpec);

	/**
	 * Converts a runtime value from a client application to this {@code ConfigType}'s
	 * serialized type.
	 *
	 * @param runtimeValue the value to convert to serializable form
	 * @return a serializable equivalent of the runtime value
	 * @throws FiberConversionException if the value does not fit this converter's constraint
	 * @see SerializableType#accepts(Object)
	 * @see #toPlatformType(Object)
	 */
	public S toSerializedType(R runtimeValue) throws FiberConversionException {
		S s = this.toPlatformType(runtimeValue);

		if (!this.serializedType.accepts(s)) {
			throw new FiberConversionException("Serialized form " + s + "(" + this.serializedType.getPlatformType().getSimpleName() + ") of runtime value " + runtimeValue + "(" + this.runtimeType.getSimpleName() + ") does not satisfy constraints for type " + this.serializedType);
		}

		return s;
	}

	/**
	 * Converts directly a runtime value from a client application to an equivalent value in the serializable
	 * {@linkplain SerializableType#getPlatformType() platform type}. This method gives no guarantees regarding
	 * the conformance of the returned value to the serialized type's constraints.
	 *
	 * @param runtimeValue the value to convert to serializable form
	 * @return an unchecked serializable equivalent of the runtime value
	 * @see SerializableType#getPlatformType()
	 * @see #toSerializedType(Object)
	 */
	public S toPlatformType(R runtimeValue) {
		return this.serializer.apply(Objects.requireNonNull(runtimeValue));
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

		return Objects.requireNonNull(this.deserializer.apply(serializedValue));
	}

	/**
	 * The runtime type of values.
	 */
	public Class<R> getRuntimeType() {
		return this.runtimeType;
	}

	/**
	 * The underlying serialized type of values.
	 */
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

	/**
	 * Applies the constraints defined by the given constraint annotation.
	 *
	 * @param processor The processor for constraints of this type.
	 * @param annotation The annotation from which to extract constraints.
	 * @param annotated The annotated element. For example, a field in a POJO.
	 * @return a ConfigType representing this type, but constrained by the given constraint annotation.
	 */
	public abstract ConfigType<R, S, T> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated);

	void checkTypeNarrowing(T newSpec) {
		if (!this.serializedType.isAssignableFrom(newSpec)) {
			throw new IllegalStateException("Cannot widen the constraints on a TypeConverter");
		}
	}
}
