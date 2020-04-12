package me.zeroeightsix.fiber.exception;

public class DuplicateChildException extends IllegalTreeStateException {
    public DuplicateChildException(String message) {
        super(message);
    }
}
