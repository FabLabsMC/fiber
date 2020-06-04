package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

import javax.annotation.Nullable;

/**
 * Thrown when a {@link io.github.fablabsmc.fablabs.api.fiber.v1.serialization.ValueSerializer}
 * cannot deserialize a value.
 */
public class ValueDeserializationException extends FiberException {
	@Nullable
	private final Object value;
	private final Class<?> targetType;

	public ValueDeserializationException(@Nullable Object value, Class<?> targetType, String message) {
		super(message);
		this.value = value;
		this.targetType = targetType;
	}

	public ValueDeserializationException(@Nullable Object value, Class<?> targetType, String message, Throwable cause) {
		super(message, cause);
		this.value = value;
		this.targetType = targetType;
	}

	/**
	 * The value that could not be deserialized.
	 */
	@Nullable
	public Object getValue() {
		return value;
	}

	/**
	 * The target type that could not be deserialized to.
	 */
	public Class<?> getTargetType() {
		return targetType;
	}
}
