package me.zeroeightsix.fiber.exception;

public class FiberException extends Exception {

    public FiberException(String s) {
        super(s);
    }

    public FiberException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
