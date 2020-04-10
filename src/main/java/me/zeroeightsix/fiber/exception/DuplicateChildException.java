package me.zeroeightsix.fiber.exception;

public class DuplicateChildException extends RuntimeFiberException {
    public DuplicateChildException(String s) {
        super(s);
    }
}
