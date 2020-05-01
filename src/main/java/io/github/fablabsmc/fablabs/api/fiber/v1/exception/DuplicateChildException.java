package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

public class DuplicateChildException extends IllegalTreeStateException {
	public DuplicateChildException(String message) {
		super(message);
	}
}
