package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;

/**
 * Thrown when an annotated POJO cannot be processed into a valid
 * {@link ConfigTree}.
 *
 * @see io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings
 */
public class FiberTypeProcessingException extends FiberException {
	public FiberTypeProcessingException(String message) {
		super(message);
	}

	public FiberTypeProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}
