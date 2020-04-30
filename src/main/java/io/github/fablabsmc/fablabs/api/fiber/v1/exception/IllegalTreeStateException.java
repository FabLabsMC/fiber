package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;

/**
 * Signals that a {@link ConfigTree} is not in an appropriate state for
 * the requested operation.
 */
public class IllegalTreeStateException extends IllegalStateException {
	public IllegalTreeStateException(String message) {
		super(message);
	}
}
