package me.zeroeightsix.fiber.api.exception;

public class DuplicateChildException extends IllegalTreeStateException {
    public DuplicateChildException(String message) {
        super(message);
    }
}
