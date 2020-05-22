package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

import javax.annotation.Nullable;

/**
 * Thrown when a {@link io.github.fablabsmc.fablabs.api.fiber.v1.serialization.ValueSerializer}
 * cannot deserialize a value.
 */
public class ValueDeserializationException extends FiberException {
	/**
	 * The value that could not be deserialized.
	 */
	@Nullable
	private final Object value;

	/**
	 * The target type that could not be deserialized to.
	 */
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

	@Nullable
	public Object getValue() {
		return value;
	}

	public Class<?> getTargetType() {
		return targetType;
	}
}
