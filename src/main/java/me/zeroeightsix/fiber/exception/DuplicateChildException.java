package me.zeroeightsix.fiber.exception;

public class DuplicateChildException extends IllegalStateException {
    public DuplicateChildException(String s) {
        super(s);
    }
}
