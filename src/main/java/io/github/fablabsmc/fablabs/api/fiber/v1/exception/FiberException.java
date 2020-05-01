package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

/**
 * An exception thrown by fiber when something goes unrecoverably wrong. Unlike {@link RuntimeFiberException}, this exception must be caught.
 */
public class FiberException extends Exception {
	public FiberException(String message) {
		super(message);
	}

	public FiberException(String message, Throwable cause) {
		super(message, cause);
	}
}
