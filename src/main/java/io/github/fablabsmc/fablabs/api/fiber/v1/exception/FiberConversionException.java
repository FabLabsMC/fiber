package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

/**
 * Thrown when a value cannot be converted to or from a valid serialized form.
 */
public class FiberConversionException extends RuntimeFiberException {
	public FiberConversionException(String message) {
		super(message);
	}
}
