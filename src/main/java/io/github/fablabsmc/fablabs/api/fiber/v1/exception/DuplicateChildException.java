package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

/**
 * Thrown when a child node is added to a config tree, but a node
 * with the same name already exists in the tree.
 */
public class DuplicateChildException extends IllegalTreeStateException {
	public DuplicateChildException(String message) {
		super(message);
	}
}
